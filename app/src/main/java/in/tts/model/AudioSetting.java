package in.tts.model;

import android.content.Context;

public class AudioSetting {
    private static transient AudioSetting audioSetting;
    private final Context context;
    private String VoiceSelection;
    private String LangSelection;
    private String AccentSelection;
    private int VoiceSpeed;

    public AudioSetting(Context context){
        this.context = context;
    }

    public static AudioSetting getAudioSetting(Context context) {
        if (audioSetting == null) {
            audioSetting = new AudioSetting(context);
        }
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
        return VoiceSpeed;
    }

    public void setVoiceSpeed(int voiceSpeed) {
        VoiceSpeed = voiceSpeed;
    }
}
