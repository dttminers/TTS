package in.tts.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;

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
import java.util.List;
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
    private Button reload, forward, backward, close;
    private Button playPause;
//    private SeekBar sbPlayer;

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;

    private Bitmap bitmap;
    private ArrayList<Bitmap> list = new ArrayList<Bitmap>();

    private File pdfFile;

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

    private String voice = "";
    private File destFile;
    private int resumePosition, lang;

    private boolean isAudioDivided = false;
    private int count = 0;
    List<String> texts;
    int dividerLimit = 500;//3900;
    private File appTmpPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/READ_IT/Audio/");
//    private Handler mSeekbarUpdateHandler = new Handler();


    @Override
    @AddTrace(name = "onCreatePdfShowingActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG_PDFM", " onCreate ");
        CommonMethod.toReleaseMemory();
        setContentView(R.layout.activity_pdf_showing);
        new ToGetPdfImages().execute();
        toBindViews();
        toSetRecent();
    }

//    private Runnable mUpdateSeekBar = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                if (mediaPlayer != null) {
////                    Log.d("TAG", " mUpdateSeekbar : " + mediaPlayer.getCurrentPosition());
//                    sbPlayer.setProgress(mediaPlayer.getCurrentPosition());
//                }
//                mSeekbarUpdateHandler.postDelayed(this, 50);
//            } catch (Exception | Error e) {
//                e.printStackTrace();
//                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//                Crashlytics.logException(e);
//                FirebaseCrash.report(e);
//            }
//        }
//    };

    private void toSetRecent() {
        Log.d("TAG_PDFM", " toSetRecent");
        CommonMethod.toReleaseMemory();
        try {
            if (!appTmpPath.exists()) {
                appTmpPath.mkdirs();
            }
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
                    PrefManager.AddedRecentPDF = true;
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
            Log.d("TAG_PDFM", " toBindViews");
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

//            sbPlayer = findViewById(R.id.sbPlayer);
//            sbPlayer.setEnabled(false);
//            sbPlayer.setVisibility(View.GONE);
//
//            sbPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    if (mediaPlayer != null) {
//                        if (fromUser)
//                            mediaPlayer.seekTo(progress);
//                    }
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//
//                }
//            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toStopAudioPlayer();
                }
            });

            reload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.d("TAG_PDFM", " reload");
                        if (playerStatus) {
                            toStopAudioPlayer();
                            toPlayAudio(true);
                        } else if (destFile.getName().length() > 0) {
                            toPlayAudio(true);
                        } else {
                            toGetData(vp.getCurrentItem() + 1, true);
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

            playPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.d("TAG_PDFM", " playPause");
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
                        } else if (resumePosition != 0) {
                            mediaPlayer.seekTo(resumePosition);
                            mediaPlayer.start();
                            playPause.setBackground(getResources().getDrawable(R.drawable.pause_button));
                            playerStatus = !playerStatus;
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
                        Log.d("TAG_PDFM", " forward playing : " + startTime + ":" + ((int) startTime) + ":" + mediaPlayer.getCurrentPosition());
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
                        Log.d("TAG_PDFM", " backward playing : " + startTime + ":" + ((int) startTime) + ":" + mediaPlayer.getCurrentPosition());
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
            Log.d("TAG_PDFM", " showPage " + index);
            CommonMethod.toReleaseMemory();
            if (pdfRenderer.getPageCount() <= index) {
                return;
            }
            if (null != currentPage) {
                currentPage.close();
            }
            currentPage = pdfRenderer.openPage(index);
            bitmap = Bitmap.createBitmap(currentPage.getWidth() + 500, currentPage.getHeight() + 500, Bitmap.Config.ARGB_8888);
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            list.add(bitmap);
//            if (bitmap != null && !bitmap.isRecycled()) {
//                bitmap.recycle();
//                bitmap = null;
//            }
//            CommonMethod.toReleaseMemory();
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
            Log.d("TAG_PDFM", " toGetData " + pos + status);
            currentPageRead = pos;
            PdfReader pr = new PdfReader(pdfFile.getPath());
            String textForReading = PdfTextExtractor.getTextFromPage(pr, pos);
            Log.d("TAG_PDF ", " text " + pos + ":" + textForReading.length() + "\n" + textForReading);
            if (textForReading.trim().length() != 0) {
                toSpeak(textForReading, status);
                CommonMethod.toReleaseMemory();
            } else {
                CommonMethod.toReleaseMemory();
                CommonMethod.toDisplayToast(PdfShowingActivity.this, " Sorry, could not read this page");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    private void toSpeak(String textForReading, final boolean status) {
        try {
            Log.d("TAG_PDFM", " toSpeak");
            CommonMethod.toReleaseMemory();
            texts = new ArrayList<>();
            Log.d("TAG_PDF", "directory " + appTmpPath + " is created : " + appTmpPath.exists());
            if (status) {
                progressBarSpeak.setVisibility(View.VISIBLE);
            }
            if (textForReading.length() >= dividerLimit) {
                try {

                    isAudioDivided = true;

                    int textLength = textForReading.length();

                    System.out.println("full Text length = " + textLength);

                    int c = textLength / dividerLimit + ((textLength % dividerLimit == 0) ? 0 : 1);

                    System.out.println("Number of division of whole text = " + count);

                    int start = 0;
                    int end = textForReading.indexOf(" ", dividerLimit);

                    for (int i = 1; i <= c; i++) {
                        texts.add(textForReading.substring(start, end));
                        start = end;
                        if ((start + dividerLimit) < textLength) {
                            end = textForReading.indexOf(" ", start + dividerLimit);
                        } else {
                            end = textLength;
                        }
                    }

                    toSetAudioFiles(
                            appTmpPath.getAbsolutePath()
                                    + File.separator
                                    + (pdfFile.getName().substring(0, (pdfFile.getName().length() - 4)))
                                    + String.valueOf(vp.getCurrentItem() + 1)
                                    + count + ".wav",
                            texts.get(count),
                            true);
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                }
            } else {
                try {
                    //You need to generate only single file
                    toSetAudioFiles(
                            appTmpPath.getAbsolutePath()
                                    + File.separator
                                    + (pdfFile.getName().substring(0, (pdfFile.getName().length() - 4)))
                                    + String.valueOf(vp.getCurrentItem() + 1)
                                    + count + ".wav",
                            texts.get(count), true);
                } catch (Exception | Error e) {
                    progressBarSpeak.setVisibility(View.GONE);
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                }
                CommonMethod.toReleaseMemory();

            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetAudioFiles(String path, String text, boolean b) {
        try {
            if (b) {
                Log.d("TAG_PDFM", " toSetAudioFiles " + path);
                destFile = new File(path);
                Log.d("TAG_PDF", " FILE Name 122: " + path + ":" + destFile.exists());
                if (destFile.exists()) {
                    if (CommonMethod.getFileSize(destFile).equals("0 B")) {
                        Log.d("TAG_PDF", " File 112 ");
                        if (destFile.delete()) {
                            Log.d("TAG_PDF", " File 1121 ");
                            toGenerateAudio(text, b);
                        } else {
                            Log.d("TAG_PDF", " File 1121 ");
                        }
                    } else {
                        Log.d("TAG_PDF", " File 122 ");
                        if (b) {
                            toPlayAudio(b);
                        }
                    }
                } else {
                    Log.d("TAG_PDF", " File 132 ");
                    toGenerateAudio(text, b);
                }
            } else{
                Log.d("TAG_PDFM", " toSetAudioFiles " + path);
                File destFile1 = new File(path);
                Log.d("TAG_PDF", " FILE Name 122: " + path + ":" + destFile.exists());
                if (destFile1.exists()) {
                    if (CommonMethod.getFileSize(destFile1).equals("0 B")) {
                        Log.d("TAG_PDF", " File 112 ");
                        if (destFile1.delete()) {
                            Log.d("TAG_PDF", " File 1121 ");
                            toGenerateAudio(text, b);
                        } else {
                            Log.d("TAG_PDF", " File 1121 ");
                        }
                    } else {
                        Log.d("TAG_PDF", " File 122 ");
                        if (b) {
                            toPlayAudio(b);
                        }
                    }
                } else {
                    Log.d("TAG_PDF", " File 132 ");
                    toGenerateAudio(text, b);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            progressBarSpeak.setVisibility(View.GONE);
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toGenerateAudio(final String string, final boolean b) {
        try {
            if (b) {
                progressBarSpeak.setVisibility(View.VISIBLE);
            }
            Log.d("TAG_PDFM", " toGenerateAudio : " + string.trim().length() + string);
            tts = new TextToSpeech(PdfShowingActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.d("TTS_TAG_PDF", " DATA LAng " + ":" + audioSetting.getLangSelection() + ":" + lang + ":" + tts.getLanguage() + ":" + tts.getAvailableLanguages());
                    }
                    if (i == TextToSpeech.SUCCESS) {
                        if (lang == TextToSpeech.LANG_MISSING_DATA || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.d("TTS_TAG_PDF", "This Language is not supported");
                        } else {
                            Log.d("TTS_TAG_PDF", " Else ");
                        }
                    } else {
                        Log.d("TTS_TAG_PDF", "Initialization Failed!");
                    }

                    Log.d("log", "initi");
                    Log.d("TAG_PDF", " Path  : " + destFile.getAbsolutePath());
                    HashMap<String, String> myHashRender = new HashMap<>();
                    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, string);
                    int i3 = tts.synthesizeToFile(string, myHashRender, destFile.getAbsolutePath());
                    Log.d("TAG_PDF ", " errrr " + i3);
                    if (i3 != TextToSpeech.SUCCESS) {
//                                CommonMethod.toDisplayToast(PdfShowingActivity.this, "Saved ");
//                    } else {
                        if (b) {
                            CommonMethod.toDisplayToast(PdfShowingActivity.this, " Can't play audio ");
                            progressBarSpeak.setVisibility(View.GONE);
                        }
                    }

                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {
                            Log.d("TAG_PDF", "Utter onStart" + ":" + s);
                        }

                        @Override
                        public void onDone(String s) {
                            Log.d("TAG_PDF", "Utter onDone" + ":" + s);
                        }

                        @Override
                        public void onError(String s) {
                            Log.d("TAG_PDF", "Utter onError" + ":" + s);
                        }
                    });

                    tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                        @Override
                        public void onUtteranceCompleted(final String s) {
                            try {
                                count++;
                                Log.d("TAG_PDF", "Utter onUtteranceCompleted  " + isAudioDivided + ":" + count + ":" + s + texts.size());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("TAG_PDF", "Utter onUtteranceCompleted 1 " + isAudioDivided + ":" + count + ":" + s + texts.size());
                                        if (b) {
                                            progressBarSpeak.setVisibility(View.GONE);
                                        }
                                    }
                                });
                                if (b) {
                                    Log.d("TAG_PDF", "Utter onUtteranceCompleted 2 " + isAudioDivided + ":" + count + ":" + s + texts.size());
                                    toPlayAudio(b);
                                } else {
                                    Log.d("TAG_PDF", "Utter onUtteranceCompleted 3 " + isAudioDivided + ":" + count + ":" + s + texts.size());
                                }
                                if (isAudioDivided) {
                                    Log.d("TAG_PDF", "Utter onUtteranceCompleted 4 " + isAudioDivided + ":" + count + ":" + s + texts.size());
                                    if (count < texts.size()) {
                                        toSetAudioFiles(
                                                appTmpPath.getAbsolutePath()
                                                        + File.separator
                                                        + (pdfFile.getName().substring(0, (pdfFile.getName().length() - 4)))
                                                        + String.valueOf(vp.getCurrentItem() + 1)
                                                        + count + ".wav",
                                                texts.get(count),
                                                false
                                        );
                                    }
                                } else {
                                    Log.d("TAG_PDF", "Utter onUtteranceCompleted 5 " + isAudioDivided + ":" + count + ":" + s);
                                }

                            } catch (Exception | Error e) {
                                CommonMethod.toReleaseMemory();
                                e.printStackTrace();
                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e);
                                FirebaseCrash.report(e);
                            }
                        }
                    });
                }
            }, "com.google.android.tts");
        } catch (Exception | Error e) {
            CommonMethod.toReleaseMemory();
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            if (b) {
                progressBarSpeak.setVisibility(View.GONE);
            }
        }
    }

    public void toPlayAudio(final boolean b) {
        CommonMethod.toReleaseMemory();
        try {
            Log.d("TAG_PDFM", " toPlayAudio");
            if (b) {
                progressBarSpeak.setVisibility(View.VISIBLE);
            }
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            File file = new File(tempDestFile);
            Log.d("TAG_PDF", " suixzse " + CommonMethod.getFileSize(destFile) + ":" + destFile.getAbsolutePath());
            FileInputStream fis = new FileInputStream(destFile);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();

//            sbPlayer.setMax(mediaPlayer.getDuration());
//            sbPlayer.setEnabled(true);
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            Log.d("TAG_PDF", " Times::: " + finalTime + " : " + startTime);
            if (oneTimeOnly == 0) {
//                                        seekbar.setMax((int) finalTime);
                Log.d("TAG_PDF", " Time 22 " + ((int) finalTime));
                oneTimeOnly = 1;
            }

//                                    tx2.setText(
            Log.d("TAG_PDF", " TIME 11 : " +
                    String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            finalTime)))
            );

