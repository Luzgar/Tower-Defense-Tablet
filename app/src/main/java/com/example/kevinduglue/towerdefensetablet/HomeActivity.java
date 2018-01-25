package com.example.kevinduglue.towerdefensetablet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kevinduglue.towerdefensetablet.map.MapDrawer;
import com.example.kevinduglue.towerdefensetablet.socket.SocketSingleton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.media.MediaExtractor.MetricsConstants.FORMAT;

/**
 * Created by kevinduglue on 16/01/2018.
 */

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.monster1) ImageButton mMonsterButton1;
    @BindView(R.id.monster2) ImageButton mMonsterButton2;
    @BindView(R.id.monster3) ImageButton mMonsterButton3;
    @BindView(R.id.monster4) ImageButton mMonsterButton4;

    @BindView(R.id.goldTextView) TextView mGoldAmount;
    @BindView(R.id.timer) TextView mTimer;

    @BindView(R.id.castleBase) ImageButton castleBase;

    @BindView(R.id.mapView) ImageView mPaths;

    @BindView(R.id.pathRadioGroup) RadioGroup mPathGroup;

    private Socket mSocket;
    private int pathSelected = 1;
    private int goldAmount = 1000;
    private MapDrawer mapDrawer;

    private int selectedBase = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mSocket = SocketSingleton.getInstance();
        mGoldAmount.setText(Integer.toString(goldAmount));


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


        new CountDownTimer(10000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                goldAmount += 1;
                mGoldAmount.setText(Integer.toString(goldAmount));
                mTimer.setText(String.format("%d:%d ",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                ));

            }

            public void onFinish() {

                mTimer.setText("FIN !");
                showModal("GAME OVER", "Vous n'avez pas réussi à détruire les bases dans le temps imparti !");

            }
        }.start();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Bitmap bitmap = Bitmap.createBitmap((int) getWindowManager()
                .getDefaultDisplay().getWidth(), (int) getWindowManager()
                .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mPaths.setImageBitmap(bitmap);
        mapDrawer = new MapDrawer(width, height, 1, canvas);
        mapDrawer.getDrawMap(1, -1);

    }

    @OnClick(R.id.monster1)
    public void summonMonster1(View view) {
        summonMonster(1, pathSelected);
    }

    @OnClick(R.id.monster2)
    public void summonMonster2(View view) {
        summonMonster(2, pathSelected);
    }

    @OnClick(R.id.monster3)
    public void summonMonster3(View view) {
        summonMonster(3, pathSelected);
    }

    @OnClick(R.id.monster4)
    public void summonMonster4(View view) {
        summonMonster(4, pathSelected);
    }

    private void summonMonster(int monsterId, int path) {
        if(selectedBase != -1) {
            try {
                if(goldAmount - (monsterId*10) < 0) {
                    Snackbar.make(findViewById(R.id.relLayout), "Pas assez d'or disponible", Snackbar.LENGTH_SHORT).show();
                } else {
                    System.out.println("SEND MONSTER "+monsterId+" ON "+path);
                    mSocket.emit("monster", new JSONObject().put("path", path).put("monster", monsterId));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @OnClick(R.id.castleBase)
    public void selectCastleBase(View view) {
        RelativeLayout rl = findViewById(R.id.pathSelection);
        if(selectedBase == -1) {
            selectedBase = 0;
            final RadioButton[] rb = new RadioButton[2];
            //rla.addRule(RelativeLayout.CENTER_HORIZONTAL);
            Typeface face= Typeface.create("casual",Typeface.NORMAL);

            rb[0]  = new RadioButton(this);
            rb[0].setText("Chemin 1");
            rb[0].setTextColor(getResources().getColor(R.color.path1));
            rb[0].setId(1 + 100);
            rb[0].setChecked(true);
            rb[0].setTypeface(face);
            mPathGroup.addView(rb[0]);

            rb[1]  = new RadioButton(this);
            rb[1].setText("Chemin 2");
            rb[1].setTextColor(getResources().getColor(R.color.path2));
            rb[1].setId(2+100);
            rb[1].setTypeface(face);
            mPathGroup.addView(rb[1]);

            mPathGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == rb[0].getId()) {
                        pathSelected = 1;
                    } else if (checkedId == rb[1].getId()) {
                        pathSelected = 2;
                    }
                }
            });

        } else if(selectedBase == 0) {
            selectedBase = -1;
            mPathGroup.removeAllViewsInLayout();
        }

        mapDrawer.getDrawMap(1, selectedBase);




        //Todo: Afficher selectBox
    }
    private void showModal(String title, String body) {
        final NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(this);

        dialogBuilder.withTitle(title)
                .withMessage(body)
                .withButton1Text("Rejouer")
                .isCancelableOnTouchOutside(false)
                .withIcon(getResources().getDrawable(R.drawable.tombstone))
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        Intent intent = new Intent(getApplicationContext(), InitialisationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .show();
    }

}
