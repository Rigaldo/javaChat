package com.javarush.task.task30.task3008;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }
    public static String readString() {
        String line = "";
        try {
            line = reader.readLine();
        }
        catch (IOException e) {
            writeMessage("Error occur. Try again.");
            return readString();
        }
        return line;
    }
    public static int readInt() {
        int result;
        try {
            result = Integer.parseInt(readString());
        }
        catch (NumberFormatException e) {
            System.out.println("Error occur. Try again.");
            return readInt();
        }
        return result;
    }
}
