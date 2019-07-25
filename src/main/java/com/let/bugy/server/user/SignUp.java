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


public class SignUp implements HttpHandler {
  @Override
  public void handle(HttpExchange he) throws IOException {
    try (InputStreamReader reader = new InputStreamReader(he.getRequestBody(), StandardCharsets.UTF_8);
        OutputStream os = he.getResponseBody())
    {

      Map <String,String> credentials = new HashMap<>();
      //REQUEST HEADERS
      Headers requestHeaders = he.getRequestHeaders();
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
      Register registration = new Register (credentials);
      String response = Boolean.toString(registration.doesExist());
      int contentLength = response.length();
      //RESPONSE Headers (sending response body length)
      Headers responseHeaders = he.getResponseHeaders();
      he.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLength);
      //write RESPONSE BODY
      os.write(response.toString().getBytes(Charset.forName("UTF-8")));
      he.close();
    }catch (Exception e){
      e.printStackTrace();
    }
    Database.printTable("users");
  }
}
