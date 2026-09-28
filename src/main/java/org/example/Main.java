package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        // Use "localhost:3306" when running locally, or "db:3306" inside Docker container
        String url = "jdbc:mysql://localhost:3306/world?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "examplepassword";

        try {
            Connection con = DriverManager.getConnection(url, user, password);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Name, Population FROM country ORDER BY Population DESC LIMIT 3");

            System.out.println("Top 3 Countries by Population:");
            while (rs.next()) {
                System.out.println(rs.getString("Name") + " - " + rs.getInt("Population"));
            }
            con.close();
        } catch (Exception e) {
            System.out.println("Database Connection Failed:");
            e.printStackTrace();
        }
    }
}