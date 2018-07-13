package in.tts.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.model.PrefManager;
import in.tts.model.User;
import in.tts.utils.CommonMethod;

public class SplashActivity extends AppCompatActivity {

    @Override
    @AddTrace(name = "onCreateSplashActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        CommonMethod.setAnalyticsData(SplashActivity.this, "MainTab", "splash", null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PrefManager prefManager = new PrefManager(SplashActivity.this);
                prefManager.getUserInfo();
//                if (User.getUser(SplashActivity.this).getId() != null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                } else {
//                    if (prefManager.isFirstTimeLaunch()) {
//                        startActivity(new Intent(SplashActivity.this, TutorialActivity.class));
//                    } else {
//                        startActivity(new Intent(SplashActivity.this, LoginActivity.class).putExtra("LOGIN", "login"));
//                    }
//                }
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonMethod.toReleaseMemory();
    }
}