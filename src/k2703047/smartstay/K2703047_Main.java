package k2703047.smartstay;

import k2703047.smartstay.service.K2703047_HotelSystem;
import k2703047.smartstay.test.K2703047_TestSuite;
import k2703047.smartstay.ui.K2703047_CLIView;
import k2703047.smartstay.ui.K2703047_GUIView;

import java.awt.GraphicsEnvironment;

/**
 * Kingston University Coursework - CI6115 Programming III
 * Student ID: 2703047
 * Module: Patterns and Algorithms
 * Assessment: SmartStay Hotel Booking System - Object-Oriented Design and Implementation
 * 
 * Main entry point for the SmartStay Hotel Booking System.
 * Supports:
 * - Dual Interface: Interactive Desktop GUI and Terminal CLI
 * - Flag options: --gui, --cli, --test
 */
public class K2703047_Main {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("             SMARTSTAY HOTEL BOOKING SYSTEM - SRI LANKA                         ");
        System.out.println("      Kingston University CI6115 Programming III: Patterns and Algorithms       ");
        System.out.println("                         Student ID: 2703047                                    ");
        System.out.println("================================================================================");

        K2703047_HotelSystem hotelSystem = new K2703047_HotelSystem();

        if (args.length > 0) {
            String flag = args[0].trim().toLowerCase();
            if (flag.equals("--test")) {
                System.out.println("Running comprehensive automated test suite...\n");
                K2703047_TestSuite.runAllTests();
                return;
            } else if (flag.equals("--gui")) {
                launchGui(hotelSystem);
                return;
            } else if (flag.equals("--cli")) {
                launchCli(hotelSystem);
                return;
            }
        }

        // Dual mode: If GUI is supported by environment, launch Desktop GUI and provide CLI
        boolean isHeadless = GraphicsEnvironment.isHeadless();
        if (!isHeadless) {
            System.out.println("[Notice] Launching Desktop GUI...");
            launchGui(hotelSystem);
        }

        System.out.println("[Notice] Initializing Interactive Console CLI...\n");
        launchCli(hotelSystem);
    }

    private static void launchGui(K2703047_HotelSystem hotelSystem) {
        try {
            K2703047_GUIView.launch(hotelSystem);
        } catch (Exception e) {
            System.err.println("Could not initialize GUI: " + e.getMessage());
        }
    }

    private static void launchCli(K2703047_HotelSystem hotelSystem) {
        K2703047_CLIView cli = new K2703047_CLIView(hotelSystem);
        cli.start();
    }
}
