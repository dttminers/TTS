package in.tts.activities;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

//import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
//import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
//
//import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
//import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
//import cafe.adriel.androidaudioconverter.model.AudioFormat;

import in.tts.R;
import in.tts.adapters.PdfPagesAdapter;
import in.tts.classes.ToSetMore;
import in.tts.utils.CommonMethod;

public class PdfReadersActivity extends AppCompatActivity {

    private RecyclerView rv;
    private LinearLayoutManager layoutManager;
    private PdfPagesAdapter pdfPagesAdapter;

    private ProgressBar progressBarSpeak;

    private MediaPlayer mMediaPlayer;

    private LinearLayout speakLayout, llCustom_loader;
    private Button reload, forward, playPause, backward, close;

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;

    private StringBuilder stringBuilder;
    private ArrayList<Bitmap> list = new ArrayList<Bitmap>();

    private File file;

    private TextToSpeech tts;

    private boolean mProcessed = false;
    private int mStatus = 0;

    private double startTime = 0;
    private double finalTime = 0;

    private int forwardTime = 5000;
    private int backwardTime = 5000;

//    private final String FILENAME = "/wpta_tts.wav";

    public static int oneTimeOnly = 0;

    //Scroll
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPagep = 1;

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

                        mMediaPlayer = new MediaPlayer();


                        tts = new TextToSpeech(PdfReadersActivity.this, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int i) {
                                try {
                                    mStatus = i;
                                    setTts(tts);
                                    if (i != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                    } else {
                                        Log.e("TTS", "Initialization Failed!");
                                    }
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                    Crashlytics.logException(e);
                                }
                            }
                        });


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

                        playPause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getApplicationContext(), "Pausing sound", Toast.LENGTH_SHORT).show();
                                mMediaPlayer.pause();
//                                speakLayout.setVisibility(View.GONE);
                            }
                        });

                        forward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int temp = (int) startTime;
                                if ((temp + forwardTime) <= finalTime) {
                                    startTime = startTime + forwardTime;
                                    mMediaPlayer.seekTo((int) startTime);
                                    Toast.makeText(getApplicationContext(), "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        backward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int temp = (int) startTime;

                                if ((temp - backwardTime) > 0) {
                                    startTime = startTime - backwardTime;
                                    mMediaPlayer.seekTo((int) startTime);
                                    Toast.makeText(getApplicationContext(), "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            rv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                                @Override
                                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                                    visibleItemCount = layoutManager.getChildCount();
                                    totalItemCount = layoutManager.getItemCount();
                                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                                    if (loading) {
                                        if (totalItemCount > previousTotal) {
                                            loading = false;
                                            previousTotal = totalItemCount;
                                        }
                                    }
//                                    Log.d("TAGPDF", "onScrollA1 " + (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) + ":" + ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)));
//                                    Log.d("TAGPDF", "onScrollA2 " + (totalItemCount - visibleItemCount) + ":" + (firstVisibleItem + visibleThreshold) + ":" + loading);
//                                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                                    if (firstVisibleItem + visibleThreshold < totalItemCount) {
                                        // End has been reached
                                        // Do something
                                        currentPagep++;
//                                        Log.d("TAGPDF", " Current Page 1 " + currentPagep);
//                                        onLoadMore(currentPage);
                                        loading = true;
                                    }
                                }
                            });
                        }

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
        }
    }

    private void setTts(TextToSpeech tts) {
        try {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    Log.d("TAGPDF", "TTS utterance onDone : " + utteranceId);
                    mProcessed = true;
                    initializeMediaPlayer();
                    playMediaPlayer(0);
                }

                @Override


                public void onError(String utteranceId) {
                    Log.d("TAGPDF", "TTS utterance error : " + utteranceId);
                }

                @Override


                public void onStart(String utteranceId) {
                    Log.d("TAGPDF", "TTS utterance onStart : " + utteranceId);
                }

            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
        }
    }

    private void initializeMediaPlayer() {
        try {
            String fileName =
                    //Environment.getDownloadCacheDirectory().getAbsolutePath()
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/" + System.currentTimeMillis() + "_" + file.getName().substring(0, 5) + ".wav";
            Uri uri = Uri.parse("file://" + fileName);
            Log.d("TAGPDF", " PATH audio 1: " + fileName);
            if (uri != null) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(getApplicationContext(), uri);
                mMediaPlayer.prepare();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
        }
    }


    private void playMediaPlayer(int status) {
        progressBarSpeak.setVisibility(View.GONE);
        // Start Playing
        if (status == 0) {
            speakLayout.setVisibility(View.VISIBLE);
            mMediaPlayer.start();
        }
        // Pause Playing
        if (status == 1) {
            speakLayout.setVisibility(View.GONE);
            mMediaPlayer.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(final int index) {
        try {
//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
            if (pdfRenderer.getPageCount() <= index) {
                return;
            }
            // Make sure to close the current page before opening another one.
            if (null != currentPage) {
                currentPage.close();
            }
            //open a specific page in PDF file
            currentPage = pdfRenderer.openPage(index);
            // Important: the destination bitmap must be ARGB (not RGB).
            Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
            // Here, we render the page onto the Bitmap.
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            // showing bitmap to an imageview
            list.add(bitmap);
//                } });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
        }
    }

    private void toGetData(Bitmap bitmap, int pos) {
        try {
//            stringBuilder.append("\n");
            stringBuilder = new StringBuilder();
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(imageFrame);
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }

            Log.d("TAGPDF", " Final_DATA " + stringBuilder);

            //create pdf reader
            PdfReader pr = new PdfReader(file.getPath());
            Log.d("TAGPDF", " Final_DATA 23 " + file.getPath() + ":" + pos + ":" + pr.getNumberOfPages());
            //text is the required String (initialized as "" )
            String text = PdfTextExtractor.getTextFromPage(pr, pos).toString();
            Log.d("TAGPDF", " Final_DATA 2 " + text);
            toSpeak(text);
//            toSpeak(stringBuilder.toString());

//            File wavFile = new File(Environment.getExternalStorageDirectory(), "recorded_audio.wav");
//            IConvertCallback callback = new IConvertCallback() {
//                @Override
//                public void onSuccess(File convertedFile) {
//                    Toast.makeText(PdfReadersActivity.this, "SUCCESS: " + convertedFile.getPath(), Toast.LENGTH_LONG).show();
//                }
//                @Override
//                public void onFailure(Exception error) {
//                    Toast.makeText(PdfReadersActivity.this, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            };
//            Toast.makeText(this, "Converting audio file...", Toast.LENGTH_SHORT).show();
//            AndroidAudioConverter.with(this)
//                    .setFile(file)
//                    .setFormat(AudioFormat.MP3)
//                    .setCallback(callback)
//                    .convert();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
        }
    }

    private void toSpeak(String string) {
        if (mStatus == TextToSpeech.SUCCESS) {

            speakLayout.setVisibility(View.VISIBLE);
            progressBarSpeak.setVisibility(View.GONE);

            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                playMediaPlayer(1);
//                speakLayout.setVisibility(View.GONE);
                progressBarSpeak.setVisibility(View.GONE);
                return;
            }


            mMediaPlayer.start();

            finalTime = mMediaPlayer.getDuration();
            startTime = mMediaPlayer.getCurrentPosition();

            if (oneTimeOnly == 0) {
//                seekbar.setMax((int) finalTime);
                Log.d("TAGPDF", "SPEAKING : " + ((int) finalTime));
                oneTimeOnly = 1;
            }

            Log.d("TAGPDF", " Start Time : " + String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    finalTime)))
            );

            Log.d("TAGPDF", " End Time : " + String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    startTime)))
            );

            HashMap<String, String> myHashRender = new HashMap<>();
            myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "READ_IT");


            String fileName =
