package com.napier.devops;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;





public class ReportServiceDev {
    private static final String dbUrl = "jdbc:mysql://localhost:3306/world?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String dbUser = "root";
    private static final String dbPassword = "D@to0000";

    public static void runReport(ReportMenu.ReportSelection selection) {
        int reportNumber = selection.getReportNumber();
        switch (reportNumber) {
            case 1:
                AllCounPop(reportNumber);
                break;
                default:
                    reportNumber = selection.getReportNumber();
                    System.out.println("\n----------------------------------------");
                    System.out.println("Report " + reportNumber + " is still in development.");
                    System.out.println("----------------------------------------\n");

        }

    }

        public static void AllCounPop( int reportNumber){
            System.out.println("\n----------------------------------------");
            System.out.println("Report " + reportNumber + " Running Report Loading Report 1.");
            System.out.println("----------------------------------------\n");
            String sqlQuery = "SELECT Name, Population, continent FROM country ORDER BY Population ;";

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlQuery)) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                // Print formatted headers (e.g., %-25s means left-align in a space of 25 characters)
                System.out.printf("%-45s %-15s%n", metaData.getColumnName(1), metaData.getColumnName(2));
                System.out.println("---------------------------------------------------------------");

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
    }