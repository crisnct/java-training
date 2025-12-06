package com.example.training;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Sends and receives a UDP packet locally using the classic DatagramSocket API
 * (exactly the API that got reimplemented in JDK 15)
 */
public class DatagramSocketExample {

    private static final int PORT = 9999;

    public static void main(String[] args) throws Exception {
        Thread receiver = new Thread(DatagramSocketExample::runReceiver);
        receiver.start();

        // Give the receiver a moment to bind
        Thread.sleep(200);

        runSender();

        receiver.join();
    }

    private static void runReceiver() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Receiver waiting on port " + PORT + "...");
            socket.receive(packet);

            String msg = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Receiver got: " + msg);
            System.out.println("From: " + packet.getAddress() + ":" + packet.getPort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runSender() {
        try (DatagramSocket socket = new DatagramSocket()) {
            String message = "Hello from Java 15 DatagramSocket";
            byte[] data = message.getBytes();

            InetAddress address = InetAddress.getByName("127.0.0.1");
            DatagramPacket packet = new DatagramPacket(data, data.length, address, PORT);

            System.out.println("Sender sending packet...");
            socket.send(packet);
            System.out.println("Sender done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
