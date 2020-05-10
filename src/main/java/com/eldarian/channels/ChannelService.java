package com.eldarian.channels;

import com.eldarian.App;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.util.ArrayList;

public class ChannelService {

    public ArrayList<Channel> channels;

    public XYSeriesCollection calibrationDataset = new XYSeriesCollection();
    public DefaultCategoryDataset histogramDataset = new DefaultCategoryDataset();
    private int timestamp = 0;

    public ChannelService() {
        this.channels = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            channels.add(new Channel(i, 1));
            System.out.println("channel" + i + "added to array");
            calibrationDataset.addSeries(channels.get(i-1).getChannelSeries());
        }
    }

    //Нужно наладить изменение только включённых функций.

    public void handleData(String line) {
        switch (App.mode) {
            case PEAKS:
                histogramDataset.addValue(1, "",  line);
                break;
            case CHANNEL:
                calibrationDataset.getSeries(App.mode.currentChannel).add(timestamp, Double.parseDouble(line));
        }
    }

    public void clearChannel(int channel) {
        timestamp = 0;
        channels.get(channel).setChannelSeries(new XYSeries("channel-"+channel));
    }
}
