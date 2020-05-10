package com.eldarian.fx;

import com.eldarian.connectionHandler.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ServerConfigController{

    @FXML
    private TextField portTextField;

    @FXML
    private TextField hostTextField;

    @FXML
    private Label statusBar;

    @FXML
    private Button connectButton;

    @FXML
    private Button disconnectButton;

    @FXML
    private void connectHandler() {

    }

    @FXML
    private void disconnectHandler() {

    }

    public enum ConnectionDisplayState {
        DISCONNECTED, ATTEMPTING, CONNECTED, AUTOCONNECTED, AUTOATTEMPTING
    }

    private void displayState(ConnectionDisplayState state) {
        switch (state) {
            case DISCONNECTED:
                connectButton.setDisable(false);
                disconnectButton.setDisable(true);
                statusBar.setText("Not connected");
                break;
            case ATTEMPTING:
            case AUTOATTEMPTING:
                connectButton.setDisable(true);
                disconnectButton.setDisable(true);
                statusBar.setText("Attempting connection");
                break;
            case CONNECTED:
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
                statusBar.setText("Connected");
                break;
            case AUTOCONNECTED:
                connectButton.setDisable(true);
                disconnectButton.setDisable(true);
                statusBar.setText("Connected");
                break;
        }
    }

    private FxSocketClient socket; //will it safe after closing the window?
    private boolean connected;



}