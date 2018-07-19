package in.tts.activities;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import in.tts.R;
import in.tts.adapters.PdfPagesAdapter;
import in.tts.classes.TTS;
import in.tts.classes.ToSetMore;
import in.tts.utils.CommonMethod;

public class PdfReadersActivity extends AppCompatActivity {

    private RecyclerView rv;
    private LinearLayoutManager layoutManager;
    private PdfPagesAdapter pdfPagesAdapter;

    private MediaPlayer mMediaPlayer;

    private LinearLayout speakLayout;
    private Button reload, forward, playPause, backward, close;

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private StringBuilder stringBuilder;
    private ArrayList<Bitmap> list = new ArrayList<Bitmap>();
    //    private TTS tts;
    private TextToSpeech tts;
    private boolean mProcessed = false;
    private int mStatus = 0;

    private final String FILENAME = "/wpta_tts.wav";

//    private ProgressDialog mProgressDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            CommonMethod.toCallLoader(PdfReadersActivity.this, "Loading...");
            setContentView(R.layout.activity_pdf_readers);
            if (getIntent().getExtras() != null) {

                if (getIntent().getStringExtra("file") != null) {

                    File file = new File(getIntent().getStringExtra("file").trim());

                    if (file.exists()) {

                        if (getSupportActionBar() != null) {
                            CommonMethod.toSetTitle(getSupportActionBar(), PdfReadersActivity.this, file.getName());
                        }

                        rv = findViewById(R.id.rvPDFReader);

                        speakLayout = findViewById(R.id.llPDFReader);
                        reload = findViewById(R.id.reload);
                        forward = findViewById(R.id.forward);
                        playPause = findViewById(R.id.playPause);
                        backward = findViewById(R.id.backward);
                        close = findViewById(R.id.close);

                        mMediaPlayer = new MediaPlayer();

                        tts = new TextToSpeech(PdfReadersActivity.this, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int i) {
                                try {
                                    mStatus = i;
                                    setTts(tts);
//                                    if (i != TextToSpeech.ERROR) {
//                                        tts.setLanguage(Locale.UK);
//                                    }
////                                    if (i == TextToSpeech.SUCCESS) {
////
////                                        if (SetLanguage() == TextToSpeech.LANG_MISSING_DATA
////                                                || SetLanguage() == TextToSpeech.LANG_NOT_SUPPORTED) {
////                                            Log.e("TTS", "This Language is not supported");
////                                        }
////                                    }
//                                    else {
//                                        Log.e("TTS", "Initialization Failed!");
//                                    }
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
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
                            CommonMethod.toCloseLoader();
                        } else {
                            CommonMethod.toCloseLoader();
                            CommonMethod.toDisplayToast(PdfReadersActivity.this, "Can't Open Pdf File");
                            finish();
                        }
                        CommonMethod.toCloseLoader();
                    } else {
                        CommonMethod.toCloseLoader();
                        CommonMethod.toDisplayToast(PdfReadersActivity.this, "Can't file does not exists");
                        finish();
                    }
                } else {
                    CommonMethod.toCloseLoader();
                    CommonMethod.toDisplayToast(PdfReadersActivity.this, "Can't Open Pdf File");
                    finish();
                }
            } else {
                CommonMethod.toCloseLoader();
                CommonMethod.toDisplayToast(PdfReadersActivity.this, "Can't Open Pdf File");
                finish();
            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    private void setTts(TextToSpeech tts) {
        try {
            if (Build.VERSION.SDK_INT >= 15) {
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override


                    public void onDone(String utteranceId) {
// Speech file is created
                        mProcessed = true;

// Initializes Media Player
                        initializeMediaPlayer();

// Start Playing Speech
                        playMediaPlayer(0);
                    }

                    @Override


                    public void onError(String utteranceId) {
                    }

                    @Override


                    public void onStart(String utteranceId) {
                    }

                });
            } else {
                tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                    @Override


                    public void onUtteranceCompleted(String utteranceId) {
// Speech file is created
                        mProcessed = true;

// Initializes Media Player
                        initializeMediaPlayer();

// Start Playing Speech
                        playMediaPlayer(0);
                    }

                });
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void initializeMediaPlayer() {
        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + FILENAME;
        Uri uri = Uri.parse("file://" + fileName);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void playMediaPlayer(int status) {
//        mProgressDialog.dismiss();

// Start Playing
        if (status == 0) {
            mMediaPlayer.start();
        }
// Pause Playing
        if (status == 1) {
            mMediaPlayer.pause();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        try {
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
//            toGetData(bitmap);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void toGetData(Bitmap bitmap) {
        try {
            stringBuilder.append("next page...");
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(imageFrame);
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
//            tts = new TTS(PdfReadersActivity.this);
//            tts.SpeakLoud(stringBuilder.toString());
            toSpeak(stringBuilder.toString());
            Log.d("TAG", " Final DATA " + stringBuilder);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void toSpeak(String string) {
        if (mStatus == TextToSpeech.SUCCESS) {

// Getting reference to the Button

//            btnSpeak.setText("Pause");
            speakLayout.setVisibility(View.VISIBLE);

            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                playMediaPlayer(1);
//                btnSpeak.setText("Speak");
                speakLayout.setVisibility(View.GONE);

                return;
            }

//            mProgressDialog.show();

// Getting reference to the EditText et_content

            HashMap<String, String> myHashRender = new HashMap();
            String utteranceID = "wpta";
            myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);


            String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + FILENAME;

            if (!mProcessed) {
                int status = tts.synthesizeToFile(string, myHashRender, fileName);
            } else {
                playMediaPlayer(0);
            }
        } else {
            String msg = "TextToSpeech Engine is not initialized";
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
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
                    Log.d("TAG ", " PAGE PDF : " + layoutManager.findFirstVisibleItemPosition() + ":" + layoutManager.findFirstCompletelyVisibleItemPosition() + ":" + layoutManager.findLastCompletelyVisibleItemPosition() + ":" + layoutManager.findLastVisibleItemPosition());
                    toGetData(list.get(layoutManager.findFirstVisibleItemPosition()));
                    break;
                default:
                    return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
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
        }
    }
}