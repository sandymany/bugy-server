package user;
import java.util.HashMap;
import java.util.Map;

// git test
public class Login {
	String username;
	String password;
	boolean doesUserExists;
	User user;

	Login(Map<String,String> credentialsMap){
		username = credentialsMap.get("username");
		password = credentialsMap.get("password");
		user = new User(username,password);
	}

	public boolean doesExist (){
		User user = new User (username,password);
		doesUserExists = user.exists();
		return(doesUserExists);
	}

	@Override
	public String toString() {
		return("username: "+username+"\n"+"password: "+password+"\n"+doesUserExists);
	}
}
