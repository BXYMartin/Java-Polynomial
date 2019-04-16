package poly;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // System.out.println("Welcome to the polynomial homework...");
        Scanner inputScanner = new Scanner(System.in);

        String targetPolyString = null;

        // Catch potential exceptions
        try {
            // Get line and remove any blank element
            targetPolyString = inputScanner.nextLine();
        } catch (Exception e) {
            System.out.println("WRONG FORMAT!");
            System.exit(0);
        }

        // Begin processing the poly
        // Instantiate the Poly Object
        Polynomial targetPoly = new Polynomial(targetPolyString);
        // Switch debug button
        //targetPoly.setDebug();
        // Check format
        try {
            if (targetPoly.checkFormat()) {
                System.out.println(targetPoly.getResult());
            } else {
                System.out.println("WRONG FORMAT!");
            }
        } catch (Throwable t) {
            System.out.println("STACK OVERFLOW!");
        }

        inputScanner.close();
    }

}
