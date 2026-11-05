
package com.napier.devops;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Retrieves available filter options from the world database.
 * This class can be reused by different population reports.
 */
public class DatabaseOptions {

    /**
     * Displays the continents available in the database.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the scanner used for user input
     * @return the selected continent, or null if cancelled
     * @throws SQLException if the database query fails
     */
    public static String selectContinent(String dbUrl, String dbUser,
                                         String dbPassword, Scanner scanner)
            throws SQLException {

        String sqlQuery = "SELECT DISTINCT Continent FROM country " +
                "ORDER BY Continent";

        return selectOption(dbUrl, dbUser, dbPassword,
                scanner, sqlQuery, "continent");
    }

    /**
     * Displays the regions available in the database.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the scanner used for user input
     * @return the selected region, or null if cancelled
     * @throws SQLException if the database query fails
     */
    public static String selectRegion(String dbUrl, String dbUser,
                                      String dbPassword, Scanner scanner)
            throws SQLException {

        String sqlQuery = "SELECT DISTINCT Region FROM country " +
                "ORDER BY Region";

        return selectOption(dbUrl, dbUser, dbPassword,
                scanner, sqlQuery, "region");
    }

    /**
     * Retrieves options from the database and allows the user
     * to select one using its corresponding number.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the scanner used for user input
     * @param sqlQuery the SQL query used to retrieve options
     * @param optionName the name of the filter being selected
     * @return the selected option, or null if cancelled
     * @throws SQLException if the database query fails
     */
    private static String selectOption(String dbUrl, String dbUser,
                                       String dbPassword, Scanner scanner,
                                       String sqlQuery, String optionName)
            throws SQLException {

        List<String> options = new ArrayList<>();

        /*
         * Retrieve the available values from the database.
         * DISTINCT prevents duplicate entries from appearing
         * in the selection menu.
         */
        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String value = rs.getString(1);

                if (value != null && !value.isBlank()) {
                    options.add(value);
                }
            }
        }

        // Check whether the database returned any options
        if (options.isEmpty()) {
            System.out.println("No " + optionName + " options were found.");
            return null;
        }

        System.out.println("\nAvailable " + optionName + " options:\n");

        // Display each option with its corresponding number
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }

        System.out.println("0. Cancel selection");

        /*
         * Keep asking until the user enters a valid number.
         * Reading input as text prevents invalid characters
         * from causing an input mismatch exception.
         */
        while (true) {
            System.out.print("\nSelect a " + optionName +
                    " (0-" + options.size() + "): ");

            String input = scanner.nextLine().trim();

            try {
                int selectedNumber = Integer.parseInt(input);

                if (selectedNumber == 0) {
                    System.out.println("Selection cancelled.");
                    return null;
                }

                if (selectedNumber >= 1 &&
                        selectedNumber <= options.size()) {

                    String selectedValue = options.get(selectedNumber - 1);

                    System.out.println("Selected " + optionName +
                            ": " + selectedValue + "\n");

                    return selectedValue;
                }

            } catch (NumberFormatException e) {
                // The user entered something other than a whole number
            }

            System.out.println("Invalid selection. Please enter a number from 0 to "
                    + options.size() + ".");
        }
    }
}
