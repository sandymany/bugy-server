package getbugs;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange; 
import java.io.IOException;
import main.SimpleHttpServer;

public class GetBugs implements HttpHandler {
  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    StringBuilder response = new StringBuilder();
    String toSearch = httpExchange.getRequestURI().getQuery();
    SearchInsects Search = new SearchInsects (toSearch);
    response.append(Search.search());
    SimpleHttpServer.writeResponse(httpExchange, response.toString());
  }
}
