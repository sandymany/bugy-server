package com.let.bugy.server.home;

import com.let.bugy.server.main.SimpleHttpServer;
import com.let.bugy.server.user.Sessions;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;

import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.util.Map;


public class UserPropertiesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange he) {
        try (InputStreamReader reader = new InputStreamReader(he.getRequestBody(), StandardCharsets.UTF_8);
             OutputStream os = he.getResponseBody())
        {
            Map <String,String> parameters;
            //Headers requestHeaders = he.getRequestHeaders(); //REQUEST HEADERS
            // REQUEST Body
            StringBuilder body = new StringBuilder();
            char[] buffer = new char[100];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                body.append(buffer, 0, read);
            }
            String response;
            parameters = SimpleHttpServer.queryToStrStrMap (body.toString());
            System.out.println("PROVJERAVAM SESSION COOKIE"+parameters);
            // RESPONSE Body
            if (Sessions.isActive(parameters.get("sessionCookie")).equals("active")) {
                System.out.println();
                response = UserProperties.getUserProperties(parameters.get("sessionCookie"));
                System.out.println("response: "+response);
            }
            else {
                response = "false cookie";
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
