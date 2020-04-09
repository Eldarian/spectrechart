package com.eldarian.channels;

import org.jfree.data.xy.XYSeries;

import java.io.File;

public class Channel {
    private int number;
    private File channelFile;
    private XYSeries channelSeries = new XYSeries("channel-1");
    private int peaksNum = 0;
    private double threshold;

    public Channel(int number, File channelFile, double threshold) {
        this.channelFile = channelFile;
        this.threshold = threshold;
        this.number = number;
    }

    public void updateBar(double value) {
        if(value > threshold) {
            peaksNum++;
        }
    }

    public void updateSeries(double value) {
        channelSeries.add(number, value);
    }

    public File getChannelFile() {
        return channelFile;
    }

    public void setChannelFile(File channelFile) {
        this.channelFile = channelFile;
    }

    public XYSeries getChannelSeries() {
        return channelSeries;
    }

    public void setChannelSeries(XYSeries channelSeries) {
        this.channelSeries = channelSeries;
    }

    public int getPeaksNum() {
        return peaksNum;
    }

    public void setPeaksNum(int peaksNum) {
        this.peaksNum = peaksNum;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
