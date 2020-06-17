package com.eldarian.fx;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ErrorViewController {

    @FXML
    private Label message;

    @FXML
    private void closeScene() {

    }

    public void setMessage(String messageText) {
        message.setText(messageText);
    }
}
