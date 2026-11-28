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

            default:
                // Handle any report number that is not supported
                System.out.println("Invalid report number: " + reportNumber);
                break;
        }
    }

    /**
     * Report 1 - Displays all countries in the world.
     * Countries are sorted from largest population to smallest.
     *
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     */
    public static void report1(String dbUrl, String dbUser, String dbPassword) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 1 - All Countries in the World");
        System.out.println("----------------------------------------\n");

        /*
         * Retrieve the required country information.
         * Join the city table to display the capital city name
         * instead of its ID.
         * A LEFT JOIN keeps countries without a recorded capital.
         * Sort the countries from largest population to smallest.
         */
        String sqlQuery = "SELECT country.Code, country.Name, " +
                "country.Continent, country.Region, country.Population, " +
                "city.Name AS Capital " +
                "FROM country " +
                "LEFT JOIN city ON country.Capital = city.ID " +
                "ORDER BY country.Population DESC;";

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
                String capital = rs.getString("Capital");

// Display N/A if the country has no recorded capital
                String capitalValue = capital == null ? "N/A" : capital;

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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
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
         * Join the city table to display the capital city name
         * instead of its ID.
         * A LEFT JOIN keeps countries without a recorded capital.
         * A PreparedStatement safely passes the selected continent
         * into the SQL query.
         */
        String sqlQuery = "SELECT country.Code, country.Name, " +
                "country.Continent, country.Region, country.Population, " +
                "city.Name AS Capital " +
                "FROM country " +
                "LEFT JOIN city ON country.Capital = city.ID " +
                "WHERE country.Continent = ? " +
                "ORDER BY country.Population DESC;";

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
                    String capital = rs.getString("Capital");

// Display N/A if the country has no recorded capital
                    String capitalValue = capital == null ? "N/A" : capital;

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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
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
         * Join the city table to display the capital city name
         * instead of its ID.
         * A LEFT JOIN keeps countries without a recorded capital.
         * The PreparedStatement safely passes the selected region
         * into the query, and DESC sorts the largest populations first.
         */
        String sqlQuery = "SELECT country.Code, country.Name, " +
                "country.Continent, country.Region, country.Population, " +
                "city.Name AS Capital " +
                "FROM country " +
                "LEFT JOIN city ON country.Capital = city.ID " +
                "WHERE country.Region = ? " +
                "ORDER BY country.Population DESC;";

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
                    String capital = rs.getString("Capital");

