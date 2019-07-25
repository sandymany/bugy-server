package com.let.bugy.server.user;

import com.let.bugy.server.features.PrettyPrinter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    /**
     * @return databaseConnection
     * @throws Exception
     */
    public static Connection getConnection () throws Exception {
        String JDBC_DRIVER = "org.h2.Driver";
        String DB_URL = "jdbc:h2:/media/leticija/88fe59cb-92ec-4a23-8e33-888bbe6ff16b/BUGY_DB";
        Class.forName(JDBC_DRIVER);
        Connection conn = DriverManager.getConnection(DB_URL);
        return(conn);
    }

    /**
     * method that prints some table from database
     * @param tableName
     */
    public static void printTable (String tableName) {
        try (Connection conn = getConnection();
             PreparedStatement getTable = conn.prepareStatement("SELECT * FROM "+tableName, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE)){
            ResultSet rs = getTable.executeQuery();
            System.out.println("tablica "+tableName);
            PrettyPrinter.printQuery(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteTable(String tableName) {
        System.out.println ("DELETING TABLE");
        try (Connection conn = getConnection();
             PreparedStatement delTable = conn.prepareStatement("DROP TABLE "+tableName)) {
            delTable.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
