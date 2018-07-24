package in.tts.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import in.tts.R;
import in.tts.model.AudioSetting;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;
import com.vsa.seekbarindicated.SeekBarIndicated;

import java.util.Locale;

public class AudioSettingActivity extends AppCompatActivity {

    private SeekBarIndicated seekBarIndicated;
    private RadioGroup rgVoiceSel, rgLangSel, rgAccentSel;
    private RadioButton rbMale, rbFemale, rbEnglish, rbHindi, rbMarathi, rbTamil, rbAccent1, rbAccent2;
    private AudioSetting audioSetting;
    private RelativeLayout rlEng;

    @Override
    @AddTrace(name = "onCreateAudioSettingActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_setting);
        CommonMethod.setAnalyticsData(AudioSettingActivity.this, "MainTab", "AudioSetting", null);

        if (getSupportActionBar() != null) {
            CommonMethod.toSetTitle(getSupportActionBar(), AudioSettingActivity.this, getString(R.string.str_title_audio_settings));
        }
//        audioSetting = new AudioSetting(AudioSettingActivity.this);
        audioSetting = AudioSetting.getAudioSetting(AudioSettingActivity.this);

        rgVoiceSel = findViewById(R.id.rgVoice);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        rgLangSel = (RadioGroup) findViewById(R.id.rgLanguageSel);
        rbEnglish = findViewById(R.id.rbEnglishLs);
        rbHindi = findViewById(R.id.rbHindiLs);
        rbMarathi = findViewById(R.id.rbMarathiLs);
        rbTamil = findViewById(R.id.rbTamilLs);

        rgAccentSel = findViewById(R.id.rgAccentSel);
        rbAccent1 = findViewById(R.id.rbAccent1);
        rbAccent2 = findViewById(R.id.rbAccent2);

        rlEng = findViewById(R.id.rlEnglish);

        seekBarIndicated = findViewById(R.id.seek1);
        seekBarIndicated.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioSetting.setVoiceSpeed(progress);
                Log.d("TAG SEEK", " onProgressChanged " + progress + ":" + fromUser + audioSetting.getVoiceSpeed());
                prefData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("TAG SEEK", " onStartTrackingTouch " + seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("TAG SEEK", " onStopTrackingTouch " + seekBar.getProgress());
            }
        });

        rgLangSel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbEnglishLs) {
                    rlEng.setVisibility(View.VISIBLE);
                } else {
                    rlEng.setVisibility(View.GONE);
                }
                switch (i) {
                    case R.id.rbEnglishLs:
                        // do operations specific to this selection
                        Log.d("TAG LangSelection", " english " + i);
//                        audioSetting.setLangSelection(String.valueOf(i));
                        audioSetting.setLangSelection(String.valueOf(Locale.ENGLISH));
                        prefData();
                        break;

                    case R.id.rbHindiLs:
                        // do operations specific to this selection
                        Log.d("TAG LangSelection", " hindi " + i);
//                        audioSetting.setVoiceSelection(String.valueOf(i));
                        audioSetting.setLangSelection(String.valueOf(Locale.CHINA));
                        prefData();
                        break;

                    case R.id.rbMarathiLs:
                        // do operations specific to this selection
                        Log.d("TAG LangSelection", " marathi " + i);
//                        audioSetting.setLangSelection(String.valueOf(i));
                        audioSetting.setLangSelection(String.valueOf(Locale.FRANCE));
                        prefData();
                        break;

                    case R.id.rbTamilLs:
                        // do operations specific to this selection
                        Log.d("TAG LangSelection", " tamil " + i);
//                        audioSetting.setLangSelection(String.valueOf(i));
                        audioSetting.setLangSelection(String.valueOf(Locale.GERMANY));
                        prefData();
                        break;

                    default:
                        audioSetting.setLangSelection(String.valueOf(Locale.ENGLISH));
                        prefData();
//                        audioSetting.setLangSelection(String.valueOf(R.id.rbEnglishLs));
//                        prefData();
//                        break;
                }

            }
        });

        rgAccentSel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbAccent1:
                        // do operations specific to this selection
                        Log.d("TAG accentSel", " british " + i);
                        audioSetting.setAccentSelection(String.valueOf(Locale.UK));
                        prefData();
                        break;

                    case R.id.rbAccent2:
                        // do operations specific to this selection
                        Log.d("TAG accentSel", " american " + i);
//                        audioSetting.setAccentSelection(String.valueOf(i));
                        audioSetting.setAccentSelection(String.valueOf(Locale.KOREA));
                        prefData();
                        break;

                    default:
                        new AudioSetting(AudioSettingActivity.this).setAccentSelection(String.valueOf(R.id.rbAccent1));
                        prefData();
                        break;
                }
            }
        });
        rgVoiceSel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rbMale.isChecked()) {
                    audioSetting.setVoiceSelection("Male");
                    prefData();
//                    Toast.makeText(AudioSettingActivity.this, "Male", Toast.LENGTH_SHORT).show();
                } else {
                    audioSetting.setVoiceSelection("Female");
                    prefData();
//                    Toast.makeText(AudioSettingActivity.this, "Female", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void prefData() {
        PrefManager prefManager = new PrefManager(AudioSettingActivity.this);
        prefManager.setAudioSetting();
        prefManager.getAudioSetting();
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