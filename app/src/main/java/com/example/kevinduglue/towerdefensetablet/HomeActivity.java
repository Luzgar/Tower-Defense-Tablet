package com.example.kevinduglue.towerdefensetablet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevinduglue.towerdefensetablet.adapters.CustomAdapter;
import com.example.kevinduglue.towerdefensetablet.map.MapView;
import com.example.kevinduglue.towerdefensetablet.monsters.Monster;
import com.example.kevinduglue.towerdefensetablet.socket.SocketSingleton;
import com.github.clans.fab.FloatingActionButton;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;

/**
 * Created by kevinduglue on 16/01/2018.
 */

public class HomeActivity extends AppCompatActivity implements SensorListener{

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

    @BindView(R.id.beginGame) Button mBeginGame;

    private static final int SHAKE_THRESHOLD = 2000;

    private Socket mSocket;
    private int goldAmount = 1000;

    private String selectedBase = "";

    private CustomAdapter adapter;

    private CountDownTimer timer;

    private boolean canPower1 = true;
    private boolean canPower2 = true;

    private int partyDuration = 600000;

    private MaterialTapTargetPrompt baseShowCase, pathShowCase, monsterShowCase, monsterDisableShowCase, speedPowerShowCase;

    private boolean isTutorial;
    NiftyDialogBuilder dialogBuilder;

    SensorManager sensorMgr;

    private long lastUpdate;
    private float last_x, last_y, last_z;

    private String attackerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTutorial = getIntent().getBooleanExtra("tutorial", false);
        attackerName = getIntent().getStringExtra("name");

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        Gson gson = new Gson();

        /**** TEMP ****/
        ArrayList<Monster> ar = new ArrayList<>();
        ar.add(new Monster(0, "skeleton", 100, 10, 10, R.drawable.squelette, true, 100));
        ar.add(new Monster(1, "warrior", 150, 10, 20, R.drawable.guerrier, true, 100));
        ar.add(new Monster(2, "magdamonster", 300, 200, 200, R.drawable.magdamonster, true, 50));
        ar.add(new Monster(3, "caterpillar", 500, 75, 120, R.drawable.caterpillar, true, 30));

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("MONSTER", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        String json = gson.toJson(ar);
        prefsEditor.putString("Monsters", json);
        prefsEditor.commit();
        /**** TEMP ****/

        SharedPreferences appSharedPrefs = getBaseContext().getSharedPreferences("MONSTER", MODE_PRIVATE);

        json = appSharedPrefs.getString("Monsters", "");

        Type type = new TypeToken<ArrayList<Monster>>(){}.getType();
        ArrayList<Monster> monsters = gson.fromJson(json, type);

        adapter=new CustomAdapter(this, monsters);

        mGridView.setAdapter(adapter);

        mSocket = SocketSingleton.getInstance();

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);

        dialogBuilder = NiftyDialogBuilder.getInstance(this);
        mapView.isTutorial(isTutorial);

