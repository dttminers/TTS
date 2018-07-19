package in.tts.activities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import in.tts.R;

public class MediaFile extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
//        setContentView(R.layout.main);
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
//
////package com.example.parsaniahardik.progressanimation;
//
//        import android.animation.ObjectAnimator;
//        import android.content.res.Resources;
//        import android.graphics.drawable.Drawable;
//        import android.os.Handler;
//        import android.support.v7.app.AppCompatActivity;
//        import android.os.Bundle;
//        import android.view.animation.DecelerateInterpolator;
//        import android.widget.ProgressBar;
//        import android.widget.TextView;
//
//public class MediaFile extends AppCompatActivity {
//
//    int pStatus = 0;
//    private Handler handler = new Handler();
//    TextView tv;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        Resources res = getResources();
//        Drawable drawable = res.getDrawable(R.drawable.circular);
//        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
//        mProgress.setProgress(0);   // Main Progress
//        mProgress.setSecondaryProgress(100); // Secondary Progress
//        mProgress.setMax(100); // Maximum Progress
//        mProgress.setProgressDrawable(drawable);
//
//      /*  ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
//        animation.setDuration(50000);
//        animation.setInterpolator(new DecelerateInterpolator());
//        animation.start();*/
//
//        tv = (TextView) findViewById(R.id.tv);
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                while (pStatus < 100) {
//                    pStatus += 1;
//
//                    handler.post(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            mProgress.setProgress(pStatus);
//                            tv.setText(pStatus + "%");
//
//                        }
//                    });
//                    try {
//                        // Sleep for 200 milliseconds.
//                        // Just to display the progress slowly
//                        Thread.sleep(16); //thread will take approx 3 seconds to finish
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
//}
//
