package com.napier.devops;

import java.util.Scanner;

public class ReportMenu {

/*
* Stores the report number selected by the user.
*/
    public static class ReportSelection {
        private final int reportNumber;
        public ReportSelection(int reportNumber) {
            this.reportNumber = reportNumber;
        }
        public int getReportNumber() {
            return reportNumber;
        }}

/*
* These are the reports required for the project.
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
            "25. Population breakdown by country"
    };

/*
* Shows the text-based console menu and returns
* the report chosen by the user, or null if they exit.
*/
    public static ReportSelection showReportMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n========================================");
        System.out.println("   World Population Reporting System    ");
        System.out.println("========================================\n");

// Print all available reports
        for (String report : REPORTS) {
            System.out.println(report);
        }
        System.out.println("0. Exit Application");
        System.out.print("\nEnter your choice (0-25): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                return null; // Exit selected
            }
            if (choice >= 1 && choice <= REPORTS.length) {
                return new ReportSelection(choice);
            } else {
                System.out.println("Invalid selection. Please choose a number between 0 and " + REPORTS.length + ".");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
        return null;
    }}