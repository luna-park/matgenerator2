package org.lunapark.develop.matgenerator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.ispeech.SpeechSynthesis;
import org.ispeech.SpeechSynthesisEvent;
import org.ispeech.error.BusyException;
import org.ispeech.error.InvalidApiKeyException;
import org.ispeech.error.NoNetworkException;
import org.lunapark.develop.matgenerator.util.Clipboard;
import org.lunapark.develop.matgenerator.util.CustomAdapter;
import org.lunapark.develop.matgenerator.util.StringField;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MatGen";
    private final String voiceMale = "rurussianmale";
    // private final String voiceFemale = "rurussianfemale";

    private Button btnGenerate, btnSpeech, btnCopy, btnDownload, btnSave;
    private TextView tvResult, tvInfo;
    private RelativeLayout mainLayout;
    private ListView lvSave;

    private ToggleButton toggleButtonAuto, toggleButtonVoice;
    private SpeechSynthesis synthesis;
    //    private Context context;
    private TextToSpeech ttsObject;
    private Random randomInt;
    private String[] adj01, adj02, noun01, noun02;
    private String phraseShow, phraseSpeech, phraseBuffer;
    private StringField strField;
    private CustomAdapter adapter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        context = this.getApplicationContext();
        preferences = getPreferences(MODE_PRIVATE);
        randomInt = new Random();
        strField = new StringField();

        mainLayout = (RelativeLayout) findViewById(R.id.mainlayout);
//        mainLayout.setEnabled(false);
        btnGenerate = (Button) findViewById(R.id.btnGenerate);
        btnSpeech = (Button) findViewById(R.id.btnSpeech);
        btnCopy = (Button) findViewById(R.id.btnCopy);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDownload = (Button) findViewById(R.id.btnDownload);

        tvResult = (TextView) findViewById(R.id.tvResult);
        tvInfo = (TextView) findViewById(R.id.tvInfo);

        toggleButtonAuto = (ToggleButton) findViewById(R.id.tBtnAuto);
        toggleButtonVoice = (ToggleButton) findViewById(R.id.tBtnVoice);

        lvSave = (ListView) findViewById(R.id.lvSave);
        lvSave.setItemsCanFocus(true);

        //instantiate custom adapter
        adapter = new CustomAdapter(this, preferences);

        //handle listview and assign adapter
        lvSave.setAdapter(adapter);
        lvSave.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StringField sField = (StringField) adapter.getItem(i);
                strField = sField;
                setResult();
            }
        });

        btnGenerate.setOnClickListener(this);
        btnSpeech.setOnClickListener(this);
        btnCopy.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnDownload.setOnClickListener(this);

        adj01 = getResources().getStringArray(R.array.adjective_pt_1);
        adj02 = getResources().getStringArray(R.array.adjective_pt_2);

        noun01 = getResources().getStringArray(R.array.adjective_pt_1);
        noun02 = getResources().getStringArray(R.array.noun_pt_2);

        // Hide some shit
        btnDownload.setVisibility(View.INVISIBLE);
