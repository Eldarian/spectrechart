package com.eldarian;

import com.eldarian.channels.DatasetService;
import com.eldarian.connectionHandler.SocketMode;
import com.eldarian.connectionHandler.SocketConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static DatasetService datasetService = new DatasetService();
    public static volatile SocketMode mode = SocketMode.PEAKS;
    public static SocketConnector socketConnector = new SocketConnector();
    public static ConnectionDisplayState connectionState = ConnectionDisplayState.DISCONNECTED;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("peaksView"));
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> System.exit(0));
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource( "views/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }


}