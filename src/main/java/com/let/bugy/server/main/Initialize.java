package com.let.bugy.server.main;

import com.let.bugy.server.user.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Initialize {

    public static void initializeTable() {
        try (Connection conn = Database.getConnection();
             PreparedStatement createTable = conn.prepareStatement("CREATE TABLE IF NOT EXISTS users (id INTEGER NOT NULL AUTO_INCREMENT, username VARCHAR (20),password VARCHAR(20))")){
            createTable.execute();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
