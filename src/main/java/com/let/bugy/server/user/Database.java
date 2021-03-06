package com.let.bugy.server.user;

import com.let.bugy.server.features.PrettyPrinter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    static String numberFromRS;

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

    public static String exists (String username,String password) {
        System.out.println("Connecting to user database, checking if exists...");
        try (Connection conn = getConnection()){
            PreparedStatement count = conn.prepareStatement ("SELECT COUNT (*) FROM users WHERE `username`= ? AND `password`= ? LIMIT 1",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            count.setString (1,username);count.setString(2,password);
            ResultSet res = count.executeQuery();
            res.next();
            //test
            numberFromRS = res.getString("count(*)");//kasnije se provjerava, ako je 1, onda se nemre registrirati
            count.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
        if(numberFromRS.equals("0")) {
            return("false");
        }
        return("true");
    }

    public static void addUser (String username, String password) {
        System.out.println("adding user to SQL base...");
        try (Connection conn = Database.getConnection()){
            //1. ADD USER TO SQL BASE
            PreparedStatement insert = conn.prepareStatement("INSERT INTO users (username,password) VALUES (?,?)");
            insert.setString(1,username);
            insert.setString(2,password);
            insert.executeUpdate();
            insert.close();

            PreparedStatement all = conn.prepareStatement ("SELECT * FROM users where `username`=? and `password`=?",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            all.setString(1,username);
            all.setString(2,password);
            ResultSet rs = all.executeQuery();
            rs.next();
            //System.out.println("number: "+rs.getString("ID"));
            //2. MAKE NEW TABLE SPECIFICALLY FOR NEW USER
            System.out.println("SHOULD ADD: "+rs.getString("ID"));
            makeNewTable(conn,rs.getString("ID"));
            all.close();


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void makeNewTable (Connection conn,String tableName) throws SQLException {
        PreparedStatement newTable = conn.prepareStatement("CREATE TABLE `"+tableName+"` (bug_id INTEGER, description VARCHAR(MAX))");
        newTable.execute();
        newTable.close();
        System.out.println("NEW TABLE:");
        printTable(tableName);
    }

    public static void printTable (String tableName) {
        try (Connection conn = getConnection();
             PreparedStatement getTable = conn.prepareStatement("SELECT * FROM `"+tableName+"`", ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE)){
            ResultSet rs = getTable.executeQuery();
            System.out.println("tablica "+tableName);
            PrettyPrinter.printQuery(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTable(String tableName) {
        System.out.println ("DELETING TABLE");
        try (Connection conn = getConnection()) {
            PreparedStatement delTable = conn.prepareStatement("DROP TABLE `"+tableName+"`");
            delTable.executeUpdate();
            delTable.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteUser (String ID) {

        try (Connection conn = Database.getConnection();
             PreparedStatement deleteUser = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            deleteUser.setString(1,ID);
            deleteUser.executeUpdate();
            deleteTable(ID);
            printTable("users");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String tableToJSON (String tableName) {

        List<String> columnNamesList = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement table = conn.prepareStatement("SELECT * FROM `"+tableName+"`", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            //table.setString(1, tableName);
            ResultSet rs = table.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();

            int numberOfColumns = rsmd.getColumnCount();
            for (int i = 1; i<=numberOfColumns;i++) {
                columnNamesList.add(rsmd.getColumnName(i));
            }

            System.out.println("COLUMNS: "+columnNamesList);
            JSONObject tablicaJSON = new JSONObject();//glavni objekt u kojem bude lista objekti
            JSONArray JSONarray = new JSONArray();
            JSONObject row;
            while (rs.next()) {
                row = new JSONObject();
                for (String column : columnNamesList) {
                    row.put(column, (rs.getString(column)));
                }
                JSONarray.put(row);
            }
            tablicaJSON.put("table", JSONarray);

            return (tablicaJSON.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return (null);
    }
}
