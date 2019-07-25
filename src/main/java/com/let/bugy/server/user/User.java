package com.let.bugy.server.user;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class User{
	String username;
	String password;
	String numberFromRS;
	Integer sessionID;
	static Set<Integer> idSet = new HashSet<>();

	User (String username,String password) {
		this.username = username;
		this.password = password;
	}
	/**
	 * method that returns false if user doesn't yet exist in table, otherwise true.
	 * @return boolean
	 **/
	public boolean exists () {
		System.out.println("Connecting to user database, checking if exists...");

		try (Connection conn = Database.getConnection()){
			PreparedStatement count = conn.prepareStatement ("SELECT COUNT (*) FROM users WHERE `username`= ? AND `password`= ? LIMIT 1",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			count.setString (1,username);count.setString(2,password);
			ResultSet res = count.executeQuery();
			res.next();
			//test
			numberFromRS = res.getString("count(*)");//kasnije se provjerava, ako je 1, onda se nemre registrirati
			count.close();

		}catch (Exception e) {
			e.printStackTrace();
		}
		if(numberFromRS.equals("0")) {
			return(false);
		}
		return(true);
	}
	public void setSessionID (String username,String password) {
		Random random = new Random();
		sessionID = random.nextInt(1000);
		while (idSet.contains(sessionID)==true) {
			sessionID = random.nextInt(100);
		}
		idSet.add(sessionID);
		System.out.println("ID za "+username+": "+sessionID);
		Session.addToActiveUsers(username,password,sessionID); //dok je login/register uspjesan automatski se dodaje na listu aktivnih usera
		//System.out.println ("Successfully logged, your ID is: "+ID);
		//System.out.println(userSession); //printa podatke o useru koji se ulogiral
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
