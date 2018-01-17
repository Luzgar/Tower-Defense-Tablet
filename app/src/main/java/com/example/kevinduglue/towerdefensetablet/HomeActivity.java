package com.example.kevinduglue.towerdefensetablet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kevinduglue on 16/01/2018.
 */

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.button) Button mButton1;
    @BindView(R.id.button2) Button mButton2;
    @BindView(R.id.button3) Button mButton3;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        try {
            mSocket = IO.socket("http://10.0.2.2:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button)
    public void button1(View view) {
        try {
            mSocket.emit("monster", new JSONObject().put("path", 1).put("monster", 1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button2)
    public void button2(View view) {
        try {
            mSocket.emit("monster", new JSONObject().put("path", 4).put("monster", 2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button3)
    public void button3(View view) {
        try {
            mSocket.emit("monster", new JSONObject().put("path", 6).put("monster", 3));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
