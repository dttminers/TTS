package in.tts.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;

import in.tts.R;
import in.tts.model.PrefManager;
import in.tts.model.User;
import in.tts.services.ClipboardMonitorService;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetImages;
import in.tts.utils.ToGetPdfFiles;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private PrefManager prefManager;

    @Override
    @AddTrace(name = "onCreateSplashActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_splash);
            startService(new Intent(this, ClipboardMonitorService.class));

            prefManager = new PrefManager(SplashActivity.this);
            //Get Firebase auth instance
            auth = FirebaseAuth.getInstance();
            CommonMethod.setAnalyticsData(SplashActivity.this, "MainTab", "splash", null);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    prefManager.getUserInfo();
//                    if (auth.getCurrentUser() != null) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
//                    } else if (User.getUser(SplashActivity.this).getId() != null) {
//                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
//                    } else {
//                        if (prefManager.isFirstTimeLaunch()) {
//                            startActivity(new Intent(SplashActivity.this, TutorialPageActivity.class));
//                        } else {
//                            startActivity(new Intent(SplashActivity.this, LoginActivity.class).putExtra("LOGIN", "login"));
//                        }
//                    }
//                    finish();
                }
            }, 3000);
            prefManager.getAudioSetting();
            if ((ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                try {
                    ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), SplashActivity.this);
                    ToGetImages.getAllShownImagesPath(SplashActivity.this, SplashActivity.this);
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                }
            }
            CommonMethod.isSignedIn(SplashActivity.this);
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
        CommonMethod.toReleaseMemory();
    }
}