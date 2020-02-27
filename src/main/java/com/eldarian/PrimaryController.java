package com.eldarian;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
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

    //      <ChartViewer fx:id="lineChartViewer" prefHeight="300.0" prefWidth="500.0"> </ChartViewer>

    @FXML
    private ChartViewer lineChartViewer;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    private void insertChart() {


        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        this.lineChartViewer.setChart(chart);

        /*this.chartPane = new ChartPane();
        lineChartViewer = chartPane.getChartViewer();*/
        //lineChartPane.getChildren().add(chartPane.getChartViewer());

    }

    @FXML
    private void initialize() {
        insertChart();
    }


    private static XYDataset createDataset() {
        XYSeries channel1 = new XYSeries("channel-1");
        for (double x = 0; x < 60; x+=0.5) {
            channel1.add(x, Math.sin(x)*4);
        }

        XYSeries channel2 = new XYSeries("channel-2");
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
                "MainApp", "X", "Y", dataset);
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
