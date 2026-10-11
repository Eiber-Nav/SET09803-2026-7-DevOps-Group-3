package com.napier.devops;

import javax.swing.*;
import java.awt.*;

public class ReportMenu {

/*
* Stores the report number selected by the user.
* More information can be added here later when
* the reports are connected to the database.
*/
    public static class ReportSelection {
        private final int reportNumber;
        public ReportSelection(int reportNumber) {
            this.reportNumber = reportNumber;
        }
        public int getReportNumber() {
            return reportNumber;
        } }

/*
* These are the reports required for the project.
* They are shown in the menu even though the
* report functions are still being developed.
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
 * Shows the main report menu and returns
 * the report chosen by the user.
 */
    public static ReportSelection showReportMenu() {
        JComboBox<String> reportBox =
                new JComboBox<>(REPORTS);
        reportBox.setPreferredSize(
                new Dimension(500, 30)
        );
        JPanel panel = new JPanel();
        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel heading =
                new JLabel(
                        "World Population Reporting System"
                );

        heading.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        panel.add(heading);
        panel.add(
                Box.createVerticalStrut(15)
        );

        panel.add(
                new JLabel("Choose a report:")
        );

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(reportBox);

        Object[] buttons = {
                "Run Report",
                "Exit"
        };

        int option =
                JOptionPane.showOptionDialog(
                        null,
                        panel,
                        "Population Reports",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        buttons,
                        buttons[0]
                );

// Close the application if Exit is selected
        if (option != 0) {
            return null;
        }
        int reportNumber =
                reportBox.getSelectedIndex() + 1;
        return new ReportSelection(
                reportNumber
        );
    }}