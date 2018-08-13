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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.tts.activities.AudioSettingActivity;
import in.tts.model.AudioSetting;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;


public class TTS implements TextToSpeech.OnUtteranceCompletedListener {

    private static TextToSpeech tts;

    private AudioSetting audioSetting;
    private PrefManager prefManager;
    ArrayList<String> male_voice_array = new ArrayList<String>();
    ArrayList<String> female_voice_array = new ArrayList<String>();
    String selectedLang;
    String langCountry;
    String selectedVoice;
    String v_male = "en-us-x-sfg#male_3-local";
    String v_female = "en-us-x-sfg#female_2-local";
    Voice v = null;
    String voice = "";
    int lang;

    public TTS(final Context mContext) {
        try {

            prefManager = new PrefManager(mContext);
            prefManager.getAudioSetting();


            tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onInit(int i) {
                    try {
                        audioSetting = AudioSetting.getAudioSetting(mContext);
                        selectedLang = CommonMethod.LocaleFromString(audioSetting.getLangSelection()).getLanguage();
                        langCountry = CommonMethod.LocaleFromString(audioSetting.getLangSelection()).getCountry();
                        selectedVoice = audioSetting.getVoiceSelection();

                        tts.setEngineByPackageName("com.google.android.tts");
                        tts.setPitch((float) 0.8);
                        tts.setSpeechRate(audioSetting.getVoiceSpeed());

                        // int lang =tts.setLanguage(new Locale("hin","IND"));
                        // int lang =tts.setLanguage(new Locale(selectedLang,langCountry));
                        //int lang =  tts.setLanguage(Locale.US);

                        if (audioSetting.getLangSelection().equals("hin_IND")) {
                            lang = tts.setLanguage(new Locale("hin", "IND"));
                        } else if (audioSetting.getLangSelection().equals("mar_IND")) {
                            lang = tts.setLanguage(new Locale("mar", "IND"));
                        } else if (audioSetting.getLangSelection().equals("ta_IND")) {
                            lang = tts.setLanguage(new Locale("ta", "IND"));
                        } else {
                            lang = tts.setLanguage(Locale.US);
                        }
                        Log.d("TTS_TAG", "VOICE BY APP " + audioSetting.getVoiceSelection() + " " + audioSetting.getLangSelection());

                        Set<String> a = new HashSet<>();
                        a.add("female");
                        a.add("male");

                        //Get all available voices
                        for (Voice tmpVoice : tts.getVoices()) {
                            if (tmpVoice.getLocale().equals(audioSetting.getLangSelection())) {
                                voiceValidator(tmpVoice.getName());
                            }

                        }

                        if (selectedVoice.equalsIgnoreCase("male")) {
                            if (male_voice_array.size() > 0) {
                                voice = male_voice_array.get(0);
                                v = new Voice(voice, new Locale(selectedLang, langCountry), 400, 200, true, a);
                                tts.setVoice(v);
                            } else {
                                // voice=v_male;
                                // If nothing is similar then select default voice
                                CommonMethod.toDisplayToast(mContext, "Selected language not found. Reading data using default language");
                                v = new Voice(v_male, new Locale("en", "US"), 400, 200, true, a);
                                tts.setVoice(v);
                            }

                        } else {
                            if (female_voice_array.size() > 0) {
                                voice = female_voice_array.get(0);
                                v = new Voice(voice, new Locale(selectedLang, langCountry), 400, 200, true, a);
                                tts.setVoice(v);
                            } else {
                                // voice=v_female;
                                // If nothing is similar then select default voice
                                CommonMethod.toDisplayToast(mContext, "Selected language not found. Reading data using default language");
                                v = new Voice(v_female, new Locale("en", "US"), 400, 200, true, a);
                                tts.setVoice(v);
                            }
                        }


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Log.d("TTS_TAG", " DATA LAng " + ":" + audioSetting.getLangSelection() + ":" + lang + ":" + tts.getLanguage() + ":" + tts.getAvailableLanguages());
                        }
//                        }
//                        int lang = tts.setLanguage(audioSetting.getLangSelection() != null ? audioSetting.getLangSelection() : Locale.US);

                        if (i == TextToSpeech.SUCCESS) {
                            if (lang == TextToSpeech.LANG_MISSING_DATA
                                    || lang == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("TTS_TAG", "This Language is not supported");
                            } else {
                                Log.d("TTS_TAG", " Else ");
                            }
                        } else {
                            Log.e("TTS_TAG", "Initialization Failed!");
                        }

                        Log.v("log", "initi");

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
            //Log.d("TTS_TAG", "NOT SPEAKING "+text);
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            //  tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        } else {

            // Log.d("TTS_TAG", "SPEAKING");
            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    public String toSaveAudioFile(String text) throws Exception, Error {
        Log.d("TAG", " SPEAK_Text " + text);
        File mMainFolder = new File(Environment.getExternalStorageDirectory(), "READ_IT/Audio");
        // Log.d("TTS_TAG", "Sound PATH " + mMainFolder.exists());

        if (!mMainFolder.exists()) {
            mMainFolder.mkdirs();
        }
//        } else {
        File audio = new File(mMainFolder + "/" + System.currentTimeMillis() + ".mp3");//;+ mAudioFilename);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            tts.synthesizeToFile(text, null, audio, "READ_IT");
//            Log.d("TAG", " TTS  :  audio size 1: " + CommonMethod.getFileSize(audio));
//        } else {
        HashMap<String, String> hm = new HashMap<>();
//            hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "READ_IT");
//            hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(System.currentTimeMillis()));
        hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
        tts.synthesizeToFile(text, hm, audio.getPath());
        Log.d("TAG", " TTS  :  audio size 2: " + CommonMethod.getFileSize(audio));
//        }
        //   Log.d("TTS_TAG", " sound_path" + audio.getAbsolutePath() + ":" + CommonMethod.getFileSize(audio));
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

    public int toSeLanguage() throws Exception, Error {
        Log.d("TTS_TAG ", " toSetLanguage " + audioSetting.getAccentSelection());
        return tts.setLanguage(CommonMethod.LocaleFromString(audioSetting.getLangSelection()) != null ? CommonMethod.LocaleFromString(audioSetting.getLangSelection()) : Locale.US);
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


    public void voiceValidator(String voice) {

        if (voice.contains("#female")) {
            female_voice_array.add(voice);
        }
        if (voice.contains("#male")) {
            male_voice_array.add(voice);
        }

    }

}


