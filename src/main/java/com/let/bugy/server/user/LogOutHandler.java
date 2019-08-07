package com.let.bugy.server.user;
import com.let.bugy.server.main.SimpleHttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class LogOutHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String searchRequest = httpExchange.getRequestURI().getQuery();
        Map<String,String> parameters = SimpleHttpServer.queryToStrStrMap(searchRequest);
        Sessions.removeFromActiveUsers(parameters.get("sessionCookie"));
        SimpleHttpServer.writeResponse(httpExchange,"loggedOut");

        Sessions.printActiveUsers();
    }
}
