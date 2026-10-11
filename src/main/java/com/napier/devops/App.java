package com.napier.devops;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        // 1. Load the MySQL JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        // 2. Connect to Database
        try {
            Connection con = DriverManager.getConnection(
                    // Use "localhost:3306" when running locally, or "db:3306" inside Docker container
                    "jdbc:mysql://db:3306/world?useSSL=false&allowPublicKeyRetrieval=true",

                    "root",
                    "D@to0000"
            );
            System.out.println("Successfully connected to the database!");
            con.close();
        } catch (SQLException e) {
            System.out.println("Database Connection Failed:");
            e.printStackTrace();
        }
    }
}