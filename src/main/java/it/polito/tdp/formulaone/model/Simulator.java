package it.polito.tdp.formulaone.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import it.polito.tdp.formulaone.model.Event.Type;

public class Simulator {
	
	private PriorityQueue<Event> queue;
	private Float p;
	private Integer t;
	private Map<Driver,Integer> pilotiPunti;//mappa di punti conseguiti dai piloti
	private Map<Integer,Driver> idMapPiloti;
	//private List<LapTime> times;
	private Integer giroCorrente;//giro al quale sto verificando i tempi, se mi accorgo che il giro 
								//è appena cambiato allora memorizzo il driveri che passa e aggiorno this.giroCorrente
	
	public void init(Float p, Integer t,List<LapTime> times, List<Driver> piloti) {
		this.queue=new PriorityQueue<>();
		this.p=p;
		this.t=t;
		this.pilotiPunti=new HashMap<>();
		this.idMapPiloti=new HashMap<>();
		this.giroCorrente=0;//inizializzato a zero così anche al primo giro registro chi passa per primo
		for(Driver d:piloti) {
			this.pilotiPunti.put(d, 0);//inizializzo la mappa con zero punti per ogni pilota
			if(!this.idMapPiloti.containsKey(d.getDriverId())) {
				this.idMapPiloti.put(d.getDriverId(), d);
			}
		}
		//inserisco tutti gli eventi nella coda
		for(LapTime time:times) {
			this.queue.add(new Event(Type.TAGLIO_TRAGUARDO,time,this.idMapPiloti.get(time.getDriverId())));
		}
	}

	public void run() {
		int count=1;
		while(!this.queue.isEmpty()) {
			Event e=this.queue.poll();
			processEvent(e);
			System.out.println(e+", PUNTI:"+this.pilotiPunti.get(e.getDriver())+" GIRO: "+giroCorrente);
			count++;
			if(e.getType()==Type.TAGLIO_TRAGUARDO && count==23) {
				System.out.println("\n");
				count=1;
			}
		}
	}

	private void processEvent(Event e) {
		switch(e.getType()) {
		case PIT_STOP:
			
			break;
		case TAGLIO_TRAGUARDO:
			Double p=Math.random();
			if(p<=this.p) {
				//aggiungo evento PIT_STOP adesso e per T tempo
				e.getTime().setMiliseconds(e.getTime().getMiliseconds()+(this.t*1000));
				this.queue.add(new Event(Type.PIT_STOP,e.getTime(),e.getDriver()));
			}else if(!e.getTime().getLap().equals(this.giroCorrente)){
					this.giroCorrente++;
					//aggiungo un punto a questo pilota perchè ha tagliato il traguardo per primo
					Integer punti=this.pilotiPunti.get(e.getDriver());
					this.pilotiPunti.remove(e.getDriver());
					this.pilotiPunti.put(e.getDriver(), ++punti);
			}
			break;
		default:
			break;
		
		}
	}
	
	public Map<Driver,Integer> getPilotiPunti(){
		return this.pilotiPunti;
	}

}
