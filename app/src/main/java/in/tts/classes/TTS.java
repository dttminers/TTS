package in.tts.classes;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import in.tts.model.AudioSetting;
import in.tts.utils.CommonMethod;


public class TTS implements TextToSpeech.OnUtteranceCompletedListener {

    private static TextToSpeech tts;
    private Context mContext;
    private AudioSetting audioSetting;

    public TTS(Context context) {
        try {
            mContext = context;
            audioSetting = AudioSetting.getAudioSetting(mContext);
            tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {

                @Override
                public void onInit(int i) {
                    try {

//                        tts.setPitch((float) 0.6);
                        tts.setSpeechRate(audioSetting.getVoiceSpeed());

//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            //tts.getVoices() +
                            Log.d("TAG", " DATA LAng " +  toSetLanguage() + ":" + audioSetting.getLangSelection());
//                        }
                        int lang = tts.setLanguage(audioSetting.getLangSelection() != null ? audioSetting.getLangSelection() : Locale.US);
                        if (i == TextToSpeech.SUCCESS) {

                            if (lang == TextToSpeech.LANG_MISSING_DATA
                                    || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("TTS", "This Language is not supported");
                            }
                        } else {
                            Log.e("TTS", "Initialization Failed!");
                        }

                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            }, "com.google.android.tts");

//            tts.setEngineByname("com.google.android.tts");

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void SpeakLoud(String text) throws Exception, Error {
        if (!isSpeaking()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public String toSaveAudioFile(String text, String mAudioFilename) throws Exception, Error {
        File mMainFolder = new File(Environment.getExternalStorageDirectory(), "READ_IT/Audio");
        Log.d("TAG", "Sound PATH " + mMainFolder.exists());

        if (!mMainFolder.exists()) {
            mMainFolder.mkdirs();
        }
//        } else {
            File audio = new File(mMainFolder + "/" + mAudioFilename);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.synthesizeToFile(text, null, audio, "READ_IT");
            } else {
                HashMap<String, String> hm = new HashMap();
                hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "READ_IT");
                tts.synthesizeToFile(text, hm, audio.getPath());
            }
            Log.d("TAG", " sound_path" + audio.getAbsolutePath());
            return audio.getAbsolutePath();
//        }
//        } else {
//            if (mContext != null) {
//                CommonMethod.toDisplayToast(mContext, "Unable to create audio file");
//            }
//            return null;
//        }
//        } else {
//            if (mContext != null) {
//                CommonMethod.toDisplayToast(mContext, "Unable to create audio File");
//            }
//            return null;
//        }
    }

    public int toSetLanguage() throws Exception, Error {
        Log.d("TAG ", " toSetLanguage " + audioSetting.getAccentSelection());
        return tts.setLanguage(audioSetting.getLangSelection() != null ? audioSetting.getLangSelection() : Locale.US);
    }


    public boolean isSpeaking() throws Exception, Error {
        return tts.isSpeaking();
    }

    public void toStop() throws Exception, Error {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
        }
    }

    public void toShutDown() throws Exception, Error {
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

//    @Override
//    public void onInit(int status) {
//
//        if (status == TextToSpeech.SUCCESS) {
//
//            if (SetLanguage() == TextToSpeech.LANG_MISSING_DATA
//                    || SetLanguage() == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e("TTS", "This Language is not supported");
////                speakloud();
//            }
//        } else {
//            Log.e("TTS", "Initilization Failed!");
//        }
//
//    }

//        tts.setOnUtteranceCompletedListener(new TextToSpeech
//                .OnUtteranceCompletedListener() {
//            public void onUtteranceCompleted(String uid) {
//                if (uid.equals("READ_IT")) {
//                    Toast.makeText(this, "Saved to " +
//                            mAudioFilename, Toast.LENGTH_LONG).show();
//                }
//            }
//        });

//        HashMap<String, String> myHashRender = new HashMap();
//        String wakeUpText = "Are you up yet?";
//        String destFileName = "/sdcard/myAppCache/wakeUp.wav";
//        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, wakeUpText);
//        tts.synthesizeToFile(text, myHashRender, Environment.DIRECTORY_DOWNLOADS+"/"+System.currentTimeMillis()+"wakeUp.wav");
//        Log.d("TAG", " path : " + Environment.DIRECTORY_DOWNLOADS+System.currentTimeMillis()+"wakeUp.wav");
