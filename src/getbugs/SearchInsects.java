package getbugs;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.HashMap;
import org.json.*;

public class SearchInsects {
        String value;
	List<String> columnNamesList = new ArrayList<>();
	List<HashMap<String,String>> dictionaryList = new ArrayList<>();
	Connection conn;
	String JDBC_DRIVER = "org.h2.Driver";
	String DB_URL = "jdbc:h2:/media/leticija/88fe59cb-92ec-4a23-8e33-888bbe6ff16b/BUGY_DB";
	HashMap<String,String> dictionary = new HashMap<>();

	SearchInsects (String value) {
		this.value = value;
		try{
			this.conn = DriverManager.getConnection(DB_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String search () {
		
		//JDBC driver name and database URL
		List<String> metaArray = new ArrayList<>();
		// Register JDBC driver
		try {
			Class.forName(JDBC_DRIVER);
		}catch(ClassNotFoundException ex ){
			ex.printStackTrace();
		}
		System.out.println("Connecting to database...");
		try {
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
		return ("SEARCHAM "+value);
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
