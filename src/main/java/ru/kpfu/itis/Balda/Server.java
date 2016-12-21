package ru.kpfu.itis.Balda;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by katemrrr on 2.12.16.
 */

public class Server {
    public static void main(String[] args) throws IOException {

        final int PORT = 3456;

        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Starting server...");

        while (true) {
            Socket clientOne = s.accept();
            System.out.println("First connected");

            Socket clientTwo = s.accept();
            System.out.println("Second connected");

            new Connection(clientOne, clientTwo);
        }
    }
}
