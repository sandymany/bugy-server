package user;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Map;
import main.SimpleHttpServer;
public class SignIn implements HttpHandler {
  @Override
  public void handle(HttpExchange httpExchange) throws IOException{
    StringBuilder response = new StringBuilder();
    Map <String,String>params = SimpleHttpServer.queryToStrStrMap(httpExchange.getRequestURI().getQuery());
    Login login = new Login (params);
    System.out.println(login);
    System.out.println();
    response.append(login.doesExist());//responds true if user exists, otherwise-false
    SimpleHttpServer.writeResponse(httpExchange, response.toString());
  }
}
