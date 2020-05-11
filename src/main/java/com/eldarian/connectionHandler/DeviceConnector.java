package com.eldarian.connectionHandler;

import com.eldarian.App;

import java.io.*;
import java.net.Socket;

public class DeviceConnector implements Runnable {
    private String address;
    private int port;
    private volatile ClientRequest mode = App.mode;
    public volatile String dataFromServer;
    private int currentChannel;

    public DeviceConnector(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void run() {
        try (Socket socket =
                     new Socket(address, port);
             DataInputStream in =
                     new DataInputStream(socket.getInputStream());
             DataOutputStream out =
                     new DataOutputStream(socket.getOutputStream()))
        {
            System.out.println("Connected");
            while(mode != ClientRequest.DISCONNECT) {
                System.out.println(mode);
                if (mode == ClientRequest.CHANNEL) {
                    getSingleChannel(mode.currentChannel, out);
                    while (mode == ClientRequest.CHANNEL && mode.currentChannel==currentChannel) {
                        dataFromServer = in.readUTF();
                        wait();
                        out.writeUTF("ok");
                    }
                }
                if (mode == ClientRequest.PEAKS) {
                    while (mode == ClientRequest.PEAKS) {
                        dataFromServer = in.readUTF();
                        wait();
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            System.out.println("Disconnected");
        }
    }


    private void getSingleChannel(int number, DataOutputStream out) throws IOException {
        currentChannel = number;
        System.out.println("channel" + number);
        out.writeUTF("channel"+number);
    }
}
