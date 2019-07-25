package com.let.bugy.server.getbugs;

import com.let.bugy.server.user.Database;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchInsects {
	String value;
	List<String> columnNamesList = new ArrayList<>();
	List<HashMap<String,String>> dictionaryList = new ArrayList<>();
	HashMap<String,String> dictionary = new HashMap<>();

	SearchInsects (String value) {
		this.value = value;
		try{
			//this.conn = DriverManager.getConnection(DB_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String search () {
		List<String> metaArray = new ArrayList<>();

		System.out.println("Connecting to database...");
		try {
			Connection conn = Database.getConnection();
			PreparedStatement st = conn.prepareStatement ("SELECT * FROM insecta",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			String statement = "";
			for (int i = 1; i<=numberOfColumns;i++){
				if (!(rsmd.getColumnName(i).toLowerCase().equals("id"))){//jer nemreju searchati broj (prema ID-u)
					statement += "SELECT * FROM insecta WHERE `"+rsmd.getColumnName(i)+"` = ? ";
					columnNamesList.add(rsmd.getColumnName(i));
					if (i != numberOfColumns){
						statement += " UNION ALL ";
					}
				}
			}
			PreparedStatement toSearch = conn.prepareStatement (statement, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			for (int i = 1; i<numberOfColumns; i++){
				toSearch.setString (i,value);
			}
			
			rs = toSearch.executeQuery();
			return(toJSON(rs));
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return (null);
	}

	public String toJSON (ResultSet rs) throws SQLException{
		JSONObject searchJSON = new JSONObject ();//glavni objekt u kojem bude lista objekti
		JSONArray JSONarray = new JSONArray ();
		JSONObject insect;
		while(rs.next()){
			insect = new JSONObject();
			for (String column:columnNamesList){
				insect.put(column,(rs.getString(column)));
			}
			JSONarray.put(insect);
		}
		searchJSON.put("search result",JSONarray);
		return(searchJSON.toString());
	}




	@Override
	public String toString() {
		return ("searchano je: "+value);
	}	
}
