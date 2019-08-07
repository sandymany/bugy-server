package com.let.bugy.server.home;

import com.let.bugy.server.getbugs.SearchInsects;
import com.let.bugy.server.main.SimpleHttpServer;
import com.let.bugy.server.user.Sessions;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;

//endpoint /home/searchBugs
public class SearchBugsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
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
        }
        else {
            response.append("Invalid session cookie.");
            SimpleHttpServer.writeResponse(httpExchange,response.toString());
        }
    }
}