//                    Environment.getDownloadCacheDirectory().getAbsolutePath()
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + System.currentTimeMillis() + "_" + file.getName().substring(0, 5) + ".wav";
            Log.d("TAGPDF", " PATH audio : " + fileName);

            if (!mProcessed) {
                int status = tts.synthesizeToFile(string, myHashRender, fileName);
                Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();
                speakLayout.setVisibility(View.VISIBLE);
                progressBarSpeak.setVisibility(View.GONE);
            } else {
                playMediaPlayer(0);
            }
        } else {
            progressBarSpeak.setVisibility(View.GONE);
            Toast.makeText(getBaseContext(), "Unable to Speak", Toast.LENGTH_SHORT).show();
        }
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
//                        Log.d("TAGPDF", " Current Page 12 " + currentPagep + (firstVisibleItem + visibleThreshold));
//                        Log.d("TAGPDF ", " PAGE PDF : " + layoutManager.findFirstVisibleItemPosition() + ":" + layoutManager.findFirstCompletelyVisibleItemPosition() + ":" + layoutManager.findLastCompletelyVisibleItemPosition() + ":" + layoutManager.findLastVisibleItemPosition());
                        progressBarSpeak.setVisibility(View.VISIBLE);

                        toGetData(list.get(firstVisibleItem + visibleThreshold), (firstVisibleItem + visibleThreshold));
//                    toGetData(list.get(visibleItemCount + totalItemCount));
//                        if (list.size()< 1) {
//                            toGetData(list.get(0));
//                        } else {
//                            toGetData(list.get(visibleItemCount + totalItemCount));
//                        }
//                        if (stringBuilder.toString().trim().length() != 0) {
//                            toSpeak(stringBuilder.toString());
//                        } else {
//                            Toast.makeText(PdfReadersActivity.this, "unable to find data", Toast.LENGTH_SHORT).show();
//                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                    }
                    break;
                default:
                    return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
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
            Crashlytics.logException(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (tts != null) {
                tts.shutdown();
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }
}