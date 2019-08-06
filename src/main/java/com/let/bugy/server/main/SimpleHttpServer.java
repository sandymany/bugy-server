package com.let.bugy.server.main;

import com.let.bugy.server.getbugs.GetBugs;
import com.let.bugy.server.home.SearchBugsHandler;
import com.let.bugy.server.infohandler.InfoHandler;
import com.let.bugy.server.user.Database;
import com.let.bugy.server.user.SignIn;
import com.let.bugy.server.user.SignUpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;


public class SimpleHttpServer {

  public static void main(String[] args) throws Exception {

    Initialize.initializeTable();
    Database.printTable("users");
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    server.createContext("/info", new InfoHandler());//instance klasa
    server.createContext("/login", new SignIn());
    server.createContext("/register",new SignUpHandler());
    server.createContext("/getBugs",new GetBugs());
    server.createContext("/home/searchBugs",new SearchBugsHandler());
    server.setExecutor(null); // creates a default executor
    server.start();
    System.out.println("The server is running");
  }

  // http://localhost:8000/info

  public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
    httpExchange.sendResponseHeaders(200, response.length());
    OutputStream os = httpExchange.getResponseBody();
    os.write(response.getBytes());
    os.close();
  }
  /**
   * returns the url parameters in a map
   * @param query
   * @return map
   */
  public static Map<String, Integer> queryToStrIntMap(String query){
    Map<String, Integer> result = new HashMap<String, Integer>();
    for (String param : query.split("&")) {
        String pair[] = param.split("=");
        if (pair.length>1) {
            result.put(pair[0], Integer.parseInt(pair[1]));
        }else{
            result.put(pair[0],Integer.parseInt(""));
        }
    }
    return result;
  }

  public static Map<String,String> queryToStrStrMap (String query) {
    Map<String,String> result = new HashMap<String,String> ();
    for (String param : query.split("&")) {
      String pair [] = param.split("=");
      if (pair.length>1) {
        result.put (pair[0], pair[1]);
      }
      else{
        result.put (pair[0],"");
      }
    }
    return result;
  }

  public static ArrayList<String> queryToList(String query) {
    ArrayList<String> list = new ArrayList<> (Arrays.asList(query.split("&")));
    return(list);
  }
}
