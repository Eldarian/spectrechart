package com.eldarian.connectionHandler;

import java.io.*;
import java.net.Socket;

public class DeviceConnector{
    public ClientRequest request;

    public DeviceConnector(String address, int port, File file) {
        try (
                Socket socket = new Socket(address, port);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                FileWriter toFile = new FileWriter(file)) {
            System.out.println("Connected");
            while (true) {
                String line = in.readUTF();
                System.out.println(line);
                toFile.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getSingleChannel(int number) {
        out.writeUTF("get"+number);
    }
}
