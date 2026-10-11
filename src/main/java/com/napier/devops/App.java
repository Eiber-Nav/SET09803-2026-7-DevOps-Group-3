package com.napier.devops;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {

// Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load the MySQL driver.");
            e.printStackTrace();
            return;
        }
        Scanner inputScanner = new Scanner(System.in);

// Try to connect to the world database
        try (
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/world?useSSL=false&allowPublicKeyRetrieval=true",
                        "root",
                        "D@to0000"
                )
        ) {
// Show clearly that the database connection worked
            System.out.println("Database Connecting....\n");
            System.out.println("Successfully connected to world db");

// Keep showing the report menu until the user exits (0)
            while (true) {
                ReportMenu.ReportSelection selection = ReportMenu.showReportMenu();
                if (selection == null) {
                    break;
                }

// Runs the report service (displays "still in development")
                ReportService.runReport(selection);

// Pause so the user can read the message before the menu pops up again
                System.out.println("Press Enter to return to the menu...");
                inputScanner.nextLine();
            }
            System.out.println("Application closed.");
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
        }}}