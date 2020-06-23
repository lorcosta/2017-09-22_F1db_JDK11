package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	private FormulaOneDAO dao=new FormulaOneDAO();
	private Graph<Race,DefaultWeightedEdge> graph;
	private Map<Integer,Race> idMapRace;
	private Simulator sim=new Simulator();
	
	public Model() {
	}
	
	public List<Season> getSeasons(){
		return dao.getAllSeasons();
	}
	
	public List<Race> creaGrafo(Season s) {
		this.idMapRace=new HashMap<>();
		this.graph=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		List<Race> races=dao.getRaceBySeason(s,idMapRace);
		Graphs.addAllVertices(this.graph,races );
		List<Adiacenza> adiacenze=dao.getPesoAdiacenza(s,idMapRace);
		for(Adiacenza a:adiacenze) {
			if(!a.getR1().equals(a.getR2()) && !this.graph.containsEdge(a.getR1(), a.getR2()) &&
					this.graph.vertexSet().contains(a.getR1()) && this.graph.vertexSet().contains(a.getR2())) {
				Graphs.addEdgeWithVertices(this.graph, a.getR1(), a.getR2(),a.getPeso());
			}
		}
		return races;
	}
	public Integer getNumVertici() {
		return this.graph.vertexSet().size();
	}
	public Integer getNumArchi() {
		return this.graph.edgeSet().size();
	}
	
	public List<Adiacenza> getPesoMassimo(){
		List<Adiacenza> massimi=new ArrayList<>();
		Double pesoMassimo=Double.MIN_VALUE;
		for(DefaultWeightedEdge e:this.graph.edgeSet()) {
			if(this.graph.getEdgeWeight(e)>pesoMassimo) {
				pesoMassimo=this.graph.getEdgeWeight(e);
			}
		}
		for(DefaultWeightedEdge e:this.graph.edgeSet()) {
			if(this.graph.getEdgeWeight(e)==pesoMassimo) {
				massimi.add(new Adiacenza(this.graph.getEdgeSource(e),this.graph.getEdgeTarget(e),this.graph.getEdgeWeight(e)));
			}
		}
		return massimi;
	}

	public Map<Driver,Integer> simula(Float p, Integer t,Race r) {
		List<LapTime> times=dao.getLapTime(r);
		List<Driver> piloti=dao.getPilotiGara(r);
		sim.init(p,t,times,piloti);
		sim.run();
		return sim.getPilotiPunti();
	}
}
