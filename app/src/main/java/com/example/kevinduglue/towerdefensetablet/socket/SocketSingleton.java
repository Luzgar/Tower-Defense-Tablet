package com.example.kevinduglue.towerdefensetablet.socket;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

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
                mSocket = IO.socket("http://192.168.43.254:3000");
                mSocket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return mSocket;
        }
    }
}
