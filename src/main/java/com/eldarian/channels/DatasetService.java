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
    public int delay = 10;
    public int frameWidth = 100;
    public double scopeThreshold = 1.0;
    public int startIndex = 0;
    public int frameIndex;
    public boolean isPeakRegistered = false;

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
        Thread.sleep(delay);
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
        startIndex = 0;
        timestamp = 0;
        frameIndex = 0;
        channels.get(channel).clearSeries();
        maxIndexes = new ArrayList<>();
        rawData = new ArrayList<>();
        isPeakRegistered = false;
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
        double newValue = Double.parseDouble(line);
        rawData.add(newValue);
        int index = startIndex;
        if (startIndex + frameWidth < rawData.size()) {
            while (index < rawData.size()) {
                System.out.println(index);
                if (detectFront(index)  && !isPeakRegistered) {
                    updateSeries();
                    startIndex = index;
                    isPeakRegistered = true;
                    break;
                }
                if(detectDrop(index) && isPeakRegistered) {
                    isPeakRegistered = false;
                }
                index++;
            }
        }
        //scopeDataset.getSeries(2).add(timestamp++, newValue);
    }

    private boolean detectDrop(int index) {
        boolean lessThanThreshold = rawData.get(index) < scopeThreshold;
        boolean isFalling = ((index - 2) > 0) && (rawData.get(index) < rawData.get(index - 1))/*&& (rawData.get(index - 1) > rawData.get(index - 2))*/;
        return lessThanThreshold && isFalling;
    }

    private boolean detectFront(int index) {
        boolean moreThanThreshold = rawData.get(index) > scopeThreshold;
        boolean isGrowing = ((index - 2) > 0) && (rawData.get(index) > rawData.get(index - 1)) /*&& (rawData.get(index - 1) > rawData.get(index - 2))*/;
        return moreThanThreshold && isGrowing;
    }

    private void updateSeries() {
        scopeDataset.getSeries(App.mode.currentChannel).clear();
        for(int i = startIndex; i < startIndex + frameWidth - 1; i++) {
            scopeDataset.getSeries(App.mode.currentChannel).add(i, rawData.get(i));
        }
        System.out.println("Frame-" + frameIndex++);
    }
}
