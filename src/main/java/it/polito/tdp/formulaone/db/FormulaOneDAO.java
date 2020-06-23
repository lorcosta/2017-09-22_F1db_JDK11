package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.formulaone.model.Adiacenza;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.LapTime;
import it.polito.tdp.formulaone.model.Race;
import it.polito.tdp.formulaone.model.Season;

public class FormulaOneDAO {

	public List<Season> getAllSeasons() {
		String sql = "SELECT year, url FROM seasons ORDER BY year";
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			List<Season> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Season(rs.getInt("year"), rs.getString("url")));
			}
			conn.close();
			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	public List<Race> getRaceBySeason(Season s,Map<Integer,Race> idMapRace){
		String sql = "SELECT * " + 
				"FROM races " + 
				"WHERE year=?";
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, s.getYear());
			ResultSet rs = st.executeQuery();
			List<Race> list = new ArrayList<>();
			while (rs.next()) {
				Race r= new Race(rs.getInt("raceId"),Year.of(rs.getInt("year")),rs.getInt("round"),
						rs.getInt("circuitId"),rs.getString("name"),rs.getDate("date").toLocalDate(),
						rs.getTime("time").toLocalTime(),rs.getString("url"));
				if(!idMapRace.containsKey(r.getRaceId())) {
					idMapRace.put(r.getRaceId(), r);
				}
				list.add(r);
			}
			conn.close();
			return list;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getPesoAdiacenza(Season s,Map<Integer,Race> idMapRace) {
		String sql="SELECT res1.raceId r1, res2.raceId r2, COUNT(DISTINCT res1.driverId) peso " + 
				"FROM results res1, results res2, races r1, races r2 " + 
				"WHERE r1.year=? AND r2.year=? AND res1.statusId=1 AND res2.statusId=1 AND "+
				"res1.driverId=res2.driverId AND res1.raceId!=res2.raceId AND res1.raceId=r1.raceId AND "+
				"res2.raceId=r2.raceId AND res1.raceId>res2.raceId " + 
				"GROUP BY res1.raceId, res2.raceId";
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, s.getYear());
			st.setInt(2, s.getYear());
			ResultSet rs = st.executeQuery();
			List<Adiacenza> adiacenze=new ArrayList<>();
			while (rs.next()) {
				Adiacenza a=new Adiacenza(idMapRace.get(rs.getInt("r1")), idMapRace.get(rs.getInt("r2")),
						rs.getDouble("peso"));
				adiacenze.add(a);
			}
			conn.close();
			return adiacenze;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<LapTime> getLapTime(Race r){
		String sql="SELECT * FROM lapTimes WHERE raceId=?";
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, r.getRaceId());
			ResultSet rs = st.executeQuery();
			List<LapTime> times=new ArrayList<>();
			while (rs.next()) {
				LapTime time=new LapTime(rs.getInt("raceId"),rs.getInt("driverId"),rs.getInt("lap"),
						rs.getInt("position"),rs.getString("time"),rs.getInt("milliseconds"));
				times.add(time);
			}
			conn.close();
			return times;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Driver> getPilotiGara(Race r){
		String sql="SELECT DISTINCT d.driverId, d.driverRef,d.number,d.code,d.forename,d.surname,d.dob,d.nationality,d.url" + 
				" FROM lapTimes l, drivers d WHERE raceId=? AND l.driverId=d.driverId";
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, r.getRaceId());
			ResultSet rs = st.executeQuery();
			List<Driver> drivers=new ArrayList<>();
			while (rs.next()) {
				Driver d= new Driver(rs.getInt("driverId"),rs.getString("driverRef"),rs.getInt("number"),
						rs.getString("code"),rs.getString("forename"),rs.getString("surname"),
						rs.getDate("dob").toLocalDate(),rs.getString("nationality"),rs.getString("url"));
				
				drivers.add(d);
			}
			conn.close();
			return drivers;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}

