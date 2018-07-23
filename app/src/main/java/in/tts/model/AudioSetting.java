package in.tts.model;

import android.content.Context;
import android.util.Log;

import java.util.Locale;

public class AudioSetting {

    private static transient AudioSetting audioSetting;
    private transient Context context;
    private String VoiceSelection ;
    private String LangSelection ;//= String.valueOf(new Locale("en"));
    private String AccentSelection ;//= String.valueOf(Locale.US);
    private int VoiceSpeed ;//= 1;

    public AudioSetting(Context context){
        this.context = context;
    }

    public static AudioSetting getAudioSetting(Context context) {
//        Log.d("TAG SEEK", "DATA1 " + audioSetting.getVoiceSpeed());
        if (audioSetting == null) {
//            Log.d("TAG SEEK", "DATA2 " + audioSetting.getVoiceSpeed());
            audioSetting = new AudioSetting(context);
        }
        Log.d("TAG SEEK", "DATA3 " + audioSetting.getVoiceSpeed());
        return audioSetting;
    }

    public void setAudioSetting(AudioSetting audioSetting) {
        audioSetting = audioSetting;
    }


    public String getVoiceSelection() {
        return VoiceSelection;
    }

    public void setVoiceSelection(String voiceSelection) {
       VoiceSelection = voiceSelection;
    }


    public String getLangSelection() {
        return LangSelection;
    }

    public void setLangSelection(String langSelection) {
        LangSelection = langSelection;
    }

    public String getAccentSelection() {
        return AccentSelection;
    }

    public void setAccentSelection(String accentSelection) {
        AccentSelection = accentSelection;
    }

    public float getVoiceSpeed() {
        Log.d("TAG", " getVoiceSpeech " + VoiceSpeed);
        return VoiceSpeed;
    }

    public void setVoiceSpeed(int voiceSpeed) {
        Log.d("TAG", " setVoiceSpeech " + voiceSpeed);
        VoiceSpeed = voiceSpeed;
    }
}