        if(isTutorial) {
            goldAmount = 100000000;
            mGoldAmount.setText(DecimalFormatSymbols.getInstance().getInfinity()); // Logo Infini pour le montant d'or pendant le tutoriel
            mTimer.setText(DecimalFormatSymbols.getInstance().getInfinity()); // Logo Infini pour le temps restant pendant le tutoriel

            // Permet d'attendre que la GridView soit créé avant de lancer le showcase des tutoriels (permet de cibler un élément de la liste)
            ViewTreeObserver vto = mGridView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                createTutorial();
                }
            });

            dialogBuilder.withTitle("Debut du tutoriel")
                    .withMessage("Bienvenue dans le tutoriel ! Vous allez decouvrir les bases pour être le meilleur des attaquants. Vous pouvez vous entraîner autant que vous voulez sur la partie visible de la carte. Une fois que vous vous sentez prêt, appuyez sur le bouton \"COMMENCER\"")
                    .withButton1Text("Ok !")
                    .withDialogColor("#34495e")
                    .withTitleColor("#1abc9c")
                    .isCancelable(false)
                    .isCancelableOnTouchOutside(false)
                    .withIcon(getResources().getDrawable(R.drawable.logo))
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                            baseShowCase.show();
                        }
                    })
                    .show();

            churchBase.setColorFilter(Color.BLACK);
            millBase.setColorFilter(Color.BLACK);


        } else {
            mBeginGame.setVisibility(View.GONE);
            mGoldAmount.setText(Integer.toString(goldAmount));
            timer = new CountDownTimer(partyDuration, 1000) { // adjust the milli seconds here

                public void onTick(long millisUntilFinished) {
                    long nbMinutesSinceBegin = ((partyDuration - millisUntilFinished)/60000)+1;
                    goldAmount += 10*nbMinutesSinceBegin;
                    mGoldAmount.setText(Integer.toString(goldAmount)); // Gagne de l'argent au fur et à mesure du temps
                    mTimer.setText(String.format("%d:%d ",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                    ));

                    for(int i = 0; i < adapter.getMonsters().size(); ++i) {
                        if (adapter.getMonster(i).getPrice() <= goldAmount){
                            System.out.println("Index : " + i);
                            adapter.getMonster(i).setDisable(false);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                public void onFinish() {

                    mTimer.setText("FIN !");
                    showEndModal("GAME OVER", "Vous n'avez pas réussi à détruire les bases dans le temps imparti !", R.drawable.tombstone);

                }
            }.start();



            dialogBuilder.withTitle("Debut de la partie")
                    .withMessage("Detruisez au moins une base ennemie en moins de 10 minutes pour remporter la partie. Bonne chance !")
                    .withButton1Text("Combattre !")
                    .isCancelableOnTouchOutside(false)
                    .isCancelable(false)
                    .withDialogColor("#34495e")
                    .withTitleColor("#1abc9c")
                    .withIcon(getResources().getDrawable(R.drawable.logo))
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogBuilder.dismiss();
                        }
                    })
                    .show();
        }




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
                        showEndModal("VICTOIRE", "Bravo ! Vous avez détruit toutes les bases", R.drawable.medal);
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

        mSocket.on("setup", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = (JSONObject) args[0];
                            if(data.getString("action").equals("start")) {
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("name", attackerName);
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
                    new CountDownTimer(500, 500) {

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            if(goldAmount < monster.getPrice()) {
                                monster.setDisable(true);
                            } else {
                                monster.setDisable(false);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }.start();
                    for(int i = 0; i < adapter.getMonsters().size(); ++i) {
                        if(goldAmount < adapter.getMonster(i).getPrice()) {
                            adapter.getMonster(i).setDisable(true);
                        }
                    }
                    adapter.notifyDataSetChanged();
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
        if(!isTutorial) {
            selectedBase = (selectedBase.equals("church")) ? "" : "church";
            mapView.generateChurchPaths(!selectedBase.equals(""));
        }
    }

    @OnClick(R.id.millBase)
    public void selectmillBase(View view) {
        if(!isTutorial) {
            selectedBase = (selectedBase.equals("windmill")) ? "" : "windmill" ;
            mapView.generateWindMillPath(!selectedBase.equals(""));
            mapView.setPathSelected(0);
        }
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

    public void activateSpeedPower(View view) {
        if(canPower2) {
            Log.d("speedpower", "speedpower activated");
            Snackbar.make(findViewById(R.id.relLayout), "Vitesse des monstres améliorées temporairement", Snackbar.LENGTH_SHORT).show();
            canPower2 = false;
            try {
                mSocket.emit("power", new JSONObject().put("power", "speed").put("name", attackerName));
                final float maxTime = 60000;
                int interval = 100;
                mPower2.setProgress(100, false);
                mPower2.setColorNormal(Color.GRAY);
                timer = new CountDownTimer(60000, interval) { // adjust the milli seconds here
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.beginGame)
    public void beginGame(View view) {
        dialogBuilder.withTitle("Fin du tutoriel")
            .withMessage("Vous venez de finir le tutoriel en tant qu'attaquant. Veuillez attendre que les defenseurs finissent également le tutoriel")
            .isCancelableOnTouchOutside(false)
            .withButton1Text("Revenir au tutoriel")
            .withDialogColor("#34495e")
            .withTitleColor("#1abc9c")
            .withIcon(getResources().getDrawable(R.drawable.logo))
            .setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                }
            }).show();
    }


    private void showEndModal(String title, String body, int icon) {
        final NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(this);

        dialogBuilder.withTitle(title)
            .withMessage(body)
            .withButton1Text("Rejouer")
            .isCancelableOnTouchOutside(false)
            .isCancelable(false)
            .withDialogColor("#34495e")
            .withTitleColor("#1abc9c")
            .withIcon(getResources().getDrawable(icon))
            .setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            })
            .show();
    }

    private void spendGold(int amount) {
        if(isTutorial) {
            mGoldAmount.setText(DecimalFormatSymbols.getInstance().getInfinity());
        } else {
            goldAmount -= amount;
            mGoldAmount.setText(Integer.toString(goldAmount));
        }

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

    private void createTutorial() {
        speedPowerShowCase = new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                .setTarget(mGridView.getChildAt(2))
                .setPrimaryText("Attaquer une base ennemie")
                .setSecondaryText("Vous disposez d'un pouvoir pour améliorer temporairement la vitesse de vos monstres présent sur la carte. Pour l'activer, secouez la tablette !")
                .setBackButtonDismissEnabled(false)
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setPromptBackground(new RectanglePromptBackground())
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            speedPowerShowCase.finish();
                        }
                    }
                }).create();

        monsterDisableShowCase = new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                .setTarget(mGridView.getChildAt(2))
                .setPrimaryText("Attaquer une base ennemie")
                .setSecondaryText("Certains monstres sont très puissants mais également très chers. Il faudra faire preuve de stratégie pour profiter au mieux de leur pouvoir.")
                .setBackButtonDismissEnabled(false)
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setPromptBackground(new RectanglePromptBackground())
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            monsterDisableShowCase.finish();
                            speedPowerShowCase.show();
                        }
                    }
                }).create();

        monsterShowCase = new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                .setTarget(mGridView.getChildAt(0))
                .setPrimaryText("Attaquer une base ennemie")
                .setSecondaryText("Vous disposez d'un certain nombre de monstres pour attaquer l'ennemie. Cliquez dessus pour l'envoyer au combat !")
                .setBackButtonDismissEnabled(false)
                .setPromptBackground(new RectanglePromptBackground())
                .setPromptFocal(new RectanglePromptFocal())
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            monsterShowCase.finish();
                            monsterDisableShowCase.show();
                        }
                    }
                }).create();

        pathShowCase = new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                .setTarget(findViewById(R.id.pathTutorial))
                .setPrimaryText("Attaquer une base ennemie")
                .setSecondaryText("Selectionner un chemin de couleurs parmi les chemins proposés")
                .setBackButtonDismissEnabled(false)
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setPromptBackground(new RectanglePromptBackground())
                .setPromptFocal(new RectanglePromptFocal())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            pathShowCase.finish();
                            monsterShowCase.show();
                        }
                    }
                }).create();


        baseShowCase = new MaterialTapTargetPrompt.Builder(HomeActivity.this)
                .setTarget(findViewById(R.id.castleBase))
                .setPrimaryText("Attaquer une base ennemie")
                .setSecondaryText("Pour attaquer une base ennemie, vous devez dans un premier temps la selectionner")
                .setBackButtonDismissEnabled(false)
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener()
                {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state)
                    {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                        {
                            baseShowCase.finish();
                            pathShowCase.show();
                        }
                    }
                }).create();
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = values[SensorManager.DATA_X];
                float y = values[SensorManager.DATA_Y];
                float z = values[SensorManager.DATA_Z];

                float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    Log.d("sensor", "shake detected w/ speed: " + speed);
                    activateSpeedPower(null);
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(int i, int i1) {

    }
}
