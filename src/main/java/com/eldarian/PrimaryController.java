package com.eldarian;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PrimaryController {

    private ChartPane chartPane;

    // Перед запуском не забыть вернуть в primary.fxml  <ChartViewer fx:id="lineChartViewer" prefHeight="300.0" prefWidth="500.0"> </ChartViewer>

    @FXML
    private ChartViewer lineChartViewer;

    @FXML
    private TextField filePath;

    @FXML
    private MenuButton channelsChooser;

    private XYSeriesCollection dataset;

    @FXML
    private void getFromFile() throws IOException {
        File file = new File(filePath.getText());
        //File file = new File("//home//dmitry", "testData.csv");

        XYSeries fileSeries = new XYSeries("series-" + (dataset.getSeriesCount()+1));
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Scanner scanner = new Scanner(reader.readLine());
            scanner.useDelimiter(",");
            scanner.useLocale(Locale.ENGLISH);

            double x = 0;
            while(scanner.hasNext()) {
                fileSeries.add(x, scanner.nextDouble());
                x++;
            }
        }
        dataset.addSeries(fileSeries);
    }

    private void openSettingsWindow() {
        Stage sourceWindow = new Stage();
        sourceWindow.setTitle("Settings");
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void initialize() {
        insertChart();
        autoShow();
    }

    @FXML
    public void autoShow() {
        channelsChooser.setOnAction(Event::consume);
    }

    private void insertChart() {
        dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        this.lineChartViewer.setChart(chart);
    }


    private static XYSeriesCollection createDataset() {
        XYSeries channel1 = new XYSeries("series-1");
        for (double x = 0; x < 60; x+=0.5) {
            channel1.add(x, Math.sin(x)*4);
        }

        XYSeries channel2 = new XYSeries("series-2");
        for (int x = 0; x < 10; x++) {
            channel2.add(x, x + Math.random() * 4.0);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(channel1);
        dataset.addSeries(channel2);
        return dataset;
    }


    private static JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "", "X", "Y", dataset);
        String fontName = "Palatino";
        chart.getTitle().setFont(new Font(fontName, Font.BOLD, 18));
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.BLACK);
        XYItemRenderer r = plot.getRenderer();
        if(r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setDefaultShapesVisible(false);
            renderer.setDrawSeriesLineAsPath(true);
            renderer.setAutoPopulateSeriesStroke(false);
            renderer.setDefaultStroke(new BasicStroke(3.0f));
            renderer.setSeriesPaint(0, Color.RED);
            renderer.setSeriesLinesVisible(0, true);
            renderer.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
            renderer.setSeriesShapesVisible(0, true);
            renderer.setSeriesShapesFilled(0, true);
            renderer.setSeriesPaint(1, Color.BLUE);
        }
        return chart;
    }


}
