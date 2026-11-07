================================================================================
KINGSTON UNIVERSITY LONDON / ESOFT METRO CAMPUS
Module Code : CI6115 - Programming III: Patterns and Algorithms
Assessment  : SmartStay Hotel Booking System - Object-Oriented Design and Implementation
Student Name: W. Chiran Shamilka Fernando
Student ID  : 2703047 (Prefix: K2703047_)
================================================================================

PROJECT STRUCTURE:
------------------
- SmartStay_Report_2703047.docx : Official submission report document with all coversheets,
                                 diagrams, code proofs, UI proofs, test cases, and source code.
- SmartStay_Report_2703047.pdf  : Official converted PDF report.
- SmartStay_2703047.jar         : Standalone executable JAR (ready to run with single click).
- RUN_GUI.bat                   : One-click launcher for Desktop Swing GUI.
- RUN_CLI.bat                   : One-click launcher for Terminal CLI.
- RUN_TESTS.bat                 : One-click launcher for 22-case automated test suite.
- src/                          : Full Java source code (all prefixed with K2703047_).
- bin/                          : Compiled class files.
- docs/                         : Markdown documentation, PlantUML source, and image proofs.
- docs/images/                  : 13 high-resolution diagram, code proof, and UI proof images.
- invoices/                     : Generated customer invoices.

HOW TO RUN:
-----------
Method 1 (Double Click):
  - Simply double-click "RUN_GUI.bat" to start the Desktop GUI.
  - Or double-click "RUN_TESTS.bat" to run the automated test suite.

Method 2 (Command Line):
  - java -jar SmartStay_2703047.jar         (Starts GUI + CLI dual mode)
  - java -jar SmartStay_2703047.jar --gui   (Starts GUI mode)
  - java -jar SmartStay_2703047.jar --cli   (Starts CLI mode)
  - java -jar SmartStay_2703047.jar --test  (Executes 22 automated test cases)

Method 3 (Recompile from Raw Source):
  - javac -d bin -encoding UTF-8 (Get-ChildItem -Path src -Recurse -Filter *.java)
  - java -cp bin k2703047.smartstay.K2703047_Main
================================================================================
