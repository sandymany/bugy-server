package features;
import main.SimpleHttpServer;

public class UserData {
	String IPAddress;

	UserData (HttpExchange he){
		IPAddress = he.getRemoteAddress().getAddress();
	}
}
