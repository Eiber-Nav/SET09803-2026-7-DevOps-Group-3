
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

            case 17:
                report17(dbUrl, dbUser, dbPassword);
                break;

            case 18:
                report18(dbUrl, dbUser, dbPassword, scanner);
                break;
            case 19:
                report19(dbUrl, dbUser, dbPassword, scanner);
                break;
            case 20:
                report20(dbUrl, dbUser, dbPassword, scanner);
                break;
            case 21:
                report21(dbUrl, dbUser, dbPassword, scanner);
                break;
            case 22:
                report22(dbUrl, dbUser, dbPassword, scanner);
                break;
            case 23:
                report23(dbUrl, dbUser, dbPassword);
                break;

            case 24:
                report24(dbUrl, dbUser, dbPassword);
                break;

            case 25:
                report25(dbUrl, dbUser, dbPassword);
                break;

            case 26:
                printPopulation(dbUrl, dbUser, dbPassword,
                        "SELECT 'World' AS Name, SUM(Population) AS Population FROM country");
                break;

            case 27:
            case 28:
            case 29:
            case 30:
                reportSelectedPopulation(reportNumber, dbUrl, dbUser, dbPassword, scanner);
                break;

            case 31:
                report31(dbUrl, dbUser, dbPassword, scanner);
                break;

            case 32:
                report32(dbUrl, dbUser, dbPassword);
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


    /**
     * Report 7 - Displays all cities in the world,
     * ordered from the largest population to the smallest.
     *
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner used for user input
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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
     */
    public static void report11(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 11 - All Cities in a District");
        System.out.println("----------------------------------------\n");

        String countryCode;
        String district;

// Allow the user to select a country first
        try {
            countryCode = DatabaseOptions.selectCountry(
                    dbUrl, dbUser, dbPassword, scanner);

            // Return to the menu if the country selection is cancelled
            if (countryCode == null) {
                System.out.println("Report cancelled.");
                return;
            }

            // Display only districts belonging to the selected country
            district = DatabaseOptions.selectDistrict(
                    dbUrl, dbUser, dbPassword, scanner, countryCode);

        } catch (SQLException e) {
            System.err.println("Unable to load countries or districts: "
                    + e.getMessage());
            return;
        }

// Return to the menu if the district selection is cancelled
        if (district == null) {
            System.out.println("Report cancelled.");
            return;
        }

        /*
         * Retrieve cities belonging to the selected country and district.
         * Filtering by both prevents cities from different countries
         * with the same district name from appearing in the report.
         * Join the country table to display the country name
         * and sort the cities from largest population to smallest.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE city.CountryCode = ? AND city.District = ? " +
                "ORDER BY city.Population DESC;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the country and district selected by the user
            stmt.setString(1, countryCode);
            stmt.setString(2, district);

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

                System.out.println("\nSelected country code: " + countryCode);
                System.out.println("Selected district: " + district);
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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
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
            // Cancel the report if there is no more input
            if (!scanner.hasNextLine()) {
                System.out.println("Report cancelled.");
                return;
            }

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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
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
            // Cancel the report if there is no more input
            if (!scanner.hasNextLine()) {
                System.out.println("Report cancelled.");
                return;
            }

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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
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
            // Cancel the report if there is no more input
            if (!scanner.hasNextLine()) {
                System.out.println("Report cancelled.");
                return;
            }

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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
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
            // Cancel the report if there is no more input
            if (!scanner.hasNextLine()) {
                System.out.println("Report cancelled.");
                return;
            }

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
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
     */
    public static void report16(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 16 - Top N Cities in a District");
        System.out.println("----------------------------------------\n");

        String countryCode;
        String district;

// Allow the user to select a country first
        try {
            countryCode = DatabaseOptions.selectCountry(
                    dbUrl, dbUser, dbPassword, scanner);

            // Return to the menu if the country selection is cancelled
            if (countryCode == null) {
                System.out.println("Report cancelled.");
                return;
            }

            // Display only districts belonging to the selected country
            district = DatabaseOptions.selectDistrict(
                    dbUrl, dbUser, dbPassword, scanner, countryCode);

        } catch (SQLException e) {
            System.err.println("Unable to load countries or districts: "
                    + e.getMessage());
            return;
        }

// Return to the menu if the district selection is cancelled
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
            // Cancel the report if there is no more input
            if (!scanner.hasNextLine()) {
                System.out.println("Report cancelled.");
                return;
            }

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
         * Retrieve cities belonging to the selected country and district.
         * Filtering by both prevents cities from different countries
         * with the same district name from appearing in the report.
         * Join the country table to display the country name.
         * Sort by population and limit the results to the number requested.
         */
        String sqlQuery = "SELECT city.Name AS CityName, " +
                "country.Name AS CountryName, " +
                "city.District, city.Population " +
                "FROM city " +
                "JOIN country ON city.CountryCode = country.Code " +
                "WHERE city.CountryCode = ? AND city.District = ? " +
                "ORDER BY city.Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the selected country, district and number of cities
            stmt.setString(1, countryCode);
            stmt.setString(2, district);
            stmt.setInt(3, numberOfCities);

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

                System.out.println("\nSelected country code: " + countryCode);
                System.out.println("Selected district: " + district);
                System.out.println("Top " + numberOfCities
                        + " populated cities requested.");
                System.out.println("Total cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void report17(String dbUrl, String dbUser, String dbPassword) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 17 - All capital cities in the world by population");
        System.out.println("---------------------------------------------------------\n");

        /*
         * Retrieve the required country information.
         * ORDER BY Population DESC places countries with
         * the largest population at the top of the report.
         */
        String sqlQuery = "SELECT city.Name AS CapitalCity, country.Name AS Country, city.Population FROM country JOIN city ON country.Capital = city.ID ORDER BY city.Population DESC;";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = stmt.executeQuery()) {

            // Print the column headings
            System.out.printf("%-45s %-45s  %-15s%n",
                    "Capital City", "Country", "Population"
            );

            System.out.println("-------------------------------------------" +
                    "------------------------------------------------" +
                    "-----------------");

            int rowCount = 0;

            // Display each country returned from the database
            while (rs.next()) {
                String capital = rs.getString("CapitalCity");
                String country = rs.getString("Country");
                long population = rs.getLong("Population");


                // Display the results using aligned columns
                System.out.printf("%-45s %-45s  %-15s%n",
                        capital, country, population);

                rowCount++;
            }

            System.out.println("\nQuery executed successfully against 'world' database.");
            System.out.println("Total countries found: " + rowCount);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Report 18 - Displays all capital cities in a selected continent.
     * Capital cities are sorted from largest population to smallest.
     *
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
     */
    public static void report18(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 18 - All Capital Cities in a Continent");
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

        /*
         * Join the country and city tables to find each capital.
         * Filter by the selected continent and sort by population.
         */
        String sqlQuery = "SELECT city.Name AS CapitalCity, " +
                "country.Name AS CountryName, city.Population " +
                "FROM country " +
                "JOIN city ON country.Capital = city.ID " +
                "WHERE country.Continent = ? " +
                "ORDER BY city.Population DESC;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the continent selected by the user
            stmt.setString(1, continent);

            try (ResultSet rs = stmt.executeQuery()) {

                System.out.printf("%-40s %-45s %-15s%n",
                        "Capital City", "Country", "Population");

                System.out.println("----------------------------------------" +
                        "----------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Display the capital cities returned by the query
                while (rs.next()) {

                    String capitalCity = rs.getString("CapitalCity");
                    String countryName = rs.getString("CountryName");
                    long population = rs.getLong("Population");

                    System.out.printf("%-40s %-45s %-15d%n",
                            capitalCity, countryName, population);

                    rowCount++;
                }

                if (rowCount == 0) {
                    System.out.println("No capital cities found.");
                }

                System.out.println("\nSelected continent: " + continent);
                System.out.println("Total capital cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Report 19 - Displays all capital cities in a selected region.
     * Capital cities are sorted from largest population to smallest.
     *
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
     */
    public static void report19(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 19 - All Capital Cities in a Region");
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

        /*
         * Join the country and city tables to find each capital.
         * Filter by the selected region and sort by population.
         */
        String sqlQuery = "SELECT city.Name AS CapitalCity, " +
                "country.Name AS CountryName, " +
                "city.Population " +
                "FROM country " +
                "JOIN city ON country.Capital = city.ID " +
                "WHERE country.Region = ? " +
                "ORDER BY city.Population DESC;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the region selected by the user
            stmt.setString(1, region);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the report column headings
                System.out.printf("%-40s %-45s %-15s%n",
                        "Capital City", "Country", "Population");

                System.out.println("----------------------------------------" +
                        "----------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Display each capital city returned by the query
                while (rs.next()) {

                    String capitalCity = rs.getString("CapitalCity");
                    String countryName = rs.getString("CountryName");
                    long population = rs.getLong("Population");

                    System.out.printf("%-40s %-45s %-15d%n",
                            capitalCity, countryName, population);

                    rowCount++;
                }

                // Display a message if no capital cities were found
                if (rowCount == 0) {
                    System.out.println("No capital cities found for this region.");
                }

                System.out.println("\nSelected region: " + region);
                System.out.println("Total capital cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Report 20 - Displays the top N populated capital cities
     * in the world.
     *
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
     */
    public static void report20(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 20 - Top N Populated Capital Cities in the World");
        System.out.println("----------------------------------------\n");

        int numberOfCities;

        /*
         * Ask the user how many capital cities to display.
         * Keep asking until a positive whole number is entered.
         */
        while (true) {

            System.out.print("Enter the number of capital cities to display (0 to cancel): ");

            // Cancel the report if there is no more input
            if (!scanner.hasNextLine()) {
                System.out.println("Report cancelled.");
                return;
            }

            String input = scanner.nextLine().trim();

            try {
                numberOfCities = Integer.parseInt(input);

                // Return to the menu if the user enters 0
                if (numberOfCities == 0) {
                    System.out.println("Report cancelled.");
                    return;
                }

                // Only positive numbers are accepted
                if (numberOfCities > 0) {
                    break;
                }

                System.out.println("Please enter a number greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        /*
         * Retrieve capital cities from the world database.
         * Join the country and city tables using the capital city ID.
         * Sort the cities from largest population to smallest
         * and limit the results to the number requested.
         */
        String sqlQuery = "SELECT city.Name AS Capital_City, " +
                "country.Name AS Country, " +
                "city.Population FROM country " +
                "JOIN city ON country.Capital = city.ID " +
                "ORDER BY city.Population DESC LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the maximum number of capital cities to return
            stmt.setInt(1, numberOfCities);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the report headings
                System.out.printf("%-45s %-45s %-15s%n",
                        "Capital City", "Country", "Population");

                System.out.println("------------------------------------------------" +
                        "------------------------------------------------" +
                        "--------------------");

                int rowCount = 0;

                // Display each capital city returned by the query
                while (rs.next()) {

                    String cityName = rs.getString("Capital_City");
                    String countryName = rs.getString("Country");
                    long population = rs.getLong("Population");

                    // Print the capital city details in aligned columns
                    System.out.printf("%-45s %-45s %-15d%n",
                            cityName, countryName, population);

                    rowCount++;
                }

                System.out.println("\nTop " + numberOfCities
                        + " populated capital cities requested.");
                System.out.println("Total capital cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public static void report21(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 21 - op N populated capital cities in a continent");
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

        int numberOfCites;

        /*
         * Ask the user how many countries to display.
         * Only positive whole numbers are accepted.
         */
        while (true) {

            System.out.print("Enter the number of cites to display (0 to cancel): ");
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
         * Retrieve countries belonging to the selected continent.
         * ORDER BY Population DESC sorts the largest populations first.
         * LIMIT restricts the results to the number requested.
         */

        String sqlQuery = "SELECT city.Name AS Capital_City, " +
                "country.Name AS Country, city.Population AS Population " +
                "FROM country " +
                "JOIN city ON country.Capital = city.ID " +
                "WHERE country.Continent = ? " +
                "ORDER BY city.Population DESC LIMIT ?";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the selected continent and number of capital cities
            stmt.setString(1, continent);
            stmt.setInt(2, numberOfCites);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the report headings
                System.out.printf("%-30s %-35s %15s%n",
                        "Capital City", "Country", "Population");

                System.out.println("---------------------------------------------------------------" +
                        "------------------");

                int rowCount = 0;

                // Display each capital city returned by the query
                while (rs.next()) {

                    String capitalCity = rs.getString("Capital_City");
                    String country = rs.getString("Country");
                    long population = rs.getLong("Population");

                    System.out.printf("%-30s %-35s %,15d%n",
                            capitalCity, country, population);

                    rowCount++;
                }

                System.out.println("\nSelected continent: " + continent);
                System.out.println("Top " + numberOfCites +
                        " populated capital cities requested.");
                System.out.println("Total capital cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

    }


    /**
     * Report 22 - Displays the top N populated capital cities in a selected region.
     *
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     * @param scanner    the shared Scanner for user input
     */
    public static void report22(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n-------------------------------------------");
        System.out.println("Report 22 - Top N Capital Cities in a Region");
        System.out.println("---------------------------------------------\n");

        // Retrieve available regions from the database
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

        int numberOfCities;

        // Ask the user how many capital cities to display
        while (true) {

            System.out.print("\nEnter the number of capital cities to display (0 to cancel): ");

            // Cancel the report if there is no more input
            if (!scanner.hasNextLine()) {
                System.out.println("Report cancelled.");
                return;
            }

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

        // Retrieve the most populated capital cities in the selected region
        String sqlQuery = "SELECT ci.Name AS CapitalCity, " +
                "co.Name AS Country, ci.Population " +
                "FROM country co " +
                "JOIN city ci ON co.Capital = ci.ID " +
                "WHERE co.Region = ? " +
                "ORDER BY ci.Population DESC " +
                "LIMIT ?;";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the selected region and maximum number of cities
            stmt.setString(1, region);
            stmt.setInt(2, numberOfCities);

            try (ResultSet rs = stmt.executeQuery()) {

                // Display the report headings
                System.out.println("\nRegion: " + region);

                System.out.printf("%-40s %-40s %-20s%n",
                        "Capital City", "Country", "Population");

                System.out.println("---------------------------------------------------------------------" +
                        "-----------------------");

                int rowCount = 0;

                // Display each capital city returned by the query
                while (rs.next()) {

                    String capitalCity = rs.getString("CapitalCity");
                    String country = rs.getString("Country");
                    long population = rs.getLong("Population");

                    System.out.printf("%-40s %-40s %,d%n",
                            capitalCity, country, population);

                    rowCount++;
                }

                if (rowCount == 0) {
                    System.out.println("No capital cities found for region: " + region);
                }

                System.out.println("\nTop " + numberOfCities + " capital cities requested.");
                System.out.println("Total capital cities displayed: " + rowCount);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Report 23 - Displays the population breakdown for each continent.
     * Shows the total population, population living in cities,
     * population outside cities and their percentages.
     *
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     */
    public static void report23(String dbUrl, String dbUser, String dbPassword) {

        System.out.println("\n--------------------------------------------");
        System.out.println("Report 23 - Population Breakdown by Continent");
        System.out.println("--------------------------------------------\n");

        /*
         * Get the total population of each continent from country.
         * Calculate city populations separately before joining,
         * so country populations are not counted more than once.
         */
        String sqlQuery =
                "SELECT co.Continent, " +
                        "SUM(co.Population) AS TotalPopulation, " +
                        "COALESCE(SUM(cp.CityPopulation), 0) AS CityPopulation " +
                        "FROM country co " +
                        "LEFT JOIN ( " +
                        "    SELECT CountryCode, SUM(Population) AS CityPopulation " +
                        "    FROM city " +
                        "    GROUP BY CountryCode " +
                        ") cp ON co.Code = cp.CountryCode " +
                        "GROUP BY co.Continent " +
                        "ORDER BY TotalPopulation DESC;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = stmt.executeQuery()) {

            // Display the report headings
            System.out.printf("%-18s %18s %18s %12s %18s %12s%n",
                    "Continent", "Total Population", "In Cities",
                    "In Cities %", "Not in Cities", "Not in %");

            System.out.println("-".repeat(105));

            int rowCount = 0;

            // Display the population information for each continent
            while (rs.next()) {

                String continent = rs.getString("Continent");
                long totalPopulation = rs.getLong("TotalPopulation");
                long cityPopulation = rs.getLong("CityPopulation");

                /*
                 * Check that the recorded city population does not exceed
                 * the total population before calculating the percentages.
                 * Display N/A if the database contains inconsistent values.
                 */
                if (cityPopulation > totalPopulation) {

                    System.out.printf(
                            "%-18s %,18d %,18d %12s %18s %12s%n",
                            continent,
                            totalPopulation,
                            cityPopulation,
                            "N/A",
                            "N/A",
                            "N/A"
                    );

                    rowCount++;
                    continue;
                }

// Calculate the population outside the recorded cities
                long outsideCities = totalPopulation - cityPopulation;

// Use the existing method to calculate population percentages
                String cityPercentage =
                        populationPercentage(cityPopulation, totalPopulation);

                String outsidePercentage =
                        populationPercentage(outsideCities, totalPopulation);

// Display the population information in aligned columns
                System.out.printf(
                        "%-18s %,18d %,18d %12s %,18d %12s%n",
                        continent,
                        totalPopulation,
                        cityPopulation,
                        cityPercentage,
                        outsideCities,
                        outsidePercentage
                );

                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No population information was found.");
            }

            System.out.println("\nTotal continents displayed: " + rowCount);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 24 - Displays the population breakdown by region.
     *
     * @param dbUrl the database connection URL
     * @param dbUser the database username
     * @param dbPassword the database password
     */

    /**
     * Report 24 - Displays the population breakdown for each region.
     * Shows the total population, population living in cities,
     * population outside cities and their percentages.
     *
     * @param dbUrl      the database connection URL
     * @param dbUser     the database username
     * @param dbPassword the database password
     */
    public static void report24(String dbUrl, String dbUser, String dbPassword) {

        System.out.println("\n--------------------------------------------");
        System.out.println("Report 24 - Population Breakdown by Region");
        System.out.println("--------------------------------------------\n");

        /*
         * Get the total population of each region from country.
         * Calculate city populations separately before joining,
         * so country populations are not counted more than once.
         */
        String sqlQuery =
                "SELECT co.Region, " +
                        "SUM(co.Population) AS TotalPopulation, " +
                        "COALESCE(SUM(cp.CityPopulation), 0) AS CityPopulation " +
                        "FROM country co " +
                        "LEFT JOIN ( " +
                        "    SELECT CountryCode, SUM(Population) AS CityPopulation " +
                        "    FROM city " +
                        "    GROUP BY CountryCode " +
                        ") cp ON co.Code = cp.CountryCode " +
                        "GROUP BY co.Region " +
                        "ORDER BY TotalPopulation DESC;";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = stmt.executeQuery()) {

            // Display the report column headings
            System.out.printf("%-30s %18s %18s %12s %18s %12s%n",
                    "Region", "Total Population", "In Cities",
                    "In Cities %", "Not in Cities", "Not in %");

            System.out.println("-".repeat(117));

            int rowCount = 0;

            // Display the population information for each region
            while (rs.next()) {

                String region = rs.getString("Region");
                long totalPopulation = rs.getLong("TotalPopulation");
                long cityPopulation = rs.getLong("CityPopulation");

                /*
                 * Check that the recorded city population does not exceed
                 * the total population before calculating the percentages.
                 * Display N/A if the database contains inconsistent values.
                 */
                if (cityPopulation > totalPopulation) {

                    System.out.printf(
                            "%-30s %,18d %,18d %12s %18s %12s%n",
                            region,
                            totalPopulation,
                            cityPopulation,
                            "N/A",
                            "N/A",
                            "N/A"
                    );

                    rowCount++;
                    continue;
                }

// Calculate the population outside the recorded cities
                long outsideCities = totalPopulation - cityPopulation;

// Use the existing method to calculate population percentages
                String cityPercentage =
                        populationPercentage(cityPopulation, totalPopulation);

                String outsidePercentage =
                        populationPercentage(outsideCities, totalPopulation);

// Display the population information in aligned columns
                System.out.printf(
                        "%-30s %,18d %,18d %12s %,18d %12s%n",
                        region,
                        totalPopulation,
                        cityPopulation,
                        cityPercentage,
                        outsideCities,
                        outsidePercentage
                );

                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No population information was found.");
            }

            System.out.println("\nTotal regions displayed: " + rowCount);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Displays total, recorded city, and non-city population for each country.
     */
    private static void report25(String dbUrl, String dbUser, String dbPassword) {
        String sql = """
                SELECT co.Name, co.Population AS TotalPopulation,
                       COALESCE(cities.Population, 0) AS CityPopulation
                FROM country co
                LEFT JOIN (
                    SELECT CountryCode, SUM(Population) AS Population
                    FROM city
                    GROUP BY CountryCode
                ) cities ON cities.CountryCode = co.Code
                ORDER BY co.Name, co.Code
                """;

        System.out.println("\nReport 25 - Population breakdown by country");
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.next()) {
                System.out.println("No data available.");
                return;
            }

            System.out.printf("%-52s %17s %17s %9s %20s %12s%n",
                    "Country", "Total population", "City population", "City %",
                    "Non-city population", "Non-city %");

            do {
                String country = rs.getString("Name");
                long total = rs.getLong("TotalPopulation");
                long city = rs.getLong("CityPopulation");

                if (city > total) {
                    System.out.printf("%-52s %,17d %,17d %9s %20s %12s  [City population exceeds total]%n",
                            country, total, city, "N/A", "N/A", "N/A");
                    continue;
                }

                long nonCity = total - city;
                System.out.printf("%-52s %,17d %,17d %9s %,20d %12s%n",
                        country, total, city, populationPercentage(city, total),
                        nonCity, populationPercentage(nonCity, total));
            } while (rs.next());
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private static String populationPercentage(long population, long total) {
        return total == 0 ? "N/A" : String.format("%.2f%%", 100.0 * population / total);
    }

    /**
     * Runs reports 27–30 using the existing geographical selection menus.
     */
    private static void reportSelectedPopulation(int reportNumber, String dbUrl,
                                                 String dbUser, String dbPassword,
                                                 Scanner scanner) {
        try {
            switch (reportNumber) {
                case 27 -> {
                    String continent = DatabaseOptions.selectContinent(dbUrl, dbUser, dbPassword, scanner);
                    if (continent != null) {
                        printPopulation(dbUrl, dbUser, dbPassword, """
                                SELECT Continent AS Name, SUM(Population) AS Population
                                FROM country WHERE Continent = ? GROUP BY Continent
                                """, continent);
                    }
                }
                case 28 -> {
                    String region = DatabaseOptions.selectRegion(dbUrl, dbUser, dbPassword, scanner);
                    if (region != null) {
                        printPopulation(dbUrl, dbUser, dbPassword, """
                                SELECT Region AS Name, SUM(Population) AS Population
                                FROM country WHERE Region = ? GROUP BY Region
                                """, region);
                    }
                }
                case 29 -> {
                    String countryCode = DatabaseOptions.selectCountry(dbUrl, dbUser, dbPassword, scanner);
                    if (countryCode != null) {
                        printPopulation(dbUrl, dbUser, dbPassword,
                                "SELECT Name, Population FROM country WHERE Code = ?", countryCode);
                    }
                }
                case 30 -> {
                    String countryCode = DatabaseOptions.selectCountry(dbUrl, dbUser, dbPassword, scanner);
                    if (countryCode == null) {
                        return;
                    }

                    String district = DatabaseOptions.selectDistrict(
                            dbUrl, dbUser, dbPassword, scanner, countryCode);
                    if (district != null) {
                        printPopulation(dbUrl, dbUser, dbPassword, """
                                SELECT CONCAT(ci.District, ', ', co.Name) AS Name,
                                       SUM(ci.Population) AS Population
                                FROM city ci
                                JOIN country co ON co.Code = ci.CountryCode
                                WHERE ci.CountryCode = ? AND ci.District = ?
                                GROUP BY ci.District, co.Name
                                """, countryCode, district);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to load selection options: " + e.getMessage());
        }
    }

    /**
     * Displays the named population returned by a population lookup.
     */
    private static void printPopulation(String dbUrl, String dbUser, String dbPassword,
                                        String sql, String... filters) {
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < filters.length; i++) {
                stmt.setString(i + 1, filters[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long population = rs.getLong("Population");
                    if (!rs.wasNull()) {
                        System.out.printf("%n%s population: %,d%n", rs.getString("Name"), population);
                        return;
                    }
                }

                System.out.println("No data available.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    /**
     * Report 31 - Shows the population of a selected city.
     * <p>
     * The user first chooses a country and then selects
     * a city from that country. The population is retrieved
     * directly from the world database.
     */
    public static void report31(String dbUrl, String dbUser,
                                String dbPassword, Scanner scanner) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 31 - Population of a City");
        System.out.println("----------------------------------------\n");

        String countryCode;

        // Let the user choose a country from the available options
        try {
            countryCode = DatabaseOptions.selectCountry(
                    dbUrl, dbUser, dbPassword, scanner
            );

        } catch (SQLException e) {
            System.err.println("Unable to load countries: " + e.getMessage());
            return;
        }

        // Return to the main menu if the user cancels
        if (countryCode == null) {
            System.out.println("Report cancelled.");
            return;
        }

        /*
         * Find all cities belonging to the selected country.
         * Each city has a unique ID, so we use that instead
         * of the city name to avoid selecting the wrong city.
         */
        String cityQuery = "SELECT ID, Name, District " +
                "FROM city " +
                "WHERE CountryCode = ? " +
                "ORDER BY Name, ID";

        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(cityQuery)) {

            stmt.setString(1, countryCode);

            try (ResultSet rs = stmt.executeQuery()) {

                java.util.List<Integer> cityIds =
                        new java.util.ArrayList<>();

                System.out.println("Select a city (0 to cancel):");

                // Display the cities available in the selected country
                while (rs.next()) {

                    cityIds.add(rs.getInt("ID"));

                    System.out.printf("%d. %s (%s)%n",
                            cityIds.size(),
                            rs.getString("Name"),
                            rs.getString("District"));
                }

                // Stop if the selected country has no cities recorded
                if (cityIds.isEmpty()) {
                    System.out.println(
                            "No cities found for the selected country."
                    );
                    return;
                }

                int selectedCityId;

                /*
                 * Keep asking until the user enters a valid number.
                 * Entering zero returns them to the main menu.
                 */
                while (true) {

                    System.out.print(
                            "Enter your choice (0-" + cityIds.size() + "): "
                    );

                    // Cancel the report if there is no more input
                    if (!scanner.hasNextLine()) {
                        System.out.println("Report cancelled.");
                        return;
                    }

                    String answer = scanner.nextLine().trim();

                    try {
                        int choice = Integer.parseInt(answer);

                        if (choice == 0) {
                            System.out.println("Report cancelled.");
                            return;
                        }

                        if (choice >= 1 && choice <= cityIds.size()) {
                            selectedCityId = cityIds.get(choice - 1);
                            break;
                        }

                    } catch (NumberFormatException e) {
                        // Invalid numbers are handled by the message below
                    }

                    System.out.println(
                            "Please choose a valid number from the list."
                    );
                }

                /*
                 * Get the population of the city that was selected.
                 * Joining the country table also lets us display
                 * the full country name instead of its code.
                 */
                String populationQuery =
                        "SELECT ci.Name AS CityName, " +
                                "co.Name AS CountryName, " +
                                "ci.District, ci.Population " +
                                "FROM city ci " +
                                "JOIN country co ON ci.CountryCode = co.Code " +
                                "WHERE ci.ID = ?";

                try (PreparedStatement populationStmt =
                             conn.prepareStatement(populationQuery)) {

                    populationStmt.setInt(1, selectedCityId);

                    try (ResultSet result = populationStmt.executeQuery()) {

                        if (result.next()) {

                            // Display the selected city's population details
                            System.out.println(
                                    "\n----------------------------------------"
                            );
                            System.out.println("City Population Information");
                            System.out.println(
                                    "----------------------------------------"
                            );

                            System.out.printf("City: %s%n",
                                    result.getString("CityName"));

                            System.out.printf("Country: %s%n",
                                    result.getString("CountryName"));

                            System.out.printf("District: %s%n",
                                    result.getString("District"));

                            System.out.printf("Population: %,d%n",
                                    result.getLong("Population"));

                        } else {
                            System.out.println(
                                    "No population information was found."
                            );
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Report 32 - Displays population information for
     * Chinese, English, Hindi, Spanish and Arabic.
     * <p>
     * The database stores the percentage of each country's
     * population that speaks a particular language.
     * We use those percentages to estimate the total number
     * of speakers and their share of the world population.
     */
    public static void report32(String dbUrl, String dbUser,
                                String dbPassword) {

        System.out.println("\n----------------------------------------");
        System.out.println("Report 32 - Language Population Report");
        System.out.println("----------------------------------------\n");

        /*
         * Calculate the number of speakers for each language.
         *
         * Chinese has several entries in the database, so
         * we group languages containing "Chinese" together.
         *
         * The results are sorted from the most spoken
         * language to the least spoken.
         */
        String sqlQuery = """
                SELECT
                    CASE
                        WHEN cl.Language LIKE '%Chinese%'
                        THEN 'Chinese'
                        ELSE cl.Language
                    END AS LanguageName,

                    ROUND(
                        SUM(
                            co.Population * cl.Percentage / 100.0
                        )
                    ) AS Speakers,

                    ROUND(
                        100.0 * SUM(
                            co.Population * cl.Percentage / 100.0
                        ) / (
                            SELECT SUM(Population)
                            FROM country
                        ),
                        2
                    ) AS WorldPercentage

                FROM countrylanguage cl

                JOIN country co
                    ON cl.CountryCode = co.Code

                WHERE cl.Language LIKE '%Chinese%'
                   OR cl.Language IN (
                       'English',
                       'Hindi',
                       'Spanish',
                       'Arabic'
                   )

                GROUP BY
                    CASE
                        WHEN cl.Language LIKE '%Chinese%'
                        THEN 'Chinese'
                        ELSE cl.Language
                    END

                ORDER BY Speakers DESC
                """;

        // Connect to MySQL and run the language report
        try (Connection conn = DriverManager.getConnection(
                dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = stmt.executeQuery()) {

            // Print the headings for the language report
            System.out.printf("%-15s %20s %20s%n",
                    "Language",
                    "Estimated Speakers",
                    "World Population %");

            System.out.println(
                    "------------------------------------------------------------"
            );

            int rowCount = 0;

            // Display each language and its calculated population
            while (rs.next()) {

                String language = rs.getString("LanguageName");
                long speakers = rs.getLong("Speakers");
                double percentage = rs.getDouble("WorldPercentage");

                System.out.printf("%-15s %,20d %19.2f%%%n",
                        language,
                        speakers,
                        percentage);

                rowCount++;
            }

            // Let the user know if the query returned no results
            if (rowCount == 0) {
                System.out.println(
                        "No language information was found."
                );
            }

            /*
             * These numbers come from the historical world database.
             * They are estimates rather than current population figures.
             */
            System.out.println(
                    "\nSpeaker counts are estimates based on the " +
                            "database's language percentages."
            );

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

}
