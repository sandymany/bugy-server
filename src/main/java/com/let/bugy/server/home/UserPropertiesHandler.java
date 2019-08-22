package com.let.bugy.server.home;

import com.let.bugy.server.main.SimpleHttpServer;
import com.let.bugy.server.user.Sessions;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;

import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserPropertiesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange he) {
        try (InputStreamReader reader = new InputStreamReader(he.getRequestBody(), StandardCharsets.UTF_8);
             OutputStream os = he.getResponseBody())
        {
            Map <String,String> parameters;
            Headers requestHeaders = he.getRequestHeaders(); //REQUEST HEADERS

            Map<String,List<String>> mapa = new HashMap<>();

            for (Map.Entry<String, List<String>> header : requestHeaders.entrySet()) {
                mapa.put(header.getKey(), header.getValue());
            }
            System.out.println("HEDERI: ");
            System.out.println(mapa);

            // REQUEST Body
            StringBuilder body = new StringBuilder();
            char[] buffer = new char[100];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                body.append(buffer, 0, read);
            }
            System.out.println("BODY: "+body);
            String response;

            // RESPONSE Body
            if (Sessions.isActive(mapa.get("Sessioncookie").get(0)).equals("active")) {
                System.out.println();
                response = UserProperties.getUserProperties(mapa.get("Sessioncookie").get(0));
                System.out.println("response: "+response);
            }
            else { //ako sessionCookie nije dobar, uvijek responda s "false"
                response = "false";
            }
            int contentLength = response.length();

            //RESPONSE Headers (sending response body length)
            Headers responseHeaders = he.getResponseHeaders();
            he.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLength);
            //write RESPONSE BODY
            os.write(response.getBytes(Charset.forName("UTF-8")));
            he.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        //Database.printTable("users");

    }
}
