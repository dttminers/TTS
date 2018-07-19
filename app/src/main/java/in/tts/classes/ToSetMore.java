package in.tts.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import in.tts.R;
import in.tts.activities.AudioSettingActivity;
import in.tts.activities.CameraOcrActivity;
import in.tts.activities.HelpActivity;
import in.tts.activities.OurOtherAppActivity;
import in.tts.activities.RecentVoiceActivity;
import in.tts.activities.SettingActivity;

public class ToSetMore {
    public static void MenuOptions(Context context, MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.actionCamera:
                    context.startActivity(new Intent(context, CameraOcrActivity.class));
                    break;

                case R.id.settings:
                    context.startActivity(new Intent(context, SettingActivity.class));
                    break;

                case R.id.audio_settings:
                    context.startActivity(new Intent(context, AudioSettingActivity.class));
                    break;

                case R.id.recent_audios:
                    context.startActivity(new Intent(context, RecentVoiceActivity.class));
                    break;

                case R.id.our_other_apps:
                    context.startActivity(new Intent(context, OurOtherAppActivity.class));
                    break;

                case R.id.help:
                    context.startActivity(new Intent(context, HelpActivity.class));
                    break;

                case android.R.id.home:
                    ((Activity) context).onBackPressed();
                    break;

                default:
                    break;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
