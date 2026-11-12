
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
     * Loads the countries from the database and lets the user
     * choose one from the list.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     * @return the selected country code, or null if cancelled
     * @throws SQLException if the database query fails
     */
    public static String selectCountry(String dbUrl, String dbUser,
                                       String dbPassword, Scanner scanner)
            throws SQLException {

        List<String> countryNames = new ArrayList<>();
        List<String> countryCodes = new ArrayList<>();

        /*
         * Keep the country names and codes in matching lists.
         * The user sees the country name, but the reports use
         * the country code when filtering the results.
         */
        String sqlQuery = "SELECT Code, Name FROM country ORDER BY Name";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                countryCodes.add(rs.getString("Code"));
                countryNames.add(rs.getString("Name"));
            }
        }

        // Show the country names using the shared menu method
        String selectedName = selectOption(
                "country", countryNames, scanner);

        if (selectedName == null) {
            return null;
        }

        /*
         * Find the selected country's position in the names list.
         * Use the same position to get its country code.
         */
        int selectedIndex = countryNames.indexOf(selectedName);

        return countryCodes.get(selectedIndex);
    }

    public static String selectCities(String dbUrl, String dbUser,
                                        String dbPassword, Scanner scanner)
            throws SQLException {

        /*
         * Retrieve the district names from the city table.
         * DISTINCT prevents duplicate district names from
         * appearing in the selection menu.
         */
        String sqlQuery = "SELECT DISTINCT Name FROM city " +
                "WHERE Name IS NOT NULL AND Name <> '' " +
                "ORDER BY Name";

        return selectOption(dbUrl, dbUser, dbPassword,
                scanner, sqlQuery, "Name");
    }



    /**
     * Displays the districts available in the database.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     * @return the selected district, or null if cancelled
     * @throws SQLException if the database query fails
     */
    public static String selectDistrict(String dbUrl, String dbUser,
                                        String dbPassword, Scanner scanner)
            throws SQLException {

        /*
         * Retrieve the district names from the city table.
         * DISTINCT prevents duplicate district names from
         * appearing in the selection menu.
         */
        String sqlQuery = "SELECT DISTINCT District FROM city " +
                "WHERE District IS NOT NULL AND District <> '' " +
                "ORDER BY District";

        return selectOption(dbUrl, dbUser, dbPassword,
                scanner, sqlQuery, "district");
    }


    /**
     * Displays a numbered list of options and allows the user
     * to select one. This method handles the menu and input.
     *
     * @param optionName the type of option being selected
     * @param options the list of available options
     * @param scanner the shared Scanner for user input
     * @return the selected option, or null if cancelled
     */
    private static String selectOption(String optionName,
                                       List<String> options,
                                       Scanner scanner) {

        // Check whether there are any options to display
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
         * Reading input as text prevents letters or other
         * invalid characters from crashing the menu.
         */
        while (true) {

            System.out.print("\nSelect a " + optionName +
                    " (0-" + options.size() + "): ");

            String input = scanner.nextLine().trim();

            try {
                int selectedNumber = Integer.parseInt(input);

                // Allow the user to cancel the selection
                if (selectedNumber == 0) {
                    System.out.println("Selection cancelled.");
                    return null;
                }

                // Check that the selected number exists in the list
                if (selectedNumber >= 1 &&
                        selectedNumber <= options.size()) {

                    String selectedValue = options.get(selectedNumber - 1);

                    System.out.println("Selected " + optionName +
                            ": " + selectedValue + "\n");

                    return selectedValue;
                }

            } catch (NumberFormatException e) {
                // Invalid input is handled by the message below
            }

            System.out.println("Invalid selection. Please enter a number from 0 to "
                    + options.size() + ".");
        }
    }

    /**
     * Retrieves filter options from the database and passes
     * them to the shared selection menu.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
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
         * Retrieve the available filter values from MySQL.
         * The queries used by the reports already remove
         * duplicates and sort the results.
         */
        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String value = rs.getString(1);

                // Skip empty or missing values
                if (value != null && !value.isBlank()) {
                    options.add(value);
                }
            }
        }

        // Reuse the same menu for continents and regions
        return selectOption(optionName, options, scanner);
    }
}
