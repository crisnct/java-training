package com.example.training;

import java.util.Formatter;
import java.util.Scanner;

public class ScannerFormatterDemo {

    public static void main(String[] args) {
        String rawInput = "Alice 10 20 30";

        Scanner scanner = new Scanner(rawInput);
        String name = scanner.next();
        int first = scanner.nextInt();
        int second = scanner.nextInt();
        int third = scanner.nextInt();
        scanner.close();

        int sum = first + second + third;

        Formatter formatter = new Formatter();
        formatter.format("Name: %s, values: %d, %d, %d, sum = %d%n", name, first, second, third, sum);
        System.out.print(formatter);
        formatter.close();

        System.out.printf("Average for %s is %.2f%n", name, (sum / 3.0));
    }
}
