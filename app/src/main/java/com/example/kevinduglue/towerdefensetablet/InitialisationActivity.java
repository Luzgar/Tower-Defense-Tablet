package com.example.kevinduglue.towerdefensetablet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kevinduglue.towerdefensetablet.socket.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
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

public class InitialisationActivity extends AppCompatActivity {

    @BindView(R.id.mainScreenTitle) TextView mMainScreenTitle;
    @BindView(R.id.inputName) EditText mInputName;
    @BindView(R.id.saveName) Button mSaveNameButton;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainScreenTitle.setText("En attente du commencement de la partie");

        mSocket = SocketSingleton.getInstance();

        mSocket.on("setup", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                try {
                    System.out.println(args[0]);
                    JSONObject data = (JSONObject) args[0];
                    if(data.getString("action").equals("name")) {
                        mInputName.setVisibility(View.VISIBLE);
                        mSaveNameButton.setVisibility(View.VISIBLE);
                        mMainScreenTitle.setVisibility(View.GONE);
                    }
                    if(data.getString("action").equals("ready")) {
                        mMainScreenTitle.setText("La partie peut commencer !");
                        mInputName.setVisibility(View.INVISIBLE);
                        mSaveNameButton.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }
            });
            }
        });
    }

    @OnClick(R.id.saveName)
    public void saveName(View view) {
        try {
            mInputName.setVisibility(View.GONE);
            mSaveNameButton.setVisibility(View.GONE);
            mMainScreenTitle.setVisibility(View.VISIBLE);
            mMainScreenTitle.setText("En attente des autres joueurs");
            mSocket.emit("setup", new JSONObject().put("action", "ready"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSocket.off("setup");
    }
}
