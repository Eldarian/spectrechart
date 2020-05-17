package com.eldarian;

// A Java program for a Server
import java.net.*;
import java.io.*;

public class Server
{
    //initialize socket and input stream
    private ServerSocket serverSocket = null;
    // constructor with port


    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
        System.out.println("Server stopped");
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            // starts server and waits for a connection
            try
            {
                System.out.println("Client accepted");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                String line;

                double x = 0.0;
                while (true)
                {
                    try
                    {
                        out.flush();
                        line = in.readLine();
                        if (line == null) break;
                        if (line.equals("channel1")) {
                            System.out.println("sending channel 1");
                            do {
                                String msg = "" + Math.round(Math.sin(x) * 4);
                                System.out.println(msg);
                                out.write(msg, 0, msg.length());
                                out.newLine();
                                out.flush();
                                x++;
                                line = in.readLine();
                                if (line == null) break;
                            } while(line.equals("ok"));
                        }
                        if (line.equals("channel5")) {
                            System.out.println("sending channel 5");
                            do {
                                String msg = "" + Math.round(Math.sin(x) * 5);
                                System.out.println(msg);
                                out.write(msg, 0, msg.length());
                                out.newLine();
                                out.flush();
                                x++;
                                line = in.readLine();
                            } while(line.equals("ok"));
                        }

                        if (line.equals("peaks")) {
                            System.out.println("sending peaks");
                            do {
                                String msg = "" + (int) (0 + Math.random()*17) + "," + (0 + Math.random()*100);
                                System.out.println(msg);
                                out.write(msg, 0, msg.length());
                                out.newLine();
                                out.flush();
                                x++;
                                line = in.readLine();
                            } while(line.equals("ok"));
                        }
                    }
                    catch(IOException i)
                    {
                        System.out.println(i);
                    }
                }
                System.out.println("Closing connection");

                // close connection
                clientSocket.close();
                in.close();
            }
            catch(IOException i) {

            }
        }

    }

    public static void main(String args[])
    {
        Server server = new Server();
        try {
            server.start(5000);
        } catch (IOException e) {

        }
    }
}
