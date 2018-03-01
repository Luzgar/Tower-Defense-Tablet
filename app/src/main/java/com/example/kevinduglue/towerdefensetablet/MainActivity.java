package com.example.kevinduglue.towerdefensetablet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.example.kevinduglue.towerdefensetablet.monsters.Monster;
import com.example.kevinduglue.towerdefensetablet.monsters.MonstersImage;
import com.example.kevinduglue.towerdefensetablet.socket.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity{

    @BindView(R.id.gameTitle) TextView mGameTitle;
    @BindView(R.id.logoLeft) ImageView mLogoLeft;
    @BindView(R.id.logoRight) ImageView mLogoRight;

    @BindView(R.id.logoLeftLayout)
    LinearLayout mlogoLeftLayout;

    @BindView(R.id.logoRightLayout)
    LinearLayout mLogoRightLayout;

    @BindView(R.id.saveName) Button mSaveName;
    @BindView(R.id.playerName) EditText mPlayerName;
    @BindView(R.id.waitingText) TextView mWaitingText;
    @BindView(R.id.textEnterLayout) LinearLayout mTextEnterLayout;

    private Socket mSocket;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefsEditor;
    private HashMap<String, Integer[]> basesPdv = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainsplash);
        ButterKnife.bind(this);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slidedown);
        mGameTitle.startAnimation(anim);
        anim = AnimationUtils.loadAnimation(this, R.anim.slide_from_left);
        mlogoLeftLayout.startAnimation(anim);
        anim = AnimationUtils.loadAnimation(this, R.anim.slide_from_right);
        mLogoRightLayout.startAnimation(anim);

        RotateAnimation ra = new RotateAnimation(180, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setFillAfter(true);
        ra.setDuration(2000);

        RotateAnimation ra2 = new RotateAnimation(540, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra2.setFillAfter(true);
        ra2.setDuration(2000);
        mLogoLeft.startAnimation(ra);
        mLogoRight.startAnimation(ra2);

        mSocket = SocketSingleton.getInstance();

        sharedPreferences = getBaseContext().getSharedPreferences("MONSTER", MODE_PRIVATE);
        prefsEditor = sharedPreferences.edit();

        setupListener();
    }

    @OnClick(R.id.saveName)
    public void saveName(View view) {
        try {
            mTextEnterLayout.setVisibility(View.GONE);
            mWaitingText.setVisibility(View.VISIBLE);
            mWaitingText.setText("En attente des autres joueurs");
            mSocket.emit("setup", new JSONObject().put("action", "ready").put("name", mPlayerName.getText()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupListener() {
        mSocket.on("setup", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) args[0];
                            if(data.getString("action").equals("name")) {
                                mTextEnterLayout.setVisibility(View.VISIBLE);
                                mWaitingText.setVisibility(View.GONE);
                            }
                            else if(data.getString("action").equals("ready")) {
                                mWaitingText.setText("Le tutoriel va commencer !");
                                mTextEnterLayout.setVisibility(View.GONE);
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("tutorial", true);
                                intent.putExtra("name", mPlayerName.getText());
                                intent.putExtra("basesHealth", basesPdv);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        mSocket.on("monster", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        Gson gson = new Gson();
                        List<Monster> monsterList =  new ArrayList<>();
                        try {
                            JSONArray monstersJSON = data.getJSONArray("monsters");
                            for(int i = 0 ; i < monstersJSON.length(); ++i) {
                                String name = monstersJSON.getJSONObject(i).getString("name");
                                int health = monstersJSON.getJSONObject(i).getInt("health");
                                int attack = monstersJSON.getJSONObject(i).getInt("attack");
                                int price =  monstersJSON.getJSONObject(i).getInt("price");
                                int image = MonstersImage.getEnumByString(name).getImageRes();
                                boolean disabled = monstersJSON.getJSONObject(i).getBoolean("disable");
                                int disableTime = monstersJSON.getJSONObject(i).getInt("disableTime");
                                monsterList.add(new Monster(i, name, health, attack, price, image, disabled, disableTime));
                            }

                            String json = gson.toJson(monsterList);
                            prefsEditor.putString("Monsters", json);
                            prefsEditor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        mSocket.on("base", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            JSONArray basesJson = data.getJSONArray("bases");
                            for(int i = 0; i < basesJson.length(); i++)
                            {
                                JSONObject baseJson = basesJson.getJSONObject(i);
                                Integer[] hp = new Integer[2];
                                hp[0] = baseJson.getInt("hp");
                                hp[1] = baseJson.getInt("hp");
                                basesPdv.put(baseJson.getString("name"), hp);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        mSocket.emit("getmonster");
    }
}
