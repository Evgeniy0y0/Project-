package org.example;

/**
 * The Main class serves as the entry point for the application.
 * This class is marked as public to ensure it is visible in the generated JavaDoc.
 * * @author Kostiantyn
 * @version 1.0
 */
public class Main {

    /**
     * Default constructor for the Main class.
     * Explicitly declaring a public constructor ensures its visibility in documentation.
     */
    public Main() {}

    /**
     * A test method to verify the automation of JavaDoc deployment.
     * This method will appear in the 'Method Summary' section of the documentation.
     */
    public void testMethod() {
        System.out.println("Method executed successfully");
    }

    /**
     * The main entry point of the program.
     * * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        System.out.println("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}