//                                    tx1.setText(
            Log.d("TAG_PDF", " TIME 12 : " +
                    String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            startTime)))
            );
            if (b) {
                progressBarSpeak.setVisibility(View.GONE);
            }
//            mSeekbarUpdateHandler.postDelayed(mUpdateSeekBar, 0);
//            }
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    if (b) {
                        progressBarSpeak.setVisibility(View.GONE);
                    }
                    playPause.setBackground(getResources().getDrawable(R.drawable.pause_button));
                    playerStatus = true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d("TAG_PDFM", "onCompletion Audio : " + ":" + isAudioDivided + count);
                    if (!isAudioDivided) {
                        toStopAudioPlayer();
                    } else {
                        if (count < texts.size()) {
                            toSetAudioFiles(
                                    appTmpPath.getAbsolutePath()
                                            + File.separator
                                            + (pdfFile.getName().substring(0, (pdfFile.getName().length() - 4)))
                                            + String.valueOf(vp.getCurrentItem() + 1)
                                            + count + ".wav",

                                    texts.get(count),
                                    true);
                        } else {
                            toStopAudioPlayer();
                        }
                    }
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toReleaseMemory();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            playerStatus = false;
//            playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
//            if (b) {
            CommonMethod.toDisplayToast(PdfShowingActivity.this, " Unable to play audio");
