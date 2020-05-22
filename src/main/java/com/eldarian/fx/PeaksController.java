package com.eldarian.fx;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;

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
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;

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
        stage.setTitle("Settings");
        stage.setScene(new Scene(root, 450, 450));
        stage.showAndWait();
    }

    @FXML
    private void initialize() {
        createXYBar();
    }

    private  void createXYBar() {
        JFreeChart barChart = ChartFactory.createBarChart("",
                                                "channels",
                                                    "voltage",
                                            App.datasetService.peaksDataset);
        barChart.setBackgroundPaint(new Color(67, 67, 67));
        CategoryPlot barPlot = (CategoryPlot) barChart.getPlot();
        CategoryAxis barAxis = barPlot.getDomainAxis();
        ValueAxis sndAxis = barPlot.getRangeAxis();

        Color axisColor = Color.white;

        sndAxis.setLabelPaint(axisColor);
        sndAxis.setTickLabelPaint(axisColor);
        sndAxis.setAxisLinePaint(axisColor);
        sndAxis.setTickMarkPaint(axisColor);

        barAxis.setLabelPaint(axisColor);
        barAxis.setTickLabelPaint(axisColor);
        barAxis.setAxisLinePaint(axisColor);
        barAxis.setTickMarkPaint(axisColor);

        barPlot.setBackgroundPaint(Color.black);
        BarRenderer barRenderer = (BarRenderer) barPlot.getRenderer();


        CategoryItemLabelGenerator labelGenerator = new StandardCategoryItemLabelGenerator(
                "{2}", new DecimalFormat("0.00"));
        barRenderer.setDefaultItemLabelGenerator(labelGenerator);
        barRenderer.setDefaultItemLabelPaint(Color.white);
        barRenderer.setDefaultItemLabelsVisible(true);

        barRenderer.setBarPainter(new StandardBarPainter());
        barRenderer.setSeriesPaint(0, Color.orange);

        histogramChartViewer.setChart(barChart);
    }
}
