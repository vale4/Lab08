package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private Graph<Airport, DefaultWeightedEdge>grafo;
	private Map<Integer, Airport>idMap;
	private ExtFlightDelaysDAO dao;
	
	public Model() {
		this.idMap=new HashMap<Integer, Airport>();
		dao=new ExtFlightDelaysDAO();
		dao.loadAllAirports(idMap);
	}
	
	public void creaGrafo(int distanzaMedia) {
		this.grafo=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		
		//Aggiungere i vertici
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//Aggiungere gli archi
		for(Rotta r : dao.getRotte(idMap, distanzaMedia)) {
			DefaultWeightedEdge edge=grafo.getEdge(r.getA1(), r.getA2());
			
			if(edge==null) {
				Graphs.addEdge(grafo, r.getA1(), r.getA2(), r.getPeso());
			}
			else {
				double peso=grafo.getEdgeWeight(edge);
				double newPeso=(peso+r.getPeso())/2;
				grafo.setEdgeWeight(edge, newPeso);
			}
		}
		
		System.out.println(String.format("Grafo Creato: #Vertci %d e #Archi %d", this.grafo.vertexSet().size(), this.grafo.edgeSet().size()));
		
		
		
	}
	
	
	public int numeroVertici() {
		return this.grafo.vertexSet().size();
	}
	
	
	public int numeroArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Rotta>getRotte(){
		List<Rotta>rotte=new ArrayList<Rotta>();
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			rotte.add(new Rotta(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), this.grafo.getEdgeWeight(e)));
		}
		return rotte;
	}

}
