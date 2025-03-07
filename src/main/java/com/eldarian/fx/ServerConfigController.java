package com.eldarian.fx;

import com.eldarian.App;
import com.eldarian.ConnectionDisplayState;
import com.eldarian.connectionHandler.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ServerConfigController{

    private Timeline timeline;

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
        App.socketConnector.connect(hostTextField.getText(), portTextField.getText());
    }

    @FXML
    private void disconnectHandler() {
        App.socketConnector.shutdown();
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

    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> displayState(App.connectionState)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


}