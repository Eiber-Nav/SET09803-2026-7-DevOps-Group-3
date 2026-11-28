package com.napier.devops;

import java.sql.*;
import java.util.Scanner;

/**
 * Handles the population reports selected from the main menu.
 * Each report gets information from the world database and
 * displays the results in the console.
 */
public class ReportServiceDev {

    /**
     * Runs the report selected by the user from the menu.
     *
     * @param selection the report selected from the main menu
     * @param scanner   the shared Scanner for user input
     */
    public static void runReport(
            ReportMenu.ReportSelection selection, Scanner scanner) {

        /*
         * Get the database host from the environment.
         * IntelliJ connects through localhost, while the
         * Docker application connects using the db service name.
         */
        String dbHost = System.getenv("DB_HOST");

        if (dbHost == null || dbHost.isBlank()) {
            dbHost = "localhost";
        }

        // Build the database connection details
        String dbUrl = "jdbc:mysql://" + dbHost
                + ":3306/world?useSSL=false&allowPublicKeyRetrieval=true";

        // Database login details
        String dbUser = "root";
        String dbPassword = System.getenv("DB_PASSWORD");

        // Check that the database password has been configured
        if (dbPassword == null || dbPassword.isBlank()) {
            System.out.println("Database password is not configured.");
            System.out.println("Please set DB_PASSWORD in the environment variables.");
            return;
        }

        int reportNumber = selection.getReportNumber();


// Run the report matching the user's selection
        switch (reportNumber) {

            default:
                // Handle any report number that is not supported
                System.out.println("Invalid report number: " + reportNumber);
                break;
        }
    }

}
