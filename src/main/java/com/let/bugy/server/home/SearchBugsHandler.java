package com.let.bugy.server.home;

import com.let.bugy.server.getbugs.SearchInsects;
import com.let.bugy.server.user.Sessions;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//endpoint /home/searchBugs
public class SearchBugsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange he) throws IOException {

        try (InputStreamReader reader = new InputStreamReader(he.getRequestBody(), StandardCharsets.UTF_8);
             OutputStream os = he.getResponseBody())
        {
            Map <String,String> parameters;
            Headers requestHeaders = he.getRequestHeaders(); //REQUEST HEADERS

            // MAPA REQUEST HEADERA
            Map<String, List<String>> mapa = new HashMap<>();
            for (Map.Entry<String, List<String>> header : requestHeaders.entrySet()) {
                mapa.put(header.getKey(), header.getValue());
            }
            System.out.println("HEDERI: ");
            System.out.println(mapa);

            // REQUEST Body, BODYJA NEMA U OVOM ENDPOINTU
            /*
            StringBuilder body = new StringBuilder();
            char[] buffer = new char[100];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                body.append(buffer, 0, read);
            }
            System.out.println("BODY: "+body);
            */
            String response;

            // RESPONSE Body
            if (Sessions.isActive(mapa.get("Sessioncookie").get(0)).equals("active")) {
                System.out.println("searching bugs for "+Sessions.activeUsers.get(mapa.get("Sessioncookie").get(0)));
                Sessions.sendToEnd(mapa.get("Sessioncookie").get(0));
                SearchInsects Search = new SearchInsects (mapa.get("Tosearch").get(0));
                response = Search.search();
                System.out.println("SEARCHING...");
            }
            else {
                response = "Invalid session cookie.";
            }
            int contentLength = response.length();

            System.out.println("RESPONDING: "+response);
            //RESPONSE Headers (sending response body length)
            Headers responseHeaders = he.getResponseHeaders();
            he.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLength);
            //write RESPONSE BODY
            os.write(response.getBytes(Charset.forName("UTF-8")));
            he.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        /*
        String searchRequest = httpExchange.getRequestURI().getQuery();
        Map<String,String> parameters = SimpleHttpServer.queryToStrStrMap(searchRequest);
        StringBuilder response = new StringBuilder();
        System.out.println(parameters);
        //if za provjeru session cookia


        if (Sessions.isActive(parameters.get("sessionCookie")).equals("active")) {
            System.out.println("searching bugs for "+Sessions.activeUsers.get(parameters.get("sessionCookie")));
            Sessions.sendToEnd(parameters.get("sessionCookie"));
            SearchInsects Search = new SearchInsects (parameters.get("toSearch"));
            response.append(Search.search());
            SimpleHttpServer.writeResponse(httpExchange, response.toString());
            System.out.println("SEARCHING...");
        }
        else {
            response.append("Invalid session cookie.");
            SimpleHttpServer.writeResponse(httpExchange,response.toString());
        }
         */
    }
}
