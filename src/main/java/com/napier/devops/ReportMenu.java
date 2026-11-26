package com.napier.devops;

import java.util.Scanner;

/**
 * Displays the available population reports and handles
 * the user's selection from the main menu.
 */
public class ReportMenu {

    /**
     * Stores the report number selected by the user.
     */
    public static class ReportSelection {
        private final int reportNumber;

        /**
         * Creates a new report selection.
         *
         * @param reportNumber the report chosen by the user
         */
        public ReportSelection(int reportNumber) {
            this.reportNumber = reportNumber;
        }

        /**
         * Returns the selected report number.
         *
         * @return the selected report number
         */
        public int getReportNumber() {
            return reportNumber;
        }
    }

    /*
     * Contains all 32 reports required for the project.
     * The report numbers match the selections handled
     * by ReportServiceDev.
     */
    private static final String[] REPORTS = {
            "1. All countries in the world by population",
            "2. All countries in a continent by population",
            "3. All countries in a region by population",
            "4. Top N populated countries in the world",
            "5. Top N populated countries in a continent",
            "6. Top N populated countries in a region",
            "7. All cities in the world by population",
            "8. All cities in a continent by population",
            "9. All cities in a region by population",
            "10. All cities in a country by population",
            "11. All cities in a district by population",
            "12. Top N populated cities in the world",
            "13. Top N populated cities in a continent",
            "14. Top N populated cities in a region",
            "15. Top N populated cities in a country",
            "16. Top N populated cities in a district",
            "17. All capital cities in the world by population",
            "18. All capital cities in a continent by population",
            "19. All capital cities in a region by population",
            "20. Top N populated capital cities in the world",
            "21. Top N populated capital cities in a continent",
            "22. Top N populated capital cities in a region",
            "23. Population breakdown by continent",
            "24. Population breakdown by region",
            "25. Population breakdown by country",
            "26. Total world population",
            "27. Population of a continent",
            "28. Population of a region",
            "29. Population of a country",
            "30. Population of a district",
            "31. Population of a city",
            "32. Language report: Chinese, English, Hindi, Spanish, Arabic"
    };


    /**
     * Shows the text-based console menu and returns
     * the report chosen by the user.
     * Invalid selections are handled without closing the application.
     *
     * @param scanner the shared Scanner for user input
     * @return the selected report, or null if the user exits
     * or no further input is available
     */
    public static ReportSelection showReportMenu(Scanner scanner) {

        System.out.println("\n========================================");
        System.out.println("   World Population Reporting System    ");
        System.out.println("========================================\n");

        // Print all available reports
        for (String report : REPORTS) {
            System.out.println(report);
        }

        System.out.println("0. Exit Application");

        /*
         * Keep asking for a selection until the user enters
         * a valid report number or chooses to exit.
         */
        while (true) {

            System.out.print("\nEnter your choice (0-"
                    + REPORTS.length + "): ");

            // Exit the menu if there is no more input to read
            if (!scanner.hasNextLine()) {
                return null;
            }

            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);

                // Exit the application when 0 is selected
                if (choice == 0) {
                    return null;
                }

                // Return the selected report if the number is valid
                if (choice >= 1 && choice <= REPORTS.length) {
                    return new ReportSelection(choice);
                }

                // Handle numbers outside the available range
                System.out.println(
                        "Invalid selection. Please choose a number between 0 and "
                                + REPORTS.length + ".");

            } catch (NumberFormatException e) {

                // Handle letters, blank input and other invalid characters
                System.out.println(
                        "Invalid input. Please enter a valid whole number.");
            }
        }
    }
}
