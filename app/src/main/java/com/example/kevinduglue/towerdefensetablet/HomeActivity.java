package com.example.kevinduglue.towerdefensetablet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kevinduglue.towerdefensetablet.adapters.CustomAdapter;
import com.example.kevinduglue.towerdefensetablet.map.MapDrawer;
import com.example.kevinduglue.towerdefensetablet.map.MapView;
import com.example.kevinduglue.towerdefensetablet.monsters.Monster;
import com.example.kevinduglue.towerdefensetablet.socket.SocketSingleton;
import com.github.clans.fab.FloatingActionButton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.media.MediaExtractor.MetricsConstants.FORMAT;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by kevinduglue on 16/01/2018.
 */

/* Tutorial
   1. Select Base
   2. Select Chemin
   3. Select Monstre

   Tablette
   1. Monstre cooldown 1s
   2. Afficher pv base
*/


public class HomeActivity extends AppCompatActivity {


    @BindView(R.id.goldTextView) TextView mGoldAmount;
    @BindView(R.id.timer) TextView mTimer;

    @BindView(R.id.castleBase) ImageButton castleBase;
    @BindView(R.id.churchBase) ImageButton churchBase;
    @BindView(R.id.millBase) ImageButton millBase;
    @BindView(R.id.hostelBase) ImageButton hostelBase;

    @BindView(R.id.mapView) MapView mapView;

    @BindView(R.id.monsterGridLayout) GridView mGridView;
    @BindView(R.id.spendGoldTextView) TextView mSpendGoldText;

    @BindView(R.id.power1) FloatingActionButton mPower1;
    @BindView(R.id.power2) FloatingActionButton mPower2;

    private Socket mSocket;
    private int goldAmount = 1000;
    private MapDrawer mapDrawer;

    private String selectedBase = "";

    private CustomAdapter adapter;

    private CountDownTimer timer;

    private boolean canPower1 = true;
    private boolean canPower2 = true;

