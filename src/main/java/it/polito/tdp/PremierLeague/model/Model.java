package it.polito.tdp.PremierLeague.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {

	private Graph<Player,DefaultWeightedEdge> grafo;
	private PremierLeagueDAO dao;
	private Map<Integer,Player> mapId;
	private List<Player> best;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
		this.mapId = new HashMap<>();
	    this.best = new LinkedList<>();
	}
	
	public String creaGrafo(double soglia) {
		this.grafo = new SimpleDirectedWeightedGraph<Player,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		List<Player> vertici = dao.getPlayer(soglia,this.mapId);
		Graphs.addAllVertices(this.grafo, vertici);
		
		List<Coppia> coppie = dao.getCoppie(soglia);
		for(Coppia c : coppie) {
			if(c.getT1() > c.getT2()) {
				Graphs.addEdgeWithVertices(this.grafo, this.mapId.get(c.getP1()),  this.mapId.get(c.getP2()), c.getT1()-c.getT2());
			}else {
				Graphs.addEdgeWithVertices(this.grafo, this.mapId.get(c.getP2()),  this.mapId.get(c.getP1()), c.getT2()-c.getT1());
			}
		}
		return "VERTICI: "+this.grafo.vertexSet().size()+"\nARCHI: "+this.grafo.edgeSet().size();
		
		
		}
	
	public List<Battuti> getBattuti(){
		int max = 0;
		List<Battuti> batt = new LinkedList<>();
		for(Player p : this.grafo.vertexSet()) {
			if(this.grafo.outDegreeOf(p) > max) {
				max = this.grafo.outDegreeOf(p);
			}
		}
		
		for(Player p : this.grafo.vertexSet()) {
			if(this.grafo.outDegreeOf(p) == max) {
				for(Player pp : Graphs.successorListOf(this.grafo,p)) {
					Battuti b = new Battuti(pp, (int)(this.grafo.getEdgeWeight(this.grafo.getEdge(p, pp))));
				    batt.add(b);
				}
				break;
			}
		}
		Collections.sort(batt);
		return batt;
		
	}
	
	public Player migl() {
		Player migl = null;
		int max = 0;
		List<Battuti> batt = new LinkedList<>();
		for(Player p : this.grafo.vertexSet()) {
			if(this.grafo.outDegreeOf(p) > max) {
				max = this.grafo.outDegreeOf(p);
			}
		}
		
		for(Player p : this.grafo.vertexSet()) {
			if(this.grafo.outDegreeOf(p) == max) {
				migl = p;
				break;
			}
		}
		
		return migl;
		
	}
	
	public List<Player> calcolaDreamTeam(int k){
		
		List<Player> parziale = new LinkedList<>();
		
		for(Player p : this.grafo.vertexSet()) {
			parziale.add(p);
			ricorsione(parziale,k);
			parziale.remove(parziale.size()-1);
		}
		return best;
		
		
	}
	
	
	
	
	private void ricorsione(List<Player> parziale, int k) {
		
		if(parziale.size()==k) {
			if(best.size()==0) {
				best = new LinkedList<>(parziale);
			}else {
				if(titTeam(parziale)> titTeam(best)) {
					best = new LinkedList<>(parziale);
				}
			}
			return;
		}
		
		if(prossimiPossibili(parziale).size()==0) {
			return;
		}
		
		for(Player p : prossimiPossibili(parziale)) {
			parziale.add(p);
			ricorsione(parziale,k);
			parziale.remove(parziale.size()-1);
		}
		
	}

	public List<Player> prossimiPossibili(List<Player> parziale){
		List<Player> noPossibili = new LinkedList<>();
		List<Player> possibili = new LinkedList<>();
		
		for(Player p : parziale) {
				for(Player pp : Graphs.successorListOf(this.grafo, p)) {
					if(!noPossibili.contains(pp)) {
						noPossibili.add(pp);
					}
				}
			
			
		}
		
		
		for(Player p : this.grafo.vertexSet()) {
			if(!noPossibili.contains(p) && !parziale.contains(p)) {
				possibili.add(p);
			}
		}
	return possibili;
	}
	
	public int titTeam(List<Player> parziale) {
		int titT=0;
		
		for(Player p : parziale) {
			titT += titG(p);
		}
		return titT;
	}
	
	
	public int titG(Player p) {
		int usc = 0;
		int ent = 0;
		
		for(DefaultWeightedEdge d : this.grafo.outgoingEdgesOf(p)) {
			usc += this.grafo.getEdgeWeight(d);
		}
		for(DefaultWeightedEdge d : this.grafo.incomingEdgesOf(p)) {
			ent += this.grafo.getEdgeWeight(d);
		}
		return usc-ent;
	}
	
}
