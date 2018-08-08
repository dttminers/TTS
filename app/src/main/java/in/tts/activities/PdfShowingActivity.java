package in.tts.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import in.tts.R;
import in.tts.adapters.PDfPagesShowingAdapter;
import in.tts.classes.ToSetMore;
import in.tts.model.AudioSetting;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;


public class PdfShowingActivity extends AppCompatActivity {

    private ViewPager vp;
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

    public int currentPageRead = 1;
    private TextToSpeech tts;

    private AudioSetting audioSetting;
    private PrefManager prefManager;

    private ArrayList<String> male_voice_array = new ArrayList<String>();
    private ArrayList<String> female_voice_array = new ArrayList<String>();

    private String selectedLang;
    private String langCountry;
    private String selectedVoice;

    private String v_male = "en-us-x-sfg#male_3-local";
    private String v_female = "en-us-x-sfg#female_2-local";

    private Voice v = null;

    private String voice = "", tempFilename = "", tempDestFile = "";
    private int resumePosition, lang;

    @Override
    @AddTrace(name = "onCreatePdfShowingActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonMethod.toReleaseMemory();
        setContentView(R.layout.activity_pdf_showing);
        new ToGetPdfImages().execute();
        toBindViews();
        toSetRecent();
    }

    private void toSetRecent() {
        CommonMethod.toReleaseMemory();
        try {

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    PrefManager prefManager = new PrefManager(PdfShowingActivity.this);
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
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toBindViews() {
        try {
            CommonMethod.toReleaseMemory();
            llCustom_loader = findViewById(R.id.llCustom_loader120);
            llCustom_loader.setVisibility(View.VISIBLE);

            vp = findViewById(R.id.vpPdf);

            speakLayout = findViewById(R.id.llPDFReader);
            reload = findViewById(R.id.reload);
            forward = findViewById(R.id.forward);
            playPause = findViewById(R.id.playPause);
            backward = findViewById(R.id.backward);
            close = findViewById(R.id.close);

            progressBarSpeak = findViewById(R.id.progressBarSpeak120);
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
//                                CommonMethod.toDisplayToast(PdfShowingActivity.this, " Audio player stopped");
                            } else {
//                                CommonMethod.toDisplayToast(PdfShowingActivity.this, " Audio player is already stopped");
                            }
                        } else {
//                            CommonMethod.toDisplayToast(PdfShowingActivity.this, " Audio player is already stopped");
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
//                        CommonMethod.toDisplayToast(PdfShowingActivity.this, " Audio player is already stopped");
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
//                            CommonMethod.toDisplayToast(PdfShowingActivity.this, " Replaying audio again.");
                            toPlayAudio(true);
                        } else if (tempDestFile.length() > 0) {
//                            CommonMethod.toDisplayToast(PdfShowingActivity.this, " Replaying audio again");
                            toPlayAudio(true);
                        } else {
//                            CommonMethod.toDisplayToast(PdfShowingActivity.this, " Replaying audio again...");
                            toGetData(vp.getCurrentItem() + 1, true);
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
//                        CommonMethod.toDisplayToast(PdfShowingActivity.this, " Audio player is already stopped");
                    }
                }
            });

            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        if (currentPageRead == vp.getCurrentItem() + 1) {
                        if (playerStatus) {
                            mediaPlayer.pause();
                            playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
                            playerStatus = !playerStatus;
                            resumePosition = mediaPlayer.getCurrentPosition();
//                            CommonMethod.toDisplayToast(PdfShowingActivity.this, " pause");
//                        } else if (tempDestFile.length() > 0) {
////                            CommonMethod.toDisplayToast(PdfShowingActivity.this, " play");
//                            if (!playerStatus) {
////                                CommonMethod.toDisplayToast(PdfShowingActivity.this, " play10  " + resumePosition);
//                                if (resumePosition > 0) {
////                                    CommonMethod.toDisplayToast(PdfShowingActivity.this, " play11 " + resumePosition);
//                                    mediaPlayer.seekTo(resumePosition);
//                                    mediaPlayer.start();
//                                    playPause.setBackground(getResources().getDrawable(R.drawable.pause_button));
//                                    playerStatus = !playerStatus;
//                                } else {
////                                    CommonMethod.toDisplayToast(PdfShowingActivity.this, " play12 " + resumePosition);
//                                    playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
//                                    playerStatus = !playerStatus;
//                                    toPlayAudio(true);
//                                }
//                            } else {
//                                toGetData(vp.getCurrentItem() + 1, true);
////                                CommonMethod.toDisplayToast(PdfShowingActivity.this, " play2");
//                            }
                        } else {
//                            CommonMethod.toDisplayToast(PdfShowingActivity.this, " playing audio...");
                            toGetData(vp.getCurrentItem() + 1, true);
                        }
//                        } else {
//                            CommonMethod.toDisplayToast(PdfShowingActivity.this, " Page Changed");
//                            toGetData(vp.getCurrentItem() + 1, true);
//                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
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
                            CommonMethod.toDisplayToast(getApplicationContext(), "You have Jumped forward 5 seconds");
                        } else {
                            CommonMethod.toDisplayToast(getApplicationContext(), "Cannot jump forward 5 seconds");
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
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
                            CommonMethod.toDisplayToast(getApplicationContext(), "You have Jumped backward 5 seconds");
                        } else {
                            CommonMethod.toDisplayToast(getApplicationContext(), "Cannot jump backward 5 seconds");
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(final int index) {
        try {
            CommonMethod.toReleaseMemory();
            if (pdfRenderer.getPageCount() <= index) {
                return;
            }
            if (null != currentPage) {
                currentPage.close();
            }
            currentPage = pdfRenderer.openPage(index);
            Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth() + 500, currentPage.getHeight() + 500, Bitmap.Config.ARGB_8888);
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            list.add(bitmap);
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    private void toGetData(int pos, boolean status) {
        try {
            currentPageRead = pos;
            PdfReader pr = new PdfReader(file.getPath());
            String text = PdfTextExtractor.getTextFromPage(pr, pos);
            Log.d("TAG ", " text " + pos + ":" + text);
            if (text.trim().length() != 0) {
//                if (text.length() > 200) {
                Log.d("Tag ", " text length : " + text.length());
//                    toSpeak(text);
//                } else
//                if (text.length() > 150) {
//                    toSpeak(text.substring(0, 150), status);
//                } else if (text.length() > 100) {
//                    toSpeak(text.substring(0, 100), status);
//                } else if (text.length() > 50) {
//                    toSpeak(text.substring(0, 50), status);
//                } else {
                toSpeak(text, status);
//                }
                CommonMethod.toReleaseMemory();
            } else {
                CommonMethod.toReleaseMemory();
                CommonMethod.toDisplayToast(PdfShowingActivity.this, " Unable to get data");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    private void toSpeak(final String string, final boolean status) {
        try {
            CommonMethod.toReleaseMemory();
            if (status) {
                progressBarSpeak.setVisibility(View.VISIBLE);
            }
            tts = new TextToSpeech(PdfShowingActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    try {
                        String exStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                        Log.d("TAG", "exStoragePath : " + exStoragePath);
                        File appTmpPath = new File(exStoragePath + "/READ_IT/Audio/");
                        boolean isDirectoryCreated = appTmpPath.mkdirs();
                        Log.d("TAG", "directory " + appTmpPath + " is created : " + isDirectoryCreated);
//                        long pp = System.currentTimeMillis();
                        tempFilename = (file.getName().substring(0, (file.getName().length() - 4))) + String.valueOf(vp.getCurrentItem() + 1) + ".wav";
                        tempDestFile = appTmpPath.getAbsolutePath() + File.separator + tempFilename;
                        Log.d("TAG", " File Already exists " + (!new File(tempDestFile).exists() && CommonMethod.getFileSize(new File(tempDestFile)).equals("0 B")));
                        if (new File(tempDestFile).exists()) {
                            if (CommonMethod.getFileSize(new File(tempDestFile)).equals("0 B")) {
                                toGenerateAudio(status, i, string);
                            } else {
                                toPlayAudio(true);
                            }
                        } else {
                            toGenerateAudio(status, i, string);
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                    CommonMethod.toReleaseMemory();
                }
            }, "com.google.android.tts");
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toGenerateAudio(final boolean status, int i, String string) {
        try {
            CommonMethod.toReleaseMemory();
            audioSetting = AudioSetting.getAudioSetting(PdfShowingActivity.this);
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
            if (tts != null) {
                for (Voice tmpVoice : tts.getVoices()) {
                    if (tmpVoice.getLocale().equals(audioSetting.getLangSelection())) {
                        voiceValidator(tmpVoice.getName());
                    }
                }
            }

            if (selectedVoice.equalsIgnoreCase("male")) {
                if (male_voice_array.size() > 0) {
                    voice = male_voice_array.get(0);
                    v = new Voice(voice, new Locale(selectedLang, langCountry), 400, 200, true, a);
                    tts.setVoice(v);
                } else {
                    CommonMethod.toDisplayToast(PdfShowingActivity.this, "Selected language not found. Reading data using default language");
                    v = new Voice(v_male, new Locale("en", "US"), 400, 200, true, a);
                    tts.setVoice(v);
                }

            } else {
                if (female_voice_array.size() > 0) {
                    voice = female_voice_array.get(0);
                    v = new Voice(voice, new Locale(selectedLang, langCountry), 400, 200, true, a);
                    tts.setVoice(v);
                } else {
                    CommonMethod.toDisplayToast(PdfShowingActivity.this, "Selected language not found. Reading data using default language");
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

            Log.d("TAG", "tempDestFile : " + tempDestFile);
            HashMap<String, String> myHashRender = new HashMap<>();
            myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, string);
            int i3 = tts.synthesizeToFile(string, myHashRender, tempDestFile);
            Log.d("TAG ", " errrr " + i3);
            if (i3 == TextToSpeech.SUCCESS) {
//                                CommonMethod.toDisplayToast(PdfShowingActivity.this, "Saved ");
            } else {
                CommonMethod.toDisplayToast(PdfShowingActivity.this, " Can't play audio ");
                progressBarSpeak.setVisibility(View.GONE);
            }

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {
                    Log.d("TAG", "Utter onStart" + status + ":" + s);
                }

                @Override
                public void onDone(String s) {
                    Log.d("TAG", "Utter onDone" + status + ":" + s);
                }

                @Override
                public void onError(String s) {
                    Log.d("TAG", "Utter onError" + status + ":" + s);
                }
            });

            tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                @Override
                public void onUtteranceCompleted(String s) {
                    Log.d("Tag", "Utter onUtteranceCompleted  " + status + ":" + s);
                    if (status) {
                        toPlayAudio(status);
                    }
                }
            });
        } catch (Exception | Error e) {
            CommonMethod.toReleaseMemory();
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void toPlayAudio(final boolean b) {
        CommonMethod.toReleaseMemory();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
        try {
            if (b) {
                progressBarSpeak.setVisibility(View.VISIBLE);
            }
            mediaPlayer = new MediaPlayer();
            Log.d("Tag", " Data " + tempDestFile);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
            if (b) {
                progressBarSpeak.setVisibility(View.GONE);
            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d("Tag", " Playing10" + mediaPlayer.isPlaying() + mp.isPlaying());
                    mp.start();
                    if (b) {
                        progressBarSpeak.setVisibility(View.GONE);
                    }
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
                    resumePosition = 0;
                    if (b) {
                        progressBarSpeak.setVisibility(View.GONE);
                    }
                    mediaPlaye.release();
                }
            });
            Log.d("Tag", " Playing4" + mediaPlayer.isPlaying());
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toReleaseMemory();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            playerStatus = false;
            CommonMethod.toDisplayToast(PdfShowingActivity.this, " Unable to play audio");
            if (b) {
                progressBarSpeak.setVisibility(View.GONE);
            }
        }
        CommonMethod.toReleaseMemory();
//            }
//        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        CommonMethod.toReleaseMemory();
        getMenuInflater().inflate(R.menu.speak_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        CommonMethod.toReleaseMemory();
        try {
            ToSetMore.MenuOptions(PdfShowingActivity.this, item);
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                case R.id.menuSpeak:
                    try {
                        toGetData(vp.getCurrentItem() + 1, true);
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
        CommonMethod.toReleaseMemory();

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
        try {

            if (tts != null) {
                tts.stop();
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
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
        try {
            CommonMethod.toReleaseMemory();
            if (tts != null) {
                tts.shutdown();
            }

            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        } catch (Exception | Error e) {
            CommonMethod.toReleaseMemory();
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void voiceValidator(String voice) {
        try {
            CommonMethod.toReleaseMemory();
            if (voice.contains("#female")) {
                female_voice_array.add(voice);
            }
            if (voice.contains("#male")) {
                male_voice_array.add(voice);
            }
            CommonMethod.toReleaseMemory();

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ToGetPdfImages extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            vp.setAdapter(new PDfPagesShowingAdapter(PdfShowingActivity.this, list));
            llCustom_loader.setVisibility(View.GONE);
            vp.setVisibility(View.VISIBLE);
            CommonMethod.toReleaseMemory();


//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//            Log.d("TAG", " getChildCount : " + vp.getChildCount() + ":" + list.size());
//            for (int i = 0; i < list.size(); i++) {
//                final int finalI = i;
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        toGetData(finalI + 1, false);
//                    }
//                }, 5000);
//            }
//            }
//        });
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (getIntent().getExtras() != null) {
                    if (getIntent().getStringExtra("file") != null) {
                        file = new File(getIntent().getStringExtra("file").trim());
                        if (file.exists()) {
                            try {
                                if (getSupportActionBar() != null) {
                                    CommonMethod.toSetTitle(getSupportActionBar(), PdfShowingActivity.this, file.getName());
                                }
                                fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

                            } catch (Exception | Error e) {
                                e.printStackTrace();
                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e);
                                FirebaseCrash.report(e);
                            } finally {
                                pdfRenderer = new PdfRenderer(fileDescriptor);
                                Log.d("Tag", " Pdf Page Count : " + pdfRenderer.getPageCount());
                                for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                                    showPage(i);
                                }
                            }
                        } else {
                            CommonMethod.toDisplayToast(PdfShowingActivity.this, "Can't file does not exists");
                            finish();
                        }
                    } else {
                        CommonMethod.toDisplayToast(PdfShowingActivity.this, "Can't Open Pdf File 1");
                        finish();
                    }
                } else {
                    CommonMethod.toDisplayToast(PdfShowingActivity.this, "Can't Open Pdf File 2");
                    finish();
                }
                CommonMethod.toReleaseMemory();
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
                CommonMethod.toReleaseMemory();
            }
            return null;
        }
    }
}