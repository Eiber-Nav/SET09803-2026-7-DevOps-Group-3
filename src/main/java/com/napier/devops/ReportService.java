package com.napier.devops;

import javax.swing.*;

public class ReportService {

/*
 * Report functions have not been implemented yet.
 * For now, selecting any report will display
 * a message letting the user know it is in development.
 */
    public static void runReport(
            ReportMenu.ReportSelection selection) {
        int reportNumber =
                selection.getReportNumber();
        JOptionPane.showMessageDialog(
                null,
                "Report " + reportNumber +
                        " is still in development.",
                "Report In Development",
                JOptionPane.INFORMATION_MESSAGE
        );
    }}