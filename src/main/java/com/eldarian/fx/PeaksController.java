package com.eldarian.fx;

import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Scanner;

import com.eldarian.App;
import com.eldarian.channels.DatasetService;
import com.eldarian.connectionHandler.SocketMode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
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
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;

public class PeaksController {

    @FXML
    private CheckBox toFile;

    @FXML
    private ChartViewer histogramChartViewer;

    @FXML
    private Button startStopButton;

    @FXML
    private void switchToScopeView() throws IOException {
        App.mode = SocketMode.STOP;
        System.out.println(App.mode.currentChannel);
        App.setRoot("scopeView");
    }

    @FXML
    private void startStop() throws IOException {
        if (App.mode == SocketMode.PEAKS) {
            stopDraw();
            startStopButton.setText("Start");
        } else {
            startDraw();
            startStopButton.setText("Stop");
        }
    }

    private void startDraw() throws IOException {
        App.mode = SocketMode.PEAKS;
        App.datasetService.clearPeaks(toFile.isSelected());
        createXYBar();
        String msg = "peaks";
        System.out.println("Requesting " + msg);
        App.socketConnector.sendMessage(msg);
    }

    private void stopDraw() {
        App.mode = SocketMode.STOP;
    }

    @FXML
    private void hold() {

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

    private void createXYBar() {
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

    @FXML
    private ChoiceBox encoding;

    File fileForReading;

    final FileChooser fileChooser = new FileChooser();


    @FXML
    private void getFromFile() throws IOException{ //TODO extract to a standalone class for multiple usage
        fileChooser.setTitle("Open File");
        fileForReading = fileChooser.showOpenDialog(new Stage());
        if (fileForReading != null) {

            DefaultCategoryDataset fileDataset = App.datasetService.peaksDataset; //TODO check is needed
            App.datasetService.clearPeaks(false);
            createXYBar();

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
                        System.out.println(currentLine);
                        App.datasetService.handleFile(currentLine);
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
                        App.datasetService.handleFile(scanner.nextLine());
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
}
