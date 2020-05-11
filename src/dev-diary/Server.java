package com.eldarian;

// A Java program for a Server
import java.net.*;
import java.io.*;

public class Server
{
    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private BufferedReader in       =  null;
    private BufferedWriter out = null;

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
            /*in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(
                    new BufferedOutputStream(socket.getOutputStream()));
*/
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String line = "";

            // reads message from client until "Over" is sent
            double x = 0.0;
            while (!line.equals("Disconnect"))
            {
                try
                {
                    out.flush();
                    line = in.readLine();
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
