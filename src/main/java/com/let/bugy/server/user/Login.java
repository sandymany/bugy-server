package com.let.bugy.server.user;
import java.util.Map;

public class Login {
	String username;
	String password;
	String doesUserExists;
	User user;
	Integer ID;

	Login(Map<String,String> credentialsMap){
		username = credentialsMap.get("username");
		password = credentialsMap.get("password");
		user = new User(username,password);
	}

	public String doesExist (){
		User user = new User (username,password);
		doesUserExists = user.exists();
		if (doesUserExists.equals("true")) {
			user.setSessionCookie(user);
			return(user.sessionCookie);
		}
		return(doesUserExists);
	}

	@Override
	public String toString() {
		return("username: "+username+"\n"+"password: "+password+"\n"+doesUserExists);
	}
}
