package com.eldarian.connectionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class FxSocketClient extends GenericSocket implements SocketListener {

    public String host;
    private SocketListener fxListener;

    @Override
    protected void initSocketConnection() throws SocketException {
        try {
            socketConnection = new Socket();
            /*
             * Allows the socket to be bound even though a previous
             * connection is in a timeout state.
             */
            socketConnection.setReuseAddress(true);
            /*
             * Create a socket connection to the server
             */
            socketConnection.connect(new InetSocketAddress(host, port));
            if (debugFlagIsSet(Constants.instance().DEBUG_STATUS)) {
                System.out.println("Connected to " + host
                        + "at port " + port);
            }
        } catch (IOException e) {
            if (debugFlagIsSet(Constants.instance().DEBUG_EXCEPTIONS)) {
                e.printStackTrace();
            }
            throw new SocketException();
        }
    }

    @Override
    protected void closeAdditionalSockets() {

    }

    @Override
    public void onMessage(String line) {
        javafx.application.Platform.runLater(() -> fxListener.onMessage(line));
    }

    @Override
    public void onClosedStatus(boolean isClosed) {
        javafx.application.Platform.runLater(() -> fxListener.onClosedStatus(isClosed));
    }


    public FxSocketClient(SocketListener fxListener,
                          String host, int port, int debugFlags) {
        super(port, debugFlags);
        this.host = host;
        this.fxListener = fxListener;
    }

    public FxSocketClient(SocketListener fxListener) {
        this(fxListener, Constants.instance().DEFAULT_HOST,
                Constants.instance().DEFAULT_PORT,
                Constants.instance().DEBUG_NONE);
    }

    public FxSocketClient(SocketListener fxListener,
                          String host, int port) {
        this(fxListener, host, port, Constants.instance().DEBUG_NONE);
    }
}
