package com.example.training;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

public class SimpleHttpClient {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;

        Socket socket = null;

        try {
            socket = new Socket(host, port);
            System.out.println("Client connected to " + host + ":" + port);

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // construim cererea HTTP (GET /)
            String request =
                "GET / HTTP/1.0\r\n" +
                "Host: " + host + "\r\n" +
                "\r\n";

            out.write(request.getBytes());
            out.flush();

            // citim tot răspunsul și îl afișăm
            int data;
            StringBuffer response = new StringBuffer();
            while ((data = in.read()) != -1) {
                response.append((char) data);
            }

            System.out.println("=== HTTP RESPONSE START ===");
            System.out.println(response.toString());
            System.out.println("=== HTTP RESPONSE END ===");

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
