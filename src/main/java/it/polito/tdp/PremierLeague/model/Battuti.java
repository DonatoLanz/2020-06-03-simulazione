package it.polito.tdp.PremierLeague.model;

public class Battuti implements Comparable<Battuti> {

	private Player p;
	private int dist;
	public Battuti(Player p, int dist) {
		super();
		this.p = p;
		this.dist = dist;
	}
	public Player getP() {
		return p;
	}
	public void setP(Player p) {
		this.p = p;
	}
	public int getDist() {
		return dist;
	}
	public void setDist(int dist) {
		this.dist = dist;
	}
	@Override
	public int compareTo(Battuti o) {
		return this.dist-o.dist;
	}
	
	
}