    private boolean behemothAdded = false;
    private boolean monsterXAdded = false;
    private int partyDuration = 600000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);


        mSocket = SocketSingleton.getInstance();
        mGoldAmount.setText(Integer.toString(goldAmount));

        ArrayList<Monster> ar = new ArrayList<>();
        ar.add(new Monster(0, "skeleton", 100, 10, 10, R.drawable.squelette, false));
        ar.add(new Monster(1, "warrior", 150, 10, 20, R.drawable.guerrier, false));
        ar.add(new Monster(2, "magdamonster", 300, 200, 200, R.drawable.magdamonster, true));
        ar.add(new Monster(3, "caterpillar", 500, 75, 120, R.drawable.caterpillar, true));
        adapter=new CustomAdapter(this, ar);
        mGridView.setAdapter(adapter);

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
                                mTimer.setText("FIN !");
                                showModal("VICTOIRE", "Bravo ! Vous avez détruit toutes les bases", R.drawable.medal);
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


        timer = new CountDownTimer(partyDuration, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                goldAmount += 1;
                mGoldAmount.setText(Integer.toString(goldAmount));
                mTimer.setText(String.format("%d:%d ",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                ));
                if((millisUntilFinished*100)/partyDuration < 50 && !behemothAdded) {
                    behemothAdded = true;
                    adapter.getMonster(2).setDisable(false);
                    adapter.notifyDataSetChanged();
                } else if ((millisUntilFinished*100)/partyDuration < 30 && !monsterXAdded) {
                    monsterXAdded = true;
                    adapter.getMonster(3).setDisable(false);
                    adapter.notifyDataSetChanged();
                }
            }

            public void onFinish() {

                mTimer.setText("FIN !");
                showModal("GAME OVER", "Vous n'avez pas réussi à détruire les bases dans le temps imparti !", R.drawable.tombstone);

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
        //mPaths.setImageBitmap(bitmap);
        mapDrawer = new MapDrawer(width, height, 1, canvas);
        mapDrawer.getDrawMap(1, -1);

    }

    public void summonMonster(final Monster monster) {
        if(!selectedBase.equals("")) {
            try {
                if (goldAmount - (monster.getPrice()) < 0) {
                    Snackbar.make(findViewById(R.id.relLayout), "Pas assez d'or disponible", Snackbar.LENGTH_SHORT).show();
                } else if(mapView.getPathSelected() == -1) {
                    Snackbar.make(findViewById(R.id.relLayout), "Aucun chemin n'a été séléctionné", Snackbar.LENGTH_SHORT).show();
                } else {

                    monster.setDisable(true);
                    adapter.notifyDataSetChanged();
                    mSocket.emit("monster", new JSONObject().put("path", mapView.getPathSelected()).put("monster", monster.getName()).put("base", selectedBase));
                    spendGold(monster.getPrice());
                    new CountDownTimer(2000, 1000) {

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            monster.setDisable(false);
                            adapter.notifyDataSetChanged();
                        }
                    }.start();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Snackbar.make(findViewById(R.id.relLayout), "Aucune base n'a été séléctionnée", Snackbar.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.castleBase)
    public void selectCastleBase(View view) {
        selectedBase = (selectedBase.equals("castle")) ? "" : "castle";
        mapView.generateCastlePath(!selectedBase.equals(""));

    }

    @OnClick(R.id.churchBase)
    public void selectchurchBase(View view) {
        selectedBase = (selectedBase.equals("church")) ? "" : "church";
        mapView.generateChurchPaths(!selectedBase.equals(""));

    }

    @OnClick(R.id.millBase)
    public void selectmillBase(View view) {
        selectedBase = (selectedBase.equals("windmill")) ? "" : "windmill" ;
        mapView.generateWindMillPath(!selectedBase.equals(""));
        mapView.setPathSelected(0);

    }

    @OnClick(R.id.hostelBase)
    public void selecthostelBase(View view) {
        selectedBase = (selectedBase.equals("tavern")) ? "" : "tavern" ;
        mapView.generateHostelPath(!selectedBase.equals(""));
        mapView.setPathSelected(0);

    }

    @OnClick(R.id.power1)
    public void activatePower1(View view) {
        if(canPower1) {
            canPower1 = false;
            final float maxTime = 10000;
            int interval = 100;
            mPower1.setProgress(100, false);
            mPower1.setColorNormal(Color.GRAY);
            timer = new CountDownTimer(10000, interval) { // adjust the milli seconds here
                public void onTick(long millisUntilFinished) {
                    float progress = (millisUntilFinished/maxTime)*100;
                    mPower1.setProgress((int) progress, true);
                }

                public void onFinish() {
                    mPower1.setProgress(0, true);
                    mPower1.setColorNormal(Color.RED);
                    canPower1 = true;
                }
            }.start();
        }
    }

    @OnClick(R.id.power2)
    public void activatePower2(View view) {
        if(canPower2) {
            canPower2 = false;
            final float maxTime = 10000;
            int interval = 100;
            mPower2.setProgress(100, false);
            mPower2.setColorNormal(Color.GRAY);
            timer = new CountDownTimer(10000, interval) { // adjust the milli seconds here
                public void onTick(long millisUntilFinished) {
                    float progress = (millisUntilFinished/maxTime)*100;
                    mPower2.setProgress((int) progress, true);
                }

                public void onFinish() {
                    mPower2.setProgress(0, true);
                    mPower2.setColorNormal(Color.RED);
                    canPower2 = true;
                }
            }.start();
        }
    }

    private void showModal(String title, String body, int icon) {
        final NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(this);

        dialogBuilder.withTitle(title)
            .withMessage(body)
            .withButton1Text("Rejouer")
            .isCancelableOnTouchOutside(false)
            .withIcon(getResources().getDrawable(icon))
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

    private void spendGold(int amount) {
        goldAmount -= amount;
        mGoldAmount.setText(Integer.toString(goldAmount));
        mSpendGoldText.setText("-"+Integer.toString(amount));
        mSpendGoldText.setVisibility(View.VISIBLE);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSpendGoldText.animate()
                        .translationY(mSpendGoldText.getHeight())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mSpendGoldText.setVisibility(View.GONE);
                                mSpendGoldText.setTranslationY(0);
                                mSpendGoldText.setAlpha(1.0f);
                            }
                        });
            }
        }, 500);
    }
}
