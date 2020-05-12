package com.eldarian.fx;

import java.io.IOException;

import com.eldarian.App;
import com.eldarian.connectionHandler.SocketMode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;

public class PeaksController {

    @FXML
    private ChartViewer histogramChartViewer;

    @FXML
    private void switchToCalibrationView() throws IOException {
        App.mode = SocketMode.FREEZED;
        System.out.println(App.mode.currentChannel);
        App.setRoot("calibrationView");
    }

    @FXML
    private void startDraw() {
        App.mode = SocketMode.PEAKS;
        App.datasetService.clearPeaks();
        createXYBar();
        String msg = "peaks";
        System.out.println("Requesting " + msg);
        App.socketConnector.sendMessage(msg);
    }

    @FXML
    private void freezeDraw() {
            App.mode = SocketMode.FREEZED;
    }

    @FXML
    private void openSettingsWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/eldarian/views/settingsView.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setTitle("My New Stage Title");
        stage.setScene(new Scene(root, 450, 450));
        stage.showAndWait();
    }

    @FXML
    private void initialize() {
        createXYBar();
    }

    private  void createXYBar() {
        JFreeChart barChart = ChartFactory.createBarChart("barchart",
                                                "channels",
                                                    "peaks",
                                            App.datasetService.peaksDataset);
        histogramChartViewer.setChart(barChart);
    }
}
