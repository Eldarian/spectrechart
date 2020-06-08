package com.eldarian.channels;

import com.eldarian.App;
import com.eldarian.connectionHandler.SocketMode;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DatasetService {

    private ArrayList<Channel> channels;

    private File chartFile;
    private FileWriter peaksWriter;

    public XYSeriesCollection scopeDataset = new XYSeriesCollection();
    public DefaultCategoryDataset peaksDataset = new DefaultCategoryDataset();
    private int timestamp = 0;

    public DatasetService() {
        this.channels = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            channels.add(new Channel(i, 1));
            System.out.println("channel" + i + "added to array");
            scopeDataset.addSeries(channels.get(i-1).getChannelSeries());
        }
    }

    //Нужно наладить изменение только включённых функций.

    public synchronized void handleData(String line) throws InterruptedException, IOException {
        switch (App.mode) {
            case PEAKS:
                String[] values = line.split(",");
                if(line.equals("0")) break;
                //peaksDataset.incrementValue(1, "channel",  line);
                peaksDataset.setValue(Double.parseDouble(values[1]), "channels", values[0]);
                break;
            case CHANNEL:
                scopeDataset.getSeries(App.mode.currentChannel).add(timestamp++, Double.parseDouble(line));
                if (timestamp>50) {
                    scopeDataset.getSeries(App.mode.currentChannel).delete(0, 0);
                }
                break;
        }
        peaksWriter.write(line);
        Thread.sleep(100);
        if (App.mode == SocketMode.FREEZED) {
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
    }

    public void clearPeaks(boolean toFile) throws IOException {
        chartFile = File.createTempFile("peaks-", ".tmp");
        peaksWriter = new FileWriter(chartFile);
        if (toFile) {
            chartFile.deleteOnExit();
        }
        peaksDataset = new DefaultCategoryDataset();
        for (int x = 1; x<=16; x++) {
            peaksDataset.addValue(0, "channels", ""+x);
        }
    }
}
