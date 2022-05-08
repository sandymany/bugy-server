package user;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException; 
import java.sql.Statement;  
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.io.*;
import java.util.*;
import features.PrettyPrinter;

public class User{
	String username;
	String password;
	String numberFromRS;

	User (String username,String password) {
		this.username = username;
		this.password = password;
	}
	public Connection getConnection () throws Exception{
		String JDBC_DRIVER = "org.h2.Driver";
		String DB_URL = "jdbc:h2:/media/leticija/88fe59cb-92ec-4a23-8e33-888bbe6ff16b/BUGY_DB";
		Class.forName(JDBC_DRIVER);
		Connection conn = DriverManager.getConnection(DB_URL);
		return(conn);
	}

	public boolean exists () {
		/*
		String JDBC_DRIVER = "org.h2.Driver";
		String DB_URL = "jdbc:h2:/media/leticija/88fe59cb-92ec-4a23-8e33-888bbe6ff16b/BUGY_DB";
		try {
			Class.forName(JDBC_DRIVER);
		} catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
		*/
		System.out.println("Connecting to user database...");

		try (/*Connection conn = DriverManager.getConnection(DB_URL);*/
		     Connection conn = getConnection ();
		     PreparedStatement createTable = conn.prepareStatement("CREATE TABLE IF NOT EXISTS users (id INTEGER NOT NULL AUTO_INCREMENT, username VARCHAR (20),password VARCHAR(20))")){
			createTable.execute();
			//PreparedStatement insert = conn.prepareStatement("INSERT INTO users (username,password) VALUES (?,?)");
			//insert.setString(1,username);
			//insert.setString(2,password);
			//insert.executeUpdate();

			PreparedStatement count = conn.prepareStatement ("SELECT COUNT (*) FROM users WHERE `username`= ? AND `password`= ? LIMIT 1",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			count.setString (1,username);count.setString(2,password);
			ResultSet res = count.executeQuery();
			PrettyPrinter.printQuery(res);
			res.next();
			numberFromRS = res.getString("count(*)");//kasnije se provjerava, ako je 1, onda se nemre registrirati
			count.close();

			//PreparedStatement all = conn.prepareStatement ("SELECT * FROM users",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			//ResultSet rs = all.executeQuery();
			//System.out.println("printing query:");
			//PrettyPrinter.printQuery(rs);
			//all.close();

		}catch (Exception e) {
			e.printStackTrace();
		}
		if(numberFromRS.equals("0")) {
			//getConnection (Connection conn);
			return(false);
		}
		return(true);
	}





		/*
		try(Scanner scanner = new Scanner (new File("/home/leticija/projects/bugy/resources/users.json"))){
			JSONArray jsarr = new JSONArray();
			JSONObject object = new JSONObject (scanner.useDelimiter("\\A").next());
			jsarr = object.getJSONArray("users");
			for (Object userObject: jsarr){
				if((((JSONObject)userObject).getString("username")).equals(username) & (((JSONObject)userObject).getString("password")).equals(password)){
					return true;
				}
			}

		}catch(IOException e){
			System.out.println("exception");
			e.printStackTrace();
		}
		return false;
	}
	*/
}
