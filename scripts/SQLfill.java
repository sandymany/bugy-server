import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException; 
import java.sql.Statement;  
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.sql.PreparedStatement;

import java.io.*;
import java.util.*;
import org.json.*;

//UKUPNI STUPCI: ID,PHYLUM,SPECIES,GENUS,SUBFAMILY,SUBPHYLUM,NAME,FAMILY,CLASS,URL,ORDER,SUBORDER,INFRAORDER,SUPERFAMILY,SUBSPECIES,NO TAXON,TRIBE,SUBTRIBE,SUPERTRIBE

public class SQLfill { 
   //JDBC driver name and database URL 
   static final String JDBC_DRIVER = "org.h2.Driver";
   static final String DB_URL = "jdbc:h2:/media/leticija/88fe59cb-92ec-4a23-8e33-888bbe6ff16b/BUGY_DB";
   static Scanner s=new Scanner(System.in);
   static ArrayList<String> metaArray = new ArrayList();

   public static void main(String[] args) {
      JSONArray jsarr = null;
      try(Scanner scanner = new Scanner(new File("/home/leticija/projects/bugy/resources/insectdatabase.json")))
      {
         JSONObject object = new JSONObject(scanner.useDelimiter("\\A").next());
         jsarr = object.getJSONArray("database");
      } 
      catch (IOException ex ) {
         ex.printStackTrace();
      } 
      //String USER = s.nextLine();
      //String PASS = s.nextLine();
      // STEP 1: Register JDBC driver
      try {
         Class.forName(JDBC_DRIVER);
      } catch(ClassNotFoundException ex ){
         ex.printStackTrace();
      }

      System.out.println("Connecting to database...");
      //STEP 2: Open a connection
      try (Connection conn = DriverManager.getConnection(DB_URL)){
         PreparedStatement create_table = conn.prepareStatement("CREATE TABLE IF NOT EXISTS insecta (id INTEGER NOT NULL AUTO_INCREMENT, name VARCHAR(100))");
         create_table.execute();
         create_table.close();
         PreparedStatement all = conn.prepareStatement("SELECT* FROM insecta",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
         
         int duplicates;
         String taxes;
         ResultSet rs;
         int n= 0;
         String q_marks;
         ArrayList<String> names = new ArrayList();
         long startTime = System.currentTimeMillis();
         for(Object e : jsarr){
            rs = all.executeQuery();
            taxes = "";
            q_marks ="";
            names.clear();
            n++;
            duplicates = 0;
            boolean flag = false;
            for(Object key : ((JSONObject) e).keySet()){
               if (((String)key).toLowerCase().equals("name")){
                  PreparedStatement count = conn.prepareStatement("SELECT COUNT(*) FROM insecta WHERE `name`=?",ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                  count.setString(1,((JSONObject)e).getString((String)key));
                  ResultSet duplicateNumber = count.executeQuery();
                  duplicateNumber.next();
                  duplicates = duplicateNumber.getInt(1);
               }
               else if(duplicates == 1){
                  //System.out.println("vec ima: "+(((JSONObject)e).getString((String)key)));
                  flag = true;
                  break;
               }
               if(!(hasColumn(rs,((String)key)))){
                  PreparedStatement add_columns = conn.prepareStatement("ALTER TABLE insecta ADD `"+((String)key)+"` TEXT");
                  add_columns.executeUpdate();
                  add_columns.close();
               }
               taxes += "`"+((String)key)+"`,";
               q_marks += "?,";
               names.add(((JSONObject)e).getString((String)key));
            }
            if(flag) continue;
            taxes = taxes.substring(0,taxes.length()-1);
            q_marks = q_marks.substring(0,q_marks.length()-1);
            
            PreparedStatement set_values = conn.prepareStatement ("INSERT INTO insecta ("+taxes+") VALUES ("+q_marks+")");
            for(int i=0;i<names.size();i++){
               set_values.setString((i+1),(names.get(i)));
            }
            set_values.execute();
            set_values.close();
            System.out.print("\r"+n);
         }
         System.out.println("done.");
         System.out.println("time needed: "+(System.currentTimeMillis()-startTime)+" milli seconds.");
         rs = all.executeQuery();
         //printQuery(rs);
         //System.out.println(getMetaDataArray(rs));
         all.close();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static List<String> getMetaDataArray(ResultSet rs) throws SQLException{
      List<String> metaArray = new ArrayList<> ();
      ResultSetMetaData rsmd = rs.getMetaData();
      for (int i=0; i<rsmd.getColumnCount();i++){
         metaArray.add(rsmd.getColumnName(i+1));
      }
      return metaArray;
   }

   public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
      ResultSetMetaData rsmd = rs.getMetaData();
      int columns = rsmd.getColumnCount();
      for (int x = 1; x <= columns; x++) {
         if (columnName.toLowerCase().equals(rsmd.getColumnName(x).toLowerCase())) {
            return true;
         }
      }
      return false;
   }

   private static void printQuery(ResultSet rs) throws SQLException {
      ResultSetMetaData rsmd = rs.getMetaData();
      ArrayList<ArrayList<String>> table = new ArrayList<>();
      int columnsNumber = rsmd.getColumnCount();
      table.add(new ArrayList<>());
      for (int i = 0; i < columnsNumber; i++) {
         table.get(0).add(rsmd.getColumnName(i+1));
      }
      int j = 1;
      boolean shouldPrint = false;
      while (rs.next()) {
         table.add(new ArrayList<>());
         for (int i = 0; i < columnsNumber; i++) {
             String cell = rs.getString(i+1);
             if(cell != null)
                  cell = (cell.length()>30)?cell.substring(0,27)+"...":cell;
             table.get(j).add(cell);
         }
         j++;
         shouldPrint = true;
      }
      PrettyPrinter pp = new PrettyPrinter(System.out);
      String[][] array = new String[table.size()][];
      for (int i = 0; i < table.size(); i++) {
              ArrayList<String> row = table.get(i);
              array[i] = row.toArray(new String[row.size()]);
      }
      if(shouldPrint){
              pp.print(array);
              System.out.println();
      }
      rs.beforeFirst();
   }
}
