// Specifies the package (folder namespace) this class belongs to
package org.example;

// The main class of your program. The class name must match the filename (Main.java).
public class Main {

    // The main method: The entry point for any Java program.
    // 'String[] args' accepts command-line arguments passed into the program.
    public static void main(String[] args) {

        // Prints "Hello and welcome!" to the console.
        // String.format() builds a formatted String object before sending it to output.
        System.out.println(String.format("Hello and welcome! Code is working!"));

        // A 'for' loop that repeats a block of code 5 times:
        // 1. 'int i = 1' creates a counter variable named 'i' starting at 1.
        // 2. 'i <= 5' is the condition; the loop continues running as long as 'i' is 5 or less.
        // 3. 'i++' increases the value of 'i' by 1 after each loop iteration.
        for (int i = 1; i <= 5; i++) {

            // Prints the current loop iteration value (e.g., "i = 1", "i = 2") to the console.
            System.out.println("i = " + i);
        }
    }
}