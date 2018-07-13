package in.tts.classes;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

public class TTS implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {

    private static TextToSpeech tts;

    public TTS(Context context) {
        tts = new TextToSpeech(context, this);
    }

    public void SpeakLoud(String text) {
        if (!isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public int SetLanguage() {
        return tts.setLanguage(Locale.US);
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            if (SetLanguage() == TextToSpeech.LANG_MISSING_DATA
                    || SetLanguage() == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
//                speakloud();
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    public void toStop() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
        }
    }

    public void toShutDown() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
        }
    }

    public void to(String earcon, int queueMode, Bundle params, String utteranceId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.playEarcon(earcon, queueMode, params, utteranceId);
        }
    }

    public boolean isSpeaking() {
        return tts.isSpeaking();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Voice toGetVoice() {
        return tts.getVoice();
    }

    public void toSetSpeechRate(float speechRate) {
        tts.setSpeechRate(speechRate);
    }

    public void toSetPitch(float pitch) {
        tts.setSpeechRate(pitch);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void toPlaySilentUtterance(long durationInMs, int queueMode, String utteranceId) {
        tts.playSilentUtterance(durationInMs, queueMode, utteranceId);
    }

    public void toSetAudioAttributes(AudioAttributes audioAttributes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.setAudioAttributes(audioAttributes);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Set<Voice> toGetListOfVoices() {
        return tts.getVoices();
    }

    public void toAddEarcon(String earcon, String packagename, int resourceId) {
        //Adds a mapping between a string of text and a sound resource in a package.
        tts.addEarcon(earcon, packagename, resourceId);
    }

    public void toAddEarcon(String earcon, String filename) {
        //This method was deprecated in API level 21. As of API level 21, replaced by addEarcon(String, File).
        tts.addEarcon(earcon, filename);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void toAddEarcon(String earcon, File file) {
        //    Adds a mapping between a string of text and a sound file.
        tts.addEarcon(earcon, file);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void toAddSpeech(CharSequence text, File file) {
        //Adds a mapping between a CharSequence (may be spanned with TtsSpans and a sound file.
        tts.addSpeech(text, file);
    }

    public void toAddSpeech(String text, String packagename, int resourceId) {
        //Adds a mapping between a string of text and a sound resource in a package.
        tts.addSpeech(text, packagename, resourceId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void toAddSpeech(CharSequence text, String packagename, int resourceId) {
        //Adds a mapping between a CharSequence (may be spanned with TtsSpans) of text and a sound resource in a package.
        tts.addSpeech(text, packagename, resourceId);
    }

    public void toAddSpeech(String text, String filename) {
        //Adds a mapping between a string of text and a sound file.
        tts.addSpeech(text, filename);
    }

    @Override
    public void onUtteranceCompleted(String s) {

    }

    //synthesizes the given text to a file using the specified parameters.
    public void toSynthesizeToFileBundle(CharSequence text, Bundle bundle, HashMap<String, String> params, File file, String utteranceId, String filename) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.synthesizeToFile(text, bundle, file, utteranceId);
        } else {
            tts.synthesizeToFile(text.toString(), params, filename);
        }
    }
}
