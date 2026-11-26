
package com.napier.devops;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Main class for the World Population Reporting System.
 * Connects to the world database and displays the report menu.
 */
public class App {

    /**
     * Starts the application and handles user selections.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {

        // Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            System.err.println("Could not load the MySQL driver.");
            e.printStackTrace();

            // Return a failure status if the driver cannot be loaded
            System.exit(1);
        }

        /*
         * Use one Scanner throughout the application.
         * The same Scanner is passed to the menu and reports
         * so they all read from the same input stream.
         */
        Scanner inputScanner = new Scanner(System.in);

        /*
         * Get the database host from the environment.
         * Use localhost when running locally and the db
         * service name when running through Docker.
         */
        String dbHost = System.getenv("DB_HOST");

        if (dbHost == null || dbHost.isBlank()) {
            dbHost = "localhost";
        }

        String dbUrl = "jdbc:mysql://" + dbHost
                + ":3306/world?useSSL=false&allowPublicKeyRetrieval=true";

        // Read the password from the environment instead of storing it in the code
        String dbUser = "root";
        String dbPassword = System.getenv("DB_PASSWORD");

        // Check that the database password is configured
        if (dbPassword == null || dbPassword.isBlank()) {
            System.err.println("DB_PASSWORD is not configured.");
            System.exit(1);
        }

        // Connect to the world database
        try (Connection con = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword)) {

            System.out.println("Successfully connected to world db");

            /*
             * Keep showing the menu until the user exits.
             * The same Scanner handles all input requests.
             */
            while (true) {

                ReportMenu.ReportSelection selection =
                        ReportMenu.showReportMenu(inputScanner);

                if (selection == null) {
                    break;
                }

                // Run the selected report using the shared Scanner
                ReportServiceDev.runReport(selection, inputScanner);

                System.out.println("\nPress Enter to return to the menu...");

// Close the application if there is no more input
                if (!inputScanner.hasNextLine()) {
                    break;
                }

                inputScanner.nextLine();
            }

            System.out.println("Application closed.");

        } catch (SQLException e) {
            System.err.println("Database connection failed.");
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();

            // Let GitHub Actions know that the application failed
            System.exit(1);
        }
    }
}
