
package com.napier.devops;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * @param scanner the shared Scanner for user input
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

            case 1:
                report1(dbUrl, dbUser, dbPassword);
                break;

            case 2:
                report2(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 3:
                report3(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 4:
                report4(dbUrl, dbUser, dbPassword, scanner);
                break;

            default:
                System.out.println("\n----------------------------------------");
                System.out.println("Report " + reportNumber + " is still in development.");
                System.out.println("----------------------------------------\n");
                break;
        }
    }

    /**
     * Report 1 - Displays all countries in the world.
     * Countries are sorted from largest population to smallest.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     */
    public static void report1(String dbUrl, String dbUser, String dbPassword) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 1 - All Countries in the World");
        System.out.println("----------------------------------------\n");

        /*
         * Retrieve the required country information.
         * ORDER BY Population DESC places countries with
         * the largest population at the top of the report.
         */
        String sqlQuery = "SELECT Code, Name, Continent, Region, " +
                "Population, Capital FROM country ORDER BY Population DESC;";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            // Print the column headings
            System.out.printf("%-8s %-45s %-18s %-28s %-15s %-10s%n",
                    "Code", "Name", "Continent", "Region",
                    "Population", "Capital");

            System.out.println("------------------------------------------------" +
                    "------------------------------------------------" +
                    "------------------------------");

            int rowCount = 0;

            // Display each country returned from the database
            while (rs.next()) {

                String code = rs.getString("Code");
                String name = rs.getString("Name");
                String continent = rs.getString("Continent");
                String region = rs.getString("Region");
                long population = rs.getLong("Population");
                int capital = rs.getInt("Capital");

                // Some countries may not have a capital recorded
                String capitalValue = rs.wasNull()
                        ? "N/A" : String.valueOf(capital);

                // Display the results using aligned columns
                System.out.printf("%-8s %-45s %-18s %-28s %-15d %-10s%n",
                        code, name, continent, region,
                        population, capitalValue);

                rowCount++;
            }

            System.out.println("\nQuery executed successfully against 'world' database.");
            System.out.println("Total countries found: " + rowCount);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Report 2 - Displays countries in a selected continent.
     * Countries are sorted from largest population to smallest.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report2(String dbUrl, String dbUser,
                               String dbPassword, Scanner scanner) {


        System.out.println("\n----------------------------------------");
        System.out.println("Report 2 - Countries in a Continent");
        System.out.println("----------------------------------------\n");


// Retrieve the available continents directly from the database
        String continent;

        try {
            continent = DatabaseOptions.selectContinent(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load continents: " + e.getMessage());
            return;
        }

// Return to the menu if the selection was cancelled
        if (continent == null) {
            return;
        }


        /*
         * Retrieve countries belonging to the selected continent.
         * A PreparedStatement is used to safely include the
         * continent entered by the user in the SQL query.
         */
        String sqlQuery = "SELECT Code, Name, Continent, Region, " +
                "Population, Capital FROM country " +
                "WHERE Continent = ? ORDER BY Population DESC;";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Replace the question mark with the selected continent
            stmt.setString(1, continent);

            try (ResultSet rs = stmt.executeQuery()) {

                // Print the column headings
                System.out.printf("%-8s %-45s %-18s %-28s %-15s %-10s%n",
                        "Code", "Name", "Continent", "Region",
                        "Population", "Capital");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "------------------------------");

                int rowCount = 0;

                // Display the countries returned from the query
                while (rs.next()) {

                    String code = rs.getString("Code");
                    String name = rs.getString("Name");
                    String countryContinent = rs.getString("Continent");
                    String region = rs.getString("Region");
                    long population = rs.getLong("Population");
                    int capital = rs.getInt("Capital");

                    // Show N/A if no capital is recorded
                    String capitalValue = rs.wasNull()
                            ? "N/A" : String.valueOf(capital);

                    // Print the country information in aligned columns
                    System.out.printf("%-8s %-45s %-18s %-28s %-15d %-10s%n",
                            code, name, countryContinent, region,
                            population, capitalValue);

                    rowCount++;
                }

                // Display a message if no countries were found
                if (rowCount == 0) {
                    System.out.println("No countries found for: " + continent);
                }

                System.out.println("\nTotal countries found: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Report 3 - Displays all countries in a selected region.
     * Countries are sorted from largest population to smallest.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report3(String dbUrl, String dbUser,
                               String dbPassword, Scanner scanner) {


        System.out.println("\n----------------------------------------");
        System.out.println("Report 3 - Countries in a Region");
        System.out.println("----------------------------------------\n");


// Retrieve the available regions directly from the database
        String region;

        try {
            region = DatabaseOptions.selectRegion(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load regions: " + e.getMessage());
            return;
        }

// Return to the menu if the selection was cancelled
        if (region == null) {
            return;
        }


        /*
         * Retrieve countries belonging to the selected region.
         * The PreparedStatement safely passes the region into
         * the query, and DESC sorts by largest population first.
         */
        String sqlQuery = "SELECT Code, Name, Continent, Region, " +
                "Population, Capital FROM country " +
                "WHERE Region = ? ORDER BY Population DESC;";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the region entered by the user
            stmt.setString(1, region);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the column headings
                System.out.printf("%-8s %-45s %-18s %-28s %-15s %-10s%n",
                        "Code", "Name", "Continent", "Region",
                        "Population", "Capital");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "------------------------------");

                int rowCount = 0;

                // Display each country returned by the query
                while (rs.next()) {

                    String code = rs.getString("Code");
                    String name = rs.getString("Name");
                    String continent = rs.getString("Continent");
                    String countryRegion = rs.getString("Region");
                    long population = rs.getLong("Population");
                    int capital = rs.getInt("Capital");

                    // Display N/A if the capital is not recorded
                    String capitalValue = rs.wasNull()
                            ? "N/A" : String.valueOf(capital);

                    // Print each country's information in aligned columns
                    System.out.printf("%-8s %-45s %-18s %-28s %-15d %-10s%n",
                            code, name, continent, countryRegion,
                            population, capitalValue);

                    rowCount++;
                }

                // Check if any countries were found
                if (rowCount == 0) {
                    System.out.println("No countries found for region: " + region);
                }

                System.out.println("\nTotal countries found: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 4 - Displays the top N populated countries in the world.
     * The user enters how many countries they want to display.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report4(String dbUrl, String dbUser,
                               String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 4 - Top N Populated Countries in the World");
        System.out.println("----------------------------------------\n");

        int numberOfCountries;

        /*
         * Ask the user how many countries to display.
         * Keep asking until a valid positive number is entered.
         */
        while (true) {

            System.out.print("Enter the number of countries to display (0 to cancel): ");

            String input = scanner.nextLine().trim();

            try {
                numberOfCountries = Integer.parseInt(input);

                // Return to the menu if the user enters 0
                if (numberOfCountries == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                // Only positive numbers are accepted
                if (numberOfCountries > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Retrieve countries from the world database.
         * ORDER BY Population DESC sorts the largest populations first.
         * LIMIT ? restricts the results to the number requested.
         */
        String sqlQuery = "SELECT Code, Name, Continent, Region, " +
                "Population, Capital FROM country " +
                "ORDER BY Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the maximum number of countries to return
            stmt.setInt(1, numberOfCountries);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the report headings
                System.out.printf("%-8s %-45s %-18s %-28s %-15s %-10s%n",
                        "Code", "Name", "Continent", "Region",
                        "Population", "Capital");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "------------------------------");

                int rowCount = 0;

                // Display each country returned by the query
                while (rs.next()) {

                    String code = rs.getString("Code");
                    String name = rs.getString("Name");
                    String continent = rs.getString("Continent");
                    String region = rs.getString("Region");
                    long population = rs.getLong("Population");
                    int capital = rs.getInt("Capital");

                    // Show N/A if the capital is not recorded
                    String capitalValue = rs.wasNull()
                            ? "N/A" : String.valueOf(capital);

                    // Print the country details in aligned columns
                    System.out.printf("%-8s %-45s %-18s %-28s %-15d %-10s%n",
                            code, name, continent, region,
                            population, capitalValue);

                    rowCount++;
                }

                System.out.println("\nTop " + numberOfCountries
                        + " populated countries requested.");
                System.out.println("Total countries displayed: " + rowCount);

            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Reads SQL statements from an external file.
     *
     * @param filePath the location of the SQL file
     * @return the contents of the SQL file as a String
     * @throws IOException if the file cannot be read
     */
    public static String readSqlFile(String filePath) throws IOException {

        // Read the SQL file using UTF-8 encoding
        return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
    }
}
