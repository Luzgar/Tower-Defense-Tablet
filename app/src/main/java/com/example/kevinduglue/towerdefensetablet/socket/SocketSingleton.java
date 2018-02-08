package com.example.kevinduglue.towerdefensetablet.socket;

import com.example.kevinduglue.towerdefensetablet.monsters.Monster;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevinduglue on 19/01/2018.
 */

public class SocketSingleton {

    private static Socket mSocket;

    public static Socket getInstance() {
        if (mSocket != null) {
            return mSocket;
        } else {
            try {
                mSocket = IO.socket("http://192.168.1.20:9091");
                mSocket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return mSocket;
        }
    }
}
