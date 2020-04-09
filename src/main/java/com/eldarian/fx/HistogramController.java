package com.eldarian.fx;

import java.io.IOException;
import java.util.Random;

import com.eldarian.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class HistogramController {

    @FXML
    private ChartViewer histogramChartViewer;

    @FXML
    private void switchToCalibrationView() throws IOException {
        App.setRoot("calibrationView");
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
        //createHistogram();
        createXYBar();
    }

    private  void createXYBar() {
        XYSeries series = new XYSeries("");
        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();

        for (int x = 1; x<=16; x++) {
            categoryDataset.addValue(0, "", ""+x);
        }
        JFreeChart barChart = ChartFactory.createBarChart("barchart",
                                                "channels",
                                                    "peaks",
                                                                categoryDataset);
        histogramChartViewer.setChart(barChart);
    }

    private void createHistogram() {
        double[] value = new double[100];
        Random generator = new Random();
        for (int i = 1; i < 100; i++) {
            value[i] = generator.nextDouble()*10;
            int number = 16;
            HistogramDataset dataset = new HistogramDataset();
            dataset.setType(HistogramType.FREQUENCY);
            dataset.addSeries("Histogram", value, number);
            String plotTitle = "Histogram";
            String xaxis = "number";
            String yaxis = "value";
            PlotOrientation orientation = PlotOrientation.VERTICAL;
            boolean show = false;
            boolean toolTips = false;
            boolean urls = false;
            JFreeChart histogram = ChartFactory.createHistogram(plotTitle, xaxis, yaxis,
                    dataset, orientation, show, toolTips, urls);
            int width = 500;
            int height = 300;
            this.histogramChartViewer.setChart(histogram);
        }


    }
}
