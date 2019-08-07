package com.let.bugy.server.user;
import java.util.*;

public class User{
	String username;
	String password;
	String sessionCookie;
	Date date;
	static Set<String> idSet = new HashSet<>();

	User (String username,String password) {
		this.username = username;
		this.password = password;
	}
	/**
	 * method that returns false if user doesn't yet exist in table, otherwise true.
	 * @return boolean
	 **/

	public String exists () {
		return(Database.exists(username,password));
	}
	public void setSessionCookie (User user) {
		sessionCookie = UUID.randomUUID().toString();
		while (idSet.contains(sessionCookie)==true) {
			sessionCookie = UUID.randomUUID().toString();
		}
		idSet.add(sessionCookie);
		System.out.println("ID za "+username+": "+sessionCookie);
		Sessions.addToActiveUsers (sessionCookie, user); //dok je login/register uspjesan automatski se dodaje na listu aktivnih usera
		//TODO: dok se logira u ovoj metodi mu se automatski posalje njegovo stanje racuna
	}
	/*
	public String toString () {
		return (username+"\t"+password);
	}
	*/





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