//            progressBarSpeak.setVisibility(View.GONE);
//            }
        }
        CommonMethod.toReleaseMemory();
    }

    private void toStopAudioPlayer() {
        try {
            Log.d("TAG_PDFM", "toStopAudioPlayer : ");
            playerStatus = false;
            resumePosition = 0;
//            if (b) {
            progressBarSpeak.setVisibility(View.GONE);
//            }
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
            playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
//            mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekBar);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
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
                mediaPlayer.release();
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

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
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
            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), PdfShowingActivity.this, pdfFile.getName());
            }
            vp.setAdapter(new PDfPagesShowingAdapter(PdfShowingActivity.this, list));
            llCustom_loader.setVisibility(View.GONE);
            vp.setVisibility(View.VISIBLE);
            CommonMethod.toReleaseMemory();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                CommonMethod.toReleaseMemory();
                if (getIntent().getExtras() != null) {
                    if (getIntent().getStringExtra("file") != null) {
                        pdfFile = new File(getIntent().getStringExtra("file").trim());
                        if (pdfFile.exists()) {
                            try {
                                fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);

                            } catch (Exception | Error e) {
                                e.printStackTrace();
                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e);
                                FirebaseCrash.report(e);
                            } finally {
                                pdfRenderer = new PdfRenderer(fileDescriptor);
                                Log.d("TAG_PDFM", " ToGetPdfImages Page count  : " + pdfRenderer.getPageCount());
                                for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                                    showPage(i);
                                }
                            }
                        } else {
                            CommonMethod.toDisplayToast(PdfShowingActivity.this, "Can't file does not exists");
//                            finish();
                            toExit();
                        }
                    } else {
                        CommonMethod.toDisplayToast(PdfShowingActivity.this, "Can't Open Pdf File ");
//                        finish();
                    }
                } else {
                    CommonMethod.toDisplayToast(PdfShowingActivity.this, "Unable  to open Pdf File 2");
//                    finish();
                }
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

    private void toExit() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }
}