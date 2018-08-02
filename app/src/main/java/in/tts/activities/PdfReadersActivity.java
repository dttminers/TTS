package in.tts.activities;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.util.concurrent.TimeUnit;

import in.tts.R;
import in.tts.adapters.PdfPagesAdapter;
import in.tts.classes.TTS;
import in.tts.classes.ToSetMore;
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

    private TTS mTts;

    private String FileName = null;
    private boolean playerStatus = false;// not playing

    public static int oneTimeOnly = 0;

    int firstVisibleItem;

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


//                        mMediaPlayer = new MediaPlayer();
                        mTts = new TTS(PdfReadersActivity.this);

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
                                if (playerStatus) {
                                    mediaPlayer.pause();
                                    playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
                                    playerStatus = !playerStatus;
                                } else {
//                                    if (!mediaPlayer.isPlaying()) {
                                        mediaPlayer.start();
                                        playPause.setBackground(getResources().getDrawable(R.drawable.pause_button));
                                        playerStatus = !playerStatus;
//                                    } else {
//                                        mediaPlayer.pause();
//                                        playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
//                                        playerStatus = !playerStatus;
//                                    }
                                }
//                                Toast.makeText(getApplicationContext(), "Pausing sound", Toast.LENGTH_SHORT).show();
//                                mMediaPlayer.pause();
//                                playMediaPlayer(1);
                            }
                        });

                        forward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int temp = (int) startTime;
                                if ((temp + forwardTime) <= finalTime) {
                                    startTime = startTime + forwardTime;
//                                    mMediaPlayer.seekTo((int) startTime);
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
//                                    mMediaPlayer.seekTo((int) startTime);
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
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(final int index) {
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
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
        }
    }

    private void toGetData(int pos) {
        try {
            PdfReader pr = new PdfReader(file.getPath());
//            Log.d("TAGPDF", " Final_DATA 23 " + file.getPath() + ":" + pos + ":" + pr.getNumberOfPages());
            //text is the required String (initialized as "" )
//            String text = PdfTextExtractor.getTextFromPage(pr, pos).toString();
            String text = PdfTextExtractor.getTextFromPage(pr, pos);
            Log.d("TAGPDF", " Final_DATA 2 " + text);
            if (text.trim().length() != 0) {
//                mTts = new TTS(PdfReadersActivity.this);
                toSpeak(text);
            } else {
                CommonMethod.toDisplayToast(PdfReadersActivity.this, " Unable to get data");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
        }
    }

    private void toSpeak(String string) {
        try {
//            mTts = new TTS(PdfReadersActivity.this);
            FileName = mTts.toSaveAudioFile(string);

//            FileName = new TTS(PdfReadersActivity.this).toSaveAudioFile(string);
            speakLayout.setVisibility(View.VISIBLE);
            progressBarSpeak.setVisibility(View.GONE);


            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
//                mediaPlayer.setDataSource("http://www.virginmegastore.me/Library/Music/CD_001214/Tracks/Track1.mp3");
                File file = new File(FileName);
                FileInputStream fis = new FileInputStream(file);
                Log.d("TAG", " " + fis.getFD() + ":" + fis);
//                mediaPlayer.setDataSource(fis.getFD());
                if (file.exists()) {
//                    Log.d("TAGPDF", " PATH" + file.getTotalSpace());
//                    Uri uri = Uri.parse("file://" + FileName);
//                    Log.d("TAGPDF", " PATH " + uri.isAbsolute());
                    if (!CommonMethod.getFileSize(file).equals("0 B")) {
//                    mediaPlayer.setDataSource(PdfReadersActivity.this, uri);
                        mediaPlayer.setDataSource("file://" + FileName);
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mediaPlayer.start();
                                playPause.setBackground(getResources().getDrawable(R.drawable.pause_button));
                                playerStatus = true;

                            }
                        });
//                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mediaPlayer) {
//                                mediaPlayer.stop();
//                                playPause.setBackground(getResources().getDrawable(R.drawable.play_button));
//                                playerStatus = false;
//                            }
//                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                playerStatus = true;
            }


//            mediaPlayer = MediaPlayer.create(PdfReadersActivity.this, Uri.parse("file://" + FileName) );
//            mediaPlayer = MediaPlayer.create(PdfReadersActivity.this, Uri.fromFile(new File(FileName)));

//            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//                playMediaPlayer(1);
//                progressBarSpeak.setVisibility(View.GONE);
//                return;
//            }

//            if (mMediaPlayer != null) {
////                mMediaPlayer.start();
//                if (FileName != null) {
////                    Uri uri = Uri.parse("file://" + FileName);
//                    Uri uri = Uri.fromFile(new File(FileName));
            Log.d("TAGPDF", " PATH audio 1: " + FileName);//+ " : " + uri.getPath());
//                    if (uri != null) {
//                        try {
//                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                            mMediaPlayer.setDataSource(PdfReadersActivity.this, uri);
////                        mMediaPlayer.prepare();
//                            mMediaPlayer.prepareAsync();
////                            playMediaPlayer(0);
//
//                            finalTime = mMediaPlayer.getDuration();
//                            startTime = mMediaPlayer.getCurrentPosition();
//
//                            if (oneTimeOnly == 0) {
////                seekbar.setMax((int) finalTime);
//                                Log.d("TAGPDF", "SPEAKING : " + ((int) finalTime));
//                                oneTimeOnly = 1;
//                            }
//
//                            Log.d("TAGPDF", " Start Time : " + String.format("%d min, %d sec",
//                                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
//                                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
//                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
//                                                    finalTime)))
//                            );
//
//                            Log.d("TAGPDF", " End Time : " + String.format("%d min, %d sec",
//                                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
//                                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
//                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
//                                                    startTime)))
//                            );
//
//                        } catch (Exception | Error e) {
//                            e.printStackTrace();
//                            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//                            Crashlytics.logException(e);
//                            CommonMethod.toDisplayToast(PdfReadersActivity.this, " Failed to play sound");
//                        }
//                    }
//                } else {
//                    CommonMethod.toDisplayToast(PdfReadersActivity.this, " No data to read");
//                }
//            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
        }
    }

//    private void playMediaPlayer(int status) {
//        progressBarSpeak.setVisibility(View.GONE);
//        // Start Playing
//        if (status == 0) {
//            speakLayout.setVisibility(View.VISIBLE);
//            mMediaPlayer.start();
//        }
//        // Pause Playing
//        if (status == 1) {
//            speakLayout.setVisibility(View.GONE);
//            mMediaPlayer.pause();
//        }
//    }

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
            if (mTts != null) {
                mTts.toStop();
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
            if (mTts != null) {
                mTts.toShutDown();
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }
}