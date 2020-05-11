package com.eldarian.fx;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import com.eldarian.App;
import com.eldarian.channels.ChannelService;
import com.eldarian.connectionHandler.ClientRequest;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CalibrationController {

    private JFreeChart chart;
    private XYSeriesCollection dataset = App.channelService.calibrationDataset;

    @FXML
    private ChartViewer lineChartViewer;

    @FXML
    private ChoiceBox channelsChooser;


    @FXML
    private void startDraw() {
        if (channelsChooser.getValue() != null) {
            int channel = (Integer) channelsChooser.getValue();
            App.channelService.clearChannel(channel);
            App.mode.currentChannel = channel;
            String msg = "channel" + channel;
            System.out.println("Sending " + msg);
            App.socketConnector.sendMessage(msg);
        }
    }

    @FXML
    private void switchToHistogram() throws IOException {
        App.mode = ClientRequest.PEAKS;
        App.setRoot("histogramView");
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
        insertChart();
        System.out.println("chart inserted");
        autoShow();
    }

    @FXML
    private void autoShow() {
        channelsChooser.setOnAction(Event::consume);
    }

    private void insertChart() {
        //dataset = createExampleDataset();
        chart = createChart(dataset);
        System.out.println("Chart created");
        this.lineChartViewer.setChart(chart);
        System.out.println("chart set");
    }

    @FXML
    private void setChannel() {
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer itemRenderer = plot.getRenderer();
        if(itemRenderer instanceof XYLineAndShapeRenderer) {
            for (int i = 0; i < 16; i++) {
                XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) itemRenderer;
                renderer.setSeriesShapesVisible(i, false);
                renderer.setSeriesLinesVisible(i, false);
            }
        }
    }

    @FXML
    private TextField filePath;

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


    private static XYSeriesCollection createExampleDataset() {
        XYSeries channel1 = new XYSeries("series-1");
        for (double x = 0; x < 60; x+=0.5) {
            channel1.add(x, Math.sin(x)*4);
        }

        /*
        * XYSeries channel1 = channels.get(0).getChannelSeries();
        * */

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
                                                        "Calibration",
                                                        "Time, s",
                                                        "Voltage, V",
                                                         dataset,
                                                         PlotOrientation.VERTICAL,
                                                        false,
                                                        true,
                                                        false);
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