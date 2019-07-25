package com.let.bugy.server.user;

import java.util.*;

public class Session {
    static Map<Integer,ArrayList<String>> activeUsers = Collections.synchronizedMap(new LinkedHashMap<Integer,ArrayList<String>>());
    /*
    Session (String username, String password,Integer sessionID) {
        this.sessionID = sessionID;
        this.username = username;
        this.password = password;
        addToActiveUsers();
    }
    */
    public static void addToActiveUsers(String username, String password, Integer sessionID) {
        List<String> korisnik = new ArrayList<>();
        Collections.addAll(korisnik, username,password);
        activeUsers.put(sessionID, (ArrayList<String>) korisnik);
        System.out.println(activeUsers);
    }
    private void removeFromActiveUsers () {
        //activeUsers.remove();
    }

    /*
            new Thread(() -> {
                System.out.println("PRINTING TABLE");
            }).start();
*/
}
