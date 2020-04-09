package com.eldarian.channels;

import java.io.File;
import java.util.ArrayList;

public class ChannelService {

    public ArrayList<Channel> channels;

    public ChannelService() {
        this.channels = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            channels.add(new Channel(i, File))
        }
    }

    //Нужно наладить изменение только включённых функций.
}
