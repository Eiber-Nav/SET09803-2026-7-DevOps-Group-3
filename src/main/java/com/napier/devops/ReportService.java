package com.napier.devops;

public class ReportService {

/*
* Report functions have not been implemented yet.
* For now, selecting any report will display
* a message in the console letting the user know it is in development.
 */
    public static void runReport(ReportMenu.ReportSelection selection) {
        int reportNumber = selection.getReportNumber();
        System.out.println("\n----------------------------------------");
        System.out.println("Report " + reportNumber + " is still in development.");
        System.out.println("----------------------------------------\n");
    }}