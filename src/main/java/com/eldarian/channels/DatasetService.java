package com.eldarian.channels;

import com.eldarian.App;
import com.eldarian.connectionHandler.SocketMode;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DatasetService {

    private ArrayList<Channel> channels;
    public int peaksOnScreen = 5;

    private File chartFile;
    private FileWriter peaksWriter;
    private List<Double> rawData = new ArrayList<>();

    public XYSeriesCollection scopeDataset = new XYSeriesCollection();
    private ArrayList<Integer> maxIndexes = new ArrayList<>();

    public DefaultCategoryDataset peaksDataset = new DefaultCategoryDataset();
    private int timestamp = 0;

    public DatasetService() {
        this.channels = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            channels.add(new Channel(i, 1));
            System.out.println("channel" + i + "added to array");
            scopeDataset.addSeries(channels.get(i - 1).getChannelSeries());

        }
    }

    //Нужно наладить изменение только включённых функций.

    public synchronized void handleData(String line) throws InterruptedException, IOException {
        switch (App.mode) {
            case PEAKS:
                handlePeaks(line);
                break;
            case SCOPE:
                handleScope(line);
                break;
        }
        //peaksWriter.write(line);
        //Thread.sleep(100);
        if (App.mode == SocketMode.STOP) {
            App.socketConnector.sendMessage("break");
            return;
        }
        App.socketConnector.sendMessage("ok");
        System.out.println(line);
    }

    public void clearChannel(int channel, boolean toFile) throws IOException {
        chartFile = File.createTempFile("scope-", ".tmp");
        if (toFile) {
            chartFile.deleteOnExit();
        }
        timestamp = 0;
        channels.get(channel).clearSeries();
        maxIndexes = new ArrayList<>();
        rawData = new ArrayList<>();
    }

    public void clearPeaks(boolean toFile) throws IOException {
        chartFile = File.createTempFile("peaks-", ".tmp");
        peaksWriter = new FileWriter(chartFile);
        if (toFile) {
            chartFile.deleteOnExit();
        }
        peaksDataset = new DefaultCategoryDataset();
        for (int x = 1; x <= 16; x++) {
            peaksDataset.addValue(0, "channels", "" + x);
        }
    }

    private void handlePeaks(String line) {
        String[] values = line.split(",");
        if (line.equals("0")) return;
        peaksDataset.setValue(Double.parseDouble(values[1]), "channels", values[0]);
    }

    private void handleScope(String line) {
        double currentValue = Double.parseDouble(line);
        if (detectMax(currentValue)) {
            maxIndexes.add(rawData.size());
            updateSeries();
        }
        rawData.add(currentValue);
    }

    private boolean detectMax(double currentValue) {
        return rawData.size() > 0 && currentValue < rawData.get(rawData.size() - 1) && rawData.get(rawData.size() - 1) > rawData.get(rawData.size() - 2);
    }

    private void updateSeries() {
        if (maxIndexes.size() % peaksOnScreen == 0 && maxIndexes.size() > 0) {
            scopeDataset.getSeries(App.mode.currentChannel).clear();
            for (int i = maxIndexes.get(maxIndexes.size() - peaksOnScreen - 1); i < maxIndexes.get(maxIndexes.size() - 1); i++) {
                scopeDataset.getSeries(App.mode.currentChannel).add(timestamp++, rawData.get(i));
            }
        }
    }
}
