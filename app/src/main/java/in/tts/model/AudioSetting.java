package in.tts.model;

import android.content.Context;
import android.util.Log;

import java.util.Locale;

public class AudioSetting {

    private static transient AudioSetting audioSetting;
    private transient Context context;
    private String VoiceSelection ;
    private Locale LangSelection ;//= String.valueOf(new Locale("en"));
    private String AccentSelection ;//= String.valueOf(Locale.US);
    private int VoiceSpeed =0;//= 1;


    public AudioSetting(Context context) {
        this.context = context;
    }

    public static AudioSetting getAudioSetting(Context context) {
        try {
//            Log.d("Tag", " audioSetting " + audioSetting.getVoiceSpeed() + ":" + audioSetting.getLangSelection());
            if (audioSetting == null) {
                audioSetting = new AudioSetting(context);
            }
        } catch (Exception| Error e){
            e.printStackTrace();

        }
        return audioSetting;
    }

    public void setAudioSetting(AudioSetting audioSettin) {
        audioSetting = audioSettin;
    }

    public String getVoiceSelection() {
        return VoiceSelection;
    }

    public void setVoiceSelection(String voiceSelection) {
       VoiceSelection = voiceSelection;
    }

    public Locale getLangSelection() {
        return LangSelection;
    }

    public void setLangSelection(Locale langSelection) {
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
