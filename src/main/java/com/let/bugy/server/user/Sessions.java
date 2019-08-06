package com.let.bugy.server.user;

import java.util.*;

public class Sessions {
    static Map<String,User> activeUsers = Collections.synchronizedMap(new LinkedHashMap<>());
    static Thread thread = new Thread(() -> {
        try {
            System.out.println("THREAD HAS STARTED !");
            System.out.println("SLEEPING FOR 5 sec");
            Thread.sleep(5000);
            removeFromActiveUsers();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    });

    public static void addToActiveUsers(String sessionCookie,User user) {
        activeUsers.put(sessionCookie, user);
        if (activeUsers.size()==1) {
            System.out.println("thread should have started.");
            //startThread();
        }
        System.out.println(activeUsers);
    }
    public static void sendToEnd (String sessionCookie) {
        User user = activeUsers.get(sessionCookie);
        activeUsers.remove(sessionCookie);
        activeUsers.put(sessionCookie,user);
        System.out.println();
        System.out.println(activeUsers);
    }
    private static void removeFromActiveUsers () {
        System.out.println("userToRemove: "+activeUsers.entrySet().iterator().next());
        //activeUsers.remove(sessionCookie);
    }
    public static String isActive (String sessionCookie) {
        if (activeUsers.containsKey(sessionCookie)) {
            return ("active");
        }
        return ("inactive");
    }
    public static void startThread() {
        thread.start();
    }

    /*
            new Thread(() -> {
                System.out.println("PRINTING TABLE");
            }).start();
*/
}
