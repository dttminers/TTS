package in.tts.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
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

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
//                startActivity(new Intent(SplashActivity.this, LoginActivity.class).putExtra("LOGIN", "login"));
                startActivity(new Intent(SplashActivity.this, MainActivity.class));

                // close this activity
                finish();
            }
        }, 3000);
    }
}
