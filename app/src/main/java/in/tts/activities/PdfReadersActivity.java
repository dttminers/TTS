package in.tts.activities;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import com.google.firebase.crash.FirebaseCrash;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import in.tts.R;
import in.tts.adapters.PdfPagesAdapter;
import in.tts.classes.ToSetMore;
import in.tts.model.AudioSetting;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class PdfReadersActivity extends AppCompatActivity {

    private RecyclerView rv;
    private LinearLayoutManager layoutManager;
    private PdfPagesAdapter pdfPagesAdapter;

    private ProgressBar progressBarSpeak;

    private MediaPlayer mediaPlayer;

    private LinearLayout speakLayout, llCustom_loader;
    private Button reload, forward, playPause, backward, close;

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;

    private StringBuilder stringBuilder;
    private ArrayList<Bitmap> list = new ArrayList<Bitmap>();

    private File file;

    private double startTime = 0;
    private double finalTime = 0;

    private int forwardTime = 5000;
    private int backwardTime = 5000;

    private boolean playerStatus = false;

    public static int oneTimeOnly = 0;

    private TextToSpeech tts;
    int firstVisibleItem;

    private AudioSetting audioSetting;
    private PrefManager prefManager;
    ArrayList<String> male_voice_array = new ArrayList<String>();
    ArrayList<String> female_voice_array = new ArrayList<String>();
    String selectedLang;
    String langCountry;
    String selectedVoice;
    String v_male = "en-us-x-sfg#male_3-local";
    String v_female = "en-us-x-sfg#female_2-local";
    Voice v = null;
    String voice = "";
    int lang;
    String tempFilename = "", tempDestFile= "";

    private int resumePosition;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            CommonMethod.toCallLoader(PdfReadersActivity.this, "Loading...");
            setContentView(R.layout.activity_pdf_readers);
            if (getIntent().getExtras() != null) {

                if (getIntent().getStringExtra("file") != null) {

                    file = new File(getIntent().getStringExtra("file").trim());

                    if (file.exists()) {

                        if (getSupportActionBar() != null) {
                            CommonMethod.toSetTitle(getSupportActionBar(), PdfReadersActivity.this, file.getName());
                        }

                        rv = findViewById(R.id.rvPDFReader);
                        llCustom_loader = findViewById(R.id.llCustom_loader12);
                        rv.setVisibility(View.VISIBLE);

                        speakLayout = findViewById(R.id.llPDFReader);
                        reload = findViewById(R.id.reload);
                        forward = findViewById(R.id.forward);
                        playPause = findViewById(R.id.playPause);
                        backward = findViewById(R.id.backward);
                        close = findViewById(R.id.close);

                        progressBarSpeak = findViewById(R.id.progressBarSpeak);

                        stringBuilder = new StringBuilder();
                        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

                        pdfRenderer = new PdfRenderer(fileDescriptor);
                        for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                            showPage(i);
                        }

                        if (list.size() > 0) {
                            layoutManager = new LinearLayoutManager(PdfReadersActivity.this);
                            rv.setLayoutManager(layoutManager);
                            rv.setHasFixedSize(true);
                            rv.setItemAnimator(new DefaultItemAnimator());
                            pdfPagesAdapter = new PdfPagesAdapter(PdfReadersActivity.this, list);
                            rv.setAdapter(pdfPagesAdapter);
                            llCustom_loader.setVisibility(View.GONE);
                            rv.setVisibility(View.VISIBLE);
                            CommonMethod.toCloseLoader();
                        } else {
                            CommonMethod.toCloseLoader();
                            CommonMethod.toDisplayToast(PdfReadersActivity.this, "Can't Open Pdf File 0");
                            finish();
                        }

                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    if (playerStatus) {
                                        if (mediaPlayer != null) {
                                            if (mediaPlayer.isPlaying()) {
                                                mediaPlayer.stop();
                                            }
                                            playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
                                            playerStatus = false;
                                            mediaPlayer.release();
                                            CommonMethod.toDisplayToast(PdfReadersActivity.this, " Audio player stopped");
                                        } else {
                                            CommonMethod.toDisplayToast(PdfReadersActivity.this, " Audio player is already stopped");
                                        }
                                    } else {
                                        CommonMethod.toDisplayToast(PdfReadersActivity.this, " Audio player is already stopped");
                                    }
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                    CommonMethod.toCloseLoader();
                                    Crashlytics.logException(e);
                                    FirebaseCrash.report(e);
                                    CommonMethod.toDisplayToast(PdfReadersActivity.this, " Audio player is already stopped");
                                }
                            }
                        });

                        reload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    if (playerStatus) {
                                        mediaPlayer.stop();
                                        playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
                                        playerStatus = false;
                                        mediaPlayer.release();
                                        CommonMethod.toDisplayToast(PdfReadersActivity.this, " Replaying audio again.");
                                        toPlayAudio();
                                    } else if (tempDestFile.length() > 0) {
                                        CommonMethod.toDisplayToast(PdfReadersActivity.this, " Replaying audio again");
                                        toPlayAudio();
                                    } else {
                                        CommonMethod.toDisplayToast(PdfReadersActivity.this, " Replaying audio again...");
                                        toGetData((firstVisibleItem + 1));
                                    }
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                    CommonMethod.toCloseLoader();
                                    Crashlytics.logException(e);
                                    FirebaseCrash.report(e);
                                    CommonMethod.toDisplayToast(PdfReadersActivity.this, " Audio player is already stopped");
                                }
                            }
                        });

                        playPause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    if (playerStatus) {
                                        mediaPlayer.pause();
                                        playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
                                        playerStatus = !playerStatus;
                                        resumePosition = mediaPlayer.getCurrentPosition();
                                        CommonMethod.toDisplayToast(PdfReadersActivity.this, " pause");
                                    } else if (tempDestFile.length() > 0) {
                                        CommonMethod.toDisplayToast(PdfReadersActivity.this, " play");
                                        if (!mediaPlayer.isPlaying()) {
                                            mediaPlayer.seekTo(resumePosition);
                                            mediaPlayer.start();
                                            playPause.setBackground(getResources().getDrawable(R.drawable.pause_button));
                                            playerStatus = !playerStatus;
                                        }
                                    } else {
                                        CommonMethod.toDisplayToast(PdfReadersActivity.this, " playing audio...");
                                        toGetData((firstVisibleItem + 1));
                                    }
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                    CommonMethod.toCloseLoader();
                                    Crashlytics.logException(e);
                                    FirebaseCrash.report(e);
                                }
                            }
                        });

                        forward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    Log.d("Tag", " forward playing : " + startTime + ":" + ((int) startTime) + ":" + mediaPlayer.getCurrentPosition());
                                    int temp = (int) startTime;
                                    if ((temp + forwardTime) <= finalTime) {
                                        startTime = startTime + forwardTime;
                                        mediaPlayer.seekTo((int) startTime);
                                        Toast.makeText(getApplicationContext(), "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                    CommonMethod.toCloseLoader();
                                    Crashlytics.logException(e);
                                    FirebaseCrash.report(e);
                                }
                            }
                        });

                        backward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    Log.d("Tag", " backward playing : " + startTime + ":" + ((int) startTime) + ":" + mediaPlayer.getCurrentPosition());
                                    int temp = (int) startTime;
                                    if ((temp - backwardTime) > 0) {
                                        startTime = startTime - backwardTime;
                                        mediaPlayer.seekTo((int) startTime);
                                        Toast.makeText(getApplicationContext(), "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                    CommonMethod.toCloseLoader();
                                    Crashlytics.logException(e);
                                    FirebaseCrash.report(e);
                                }
                            }
                        });

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            rv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                                @Override
                                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                                }
                            });
                        }

                        PrefManager prefManager = new PrefManager(PdfReadersActivity.this);
                        ArrayList<String> list = prefManager.toGetPDFListRecent();
                        if (list != null) {
                            if (!list.contains(getIntent().getStringExtra("file").trim())) {
                                list.add(getIntent().getStringExtra("file").trim());
                            }
                        } else {
                            list = new ArrayList<>();
                            list.add(getIntent().getStringExtra("file").trim());
                        }
                        prefManager.toSetPDFFileListRecent(list);

                        CommonMethod.toCloseLoader();
                    } else {
                        CommonMethod.toCloseLoader();
                        CommonMethod.toDisplayToast(PdfReadersActivity.this, "Can't file does not exists");
                        finish();
                    }
                } else {
                    CommonMethod.toCloseLoader();
                    CommonMethod.toDisplayToast(PdfReadersActivity.this, "Can't Open Pdf File 1");
                    finish();
                }
            } else {
                CommonMethod.toCloseLoader();
                CommonMethod.toDisplayToast(PdfReadersActivity.this, "Can't Open Pdf File 2");
                finish();
            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(final int index) {
        try {
            if (pdfRenderer.getPageCount() <= index) {
                return;
            }
            if (null != currentPage) {
                currentPage.close();
            }
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            currentPage = pdfRenderer.openPage(index);
            Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            list.add(bitmap);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toGetData(int pos) {
        try {
            PdfReader pr = new PdfReader(file.getPath());
            String text = PdfTextExtractor.getTextFromPage(pr, pos);
            if (text.trim().length() != 0) {
                if (text.length()>200) {
                    Log.d("Tag ", " text length : "+ text.length());
                toSpeak(text);
                }else if (text.length() > 150) {
                    toSpeak(text.substring(0, 150));
                } else if (text.length() > 100) {
                    toSpeak(text.substring(0, 100));
                } else if (text.length() > 50) {
                    toSpeak(text.substring(0, 50));
                } else {
                    toSpeak(text);
                }
            } else {
                CommonMethod.toDisplayToast(PdfReadersActivity.this, " Unable to get data");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSpeak(final String string) {
        try {
            tts = new TextToSpeech(PdfReadersActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    try {
                        audioSetting = AudioSetting.getAudioSetting(PdfReadersActivity.this);
                        selectedLang = audioSetting.getLangSelection().getLanguage();
                        langCountry = audioSetting.getLangSelection().getCountry();
                        selectedVoice = audioSetting.getVoiceSelection();

                        tts.setEngineByPackageName("com.google.android.tts");
                        tts.setPitch((float) 0.8);
                        tts.setSpeechRate(audioSetting.getVoiceSpeed());

                        if (audioSetting.getLangSelection().equals("hin_IND")) {
                            lang = tts.setLanguage(new Locale("hin", "IND"));
                        } else if (audioSetting.getLangSelection().equals("mar_IND")) {
                            lang = tts.setLanguage(new Locale("mar", "IND"));
                        } else if (audioSetting.getLangSelection().equals("ta_IND")) {
                            lang = tts.setLanguage(new Locale("ta", "IND"));
                        } else {
                            lang = tts.setLanguage(Locale.US);
                        }

                        Set<String> a = new HashSet<>();
                        a.add("female");
                        a.add("male");

                        //Get all available voices
                        for (Voice tmpVoice : tts.getVoices()) {
                            if (tmpVoice.getLocale().equals(audioSetting.getLangSelection())) {
                                voiceValidator(tmpVoice.getName());
                            }
                        }

                        if (selectedVoice.equalsIgnoreCase("male")) {
                            if (male_voice_array.size() > 0) {
                                voice = male_voice_array.get(0);
                                v = new Voice(voice, new Locale(selectedLang, langCountry), 400, 200, true, a);
                                tts.setVoice(v);
                            } else {
                                CommonMethod.toDisplayToast(PdfReadersActivity.this, "Selected language not found. Reading data using default language");
                                v = new Voice(v_male, new Locale("en", "US"), 400, 200, true, a);
                                tts.setVoice(v);
                            }

                        } else {
                            if (female_voice_array.size() > 0) {
                                voice = female_voice_array.get(0);
                                v = new Voice(voice, new Locale(selectedLang, langCountry), 400, 200, true, a);
                                tts.setVoice(v);
                            } else {
                                CommonMethod.toDisplayToast(PdfReadersActivity.this, "Selected language not found. Reading data using default language");
                                v = new Voice(v_female, new Locale("en", "US"), 400, 200, true, a);
                                tts.setVoice(v);
                            }
                        }


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Log.d("TTS_TAG", " DATA LAng " + ":" + audioSetting.getLangSelection() + ":" + lang + ":" + tts.getLanguage() + ":" + tts.getAvailableLanguages());
                        }
                        if (i == TextToSpeech.SUCCESS) {
                            if (lang == TextToSpeech.LANG_MISSING_DATA
                                    || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.d("TTS_TAG", "This Language is not supported");
                            } else {
                                Log.d("TTS_TAG", " Else ");
                            }
                        } else {
                            Log.d("TTS_TAG", "Initialization Failed!");
                        }

                        Log.d("log", "initi");


                        String exStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                        Log.d("MainActivity", "exStoragePath : " + exStoragePath);
                        File appTmpPath = new File(exStoragePath + "/sounds/");
                        boolean isDirectoryCreated = appTmpPath.mkdirs();
                        Log.d("MainActivity", "directory " + appTmpPath + " is created : " + isDirectoryCreated);
                        long pp = System.currentTimeMillis();

                        tempFilename = pp + ".wav";
                        tempDestFile = appTmpPath.getAbsolutePath() + File.separator + tempFilename;
                        Log.d("MainActivity", "tempDestFile : " + tempDestFile);
                        HashMap<String, String> myHashRender = new HashMap<>();
                        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, string);
                        int i3 = tts.synthesizeToFile(string, myHashRender, tempDestFile);
                        Log.d("TAG ", " errrr " + i3);
                        if (i3 == TextToSpeech.SUCCESS) {
                            CommonMethod.toDisplayToast(PdfReadersActivity.this, "Saved ");
                        }
                        toPlayAudio();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            }, "com.google.android.tts");
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void toPlayAudio() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("Tag", " Data " + tempDestFile);
                    speakLayout.setVisibility(View.VISIBLE);
                    progressBarSpeak.setVisibility(View.GONE);

                    setVolumeControlStream(AudioManager.STREAM_MUSIC);
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    File file = new File(tempDestFile);
                    Log.d("TAG", " suixzse " + CommonMethod.getFileSize(file));
                    FileInputStream fis = new FileInputStream(file);
                    Log.d("Tag", " Playing3" + mediaPlayer.isPlaying());
                    mediaPlayer.setDataSource(fis.getFD());
                    Log.d("Tag", " Playing36" + mediaPlayer.isPlaying());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Log.d("Tag", " Playing22" + mediaPlayer.isPlaying());
                    finalTime = mediaPlayer.getDuration();
                    startTime = mediaPlayer.getCurrentPosition();
                    Log.d("TAG", " Time " + finalTime + " " + startTime);
                    if (oneTimeOnly == 0) {
//                                        seekbar.setMax((int) finalTime);
                        Log.d("TAg", " Time 22 " + ((int) finalTime));
                        oneTimeOnly = 1;
                    }

//                                    tx2.setText(
                    Log.d("TAG", " TIME 11 : " +
                            String.format("%d min, %d sec",
                                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                                    finalTime)))
                    );

//                                    tx1.setText(
                    Log.d("TAG", " TIME 12 : " +
                            String.format("%d min, %d sec",
                                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                                    startTime)))
                    );


                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            Log.d("Tag", " Playing10" + mediaPlayer.isPlaying() + mp.isPlaying());
                            mp.start();
                            Log.d("Tag", " Playing11" + mediaPlayer.isPlaying() + mp.isPlaying());
                            playPause.setBackground(getResources().getDrawable(R.drawable.pause_button));
                            playerStatus = true;
                            Log.d("Tag", " Playing12" + mediaPlayer.isPlaying());
                        }
                    });
                    Log.d("Tag", " Playing2" + mediaPlayer.isPlaying());
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlaye) {
                            Log.d("Tag", " Playing13" + mediaPlayer.isPlaying() + mediaPlaye.isPlaying());
                            mediaPlaye.stop();
                            playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
                            playerStatus = false;
                            mediaPlaye.release();
                        }
                    });
                    Log.d("Tag", " Playing4" + mediaPlayer.isPlaying());
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                    playerStatus = false;
                }
            }
        }, 5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.speak_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            ToSetMore.MenuOptions(PdfReadersActivity.this, item);
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                case R.id.menuSpeak:
                    try {
                        progressBarSpeak.setVisibility(View.VISIBLE);
                        toGetData((firstVisibleItem + 1));
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                    break;
                default:
                    return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (tts != null) {
                tts.stop();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    try{
            if (tts != null) {
                tts.shutdown();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void voiceValidator(String voice) {
        try {
            if (voice.contains("#female")) {
                female_voice_array.add(voice);
            }
            if (voice.contains("#male")) {
                male_voice_array.add(voice);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }
}