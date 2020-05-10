package com.eldarian.connectionHandler;

import com.eldarian.App;
import com.eldarian.fx.ServerConfigController;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import static com.eldarian.connectionHandler.ClientRequest.PEAKS;

public class SocketConnector {
    private FxSocketClient socket; //will it safe after closing the window?
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
    private synchronized void waitForDisconnect() {
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
    private synchronized void notifyDisconnected() {
        connected = false;
        notifyAll();
    }

    /*
     * Synchronized method to set isConnected boolean
     */
    private synchronized void setIsConnected(boolean connected) {
        this.connected = connected;
    }

    /*
     * Synchronized method to check for value of connected boolean
     */
    private synchronized boolean isConnected() {
        return (connected);
    }

    private void connect(String host, String port) {
        socket = new FxSocketClient(new FxSocketListener(),
                host,
                Integer.parseInt(port),
                Constants.instance().DEBUG_NONE);
        socket.connect();
    }

    class ShutDownThread extends Thread {

        @Override
        public void run() {
            if (socket != null) {
                if (socket.debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                    LOGGER.info("ShutdownHook: Shutting down Server Socket");
                }
                socket.shutdown();
            }
        }
    }

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            if (line != null && !line.equals("")) {
                App.channelService.handleData(line);
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
