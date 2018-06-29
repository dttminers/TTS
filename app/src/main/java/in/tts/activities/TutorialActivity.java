package in.tts.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import in.tts.R;
import in.tts.model.PrefManager;

public class TutorialActivity extends AppCompatActivity {

    private Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


            // ClickListener for NEXT button
            // When clicked on Button ViewSwitcher will switch between views
            // The current view will go out and next view will come in with specified animation
            findViewById(R.id.img1).setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    btnSkip.setVisibility(View.VISIBLE);
//                simpleViewSwitcher.showNext();
                    v.setVisibility(View.GONE);

                }
            });

            findViewById(R.id.img2).setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    btnSkip.setVisibility(View.VISIBLE);
//                simpleViewSwitcher.showNext();
                    v.setVisibility(View.GONE);
                }
            });

            findViewById(R.id.img3).setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    btnSkip.setVisibility(View.VISIBLE);
//                simpleViewSwitcher.showNext();
                    v.setVisibility(View.GONE);
                }
            });

            findViewById(R.id.img4).setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    btnSkip.setVisibility(View.VISIBLE);
//                simpleViewSwitcher.showNext();
                    v.setVisibility(View.GONE);
                }
            });

            findViewById(R.id.img5).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    launchHomeScreen(false);
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void launchHomeScreen(boolean status) {
        PrefManager prefManager = new PrefManager(this);
        prefManager.setFirstTimeLaunch(false);
        if (status) {
            startActivity(new Intent(TutorialActivity.this, MainActivity.class));
            finish();
        } else {
            startActivity(new Intent(TutorialActivity.this, LoginActivity.class).putExtra("LOGIN", "login"));
            finish();
        }
    }
}