//        lvSave.setVisibility(View.INVISIBLE);
//        btnSave.setVisibility(View.INVISIBLE);

        String appInfo = "";
        try {
            appInfo = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvInfo.setText(getString(R.string.app_name) + " v" + appInfo + ". " + getString(R.string.txt_info));

        /**
         * TTS
         */


        prepareTTSEngine();


        generatePhrase();
        tvResult.setText(getResources().getString(R.string.label_init));
    }

    /**
     * Prepare TTS
     */
    private void prepareTTSEngine() {
        // Init iSpeech engine
        try {
            synthesis = SpeechSynthesis.getInstance(this);

            synthesis.setSpeechSynthesisEvent(new SpeechSynthesisEvent() {

                public void onPlaySuccessful() {
                    btnGenerate.setEnabled(true);
                    btnSpeech.setEnabled(true);
                    synthesis.stop();
                }

                public void onPlayStopped() {
                    btnGenerate.setEnabled(true);
                    btnSpeech.setEnabled(true);
                }

                public void onPlayFailed(Exception e) {
                    Log.e(TAG, "onPlayFailed");

                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this);
                    builder.setMessage("Error: " + e.toString())
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                public void onPlayStart() {
                    btnGenerate.setEnabled(false);
                    btnSpeech.setEnabled(false);
                }

                @Override
                public void onPlayCanceled() {
                    btnGenerate.setEnabled(true);
                    btnSpeech.setEnabled(true);
                }

            });

        } catch (InvalidApiKeyException e) {
            Log.e(TAG, "Invalid API key\n" + e.getStackTrace());
            Toast.makeText(this, "ERROR: Invalid iSpeech API key",
                    Toast.LENGTH_LONG).show();
        }
        synthesis.setStreamType(AudioManager.STREAM_MUSIC);
        // Init internal tts
        ttsObject = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale locale = new Locale("ru");
                    ttsObject.setLanguage(locale);
                    tvResult.setText(phraseShow);
                    mainLayout.setEnabled(true);
                    Toast.makeText(getApplicationContext(),
                            "SUCCESS: Internal TTS-engine start",
                            Toast.LENGTH_LONG).show();


                } else {
                    Toast.makeText(getApplicationContext(),
                            "ERROR: Internal TTS-engine failed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

//        ttsObject.setSpeechRate(0.8f);
    }


    /**
     * Генерация фразы
     */
    private void generatePhrase() {

        int rndAdj01 = randomInt.nextInt(adj01.length);
        int rndAdj02 = randomInt.nextInt(adj02.length);
        int rndNoun01 = randomInt.nextInt(noun01.length);
        int rndNoun02 = randomInt.nextInt(noun02.length);

        strField.adj1 = adj01[rndAdj01];
        strField.adj2 = adj02[rndAdj02];
        strField.noun1 = noun01[rndNoun01].toLowerCase();
        strField.noun2 = noun02[rndNoun02];

        setResult();
    }

    private void setResult() {
        phraseSpeech = strField.adj1 + " " + strField.adj2 + " " + strField.noun1 + " " + strField.noun2;
        phraseShow = strField.adj1 + strField.adj2 + "\n" + strField.noun1 + strField.noun2;
        phraseBuffer = strField.adj1 + strField.adj2 + " " + strField.noun1 + strField.noun2;

        tvResult.setText(phraseShow);
    }


    /**
     * Произношение
     */
    private void speechPhrase() {
        btnGenerate.setEnabled(false);
        btnSpeech.setEnabled(false);
        try {

            if (toggleButtonVoice.isChecked() && isOnline()) {
                synthesis.setVoiceType(voiceMale);
                synthesis.speak(phraseSpeech);

            } else {
                if (!isOnline()) {
                    Toast.makeText(this,
                            "Сеть недоступна. Используется встроенный синтезатор",
                            Toast.LENGTH_LONG).show();
                }
                if (ttsObject != null) ttsObject.speak(phraseShow, TextToSpeech.QUEUE_FLUSH, null);

                btnGenerate.setEnabled(true);
                btnSpeech.setEnabled(true);

            }

        } catch (BusyException e) {
            Log.e(TAG, "SDK is busy");
            e.printStackTrace();
            Toast.makeText(this, "ERROR: iSpeech SDK is busy",
                    Toast.LENGTH_LONG).show();
        } catch (NoNetworkException e) {
            Log.e(TAG, "Network is not available\n" + e.getStackTrace());
            Toast.makeText(this,
                    "Сеть недоступна. Попробуйте переключить голос на [Ж]",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Обработка нажатий
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnGenerate:
                generatePhrase();
                if (toggleButtonAuto.isChecked()) {
                    speechPhrase();
                }
                break;

            case R.id.btnSpeech:
                speechPhrase();
                break;

            case R.id.btnCopy:
                Clipboard.copy(this, phraseBuffer);
                Toast.makeText(this, phraseBuffer + " " + getString(R.string.hint_copy),
                        Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnSave:
                adapter.add(strField);
                break;

            case R.id.btnDownload:
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                break;
        }

    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            System.exit(0);
//        }
//        return false;
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Ask the user if they want to quit
            new AlertDialog.Builder(this)
                    // .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.txt_quit))
                    .setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    System.exit(0);
                                }

                            }).setNegativeButton(android.R.string.no, null)
                    .show();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }


    @Override
    protected void onDestroy() {
        if (ttsObject != null) {
            ttsObject.stop();
            ttsObject.shutdown();
        }
        super.onDestroy();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null)
                && activeNetwork.isConnectedOrConnecting();
    }

}
