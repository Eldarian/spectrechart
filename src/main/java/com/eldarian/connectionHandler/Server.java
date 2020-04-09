package com.eldarian.connectionHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    //initialize socket and input stream
    private Socket socket   = null;
    private ServerSocket server   = null;
    private DataInputStream in       =  null;
    private DataOutputStream out = null;

    // constructor with port
    public Server(int port)
    {
        // starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(
                    new BufferedOutputStream(socket.getOutputStream()));

            String line = "";

            // reads message from client until "Over" is sent
            double x = 0.0;
            while (x < 60.0)
            {
                try
                {
                    out.flush();
                    //line = in.readUTF();
                    System.out.println(line);
                    String outline = Math.round(Math.sin(x)*4) + ",";
                    out.writeUTF(outline);
                    x++;

                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");

            // close connection
            socket.close();
            in.close();
        }
        catch(IOException i)
        {
            //System.out.println(i);
        }
    }

    public static void main(String args[])
    {
        Server server = new Server(5000);
    }
}
