package in.tts.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import in.tts.browser.fragment.TtsFragment;
import in.tts.classes.TTS;

public class AudioPlayerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            Log.d("Tts_TAG", "intent action = " + action + " : " + intent.getExtras());

            Log.d("Tts_TAG", " Cancel : ");
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
            if (nMgr != null) {
                nMgr.cancel(1);
            }
//            if (tts != null) {
//                if (tts.isSpeaking()) {
//                    tts.stop();
//                }
//            }
//        long id = intent.getLongExtra("id", -1);

//        if (action.equalsIgnoreCase("playpause")){
//            if (TtsFragment. != null) {
//                TtsFragment.toStop(context);
//            }
//        toPause();
//        } else if {}

//        if(Constant.PLAY_ALBUM.equals(action)) {
//            //playAlbum(id);
//        } else if(Constant.QUEUE_ALBUM.equals(action)) {
//            //queueAlbum(id);
//        } else if(Constant.PLAY_TRACK.equals(action)) {
//            //playTrack(id);
//        } else if(Constant.QUEUE_TRACK.equals(action)) {
//            //queueTrack(id);
//        } else if(Constant.PLAY_PAUSE_TRACK.equals(action)) {
//            //                playPauseTrack();
//            System.out.println("press play");
//        } else if(Constant.HIDE_PLAYER.equals(action)) {
//            //                hideNotification();
//            System.out.println("press next");
//        }
//        else {
//        }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }
}