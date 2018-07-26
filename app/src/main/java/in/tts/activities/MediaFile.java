package in.tts.activities;

import com.google.firebase.perf.metrics.AddTrace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.pspdfkit.document.processor.NewPage;
import com.pspdfkit.document.processor.PdfProcessor;
import com.pspdfkit.document.processor.PdfProcessorTask;

import java.io.File;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class MediaFile extends AppCompatActivity {

    @Override
    @AddTrace(name = "onCreateHelpActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_pdfreader);
        CommonMethod.setAnalyticsData(this, "MainTab", "Help", null);

        if (getSupportActionBar() != null) {
            CommonMethod.toSetTitle(getSupportActionBar(), this, getString(R.string.str_title_help));
        }

//        PDFView pdfView = findViewById(R.id.pdfView1);
//
//        pdfView.fromFile(new File("/storage/emulated/0/Download/easymock_tutorial.pdf"))
//                .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
//                .enableSwipe(true)
//                .enableDoubletap(true)
//                .defaultPage(1)
//                .enableAnnotationRendering(false)
//                .password(null)
//                .load();

        createNewDocumentFromCanvas(MediaFile.this);
    }

    private void createNewDocumentFromCanvas(Context context) {
        final File outputFile = new File("/storage/emulated/0/Download/easymock_tutorial.pdf");//new File(context.getFilesDir(), "new-document.pdf");

        // Create a canvas based on an A4 page.
        final NewPage pageCanvas = NewPage.fromCanvas( NewPage.PAGE_SIZE_A4, new NewPage.OnDrawCanvasCallback() {
            @Override
            public void onDrawCanvas(Canvas canvas) {
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                Path path = new Path();
                path.cubicTo(0f, 0f, 100f, 300f, 400f, 300f);
                canvas.drawPath(path, paint);
            }
        }).build();

        // Create a new processor task, passing in the new page definition.
        final PdfProcessorTask task = PdfProcessorTask.newPage(pageCanvas);

        // Start document processing.
        PdfProcessor.processDocument(task, outputFile);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
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
}

// /
//import android.os.PersistableBundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.util.Log;
//
//import com.crashlytics.android.Crashlytics;
//import com.flurry.android.FlurryAgent;
//import com.github.barteksc.pdfviewer.PDFView;
//import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
////import com.github.barteksc.pdfviewer.util.FitPolicy;
//import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
//import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
//import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
//import com.google.firebase.crash.FirebaseCrash;
//import com.shockwave.pdfium.PdfDocument;
//
//import java.util.List;
//
//import in.tts.R;
//import in.tts.utils.CommonMethod;
//
//public class MediaFile extends AppCompatActivity {
//
//    private PDFView pdfView;
//
//    int pageNumber = 0;
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        setContentView(R.layout.main_pdfreader);
//        try {
//
//            if (getSupportActionBar() != null) {
//                CommonMethod.toSetTitle(getSupportActionBar(), MediaFile.this, "Media");
//            }
//
//            pdfView = findViewById(R.id.pdfView1);
//
//            Log.d("TAG", "media");
//            pdfView.fromAsset("Akshipta_Resume.pdf")
//                    .defaultPage(0)
//                    .onPageChange(new OnPageChangeListener() {
//                        @Override
//                        public void onPageChanged(int page, int pageCount) {
//                            pageNumber = page;
//                            setTitle(String.format("%s %s / %s", "Page ", page + 1, pageCount));
//                        }
//                    })
//                    .enableAnnotationRendering(true)
//                    .onLoad(new OnLoadCompleteListener() {
//                        @Override
//                        public void loadComplete(int nbPages) {
//
//                        }
//                    })
//                    .scrollHandle(new DefaultScrollHandle(this))
//                    .spacing(10) // in dp
//                    .onPageError(new OnPageErrorListener() {
//                        @Override
//                        public void onPageError(int page, Throwable t) {
//
//                        }
//                    })
//                    .load();
//
//            PdfDocument.Meta meta = pdfView.getDocumentMeta();
//            Log.d("TAG", "title 1= " + pdfView.getCurrentPage() +":"+ pdfView.getPageCount()+":"+ pdfView.getChildCount());
//            Log.d("TAG", "title = " + meta.getTitle());
//            Log.d("TAG", "author = " + meta.getAuthor());
//            Log.d("TAG", "subject = " + meta.getSubject());
//            Log.d("TAG", "keywords = " + meta.getKeywords());
//            Log.d("TAG", "creator = " + meta.getCreator());
//            Log.d("TAG", "producer = " + meta.getProducer());
//            Log.d("TAG", "creationDate = " + meta.getCreationDate());
//            Log.d("TAG", "modDate = " + meta.getModDate());
//
//            printBookmarksTree(pdfView.getTableOfContents(), "-");
//        } catch (Exception | Error e) {
//            e.printStackTrace();
//            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//            Crashlytics.logException(e);
//            FirebaseCrash.report(e);
//        }
//    }
//
//    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
//        for (PdfDocument.Bookmark b : tree) {
//            Log.d("TAG", String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));
//            if (b.hasChildren()) {
//                printBookmarksTree(b.getChildren(), sep + "-");
//            }
//        }
//    }
//}
//
////public class MediaFile extends Activity implements TextToSpeech.OnInitListener {
////
////    private TextToSpeech mTts;
////
////    private int mStatus = 0;
////
////    private MediaPlayer mMediaPlayer;
////
////    private boolean mProcessed = false;
////
////    private final String FILENAME = "wpta_tts.wav";
////
////    private ProgressDialog mProgressDialog;
////
////    @SuppressWarnings("deprecation")
////
////    @TargetApi(15)
////
////
////    public void setTts(TextToSpeech tts) {
////        this.mTts = tts;
////
////        if (Build.VERSION.SDK_INT >= 15) {
////            this.mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
////                @Override
////
////
////                public void onDone(String utteranceId) {
////// Speech file is created
////                    mProcessed = true;
////
////// Initializes Media Player
////                    initializeMediaPlayer();
////
////// Start Playing Speech
////                    playMediaPlayer(0);
////                }
////
////                @Override
////
////
////                public void onError(String utteranceId) {
////                }
////
////                @Override
////
////
////                public void onStart(String utteranceId) {
////                }
////
////            });
////        } else {
////            this.mTts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
////                @Override
////
////
////                public void onUtteranceCompleted(String utteranceId) {
////// Speech file is created
////                    mProcessed = true;
////
////// Initializes Media Player
////                    initializeMediaPlayer();
////
////// Start Playing Speech
////                    playMediaPlayer(0);
////                }
////
////            });
////        }
////    }
////
////
////    @Override
////
////
////    public void onCreate(Bundle savedInstanceState) {
////
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////
////// Instantiating TextToSpeech class
////        mTts = new TextToSpeech(this, this);
////
////// Getting reference to the button btn_speek
////        Button btnSpeek = (Button) findViewById(R.id.btn_speak);
////
////// Creating a progress dialog window
////        mProgressDialog = new ProgressDialog(this);
////
////// Creating an instance of MediaPlayer
////        mMediaPlayer = new MediaPlayer();
////
////// Close the dialog window on pressing back button
////        mProgressDialog.setCancelable(true);
////
////// Setting a horizontal style progress bar
////        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////
/////* Setting a message for this progress dialog
//// * Use the method setTitle(), for setting a title
//// * for the dialog window
//// *  */
////        mProgressDialog.setMessage("Please wait ...");
////
////// Defining click event listener for the button btn_speak
////        OnClickListener btnClickListener = new OnClickListener() {
////
////            @Override
////
////
////            public void onClick(View v) {
////
////                if (mStatus == TextToSpeech.SUCCESS) {
////
////// Getting reference to the Button
////                    Button btnSpeak = (Button) findViewById(R.id.btn_speak);
////
////                    btnSpeak.setText("Pause");
////
////                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
////                        playMediaPlayer(1);
////                        btnSpeak.setText("Speak");
////                        return;
////                    }
////
////                    mProgressDialog.show();
////
////// Getting reference to the EditText et_content
////                    EditText etContent = (EditText) findViewById(R.id.et_content);
////
////                    HashMap<String, String> myHashRender = new HashMap();
////                    String utteranceID = "wpta";
////                    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
////
////
////                    String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + FILENAME;
////
////                    if (!mProcessed) {
////                        int status = mTts.synthesizeToFile(etContent.getText().toString(), myHashRender, fileName);
////                    } else {
////                        playMediaPlayer(0);
////                    }
////                } else {
////                    String msg = "TextToSpeech Engine is not initialized";
////                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
////                }
////            }
////
////        };
////
////// Set Click event listener for the button btn_speak
////        btnSpeek.setOnClickListener(btnClickListener);
////
////// Getting reference to the EditText et_content
////        EditText etContent = (EditText) findViewById(R.id.et_content);
////
////        etContent.addTextChangedListener(new TextWatcher() {
////
////            @Override
////
////
////            public void onTextChanged(CharSequence s, int start, int before, int count) {
////            }
////
////            @Override
////
////
////            public void beforeTextChanged(CharSequence s, int start, int count,
////                                          int after) {
////            }
////
////            @Override
////
////
////            public void afterTextChanged(Editable s) {
////                mProcessed = false;
////                mMediaPlayer.reset();
////
////// Getting reference to the button btn_speek
////                Button btnSpeek = (Button) findViewById(R.id.btn_speak);
////
////// Changing button Text to Speek
////                btnSpeek.setText("Speek");
////            }
////
////        });
////
////        OnCompletionListener mediaPlayerCompletionListener = new OnCompletionListener() {
////
////            @Override
////
////
////            public void onCompletion(MediaPlayer mp) {
////// Getting reference to the button btn_speek
////                Button btnSpeek = (Button) findViewById(R.id.btn_speak);
////
////// Changing button Text to Speek
////                btnSpeek.setText("Speek");
////            }
////
////        };
////
////        mMediaPlayer.setOnCompletionListener(mediaPlayerCompletionListener);
////    }
////
////
////    @Override
////
////
////    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.activity_main, menu);
////        return true;
////    }
//// 
////
////    @Override
////
////
////    protected void onDestroy() {
//// 
////// Stop the TextToSpeech Engine
////        mTts.stop();
//// 
////// Shutdown the TextToSpeech Engine
////        mTts.shutdown();
//// 
////// Stop the MediaPlayer
////        mMediaPlayer.stop();
//// 
////// Release the MediaPlayer
////        mMediaPlayer.release();
//// 
////        super.onDestroy();
////    }
//// 
////
////    @Override
////
////
////    public void onInit(int status) {
////        mStatus = status;
////        setTts(mTts);
////    }
//// 
////
////
////    private void initializeMediaPlayer() {
////        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + FILENAME;
//// 
////        Uri uri  =Uri.parse("file://" + fileName);
//// 
////        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//// 
////        try {
////            mMediaPlayer.setDataSource(getApplicationContext(), uri);
////            mMediaPlayer.prepare();
////        } catch (Exception e) {
////            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
////        }
////    }
//// 
////
////
////    private void playMediaPlayer(int status) {
////        mProgressDialog.dismiss();
//// 
////// Start Playing
////        if (status == 0) {
////            mMediaPlayer.start();
////        }
//// 
////// Pause Playing
////        if (status == 1) {
////            mMediaPlayer.pause();
////        }
////    }
////}
////
////        MediaPlayer mediaPlayer = MediaPlayer.create(MediaFile.this, R.raw.sound_file_1);
////        mediaPlayer.start();
////        Uri myUri = Uri.parse("https://soundcloud.com/uri-music/black-flag-2"); // initialize Uri here
////        MediaPlayer mediaPlayer = new MediaPlayer();
////        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
////        mediaPlayer.setDataSource(getApplicationContext(), myUri);
////        mediaPlayer.prepare();
////        mediaPlayer.start();
////    }
////<<<<<<< HEAD
////}
//////
//////package com.example.parsaniahardik.progressanimation;
////
////        import android.animation.ObjectAnimator;
////        import android.content.res.Resources;
////        import android.graphics.drawable.Drawable;
////        import android.os.Handler;
////        import android.support.v7.app.AppCompatActivity;
////        import android.os.Bundle;
////        import android.view.animation.DecelerateInterpolator;
////        import android.widget.ProgressBar;
////        import android.widget.TextView;
////
////public class MediaFile extends AppCompatActivity {
////
////    int pStatus = 0;
////    private Handler handler = new Handler();
////    TextView tv;
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////
////        Resources res = getResources();
////        Drawable drawable = res.getDrawable(R.drawable.circular);
////        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
////        mProgress.setProgress(0);   // Main Progress
////        mProgress.setSecondaryProgress(100); // Secondary Progress
////        mProgress.setMax(100); // Maximum Progress
////        mProgress.setProgressDrawable(drawable);
////
////      /*  ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
////        animation.setDuration(50000);
////        animation.setInterpolator(new DecelerateInterpolator());
////        animation.start();*/
////
////        tv = (TextView) findViewById(R.id.tv);
////        new Thread(new Runnable() {
////
////            @Override
////            public void run() {
////                while (pStatus < 100) {
////                    pStatus += 1;
////
////                    handler.post(new Runnable() {
////
////                        @Override
////                        public void run() {
////                            mProgress.setProgress(pStatus);
////                            tv.setText(pStatus + "%");
////
////                        }
////                    });
////                    try {
////                        // Sleep for 200 milliseconds.
////                        // Just to display the progress slowly
////                        Thread.sleep(16); //thread will take approx 3 seconds to finish
////                    } catch (InterruptedException e) {
////                        e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
////                    }
////                }
////            }
////        }).start();
////    }
////}
////}
