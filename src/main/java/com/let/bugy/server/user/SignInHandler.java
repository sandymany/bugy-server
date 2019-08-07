package com.let.bugy.server.user;
import com.let.bugy.server.main.SimpleHttpServer;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SignInHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange he) throws IOException{

    try (InputStreamReader reader = new InputStreamReader(he.getRequestBody(), StandardCharsets.UTF_8);
         OutputStream os = he.getResponseBody())
    {
      Map <String,String> credentials = new HashMap<>();
      Headers requestHeaders = he.getRequestHeaders(); //REQUEST HEADERS
      // REQUEST Body
      StringBuilder body = new StringBuilder();
      char[] buffer = new char[100];
      int read;
      while ((read = reader.read(buffer)) != -1) {
        body.append(buffer, 0, read);
      }
      credentials = SimpleHttpServer.queryToStrStrMap (body.toString());//credentials for Register class
      // RESPONSE Body
      //stvaranje objekta za usera
      Login login = new Login (credentials);
      String response = login.doesExist();
      int contentLength = response.length();
      //RESPONSE Headers (sending response body length)
      Headers responseHeaders = he.getResponseHeaders();
      he.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLength);
      //write RESPONSE BODY
      os.write(response.toString().getBytes(Charset.forName("UTF-8")));
      he.close();
      Sessions.printActiveUsers();
    }catch (Exception e){
      e.printStackTrace();
    }

    /*
    StringBuilder response = new StringBuilder();
    Map <String,String>params = SimpleHttpServer.queryToStrStrMap(httpExchange.getRequestURI().getQuery());
    Login login = new Login (params);
    //System.out.println(login); //printa podatke o useru (overridan toString)
    System.out.println();
    response.append(login.doesExist());//responds true if user exists, otherwise-false
    //TODO if login is successful, user gets his account properties in json
    SimpleHttpServer.writeResponse(httpExchange, response.toString());
    //Database.printTable("users");
    Sessions.printActiveUsers();
     */
  }
}
