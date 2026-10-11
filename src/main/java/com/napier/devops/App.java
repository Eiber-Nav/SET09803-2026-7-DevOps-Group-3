package com.napier.devops;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {

// Load the MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not load the MySQL driver.",
                    "Driver Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

// Try to connect to the world database
        try (
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/world?useSSL=false&allowPublicKeyRetrieval=true",
                        "root",
                        "D@to0000"
                )
        ) {

// Show clearly that the database connection worked
            System.out.println(
                    "Successfully connected to the world database."
            );
            JOptionPane.showMessageDialog(
                    null,
                    "Database Connecting..\n\n" +
                            "Connected to world db",
                    "Database Report",
                    JOptionPane.INFORMATION_MESSAGE
            );

/*
* Once the database connection is confirmed,
* keep showing the report menu until the user exits.
*/
            while (true) {
                ReportMenu.ReportSelection selection =
                        ReportMenu.showReportMenu();
                if (selection == null) {
                    break;
                }

/*
* Reports are not implemented yet.
* This currently only shows the
* development message.
*/
                ReportService.runReport(selection);
            }
            System.out.println(
                    "Application closed."
            );
        } catch (SQLException e) {

// Show clearly if the database connection fails
            System.out.println(
                    "Database connection failed."
            );
            JOptionPane.showMessageDialog(
                    null,
                    "Database connection failed.\n\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }}}