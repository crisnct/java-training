package com.example.training;

// @formatter:off
/**
 * HOW TO CREATE MSI INSTALLER ON WINDOWS
 *
 * "C:\Program Files\Eclipse Adoptium\jdk-25.0.0.36-hotspot\bin\jpackage" --name Application ^
 *          --input D:\Workspace\java-training\java14\target ^
 *          --main-jar java14-1.0.0.jar ^
 *          --type msi ^
 *          --win-dir-chooser ^
 *          --win-menu ^
 *          --win-shortcut ^
 *          --icon app.ico
 */
// @formatter:on
public class Application {

  public static void main(String[] args) {
    GreetingService g = new DefaultGreetingService();
    System.out.println(g.greet("Cristian"));
  }
}
