package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Coppia;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Player> getPlayer(double soglia, Map<Integer,Player> map){
		String sql = "SELECT p.* "
				+ "FROM actions a, players p "
				+ "WHERE p.PlayerID = a.PlayerID "
				+ "GROUP BY a.PlayerID "
				+ "HAVING AVG(a.Goals) > ? ";
				
		List<Player> result = new LinkedList<>();
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, soglia);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
				map.put(player.getPlayerID(), player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	
	
	
	public List<Coppia> getCoppie(double soglia){
		String sql = "SELECT a1.PlayerID AS p1,a2.PlayerID AS p2, SUM(a1.TimePlayed) AS t1, SUM(a2.TimePlayed) AS t2 "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.MatchID = a2.MatchID AND a1.TeamID != a2.TeamID AND a1.`Starts` = 1 AND a2.`Starts` = 1 AND a1.PlayerID > a2.PlayerID AND a1.PlayerID IN (SELECT a.PlayerID "
				+ "FROM actions a "
				+ "GROUP BY a.PlayerID "
				+ "HAVING AVG(a.Goals) > ?) AND a2.PlayerID IN (SELECT a.PlayerID "
				+ "FROM actions a "
				+ "GROUP BY a.PlayerID "
				+ "HAVING AVG(a.Goals) > ?) "
				+ "GROUP BY a1.PlayerID,a2.PlayerID "
				+ "HAVING ABS(SUM(a1.TimePlayed) - SUM(a2.TimePlayed)) !=0 ";
		List<Coppia> result = new LinkedList<>();
				
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, soglia);
			st.setDouble(2, soglia);
			ResultSet res = st.executeQuery();
			while (res.next()) {

			Coppia c = new Coppia(res.getInt("p1"), res.getInt("p2"), res.getInt("t1"), res.getInt("t2"));
			result.add(c);
			}
			conn.close();
			System.out.println(result.size());
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
}
