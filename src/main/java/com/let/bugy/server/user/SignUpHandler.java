package com.let.bugy.server.user;
import com.let.bugy.server.main.SimpleHttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import com.sun.net.httpserver.Headers;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.util.HashMap;
import java.lang.Boolean;
import java.util.Map;


public class SignUpHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange he) {
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
      String response;
      //ako korisnik vec postoji->true,ako je invalid credentials->false,else->cookie
      credentials = SimpleHttpServer.queryToStrStrMap (body.toString());//credentials for Register class
      if (credentials.get("username").replaceAll("\\s","").isEmpty() ||
          credentials.get("password").replaceAll("\\s","").isEmpty() ||
          credentials.get("username").length() > 15 || credentials.get("password").length() > 15 ){
        response = "false";
        System.out.println("RESPONDING: "+response);
      }
      else {
        // RESPONSE Body
        //stvaranje objekta za usera
        Register registration = new Register(credentials);
        response = registration.doesExist();
        System.out.println("RESPONDING: "+response);
      }
      int contentLength = response.length();
      //RESPONSE Headers (sending response body length)
      //Headers responseHeaders = he.getResponseHeaders();
      he.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLength);
      //write RESPONSE BODY
      os.write(response.getBytes(Charset.forName("UTF-8")));
      he.close();
      Sessions.printActiveUsers();
    } catch (Exception e) {
      e.printStackTrace();
    }
    //Database.printTable("users");
  }
}
