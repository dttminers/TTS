package in.tts.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;

import in.tts.R;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetImages;
import in.tts.utils.ToGetPdfFiles;

public class TutorialActivity extends AppCompatActivity {

    private Button btnSkip;
    private int i = 0;

    @Override
    @AddTrace(name = "onCreateTutorialActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonMethod.setAnalyticsData(TutorialActivity.this, "MainTab", "Tutorial", null);
        try {
            // Making notification bar transparent
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }

            setContentView(R.layout.activity_tutorial);

            btnSkip = findViewById(R.id.btn_skip);
            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchHomeScreen(false);
                }
            });

            findViewById(R.id.img1).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    i++;
                    switch (i) {
                        case 0:
                            v.setBackground(ContextCompat.getDrawable(TutorialActivity.this, R.drawable.tutorial1));
                            break;
                        case 1:
                            v.setBackground(ContextCompat.getDrawable(TutorialActivity.this, R.drawable.tutorial2));
                            break;
                        case 2:
                            v.setBackground(ContextCompat.getDrawable(TutorialActivity.this, R.drawable.tutorial3));
                            break;
                        case 3:
                            v.setBackground(ContextCompat.getDrawable(TutorialActivity.this, R.drawable.tutorial4));
                            break;
                        case 4:
                            v.setBackground(ContextCompat.getDrawable(TutorialActivity.this, R.drawable.tutorial5));
                            break;
                        case 5:
//                            v.setBackground(ContextCompat.getDrawable(TutorialActivity.this,R.drawable.tutorial1));
//                            break;
                            launchHomeScreen(true);
                            break;

                        default:
                            launchHomeScreen(true);
                            break;
                    }
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void fn_permission() {
        try {
            Log.d("TAG", " pdf permission ");
            if ((ContextCompat.checkSelfPermission(TutorialActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
//                    Log.d("TAG", "Home0131 ");
//                } else {
                ActivityCompat.requestPermissions(TutorialActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
                Log.d("TAG", "Home0231 ");
//                }
            } else {
                Log.d("TAG", "Home0331 ");
//                toGetPDF();
                ToGetPdfFiles.getfile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
                ToGetImages.getAllShownImagesPath(TutorialActivity.this);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void launchHomeScreen(boolean status) {
        PrefManager prefManager = new PrefManager(this);
        prefManager.setFirstTimeLaunch(false);
//        if (status) {
//            startActivity(new Intent(TutorialActivity.this, TutorialActivity.class));
//            finish();
//        } else {
            startActivity(new Intent(TutorialActivity.this, LoginActivity.class).putExtra("LOGIN", "login"));
            finish();
//        }
    }
}