package in.tts.activities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import in.tts.R;
//
//public class MediaFile extends Activity implements TextToSpeech.OnInitListener {
//
//    private TextToSpeech mTts;
//
//    private int mStatus = 0;
//
//    private MediaPlayer mMediaPlayer;
//
//    private boolean mProcessed = false;
//
//    private final String FILENAME = "wpta_tts.wav";
//
//    private ProgressDialog mProgressDialog;
//
//
//    @SuppressWarnings("deprecation")
//
//    @TargetApi(15)
//
//
//    public void setTts(TextToSpeech tts) {
//        this.mTts = tts;
//
//        if (Build.VERSION.SDK_INT >= 15) {
//            this.mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//                @Override
//
//
//                public void onDone(String utteranceId) {
//// Speech file is created
//                    mProcessed = true;
//
//// Initializes Media Player
//                    initializeMediaPlayer();
//
//// Start Playing Speech
//                    playMediaPlayer(0);
//                }
//
//                @Override
//
//
//                public void onError(String utteranceId) {
//                }
//
//                @Override
//
//
//                public void onStart(String utteranceId) {
//                }
//
//            });
//        } else {
//            this.mTts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
//                @Override
//
//
//                public void onUtteranceCompleted(String utteranceId) {
//// Speech file is created
//                    mProcessed = true;
//
//// Initializes Media Player
//                    initializeMediaPlayer();
//
//// Start Playing Speech
//                    playMediaPlayer(0);
//                }
//
//            });
//        }
//    }
//
//
//    @Override
//
//
//    public void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//// Instantiating TextToSpeech class
//        mTts = new TextToSpeech(this, this);
//
//// Getting reference to the button btn_speek
//        Button btnSpeek = (Button) findViewById(R.id.btn_speak);
//
//// Creating a progress dialog window
//        mProgressDialog = new ProgressDialog(this);
//
//// Creating an instance of MediaPlayer
//        mMediaPlayer = new MediaPlayer();
//
//// Close the dialog window on pressing back button
//        mProgressDialog.setCancelable(true);
//
//// Setting a horizontal style progress bar
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//
///** Setting a message for this progress dialog
// * Use the method setTitle(), for setting a title
// * for the dialog window
// *  */
//        mProgressDialog.setMessage("Please wait ...");
//
//// Defining click event listener for the button btn_speak
//        OnClickListener btnClickListener = new OnClickListener() {
//
//            @Override
//
//
//            public void onClick(View v) {
//
//                if (mStatus == TextToSpeech.SUCCESS) {
//
//// Getting reference to the Button
//                    Button btnSpeak = (Button) findViewById(R.id.btn_speak);
//
//                    btnSpeak.setText("Pause");
//
//                    if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
//                        playMediaPlayer(1);
//                        btnSpeak.setText("Speak");
//                        return;
//                    }
//
//                    mProgressDialog.show();
//
//// Getting reference to the EditText et_content
//                    EditText etContent = (EditText) findViewById(R.id.et_content);
//
//                    HashMap<String, String> myHashRender = new HashMap();
//                    String utteranceID = "wpta";
//                    myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceID);
//
//
//                    String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + FILENAME;
//
//                    if (!mProcessed) {
//                        int status = mTts.synthesizeToFile(etContent.getText().toString(), myHashRender, fileName);
//                    } else {
//                        playMediaPlayer(0);
//                    }
//                } else {
//                    String msg = "TextToSpeech Engine is not initialized";
//                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        };
//
//// Set Click event listener for the button btn_speak
//        btnSpeek.setOnClickListener(btnClickListener);
//
//// Getting reference to the EditText et_content
//        EditText etContent = (EditText) findViewById(R.id.et_content);
//
//        etContent.addTextChangedListener(new TextWatcher() {
//
//            @Override
//
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//// TODO Auto-generated method stub
//            }
//
//            @Override
//
//
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//// TODO Auto-generated method stub
//            }
//
//            @Override
//
//
//            public void afterTextChanged(Editable s) {
//                mProcessed = false;
//                mMediaPlayer.reset();
//
//// Getting reference to the button btn_speek
//                Button btnSpeek = (Button) findViewById(R.id.btn_speak);
//
//// Changing button Text to Speek
//                btnSpeek.setText("Speek");
//            }
//
//        });
//
//        OnCompletionListener mediaPlayerCompletionListener = new OnCompletionListener() {
//
//            @Override
//
//
//            public void onCompletion(MediaPlayer mp) {
//// Getting reference to the button btn_speek
//                Button btnSpeek = (Button) findViewById(R.id.btn_speak);
//
//// Changing button Text to Speek
//                btnSpeek.setText("Speek");
//            }
//
//        };
//
//        mMediaPlayer.setOnCompletionListener(mediaPlayerCompletionListener);
//    }
//
//
//    @Override
//
//
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
// 
//
//    @Override
//
//
//    protected void onDestroy() {
// 
//// Stop the TextToSpeech Engine
//        mTts.stop();
// 
//// Shutdown the TextToSpeech Engine
//        mTts.shutdown();
// 
//// Stop the MediaPlayer
//        mMediaPlayer.stop();
// 
//// Release the MediaPlayer
//        mMediaPlayer.release();
// 
//        super.onDestroy();
//    }
// 
//
//    @Override
//
//
//    public void onInit(int status) {
//        mStatus = status;
//        setTts(mTts);
//    }
// 
//
//
//    private void initializeMediaPlayer() {
//        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + FILENAME;
// 
//        Uri uri  =Uri.parse("file://" + fileName);
// 
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
// 
//        try {
//            mMediaPlayer.setDataSource(getApplicationContext(), uri);
//            mMediaPlayer.prepare();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
// 
//
//
//    private void playMediaPlayer(int status) {
//        mProgressDialog.dismiss();
// 
//// Start Playing
//        if (status == 0) {
//            mMediaPlayer.start();
//        }
// 
//// Pause Playing
//        if (status == 1) {
//            mMediaPlayer.pause();
//        }
//    }
//}
//
public class MediaFile extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.main);
//        MediaPlayer mediaPlayer = MediaPlayer.create(MediaFile.this, R.raw.sound_file_1);
//        mediaPlayer.start();
//        Uri myUri = Uri.parse("https://soundcloud.com/uri-music/black-flag-2"); // initialize Uri here
//        MediaPlayer mediaPlayer = new MediaPlayer();
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setDataSource(getApplicationContext(), myUri);
//        mediaPlayer.prepare();
//        mediaPlayer.start();
    }
}