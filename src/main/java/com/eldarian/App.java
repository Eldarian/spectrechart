package com.eldarian;

import com.eldarian.channels.Channel;
import com.eldarian.channels.ChannelService;
import com.eldarian.connectionHandler.ClientRequest;
import com.eldarian.connectionHandler.DeviceConnector;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    public static ChannelService channelService;

    public static volatile ClientRequest mode = ClientRequest.PEAKS;

    public static Thread device;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("histogramView"));
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

    public static void main(String[] args)
    {
        device = new Thread(new DeviceConnector("127.0.0.1", 5000));
        device.start();
        launch();
    }

}