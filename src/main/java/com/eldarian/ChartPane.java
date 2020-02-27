package com.eldarian;

import  javafx.scene.Node;
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

import java.awt.*;

class ChartPane extends AnchorPane {

    private ChartViewer chartViewer;

    public ChartPane() {
        super();
        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        this.chartViewer = new ChartViewer(chart);
        getChildren().add(this.chartViewer);
    }

    public ChartViewer getChartViewer() {
        return chartViewer;
    }

    public ChartPane(Node... children) {
        super();
        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        this.chartViewer = new ChartViewer(chart);
        getChildren().add(this.chartViewer);
        getChildren().addAll(children);
    }

    private static XYDataset createDataset() {
        XYSeries channel1 = new XYSeries("channel-1");
        for (int x = 0; x < 10; x++) {
            channel1.add(x, x + Math.random() * 4.0);
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
            renderer.setSeriesPaint(1, Color.BLUE);
        }
        return chart;
    }
}
