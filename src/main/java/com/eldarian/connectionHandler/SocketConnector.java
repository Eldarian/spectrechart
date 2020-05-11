package com.eldarian.connectionHandler;

import com.eldarian.App;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

public class SocketConnector {
    private FxSocketClient fxSocket;
    private boolean connected;

    private final static Logger LOGGER
            = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());


    public SocketConnector() {
            setIsConnected(false);
            Runtime.getRuntime().addShutdownHook(new ShutDownThread());
    }

    /*
     * Synchronized method set up to wait until there is no socket connection.
     * When notifyDisconnected() is called, waiting will cease.
     */
    public synchronized void waitForDisconnect() {
        while (connected) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    /*
     * Synchronized method responsible for notifying waitForDisconnect()
     * method that it's OK to stop waiting.
     */
    public synchronized void notifyDisconnected() {
        connected = false;
        notifyAll();
    }

    /*
     * Synchronized method to set isConnected boolean
     */
    public synchronized void setIsConnected(boolean connected) {
        this.connected = connected;
    }

    /*
     * Synchronized method to check for value of connected boolean
     */
    public synchronized boolean isConnected() {
        return (connected);
    }

    public void connect(String host, String port) {
        fxSocket = new FxSocketClient(new FxSocketListener(),
                host,
                Integer.parseInt(port),
                Constants.instance().DEBUG_NONE);
        fxSocket.connect();
    }

    public void sendMessage(String msg) {
        fxSocket.sendMessage(msg);
    }

    class ShutDownThread extends Thread {

        @Override
        public void run() {
            if (fxSocket != null) {
                if (fxSocket.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                    LOGGER.info("ShutdownHook: Shutting down Server Socket");
                }
                fxSocket.shutdown();
            }
        }
    }

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            if (line != null && !line.equals("")) {
                try {
                    App.channelService.handleData(line);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onClosedStatus(boolean isClosed) {
            if (isClosed) {
                notifyDisconnected();

            } else {
                setIsConnected(true);
            }
        }
    }
}
