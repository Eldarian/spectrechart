package com.eldarian;

import com.eldarian.channels.DatasetService;
import com.eldarian.connectionHandler.SocketMode;
import com.eldarian.connectionHandler.SocketConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static DatasetService datasetService = new DatasetService();
    public static volatile SocketMode mode = SocketMode.PEAKS;
    public static SocketConnector socketConnector = new SocketConnector();
    public static ConnectionDisplayState connectionState = ConnectionDisplayState.DISCONNECTED;
    private static ArrayList<String> errorList = new ArrayList<>();
    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("peaksView"));
        stage.setTitle("THz Spectrum Tools");
        stage.getIcons().add(new Image(App.class.getResourceAsStream("logoRB-big.png")));
        stage.setScene(scene);
        stage.setMinWidth(800.0);
        stage.setMinHeight(600.0);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void openErrorWindow(String message){
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/eldarian/views/ErrorView.fxml"));
        //Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setTitle("Error");
        AnchorPane root = new AnchorPane();
        Label text = new Label();
        text.setText(message);
        root.getChildren().add(text);
        stage.setScene(new Scene(root, 450, 450));
        stage.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }


}