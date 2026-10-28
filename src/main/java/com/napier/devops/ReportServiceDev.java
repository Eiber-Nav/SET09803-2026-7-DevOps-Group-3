package com.napier.devops;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class ReportServiceDev {

    public static void runReport(ReportMenu.ReportSelection selection) {
        String dbUrl = "jdbc:mysql://localhost:3306/world?useSSL=false&allowPublicKeyRetrieval=true";
        String dbUser = "root";       // Change to your database user
        String dbPassword = "D@to0000"; // Change to your database password
        int reportNumber = selection.getReportNumber();
        if (reportNumber == 1) {
            System.out.println("\n----------------------------------------");
            System.out.println("Report " + reportNumber + " Running Report Loading Report 1.");
            System.out.println("----------------------------------------\n");

            // Database connection configuration for Docker / Local MySQL
            // (Assuming your database name is 'world', user is 'root' or similar, check your docker-compose.yml)
            //String dbUrl = "jdbc:mysql://localhost:3306/world?useSSL=false&allowPublicKeyRetrieval=true";
            //String dbUser = "root";       // Change to your database user
            //String dbPassword = "D@to0000"; // Change to your database password

            // SQL query to test the 'world' database (e.g., getting top 5 countries by population)
            String sqlQuery = "SELECT Name, Population FROM country ORDER BY Population ;";

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlQuery)) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                // Print formatted headers (e.g., %-25s means left-align in a space of 25 characters)
                System.out.printf("%-45s %-15s%n", metaData.getColumnName(1), metaData.getColumnName(2));
                System.out.println("------------------------------------------------------------");

                // Print rows with aligned columns
                int rowCount = 0;
                while (rs.next()) {
                    String name = rs.getString(1);
                    String population = rs.getString(2);

                    // %-25s formats the name column, %-15s formats the population column
                    System.out.printf("%-50s %-15s%n", name, population);
                    rowCount++;
                }


                System.out.println("\nQuery executed successfully against 'world' database. Total rows: " + rowCount);

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            }
        }

            reportNumber = selection.getReportNumber();
            System.out.println("\n----------------------------------------");
            System.out.println("Report " + reportNumber + " is still in development.");
            System.out.println("----------------------------------------\n");

    }

    // Required helper method if you still want to read custom .sql query text files from disk
    public static String readSqlFile(String filePath) throws IOException {
        return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
    }
}