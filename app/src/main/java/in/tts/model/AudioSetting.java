package in.tts.model;

public class AudioSetting {

    private String VoiceSelection;
    private String LangSelection;
    private String AccentSelection;
    private float VoiceSpeed;


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

    public void setVoiceSpeed(float voiceSpeed) {
        VoiceSpeed = voiceSpeed;
    }
}
