package user;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import features.PrettyPrinter;

public class Register {
	
	String username;
	String password;
	User user;

	Register (Map<String,String> credentialsMap){
		username = credentialsMap.get("username");
		password = credentialsMap.get("password");
		user = new User(username,password);
	}

	public boolean doesExist (){
		boolean doesUserExists = user.exists();
		if (doesUserExists == false){
			addUser(username,password);
		}
		return(doesUserExists);
	}
	
	private void addUser (String username, String password) {
		System.out.println("adding user to SQL base...");
		
		try (Connection conn = user.getConnection();
		     PreparedStatement createTable = conn.prepareStatement("CREATE TABLE IF NOT EXISTS users (id INTEGER NOT NULL AUTO_INCREMENT, username VARCHAR (20),password VARCHAR(20))")){
			createTable.execute();
			PreparedStatement insert = conn.prepareStatement("INSERT INTO users (username,password) VALUES (?,?)");
			insert.setString(1,username);
			insert.setString(2,password);
			insert.executeUpdate();

			PreparedStatement all = conn.prepareStatement ("SELECT * FROM users",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = all.executeQuery();
			//System.out.println("printing query:");
			PrettyPrinter.printQuery(rs);
			all.close();

		}catch (Exception e) {
			e.printStackTrace();
		}
		

	}

	@Override
	public String toString() {
		return("username: "+username+"\n"+"password: "+password+"\n"+user.exists());
	}

}
