package com.eldarian.channels;

import com.eldarian.App;
import com.eldarian.connectionHandler.SocketMode;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;

public class DatasetService {

    public ArrayList<Channel> channels;

    public XYSeriesCollection calibrationDataset = new XYSeriesCollection();
    public DefaultCategoryDataset peaksDataset = new DefaultCategoryDataset();
    private int timestamp = 0;

    public DatasetService() {
        this.channels = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            channels.add(new Channel(i, 1));
            System.out.println("channel" + i + "added to array");
            calibrationDataset.addSeries(channels.get(i-1).getChannelSeries());
        }
    }

    //Нужно наладить изменение только включённых функций.

    public synchronized void handleData(String line) throws InterruptedException{
        switch (App.mode) {
            case PEAKS:
                if(line.equals("0")) break;
                peaksDataset.incrementValue(1, "",  line);
                break;
            case CHANNEL:
                calibrationDataset.getSeries(App.mode.currentChannel).add(timestamp++, Double.parseDouble(line));
                break;
        }
        Thread.sleep(100);
        if (App.mode == SocketMode.FREEZED) {
            App.socketConnector.sendMessage("break");
            return;
        }
        App.socketConnector.sendMessage("ok");
        System.out.println(line);
    }

    public void clearChannel(int channel) {
        timestamp = 0;
        channels.get(channel).clearSeries();
    }

    public void clearPeaks() {
        peaksDataset = new DefaultCategoryDataset();
        for (int x = 1; x<=16; x++) {
            peaksDataset.addValue(0, "", ""+x);
        }
    }
}
