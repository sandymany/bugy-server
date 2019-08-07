package com.let.bugy.server.user;

import java.util.*;

public class Sessions {
    public static Map<String,User> activeUsers = Collections.synchronizedMap(new LinkedHashMap<>());
    static Thread thread = new Thread(() -> {
        try {
            System.out.println("THREAD HAS STARTED !");
            Date time = new Date();
            Long diff = time.getTime()-activeUsers.entrySet().iterator().next().getValue().date.getTime();
            //System.out.println(time);
            if (diff >= 0) {
                System.out.println("gonna sleep for: "+(5000-diff));
                Thread.sleep(5000-diff);
                System.out.println("im removing !!");
                removeFromActiveUsers(activeUsers.entrySet().iterator().next().getKey());
                printActiveUsers();
            }
            else {
                System.out.println("immediately removing from active: "+activeUsers.entrySet().iterator().next().getValue().username);
                removeFromActiveUsers(activeUsers.entrySet().iterator().next().getKey());
                printActiveUsers();
            }
            if (!(activeUsers.isEmpty())) {
                System.out.println("restarting myself..");
                restartThread();
            }
            else {
                System.out.println("im shutting down !");
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    });

    public static void addToActiveUsers(String sessionCookie,User user) {
        System.out.println("ADDING USER TO ACTIVE: "+user);
        user.date = new Date();
        //System.out.println(user.date);
        System.out.println("updated date: "+user.date);
        activeUsers.put(sessionCookie, user);
        if (activeUsers.size()==1) {
            System.out.println("thread have started.");
            printActiveUsers();
            //startThread();
        }
        System.out.println(activeUsers);
    }
    public static void sendToEnd (String sessionCookie) {
        if (activeUsers.entrySet().iterator().next().getValue().equals(sessionCookie)) {
            thread.interrupt();
            //startThread();
        }
        User u = activeUsers.remove(sessionCookie);
        u.date = new Date();
        System.out.println("updated date: "+u.date);
        activeUsers.put(sessionCookie,u);
        System.out.println();
        System.out.println(activeUsers);
    }
    public static void removeFromActiveUsers (String sessionCookie) {
        System.out.println("userToRemove: "+sessionCookie);
        activeUsers.remove(sessionCookie);
    }
    public static String isActive (String sessionCookie) {
        if (activeUsers.containsKey(sessionCookie)) {
            return ("active");
        }
        return ("inactive");
    }
    private static void startThread() {
        thread.start();
    }
    private static void restartThread() {
        thread.interrupt();
        thread.start();
    }

    public static void printActiveUsers() {
        System.out.println("# ACTIVE USERS #");
        for(String key: activeUsers.keySet()) {
            System.out.println((activeUsers.get(key)).username);
        }
    }

    /*
            new Thread(() -> {
                System.out.println("PRINTING TABLE");
            }).start();
*/
}
