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
import java.util.List;
import java.util.Locale;
import java.util.Set;

import in.tts.model.AudioSetting;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class TTS10 implements TextToSpeech.OnUtteranceCompletedListener {

    private static TextToSpeech tts;

    private AudioSetting audioSetting;
    private PrefManager prefManager;
    ArrayList<Voice> male_voice_array = new ArrayList<>();
    ArrayList<Voice> female_voice_array = new ArrayList<>();
    ArrayList<Voice> normal = new ArrayList<>();
    String selectedLang;
    String langCountry;
    String selectedVoice;
    String v_male = "en-us-x-sfg#male_3-local";
    String v_female = "en-us-x-sfg#female_2-local";
    //    Voice v = null;
    String voice = "";
    int lang;

    public TTS10(final Context mContext) {
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

                        if (audioSetting.getLangSelection().equals("hin_IND")) {
                            lang = tts.setLanguage(new Locale("hin", "IND"));
                        } else if (audioSetting.getLangSelection().equals("mar_IND")) {
                            lang = tts.setLanguage(new Locale("mar", "IND"));
//                        } else if (audioSetting.getLangSelection().equals("ta_IND")) {
                        } else if (audioSetting.getLangSelection().equals("ta")) {
//                            lang = tts.setLanguage(new Locale("ta", "IND"));
                            lang = tts.setLanguage(new Locale("ta"));
                            //  getVoices Voice[Name: ta, locale: ta, quality: 100, latency: 400, requiresNetwork: true, features: [networkTimeoutMs, networkRetriesCount, notInstalled]]:ta:ta_IND:false:false
                        } else {
                            lang = tts.setLanguage(Locale.US);
                        }
                        Log.d("TTS_TAG", "VOICE BY APP " + audioSetting.getVoiceSelection() + " " + audioSetting.getLangSelection());

                        Set<String> a = new HashSet<>();
                        a.add("female");
                        a.add("male");

                        //Get all available voices
                        for (Voice tmpVoice : tts.getVoices()) {
                            Log.d("TAG", " getVoices "
                                    + tmpVoice + ":"
                                    + tmpVoice.getLatency() + ":"
                                    + tmpVoice.isNetworkConnectionRequired() + ":"
                                    + tmpVoice.getName() + ":"
                                    + audioSetting.getLangSelection() + ":"
                                    + (tmpVoice.getLocale().toString().equalsIgnoreCase(audioSetting.getLangSelection())) + ":"
                                    + (tmpVoice.getLocale().equals(audioSetting.getLangSelection())));
                            if (tmpVoice.getLocale().toString().equalsIgnoreCase(audioSetting.getLangSelection())) {
                                voiceValidator(tmpVoice);
                            }
                        }

                        if (selectedVoice.equalsIgnoreCase("male")) {
                            Log.d("TAG", " setVoices male 1");
                            if (male_voice_array.size() > 0) {
                                Log.d("TAG", " setVoices  male 2");
                                tts.setVoice(new Voice(male_voice_array.get(0).getName(), male_voice_array.get(0).getLocale(), male_voice_array.get(0).getQuality(), male_voice_array.get(0).getLatency(), male_voice_array.get(0).isNetworkConnectionRequired(), a));
                            } else {
                                Log.d("TAG", " setVoices male 3" + normal.size());
                                if (normal.size() > 0) {
                                    Log.d("TAG", " setVoices male 31 " + normal.get(0).getName());
                                    tts.setVoice(new Voice(normal.get(0).getName(), normal.get(0).getLocale(), normal.get(0).getQuality(), normal.get(0).getLatency(), normal.get(0).isNetworkConnectionRequired(), a));
                                } else {
                                    Log.d("TAG", " setVoices male 32 " + normal.size());
                                    tts.setVoice(new Voice(v_male, new Locale("en", "US"), 400, 200, true, a));
                                }
                            }
                        } else if (selectedVoice.equalsIgnoreCase("female")) {
                            Log.d("TAG", " setVoices female 1");
                            if (female_voice_array.size() > 0) {
                                Log.d("TAG", " setVoices female 2");
                                tts.setVoice(new Voice(female_voice_array.get(0).getName(), female_voice_array.get(0).getLocale(), female_voice_array.get(0).getQuality(), female_voice_array.get(0).getLatency(), female_voice_array.get(0).isNetworkConnectionRequired(), a));
                            } else {
                                Log.d("TAG", " setVoices female 3 " + normal.size());
                                if (normal.size() > 0) {
                                    Log.d("TAG", " setVoices female 31 " + normal.get(0).getName());
                                    tts.setVoice(new Voice(normal.get(0).getName(), normal.get(0).getLocale(), normal.get(0).getQuality(), normal.get(0).getLatency(), normal.get(0).isNetworkConnectionRequired(), a));
                                } else {
                                    Log.d("TAG", " setVoices female 32 " + normal.size());
                                    tts.setVoice(new Voice(v_female, new Locale("en", "US"), 400, 200, true, a));
                                }
                            }
                        } else {
                            Log.d("TAG", " setVoices others 3 " + normal.size());
                        }


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Log.d("TTS_TAG", " DATA LAng " + ":" + audioSetting.getLangSelection() + ":" + lang + ":" + tts.getLanguage() + ":" + tts.getAvailableLanguages());
                        }

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
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void SpeakLoud(String text, String fileName) throws Exception, Error {
        Log.d("Tag", " Speak " + tts.isSpeaking() + text);
        text = "namaste ";
        if (text.length() >= 1000) {
            List<String> texts = new ArrayList<>();
            int textLength = text.length();
            int c = textLength / 1000 + ((textLength % 1000 == 0) ? 0 : 1);
            int start = 0;
            int end = text.indexOf(" ", 1000);

            for (int i = 1; i <= c; i++) {
                texts.add(text.substring(start, end));
                start = end;
                if ((start + 1000) < textLength) {
                    end = text.indexOf(" ", start + 1000);
                } else {
                    end = textLength;
                }
            }

            for (int i = 0; i < texts.size(); i++) {
                Log.d("TAG", " Speak 1 " + fileName + ":" + text);
                tts.speak(texts.get(i), isSpeaking() ? TextToSpeech.QUEUE_ADD : TextToSpeech.QUEUE_FLUSH, null, text);
                toSaveAudioFile(texts.get(i), fileName + "_" + c + i);
            }

        } else {
            Log.d("TAG", " Speak 1 " + fileName + ":" + text);
            tts.speak(text, isSpeaking() ? TextToSpeech.QUEUE_ADD : TextToSpeech.QUEUE_FLUSH, null, text);
            toSaveAudioFile(text, fileName);
        }
    }

    private String toSaveAudioFile(String text, final String fileName) throws Exception, Error {
        Log.d("TAG", " toSaveAudioFile 1 " + fileName + ":" + text);
        File mMainFolder = new File(Environment.getExternalStorageDirectory(), "READ_IT/Audio");
        if (!mMainFolder.exists()) {
            mMainFolder.mkdirs();
        }
        final File audio;
        if (fileName == null) {
            audio = new File(mMainFolder + "/" + "AUDIO" + System.currentTimeMillis() + ".wav");//;+ mAudioFilename);
        } else {
            audio = new File(mMainFolder + "/" + fileName + ".wav");
        }

        HashMap<String, String> myHashRender = new HashMap<>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
        final int i3 = tts.synthesizeToFile(text, myHashRender, audio.getAbsolutePath());

        Log.d("TAG", " TTS  :  toSaveAudioFile 2: " + i3 + CommonMethod.getFileSize(audio));
        tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
            @Override
            public void onUtteranceCompleted(String s) {
                Log.d("TAG", " TTS  :  toSaveAudioFile 3: " + CommonMethod.getFileSize(audio) + ":" + audio.getAbsolutePath() + "\n:::: " + s);
            }
        });
        return audio.getAbsolutePath();
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
    public void toSynthesizeToFileBundle(CharSequence text, Bundle
            bundle, HashMap<String, String> params, File file, String utteranceId, String filename) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.synthesizeToFile(text, bundle, file, utteranceId);
        } else {
            tts.synthesizeToFile(text.toString(), params, filename);
        }
    }


    public void voiceValidator(Voice voice) {

        if (voice.toString().contains("#female")) {
            Log.d("TAG", " setVoices voiceValidator female if " + voice);
            female_voice_array.add(voice);
        } else {
            Log.d("TAG", " setVoices  voiceValidator female else " + voice + ":" + normal.contains(voice));
            if (!normal.contains(voice)) {
                normal.add(voice);
            }
        }
        if (voice.toString().contains("#male")) {
            Log.d("TAG", " setVoices  voiceValidator male if " + voice);
            male_voice_array.add(voice);
        } else {
            Log.d("TAG", " setVoices  voiceValidator male else " + voice + ":" + normal.contains(voice));
            if (!normal.contains(voice)) {
                normal.add(voice);
            }
        }
    }
}

