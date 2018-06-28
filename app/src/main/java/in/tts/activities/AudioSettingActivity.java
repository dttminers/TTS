package in.tts.activities;

import android.os.Bundle;
import android.app.Activity;
import android.widget.SeekBar;

import in.tts.R;

public class AudioSettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_setting);

        SeekBar yourSeekbar = (SeekBar)findViewById(R.id.seek1);
//        yourSeekbar.setMax(20);

        yourSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress >= 0 && progress <= seekBar.getMax()) {

                        String progressString = String.valueOf(progress * 10);
//                        yourTextView.setText(progressString); // the TextView Reference
                        seekBar.setSecondaryProgress(progress);
                    }
                }

            }
        });

    }

}
