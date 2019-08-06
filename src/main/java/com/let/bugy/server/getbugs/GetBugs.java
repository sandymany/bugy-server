package com.let.bugy.server.getbugs;
import com.let.bugy.server.main.SimpleHttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange; 
import java.io.IOException;

public class GetBugs implements HttpHandler {
  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    String toSearch = httpExchange.getRequestURI().getQuery();
    SearchInsects Search = new SearchInsects (toSearch);
    StringBuilder response = new StringBuilder();
    response.append(Search.search());
    SimpleHttpServer.writeResponse(httpExchange, response.toString());
  }
}
