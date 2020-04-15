package com.eldarian.channels;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.File;
import java.util.ArrayList;

public class ChannelService {

    public ArrayList<Channel> channels;

    public XYSeriesCollection calibrationDataset;
    private int timestamp = 0;

    public ChannelService() {
        this.channels = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            channels.add(new Channel(i, new File("data/channel-"+i+".csv"), 1));
            calibrationDataset.addSeries(channels.get(i).getChannelSeries());
        }
    }

    //Нужно наладить изменение только включённых функций.

    public void update(int currentChannel, String data) {
        channels.get(currentChannel).getChannelSeries().add(timestamp, Double.parseDouble(data));
    }

    public void clearChannel(int channel) {
        timestamp = 0;
        channels.get(channel).setChannelSeries(new XYSeries("channel-"+channel));
    }
}
