package com.eldarian.fx;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

import com.eldarian.App;
import com.eldarian.connectionHandler.SocketMode;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScopeController {

    private JFreeChart chart;
    private XYSeriesCollection dataset = App.datasetService.scopeDataset;

    @FXML
    private CheckBox toFile;

    @FXML
    private ChartViewer lineChartViewer;

    @FXML
    private ChoiceBox channelsChooser;

    @FXML
    private Button startStopButton;

    @FXML
    private void startStop() throws IOException {
        if (App.mode == SocketMode.SCOPE) {
            stopDraw();
            startStopButton.setText("Start");
        } else {
            startDraw();
            startStopButton.setText("Stop");
        }
    }


    private void startDraw() throws IOException {
        App.mode = SocketMode.SCOPE;
        if (channelsChooser.getValue() != null) {
            int channel = (Integer) channelsChooser.getValue();
            changeChannel(channel);
            App.datasetService.clearChannel(channel, toFile.isSelected());
            App.mode.currentChannel = channel;
            String msg = "channel" + channel;
            System.out.println("Sending " + msg);
            App.socketConnector.sendMessage(msg);
        }
    }

    private void changeChannel(int newChannel) {
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            for (int i = 0; i < dataset.getSeriesCount(); i++) {
                if (i == newChannel) {
                    renderer.setSeriesVisible(i, true);
                    continue;
                }
                renderer.setSeriesVisible(i, false);
            }
        }
    }


    private void stopDraw() {
        App.mode = SocketMode.STOP;
    }

    @FXML
    private void hold() {

    }

    @FXML
    private void switchToHistogram() throws IOException {
        App.mode = SocketMode.STOP;
        App.setRoot("peaksView");
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
        channelsChooser.getSelectionModel().selectFirst();
        encoding.getSelectionModel().selectFirst();
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
        if (itemRenderer instanceof XYLineAndShapeRenderer) {
            for (int i = 0; i < 16; i++) {
                XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) itemRenderer;
                renderer.setSeriesShapesVisible(i, false);
                renderer.setSeriesLinesVisible(i, false);
            }
        }
    }

    /*@FXML
    private TextField filePath;*/

    @FXML
    private ChoiceBox encoding;

    File fileForReading;

    final FileChooser fileChooser = new FileChooser();

    @FXML
    private void getFromFile() {
        fileChooser.setTitle("Open File");
        fileForReading = fileChooser.showOpenDialog(new Stage());
        if (fileForReading != null) {
            /*filePath.clear();
            filePath.appendText(file.getAbsolutePath());
*/
            //fileForReading = new File(filePath.getText());
            //fileForReading = new File("C:\\Users\\Eldarian\\Documents\\spectrechart\\src\\main\\resources\\com\\eldarian\\mem4_ch1.csv");

            //XYSeries fileSeries = new XYSeries("channel-" + (/*dataset.getSeriesCount() + */17));
            XYSeries fileSeries = dataset.getSeries(1);
            fileSeries.clear();
            String pattern = "\\s";
            if (encoding.getValue().equals("UTF-8 BOM")) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(new BOMInputStream(new FileInputStream(
                                fileForReading), false, ByteOrderMark.UTF_8,
                                ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE,
                                ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE)))) {
                    // use br here
                    String currentLine;
                    int x = 0;
                    while ((currentLine = br.readLine()) != null && x < 5000) {
                        double newValue = Double.parseDouble(currentLine);
                        fileSeries.add(x, newValue);
                        x++;
                    }
                } catch (Exception e) {
                    App.openErrorWindow(e.getMessage() + ", " + e.toString());
                }
            } else {

                try (Scanner scanner = new Scanner(fileForReading, (String) encoding.getValue())) {
                    int x = 0;
                    while (scanner.hasNext() && x < 5000) {
                        scanner.skip(pattern);
                        scanner.useLocale(Locale.ENGLISH);
                        double newValue = scanner.nextDouble();
                        //double fileTimestamp = scanner.nextDouble();
                        //System.out.println(scanner.nextLine());
                        fileSeries.add(x, newValue);
                        x++;
                    }
                } catch (IOException e) {
                    App.openErrorWindow(e.getMessage());
                } catch (Exception e) {
                    App.openErrorWindow(e.toString());
                }
            }
        }
    }


    private static JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Time, s",
                "Voltage, V",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
        String fontName = "Palatino";
        chart.getTitle().setFont(new Font(fontName, Font.BOLD, 18));
        chart.setBackgroundPaint(new Color(67, 67, 67));
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis lineAxis = (NumberAxis) plot.getDomainAxis();

        lineAxis.setAutoRangeIncludesZero(false);

        plot.setBackgroundPaint(Color.BLACK);
        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            ValueAxis yAxis = plot.getRangeAxis();
            ValueAxis xAxis = plot.getDomainAxis();

            Color axisColor = Color.white;

            //plot.setDomainGridlinesVisible(false);

            yAxis.setLabelPaint(axisColor);
            yAxis.setTickLabelPaint(axisColor);
            yAxis.setAxisLinePaint(axisColor);
            yAxis.setTickMarkPaint(axisColor);

            xAxis.setLabelPaint(axisColor);
            xAxis.setTickLabelPaint(axisColor);
            xAxis.setAxisLinePaint(axisColor);
            xAxis.setTickMarkPaint(axisColor);

            //xAxis.setTickMarksVisible(false);
            //xAxis.setTickLabelsVisible(false);

            renderer.setDefaultShapesVisible(false);
            renderer.setDrawSeriesLineAsPath(true);
            renderer.setAutoPopulateSeriesStroke(false);
            renderer.setDefaultStroke(new BasicStroke(1.0f));
            renderer.setDataBoundsIncludesVisibleSeriesOnly(true);
            renderer.setSeriesPaint(0, Color.GREEN);
            renderer.setSeriesLinesVisible(0, true);
            renderer.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
            renderer.setSeriesShapesVisible(0, true);
            renderer.setSeriesShapesFilled(0, true);
        }
        return chart;
    }
}