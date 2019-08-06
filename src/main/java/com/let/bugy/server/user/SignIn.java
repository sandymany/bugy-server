package com.let.bugy.server.user;
import com.let.bugy.server.main.SimpleHttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Map;

public class SignIn implements HttpHandler {
  @Override
  public void handle(HttpExchange httpExchange) throws IOException{

    StringBuilder response = new StringBuilder();
    Map <String,String>params = SimpleHttpServer.queryToStrStrMap(httpExchange.getRequestURI().getQuery());
    Login login = new Login (params);
    //System.out.println(login); //printa podatke o useru (overridan toString)
    System.out.println();
    response.append(login.doesExist());//responds true if user exists, otherwise-false
    //TODO if login is successful, user gets his account properties in json
    SimpleHttpServer.writeResponse(httpExchange, response.toString());
    //Database.printTable("users");
  }
}
