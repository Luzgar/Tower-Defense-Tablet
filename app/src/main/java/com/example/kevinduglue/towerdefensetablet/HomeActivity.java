package com.example.kevinduglue.towerdefensetablet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
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

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.button) Button mButton1;
    @BindView(R.id.button2) Button mButton2;
    @BindView(R.id.button3) Button mButton3;

    @BindView(R.id.radioButton) RadioButton mButton4;
    @BindView(R.id.radioButton2) RadioButton mButton5;

    @BindView(R.id.goldAmount) TextView mGoldAmount;
    @BindView(R.id.imageView) ImageView mPaths;

    private Socket mSocket;
    private int pathSelected = 1;
    private int goldAmount = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_alpha);
        ButterKnife.bind(this);

        mSocket = SocketSingleton.getInstance();
        mButton4.setChecked(true);
        mSocket.on("gold", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = (JSONObject) args[0];
                        try
                        {
                            int amount = json.getInt("amount");
                            goldAmount -= amount;
                            mGoldAmount.setText(Integer.toString(goldAmount));
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        mSocket.on("state", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = (JSONObject) args[0];
                        try
                        {
                            if(json.getString("action").equals("pause")) {
                                Snackbar.make(findViewById(R.id.relLayout), "Jeux en pause", Snackbar.LENGTH_SHORT).show();
                            } else if(json.getString("action").equals("stop")) {
                                Snackbar.make(findViewById(R.id.relLayout), "Jeux stoppé", Snackbar.LENGTH_SHORT).show();
                            } else if(json.getString("action").equals("resume")){
                                Snackbar.make(findViewById(R.id.relLayout), "Le jeu repart", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mButton4.setTextColor(getResources().getColor(R.color.path1));
        mButton5.setTextColor(getResources().getColor(R.color.path2));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Bitmap bitmap = Bitmap.createBitmap((int) getWindowManager()
                .getDefaultDisplay().getWidth(), (int) getWindowManager()
                .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mPaths.setImageBitmap(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.rgb(3, 169, 244));
        paint.setStrokeWidth(10);
        int startx = 50;
        int starty = height/2;
        int endx = width;
        int endy = height/2;
        canvas.drawLine(startx, starty, width/2, height/2-200, paint);
        canvas.drawLine(width/2, height/2-200, endx, endy, paint);

        paint = new Paint();
        paint.setColor(Color.rgb(156, 39, 176));
        paint.setStrokeWidth(10);
        canvas.drawLine(startx, starty, width/2, height/2+200, paint);
        canvas.drawLine(width/2, height/2+200, endx, endy, paint);
    }

    @OnClick(R.id.button)
    public void button1(View view) {
        try {
            mSocket.emit("monster", new JSONObject().put("path", pathSelected).put("monster", 1));
            if(goldAmount - 10 < 0) {
                Snackbar.make(findViewById(R.id.relLayout), "Pas assez d'or disponible", Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button2)
    public void button2(View view) {
        try {
            System.out.println(pathSelected);
            mSocket.emit("monster", new JSONObject().put("path", pathSelected).put("monster", 2));
            if(goldAmount - 50 < 0) {
                Snackbar.make(findViewById(R.id.relLayout), "Pas assez d'or disponible", Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button3)
    public void button3(View view) {
        try {
            mSocket.emit("monster", new JSONObject().put("path", pathSelected).put("monster", 3));
            if(goldAmount - 100 < 0) {
                Snackbar.make(findViewById(R.id.relLayout), "Pas assez d'or disponible", Snackbar.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.radioButton)
    public void button4(View view) {
        pathSelected = 1;
    }

    @OnClick(R.id.radioButton2)
    public void button5(View view) {
        pathSelected = 2;
    }
}
