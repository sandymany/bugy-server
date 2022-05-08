package main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.*;
import java.net.HttpURLConnection;
import java.io.*;
import java.util.Set;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;

import infohandler.InfoHandler;
import getbugs.GetBugs;
import user.*;

public class SimpleHttpServer {

  public static void main(String[] args) throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    server.createContext("/info", new InfoHandler());//instance klasa
    server.createContext("/login", new SignIn());
    server.createContext("/register",new SignUp());
    server.createContext("/getBugs",new GetBugs());
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
