package com.let.bugy.server.home;

import com.let.bugy.server.user.Database;
import com.let.bugy.server.user.Sessions;
import com.let.bugy.server.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserProperties {
    static String userProperties;

    public static String getUserProperties (String sessionCookie) {
        String username;
        User user;
        String password;

        user = Sessions.activeUsers.get(sessionCookie);
        username = user.getUsername();
        password = user.getPassword();

        try {
            System.out.println("Getting user's id from database...");
            Connection conn = Database.getConnection();
            PreparedStatement st = conn.prepareStatement ("SELECT * FROM users WHERE `username`=? AND `password`=?", ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            st.setString(1,username);
            st.setString(2,password);
            ResultSet rs = st.executeQuery();
            rs.next();

            System.out.println("GETTING PROPERTIES ON ID: "+rs.getString("ID"));
            userProperties = Database.tableToJSON(rs.getString("ID"));

        }catch (Exception e) {
            e.printStackTrace();
        }

        return (userProperties);
    }
}