//        if (tts.isSpeaking()) {
//            //Log.d("TTS_TAG", "NOT SPEAKING "+text);
//            if (text.length() > 3999) {
//            tts.speak(text.substring(0, 3999), TextToSpeech.QUEUE_FLUSH, null, text);
//
//            //  tts.speak(text, TextToSpeech.QUEUE_ADD, null);
//        } else {
//
//            // Log.d("TTS_TAG", "SPEAKING");
////            tts.speak(text, TextToSpeech.QUEUE_ADD, null);
//            if (text.length() > 3999) {
//                tts.speak(text.substring(0, 3999), TextToSpeech.QUEUE_ADD, null, text);
//            } else {
//                tts.speak(text.substring(4000, text.length() > 7000 ? 7000 : text.length()), TextToSpeech.QUEUE_ADD, null, text);
//            }
//        }

/*
if (selectedVoice.equalsIgnoreCase("male")) {
        if (male_voice_array.size() > 0) {
        voice = male_voice_array.get(0);
        v = new Voice(voice, new Locale(selectedLang, langCountry), 400, 200, true, a);
        tts.setVoice(v);
        } else {
//                                if (b) {
//                                    CommonMethod.toDisplayToast(PdfShowingActivity.this, "Selected language not found. Reading data using default language");
//                                }
        v = new Voice(v_male, new Locale("en", "US"), 400, 200, true, a);
        tts.setVoice(v);
        }
        } else {
        if (female_voice_array.size() > 0) {
        voice = female_voice_array.get(0);
        v = new Voice(voice, new Locale(selectedLang, langCountry), 400, 200, true, a);
        tts.setVoice(v);
        } else {
        if (b) {
        CommonMethod.toDisplayToast(PdfShowingActivity.this, "Selected language not found. Reading data using default language");
        }
        v = new Voice(v_female, new Locale("en", "US"), 400, 200, true, a);
        tts.setVoice(v);
        }
        }
//                    }
*/
/*

    public String toSaveAudioFile(String text, String fileName) throws Exception, Error {
        Log.d("TAG", " SPEAK_Text " + text);
        File audio;
        File mMainFolder = new File(Environment.getExternalStorageDirectory(), "READ_IT/Audio");
        // Log.d("TTS_TAG", "Sound PATH " + mMainFolder.exists());

        if (!mMainFolder.exists()) {
            mMainFolder.mkdirs();
        }
//        } else {
        if (fileName == null) {
            audio = new File(mMainFolder + "/" + "AUDIO" + System.currentTimeMillis() + ".wav");//;+ mAudioFilename);
        } else {
            audio = new File(mMainFolder + "/" + fileName + ".wav");
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            tts.synthesizeToFile(text, null, audio, "READ_IT");
//            Log.d("TAG", " TTS  :  audio size 1: " + CommonMethod.getFileSize(audio));
//        } else {
//        HashMap<String, String> hm = new HashMap<>();
////            hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "READ_IT");
////            hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, String.valueOf(System.currentTimeMillis()));
//        hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
//        tts.synthesizeToFile(text, hm, audio.getPath());
        HashMap<String, String> myHashRender = new HashMap<>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
        int i3 = tts.synthesizeToFile(text, myHashRender, audio.getAbsolutePath());
        Log.d("TAG", " TTS  :  audio size 2: " + i3 + CommonMethod.getFileSize(audio));
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
    }*/