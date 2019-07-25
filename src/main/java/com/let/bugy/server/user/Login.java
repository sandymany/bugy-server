package com.let.bugy.server.user;
import java.util.Map;

public class Login {
	String username;
	String password;
	boolean doesUserExists;
	User user;
	Integer ID;

	Login(Map<String,String> credentialsMap){
		username = credentialsMap.get("username");
		password = credentialsMap.get("password");
		user = new User(username,password);
	}

	public boolean doesExist (){
		User user = new User (username,password);
		doesUserExists = user.exists();
		if (doesUserExists == true) {
			user.setSessionID(username,password);
		}
		return(doesUserExists);
	}

	@Override
	public String toString() {
		return("username: "+username+"\n"+"password: "+password+"\n"+doesUserExists);
	}
}
