/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.comp611.assignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

/**
 *
 * @author alex
 */
public class CalculatorServer {

    private boolean stopRequested;
    private final ThreadPool threadPool;

    public static final int PORT = 7777; // some unused port number

    public CalculatorServer() {
        this.stopRequested = false;
        this.threadPool = new ThreadPool(10);
    }

    public void startServer() {
        stopRequested = false;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started at "
                    + InetAddress.getLocalHost() + " on port " + PORT);
        } catch (IOException e) {
            System.err.println("Server can't listen on port: " + e);
            System.exit(-1);
        }
        try {
            while (!stopRequested) {  // block until the next client requests a connection
                // note that the server socket could set an accept timeout
                Socket socket = serverSocket.accept();
                System.out.println("Connection made with " + socket.getInetAddress());
                // start a game with this connection, note that a server
                // might typically keep a reference to each game
                ChatConnection con = new ChatConnection(socket);
                Thread thread = new Thread(con);
                thread.start();
            }
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Can't accept client connection: " + e);
        }
        System.out.println("Server finishing");
    }

    // stops server AFTER the next client connection has been made
    // (since this server socket doesn't timeout on client connections)
    public void requestStop() {
        stopRequested = true;
    }

    public static void main(String[] args) {
        CalculatorServer server = new CalculatorServer();
        server.startServer();
    }

    private class ChatConnection implements Runnable {

        private final Socket socket;
        private PrintWriter pw;

        public ChatConnection(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            // output stream to client
            BufferedReader br; // input stream from client
            try {  // create an autoflush output stream for the socket
                pw = new PrintWriter(socket.getOutputStream(), true);
                // create a buffered input stream for this socket
                br = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));

                pw.println("Connected to online calculator.");
                pw.println("Please a simple calculation in postfix format: ");
                pw.println("The opperands are: +, -, x, /\n");
                pw.println("Enter QUIT to quit");

                String response = "";

                do {
                    String clientBroadcast = br.readLine();
                    response = clientBroadcast;

                    //task?
                    if (response != null && !response.equals("QUIT")) {
                        String[] parts = response.split(" ");

                        CalculateTask<String, String> task = new CalculateTask<>(parts, pw, clientBroadcast);
                        task.addListener((observer) -> pw.println(observer));
                        threadPool.perform(task);
                    }

                } while (response == null || !response.equals("QUIT"));
                br.close();
                pw.close();
                System.out.println("Closing connection with " + socket.getInetAddress());
            } catch (IOException e) {
                System.err.println("Server error with game: " + e);
            }
        }

        //returns null if string doesn't match
        //task?
        public void sendMessage(String message) {
            pw.println(message);
        }
    }

    private class CalculateTask<E,F> extends Task<E,F> {

        private PrintWriter pw;
        private String[] parts;
        private String postfix;

        public CalculateTask(String[] postfix, PrintWriter pw, E original) {
            super(original);
            this.pw = pw;
            this.parts = (String[]) postfix;
            this.postfix = (String) original;
        }

        @Override
        public void run() {
            Stack<Double> operands = new Stack<Double>();
            boolean incorrectExpression = false;
            int count = 0;

            for (String str : parts) {
                if (!incorrectExpression) {
                    if (str.trim().equals("")) {
                        continue;
                    }

                    switch (str) {
                        case "+":
                        case "-":
                        case "*":
                        case "/":
                            if (operands.isEmpty() || operands.size() == 1) {
                                break;
                            }
                            double right = operands.pop();
                            double left = operands.pop();
                            double value = 0;
                            switch (str) {
                                case "+":
                                    value = left + right;
                                    count++;
                                    break;
                                case "-":
                                    value = left - right;
                                    count++;
                                    break;
                                case "*":
                                    value = left * right;
                                    count++;
                                    break;
                                case "/":
                                    value = left / right;
                                    count++;
                                    break;
                                default:
                                    break;
                            }
                            operands.push(value);
                            break;
                        default:
                            double output = checkInput(str, postfix, incorrectExpression);
                            if (output == -3569871.69) {
                                incorrectExpression = true;
                            }
                            operands.push(output);
                            break;
                    }
                }
            }

            if (!incorrectExpression && count != 0 && operands.size() == 1) {
                String output = "[" + postfix + "] = " + operands.pop();
                this.notifyAll((F) output);
            } else {
                String output = "[" + postfix + "] is not a valid postfix expression.";
                this.notifyAll((F) output);
            }
        }

        private double checkInput(String str, String original, Boolean checkInput) {

            double output = -3569871.69;

            try {
                output = Double.parseDouble(str);
            } catch (Exception e) {
                checkInput = true;
            }

            return output;
        }
    }
}
