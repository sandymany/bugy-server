package com.let.bugy.server.user;

import java.util.Map;

public class Register {
	String username;
	String password;
	User user;

	Register (Map<String,String> credentialsMap){
		username = credentialsMap.get("username");
		password = credentialsMap.get("password");
		user = new User(username,password);
	}

	public String doesExist (){
		String doesUserExists = user.exists();
		if (doesUserExists.equals("false")){
			addUser();
			user.setSessionCookie(user);
			return (user.sessionCookie);
		}
		return(doesUserExists);
	}

	/**
	 * method that adds user to users base
	 */
	private void addUser () {
		Database.addUser(user.username,user.password);
	}

	@Override
	public String toString() {
		return("username: "+username+"\n"+"password: "+password+"\n"+user.exists());
	}

}
