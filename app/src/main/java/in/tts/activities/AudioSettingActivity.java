package in.tts.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics; import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import in.tts.R;
import in.tts.utils.CommonMethod;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

public class AudioSettingActivity extends AppCompatActivity {

    @Override
    @AddTrace(name = "onCreateAudioSettingActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_setting);
        CommonMethod.setAnalyticsData(AudioSettingActivity.this, "MainTab", "AudioSetting", null);

        if (getSupportActionBar() != null) {
            CommonMethod.toSetTitle(getSupportActionBar(), AudioSettingActivity.this, getString(R.string.str_title_audio_settings));
        }

//        final SeekBar yourSeekbar = (SeekBar) findViewById(R.id.seek1);
//        yourSeekbar.setMax(20);
//        yourSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
////                yourSeekbar.setProgress(storedValue);
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    if (progress >= 0 && progress <= seekBar.getMax()) {
//
//                        String progressString = String.valueOf(progress * 10);
////                        yourTextView.setText(progressString); // the TextView Reference
//                        seekBar.setSecondaryProgress(progress);
//                        seekBar.setProgress(progress);
//                    }
//                }
//
//            }
//        });
        final RelativeLayout rlEng = (RelativeLayout) findViewById(R.id.rlEnglish);

        RadioGroup rg = (RadioGroup) findViewById(R.id.rgLanguageSel);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.rbEnglishLs) {
                    rlEng.setVisibility(View.VISIBLE);
                } else {
                    rlEng.setVisibility(View.GONE);
                }
            }
        });
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
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}