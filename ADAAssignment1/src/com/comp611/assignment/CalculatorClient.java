/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.comp611.assignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alex
 */
public class CalculatorClient {

    public static final String HOST_NAME = "localhost";
    public static final int HOST_PORT = 7777; // host port number

    private Socket socket;

    private WriteThread writeThread;
    private ReadThread readThread;

    public CalculatorClient() {

    }

    public void startClient() {
        socket = null;

        try {
            socket = new Socket(HOST_NAME, HOST_PORT);
        } catch (IOException e) {
            System.err.println("Client could not make connection: " + e);
            System.exit(-1);
        }

        PrintWriter pw; // output stream to server
        BufferedReader br; // input stream from server

        try {  // create an autoflush output stream for the socket

            // create a buffered input stream for this socket
            br = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            writeThread = new WriteThread();
            readThread = new ReadThread(br);
            Thread thread1 = new Thread(writeThread);
            Thread thread2 = new Thread(readThread);
            thread1.start();
            thread2.start();
        } catch (IOException e) {
            System.err.println("Client error with game: " + e);
        }

    }

    public void stop() {
        readThread.stopRead();
        writeThread.stopWrite();
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(CalculatorClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        CalculatorClient client = new CalculatorClient();
        client.startClient();
    }

    private class ReadThread implements Runnable {

        private BufferedReader br;
        private boolean stopRequested;

        public ReadThread(BufferedReader br) {
            this.br = br;
            this.stopRequested = false;
        }

        public void stopRead() {
            this.stopRequested = true;
        }

        @Override
        public void run() {
            while (!stopRequested) {
                String serverResponse = "";
                try {
                    serverResponse = br.readLine();
                    if (serverResponse != null) {
                        System.out.println(serverResponse);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CalculatorClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculatorClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class WriteThread implements Runnable {

        private boolean stopRequested;

        public WriteThread() {
            this.stopRequested = false;
        }

        public void stopWrite() {
            this.stopRequested = true;
        }

        @Override
        public void run() {
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                Scanner scan = new Scanner(System.in);
                while (!stopRequested) {
                    String request = scan.nextLine();
                    
                    pw.println(request);
                    
                    if (request.equals("QUIT")) {
                        stop();
                    }
                }
                pw.close();
                scan.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculatorClient.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