// Display N/A if the country has no recorded capital
                    String capitalValue = capital == null ? "N/A" : capital;

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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
     */
    public static void report4(String dbUrl, String dbUser,
                               String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 4 - Top N Populated Countries in the World");
        System.out.println("----------------------------------------\n");

        int numberOfCites;

        /*
         * Ask the user how many countries to display.
         * Keep asking until a valid positive number is entered.
         */
        while (true) {

            System.out.print("Enter the number of countries to display (0 to cancel): ");

            // Cancel the report if there is no more input
            if (!scanner.hasNextLine()) {
                System.out.println("Report cancelled.");
                return;
            }

            String input = scanner.nextLine().trim();

            try {
                numberOfCites = Integer.parseInt(input);

                // Return to the menu if the user enters 0
                if (numberOfCites == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                // Only positive numbers are accepted
                if (numberOfCites > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Retrieve countries from the world database.
         * Join the city table to display the capital city name
         * instead of its ID.
         * A LEFT JOIN keeps countries without a recorded capital.
         * ORDER BY Population DESC sorts the largest populations first.
         * LIMIT ? restricts the results to the number requested.
         */
        String sqlQuery = "SELECT country.Code, country.Name, " +
                "country.Continent, country.Region, country.Population, " +
                "city.Name AS Capital " +
                "FROM country " +
                "LEFT JOIN city ON country.Capital = city.ID " +
                "ORDER BY country.Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the maximum number of countries to return
            stmt.setInt(1, numberOfCites);

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
                    String capital = rs.getString("Capital");

// Display N/A if the country has no recorded capital
                    String capitalValue = capital == null ? "N/A" : capital;

                    // Print the country details in aligned columns
                    System.out.printf("%-8s %-45s %-18s %-28s %-15d %-10s%n",
                            code, name, continent, region,
                            population, capitalValue);

                    rowCount++;
                }

                System.out.println("\nTop " + numberOfCites
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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
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

        int numberOfCountry;

        /*
         * Ask the user how many countries to display.
         * Only positive whole numbers are accepted.
         */
        while (true) {

            System.out.print("Enter the number of countries to display (0 to cancel): ");
            // Cancel the report if there is no more input
            if (!scanner.hasNextLine()) {
                System.out.println("Report cancelled.");
                return;
            }

            String input = scanner.nextLine().trim();

            try {
                numberOfCountry = Integer.parseInt(input);

                if (numberOfCountry == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                if (numberOfCountry > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Retrieve countries belonging to the selected continent.
         * Join the city table to display the capital city name
         * instead of its ID.
         * A LEFT JOIN keeps countries without a recorded capital.
         * Sort the countries from largest population to smallest
         * and limit the results to the number requested.
         */
        String sqlQuery = "SELECT country.Code, country.Name, " +
                "country.Continent, country.Region, country.Population, " +
                "city.Name AS Capital " +
                "FROM country " +
                "LEFT JOIN city ON country.Capital = city.ID " +
                "WHERE country.Continent = ? " +
                "ORDER BY country.Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the continent and maximum number of countries
            stmt.setString(1, continent);
            stmt.setInt(2, numberOfCountry);

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
                    String capital = rs.getString("Capital");

// Display N/A if the country has no recorded capital
                    String capitalValue = capital == null ? "N/A" : capital;

                    // Display the country details in aligned columns
                    System.out.printf("%-8s %-45s %-18s %-28s %-15d %-10s%n",
                            code, name, countryContinent, region,
                            population, capitalValue);

                    rowCount++;
                }

                System.out.println("\nSelected continent: " + continent);
                System.out.println("Top " + numberOfCountry
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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
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

        int numberOfCites;

        /*
         * Ask the user how many countries to display.
         * Keep asking until a valid number is entered.
         */
        while (true) {

            System.out.print("Enter the number of countries to display (0 to cancel): ");
            // Cancel the report if there is no more input
            if (!scanner.hasNextLine()) {
                System.out.println("Report cancelled.");
                return;
            }

            String input = scanner.nextLine().trim();

            try {
                numberOfCites = Integer.parseInt(input);

                if (numberOfCites == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                if (numberOfCites > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Retrieve countries belonging to the selected region.
         * Join the city table to display the capital city name
         * instead of its ID.
         * A LEFT JOIN keeps countries without a recorded capital.
         * Sort the countries from largest population to smallest
         * and limit the results to the number requested.
         */
        String sqlQuery = "SELECT country.Code, country.Name, " +
                "country.Continent, country.Region, country.Population, " +
                "city.Name AS Capital " +
                "FROM country " +
                "LEFT JOIN city ON country.Capital = city.ID " +
                "WHERE country.Region = ? " +
                "ORDER BY country.Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Pass the selected region and requested number to the query
            stmt.setString(1, region);
            stmt.setInt(2, numberOfCites);

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
                    String capital = rs.getString("Capital");

// Display N/A if the country has no recorded capital
                    String capitalValue = capital == null ? "N/A" : capital;

                    System.out.printf("%-8s %-45s %-18s %-28s %-15d %-10s%n",
                            code, name, continent, countryRegion,
                            population, capitalValue);

                    rowCount++;
                }

                System.out.println("\nSelected region: " + region);
                System.out.println("Top " + numberOfCites
                        + " populated countries requested.");
                System.out.println("Total countries displayed: " + rowCount);

            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


}
