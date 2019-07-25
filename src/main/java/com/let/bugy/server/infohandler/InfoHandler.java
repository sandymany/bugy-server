package com.let.bugy.server.infohandler;
import com.let.bugy.server.main.SimpleHttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

// http://localhost:8000/info
public class InfoHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    String response = "<title>BUGY</title><h1>Welcome to BUGY!</h1></br><body>Use /getBugs?(your_search) to search bugs from bugbase</body>";
    SimpleHttpServer.writeResponse(httpExchange, response);
  }
}
