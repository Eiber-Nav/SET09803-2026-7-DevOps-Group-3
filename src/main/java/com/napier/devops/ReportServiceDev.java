
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

            case 5:
                report5(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 6:
                report6(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 7:
                report7(dbUrl, dbUser, dbPassword);
                break;

            case 8:
                report8(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 9:
                report9(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 10:
                report10(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 11:
                report11(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 12:
                report12(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 13:
                report13(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 14:
                report14(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 15:
                report15(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 16:
                report16(dbUrl, dbUser, dbPassword, scanner);
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
     * Report 5 - Displays the top N populated countries
     * in a selected continent.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report5(String dbUrl, String dbUser,
                               String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 5 - Top N Countries in a Continent");
        System.out.println("----------------------------------------\n");

        // Retrieve the available continents from the database
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

        int numberOfCountries;

        /*
         * Ask the user how many countries to display.
         * Only positive whole numbers are accepted.
         */
        while (true) {

            System.out.print("Enter the number of countries to display (0 to cancel): ");
            String input = scanner.nextLine().trim();

            try {
                numberOfCountries = Integer.parseInt(input);

                if (numberOfCountries == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                if (numberOfCountries > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Retrieve countries belonging to the selected continent.
         * ORDER BY Population DESC sorts the largest populations first.
         * LIMIT restricts the results to the number requested.
         */
        String sqlQuery = "SELECT Code, Name, Continent, Region, " +
                "Population, Capital FROM country " +
                "WHERE Continent = ? " +
                "ORDER BY Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the continent and maximum number of countries
            stmt.setString(1, continent);
            stmt.setInt(2, numberOfCountries);

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
                    String countryContinent = rs.getString("Continent");
                    String region = rs.getString("Region");
                    long population = rs.getLong("Population");
                    int capital = rs.getInt("Capital");

                    // Show N/A if the capital is not recorded
                    String capitalValue = rs.wasNull()
                            ? "N/A" : String.valueOf(capital);

                    // Display the country details in aligned columns
                    System.out.printf("%-8s %-45s %-18s %-28s %-15d %-10s%n",
                            code, name, countryContinent, region,
                            population, capitalValue);

                    rowCount++;
                }

                System.out.println("\nSelected continent: " + continent);
                System.out.println("Top " + numberOfCountries
                        + " populated countries requested.");
                System.out.println("Total countries displayed: " + rowCount);

            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 6 - Displays the top N populated countries
     * in a selected region.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report6(String dbUrl, String dbUser,
                               String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 6 - Top N Countries in a Region");
        System.out.println("----------------------------------------\n");

        // Get the available regions from the database
        String region;

        try {
            region = DatabaseOptions.selectRegion(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load regions: " + e.getMessage());
            return;
        }

        // Return to the menu if the user cancels
        if (region == null) {
            return;
        }

        int numberOfCountries;

        /*
         * Ask the user how many countries to display.
         * Keep asking until a valid number is entered.
         */
        while (true) {

            System.out.print("Enter the number of countries to display (0 to cancel): ");
            String input = scanner.nextLine().trim();

            try {
                numberOfCountries = Integer.parseInt(input);

                if (numberOfCountries == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                if (numberOfCountries > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Find the most populated countries in the selected region.
         * The LIMIT parameter controls how many results are returned.
         */
        String sqlQuery = "SELECT Code, Name, Continent, Region, " +
                "Population, Capital FROM country " +
                "WHERE Region = ? " +
                "ORDER BY Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Pass the selected region and requested number to the query
            stmt.setString(1, region);
            stmt.setInt(2, numberOfCountries);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the report headings
                System.out.printf("%-8s %-45s %-18s %-28s %-15s %-10s%n",
                        "Code", "Name", "Continent", "Region",
                        "Population", "Capital");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "------------------------------");

                int rowCount = 0;

                // Print each country returned from the database
                while (rs.next()) {

                    String code = rs.getString("Code");
                    String name = rs.getString("Name");
                    String continent = rs.getString("Continent");
                    String countryRegion = rs.getString("Region");
                    long population = rs.getLong("Population");
                    int capital = rs.getInt("Capital");

                    // Some countries may not have a capital recorded
                    String capitalValue = rs.wasNull()
                            ? "N/A" : String.valueOf(capital);

                    System.out.printf("%-8s %-45s %-18s %-28s %-15d %-10s%n",
                            code, name, continent, countryRegion,
                            population, capitalValue);

                    rowCount++;
                }

                System.out.println("\nSelected region: " + region);
                System.out.println("Top " + numberOfCountries
                        + " populated countries requested.");
                System.out.println("Total countries displayed: " + rowCount);

            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 7 - Displays all cities in the world,
     * ordered from the largest population to the smallest.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     */
    public static void report7(String dbUrl, String dbUser,
                               String dbPassword) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 7 - All Cities in the World");
        System.out.println("----------------------------------------\n");

        /*
         * Get all cities from the database.
         * The country table is joined so the report can
         * display the country name instead of only its code.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "ORDER BY city.Population DESC;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = stmt.executeQuery()) {

            // Display the column headings
            System.out.printf("%-35s %-40s %-30s %-15s%n",
                    "City", "Country", "District", "Population");

            System.out.println("------------------------------------------------" +
                    "------------------------------------------------" +
                    "--------------------");

            int rowCount = 0;

            // Display each city returned by the database
            while (rs.next()) {

                String cityName = rs.getString("CityName");
                String countryName = rs.getString("CountryName");
                String district = rs.getString("District");
                long population = rs.getLong("Population");

                System.out.printf("%-35s %-40s %-30s %-15d%n",
                        cityName, countryName, district, population);

                rowCount++;
            }

            System.out.println("\nTotal cities displayed: " + rowCount);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 8 - Shows all cities in a selected continent,
     * ordered from the highest population to the lowest.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner used for user input
     */
    public static void report8(String dbUrl, String dbUser,
                               String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 8 - All Cities in a Continent");
        System.out.println("----------------------------------------\n");

        String continent;

        // Let the user choose a continent from the database
        try {
            continent = DatabaseOptions.selectContinent(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load continents: " + e.getMessage());
            return;
        }

        // Return to the menu if the user cancels
        if (continent == null) {
            System.out.println("Report cancelled.");
            return;
        }

        /*
         * Cities have a country code, but no continent column.
         * Join the country table to find which continent each city belongs to.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE country.Continent = ? " +
                "ORDER BY city.Population DESC;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Use the continent selected by the user
            stmt.setString(1, continent);

            try (ResultSet rs = stmt.executeQuery()) {

                System.out.printf("%-35s %-40s %-30s %-15s%n",
                        "City", "Country", "District", "Population");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Print the cities returned for this continent
                while (rs.next()) {

                    String cityName = rs.getString("CityName");
                    String countryName = rs.getString("CountryName");
                    String district = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-40s %-30s %-15d%n",
                            cityName, countryName, district, population);

                    rowCount++;
                }

                System.out.println("\nSelected continent: " + continent);
                System.out.println("Total cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 9 - Shows all cities in a selected region,
     * starting with the most populated.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report9(String dbUrl, String dbUser,
                               String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 9 - All Cities in a Region");
        System.out.println("----------------------------------------\n");

        String region;

        // Get the region selected by the user
        try {
            region = DatabaseOptions.selectRegion(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load regions: " + e.getMessage());
            return;
        }

        // Go back to the menu if the user cancels
        if (region == null) {
            System.out.println("Report cancelled.");
            return;
        }

        /*
         * The city table does not store region information.
         * Join it with the country table so we can filter
         * cities using the region of their country.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE country.Region = ? " +
                "ORDER BY city.Population DESC;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Pass the selected region to the SQL query
            stmt.setString(1, region);

            try (ResultSet rs = stmt.executeQuery()) {

                System.out.printf("%-35s %-40s %-30s %-15s%n",
                        "City", "Country", "District", "Population");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Display the cities returned for the selected region
                while (rs.next()) {

                    String cityName = rs.getString("CityName");
                    String countryName = rs.getString("CountryName");
                    String district = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-40s %-30s %-15d%n",
                            cityName, countryName, district, population);

                    rowCount++;
                }

                System.out.println("\nSelected region: " + region);
                System.out.println("Total cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 10 - Displays all cities in a selected country,
     * ordered from the largest population to the smallest.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report10(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 10 - All Cities in a Country");
        System.out.println("----------------------------------------\n");

        String countryCode;

        // Allow the user to select a country from the database
        try {
            countryCode = DatabaseOptions.selectCountry(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load countries: " + e.getMessage());
            return;
        }

        // Return to the menu if the user cancels
        if (countryCode == null) {
            System.out.println("Report cancelled.");
            return;
        }

        /*
         * Retrieve the cities belonging to the selected country.
         * Join the country table to display the country name,
         * and sort the cities by population in descending order.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE city.CountryCode = ? " +
                "ORDER BY city.Population DESC;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Use the country code selected by the user
            stmt.setString(1, countryCode);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the column headings
                System.out.printf("%-35s %-40s %-30s %-15s%n",
                        "City", "Country", "District", "Population");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Display each city returned for the selected country
                while (rs.next()) {

                    String cityName = rs.getString("CityName");
                    String countryName = rs.getString("CountryName");
                    String district = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-40s %-30s %-15d%n",
                            cityName, countryName, district, population);

                    rowCount++;
                }

                // Display a message if no cities were found
                if (rowCount == 0) {
                    System.out.println("No cities found for the selected country.");
                }

                System.out.println("\nSelected country code: " + countryCode);
                System.out.println("Total cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 11 - Displays all cities in a selected district,
     * ordered from the largest population to the smallest.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report11(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 11 - All Cities in a District");
        System.out.println("----------------------------------------\n");

        String district;

        // Allow the user to select a district from the database
        try {
            district = DatabaseOptions.selectDistrict(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load districts: " + e.getMessage());
            return;
        }

        // Return to the menu if the user cancels
        if (district == null) {
            System.out.println("Report cancelled.");
            return;
        }

        /*
         * Retrieve cities belonging to the selected district.
         * Join the country table to show the country name
         * and sort the results by population.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE city.District = ? " +
                "ORDER BY city.Population DESC;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the district selected by the user
            stmt.setString(1, district);

            try (ResultSet rs = stmt.executeQuery()) {

                System.out.printf("%-35s %-40s %-30s %-15s%n",
                        "City", "Country", "District", "Population");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Display the cities returned for the selected district
                while (rs.next()) {

                    String cityName = rs.getString("CityName");
                    String countryName = rs.getString("CountryName");
                    String cityDistrict = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-40s %-30s %-15d%n",
                            cityName, countryName, cityDistrict, population);

                    rowCount++;
                }

                if (rowCount == 0) {
                    System.out.println("No cities found for the selected district.");
                }

                System.out.println("\nSelected district: " + district);
                System.out.println("Total cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 12 - Displays the top N populated cities in the world.
     * The user chooses how many cities to display.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report12(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 12 - Top N Populated Cities in the World");
        System.out.println("----------------------------------------\n");

        int numberOfCities;

        /*
         * Ask the user how many cities to display.
         * Keep asking until a positive whole number is entered,
         * or return to the menu if the user enters zero.
         */
        while (true) {

            System.out.print("Enter the number of cities to display (0 to cancel): ");
            String input = scanner.nextLine().trim();

            try {
                numberOfCities = Integer.parseInt(input);

                if (numberOfCities == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                if (numberOfCities > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Retrieve cities from the world database.
         * ORDER BY sorts the largest populations first,
         * while LIMIT controls the number of results.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "ORDER BY city.Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the number of cities requested by the user
            stmt.setInt(1, numberOfCities);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the report column headings
                System.out.printf("%-35s %-40s %-30s %-15s%n",
                        "City", "Country", "District", "Population");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Print each city returned by the database
                while (rs.next()) {

                    String cityName = rs.getString("CityName");
                    String countryName = rs.getString("CountryName");
                    String district = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-40s %-30s %-15d%n",
                            cityName, countryName, district, population);

                    rowCount++;
                }

                System.out.println("\nTop " + numberOfCities
                        + " populated cities requested.");
                System.out.println("Total cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 13 - Displays the top N populated cities
     * in a selected continent.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report13(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 13 - Top N Cities in a Continent");
        System.out.println("----------------------------------------\n");

        String continent;

        // Allow the user to select a continent from the database
        try {
            continent = DatabaseOptions.selectContinent(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load continents: " + e.getMessage());
            return;
        }

        // Return to the menu if the user cancels
        if (continent == null) {
            System.out.println("Report cancelled.");
            return;
        }

        int numberOfCities;

        /*
         * Ask how many cities the user wants to display.
         * Only accept positive whole numbers.
         */
        while (true) {

            System.out.print("Enter the number of cities to display (0 to cancel): ");
            String input = scanner.nextLine().trim();

            try {
                numberOfCities = Integer.parseInt(input);

                if (numberOfCities == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                if (numberOfCities > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Join the city and country tables to find the
         * cities belonging to the selected continent.
         * Sort by population and limit the results.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE country.Continent = ? " +
                "ORDER BY city.Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the selected continent and number of cities
            stmt.setString(1, continent);
            stmt.setInt(2, numberOfCities);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the column headings
                System.out.printf("%-35s %-40s %-30s %-15s%n",
                        "City", "Country", "District", "Population");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Display the cities returned by the query
                while (rs.next()) {

                    String cityName = rs.getString("CityName");
                    String countryName = rs.getString("CountryName");
                    String district = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-40s %-30s %-15d%n",
                            cityName, countryName, district, population);

                    rowCount++;
                }

                if (rowCount == 0) {
                    System.out.println("No cities found for the selected continent.");
                }

                System.out.println("\nSelected continent: " + continent);
                System.out.println("Top " + numberOfCities
                        + " populated cities requested.");
                System.out.println("Total cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 14 - Displays the top N populated cities
     * in a selected region.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report14(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 14 - Top N Cities in a Region");
        System.out.println("----------------------------------------\n");

        String region;

        // Allow the user to select a region from the database
        try {
            region = DatabaseOptions.selectRegion(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load regions: " + e.getMessage());
            return;
        }

        // Return to the menu if the user cancels
        if (region == null) {
            System.out.println("Report cancelled.");
            return;
        }

        int numberOfCities;

        /*
         * Ask how many cities the user wants to display.
         * Keep asking until a positive whole number is entered.
         */
        while (true) {

            System.out.print("Enter the number of cities to display (0 to cancel): ");
            String input = scanner.nextLine().trim();

            try {
                numberOfCities = Integer.parseInt(input);

                if (numberOfCities == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                if (numberOfCities > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Join the city and country tables to find cities
         * belonging to the selected region.
         * Sort by population and limit the results.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE country.Region = ? " +
                "ORDER BY city.Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the selected region and number of cities
            stmt.setString(1, region);
            stmt.setInt(2, numberOfCities);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the column headings
                System.out.printf("%-35s %-40s %-30s %-15s%n",
                        "City", "Country", "District", "Population");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Display the cities returned by the query
                while (rs.next()) {

                    String cityName = rs.getString("CityName");
                    String countryName = rs.getString("CountryName");
                    String district = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-40s %-30s %-15d%n",
                            cityName, countryName, district, population);

                    rowCount++;
                }

                if (rowCount == 0) {
                    System.out.println("No cities found for the selected region.");
                }

                System.out.println("\nSelected region: " + region);
                System.out.println("Top " + numberOfCities
                        + " populated cities requested.");
                System.out.println("Total cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 15 - Displays the top N populated cities
     * in a selected country.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report15(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 15 - Top N Cities in a Country");
        System.out.println("----------------------------------------\n");

        String countryCode;

        // Allow the user to select a country from the database
        try {
            countryCode = DatabaseOptions.selectCountry(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load countries: " + e.getMessage());
            return;
        }

        // Return to the menu if the user cancels
        if (countryCode == null) {
            System.out.println("Report cancelled.");
            return;
        }

        int numberOfCities;

        /*
         * Ask how many cities the user wants to display.
         * Keep asking until a positive whole number is entered.
         */
        while (true) {

            System.out.print("Enter the number of cities to display (0 to cancel): ");
            String input = scanner.nextLine().trim();

            try {
                numberOfCities = Integer.parseInt(input);

                if (numberOfCities == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                if (numberOfCities > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Retrieve cities belonging to the selected country.
         * Join the country table to display the country name.
         * Sort by population and limit the results.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE city.CountryCode = ? " +
                "ORDER BY city.Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the selected country and number of cities
            stmt.setString(1, countryCode);
            stmt.setInt(2, numberOfCities);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the column headings
                System.out.printf("%-35s %-40s %-30s %-15s%n",
                        "City", "Country", "District", "Population");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Display the cities returned by the query
                while (rs.next()) {

                    String cityName = rs.getString("CityName");
                    String countryName = rs.getString("CountryName");
                    String district = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-40s %-30s %-15d%n",
                            cityName, countryName, district, population);

                    rowCount++;
                }

                if (rowCount == 0) {
                    System.out.println("No cities found for the selected country.");
                }

                System.out.println("\nSelected country code: " + countryCode);
                System.out.println("Top " + numberOfCities
                        + " populated cities requested.");
                System.out.println("Total cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 16 - Displays the top N populated cities
     * in a selected district.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     * @param scanner the shared Scanner for user input
     */
    public static void report16(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 16 - Top N Cities in a District");
        System.out.println("----------------------------------------\n");

        String district;

        // Allow the user to select a district from the database
        try {
            district = DatabaseOptions.selectDistrict(
                    dbUrl, dbUser, dbPassword, scanner);

        } catch (SQLException e) {
            System.err.println("Unable to load districts: " + e.getMessage());
            return;
        }

        // Return to the menu if the user cancels
        if (district == null) {
            System.out.println("Report cancelled.");
            return;
        }

        int numberOfCities;

        /*
         * Ask how many cities the user wants to display.
         * Keep asking until a positive whole number is entered.
         */
        while (true) {

            System.out.print("Enter the number of cities to display (0 to cancel): ");
            String input = scanner.nextLine().trim();

            try {
                numberOfCities = Integer.parseInt(input);

                if (numberOfCities == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                if (numberOfCities > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Retrieve cities belonging to the selected district.
         * Join the country table to display the country name.
         * Sort by population and limit the results.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE city.District = ? " +
                "ORDER BY city.Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the selected district and number of cities
            stmt.setString(1, district);
            stmt.setInt(2, numberOfCities);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the column headings
                System.out.printf("%-35s %-40s %-30s %-15s%n",
                        "City", "Country", "District", "Population");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Display the cities returned by the query
                while (rs.next()) {

                    String cityName = rs.getString("CityName");
                    String countryName = rs.getString("CountryName");
                    String cityDistrict = rs.getString("District");
                    long population = rs.getLong("Population");

                    System.out.printf("%-35s %-40s %-30s %-15d%n",
                            cityName, countryName, cityDistrict, population);

                    rowCount++;
                }

                if (rowCount == 0) {
                    System.out.println("No cities found for the selected district.");
                }

                System.out.println("\nSelected district: " + district);
                System.out.println("Top " + numberOfCities
                        + " populated cities requested.");
                System.out.println("Total cities displayed: " + rowCount);
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
