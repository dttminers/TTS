package in.tts.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import org.json.JSONObject;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PrefManager {

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IMAGE_LIST = "IMAGE_LIST";
    private static final String IMAGE_LIST_INFO = "IMAGE_LIST_INFO";

    private static final String PDF_LIST = "PDF_LIST";
    private static final String PDF_LIST_INFO = "PDF_LIST_INFO";

    private static final String USER_INFO = "USER_INFO";
    private static final String USER_INFO_PREFERS = "USER_INFO_PREFERS";

    private static final String AUDIO_SETTING_INFO = "AUDIO_SETTING_INFO";
    private static final String AUDIO_SETTING_PREFERS = "AUDIO_SETTING_PREFERS";

    private static final String WEB_PREFERENCES = "WEB_PREFERENCES";
    private static final String WEB_LINKS = "WEB_LINKS";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public PrefManager(Context context) {
        _context = context;
        pref = _context.getSharedPreferences(PREF_NAME, 0);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    @AddTrace(name = "onCallIsFirstTimeLaunch", enabled = true)
    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void getUserInfo() {
        try {
            String us = _context.getSharedPreferences(USER_INFO_PREFERS, 0).getString(USER_INFO, null);
            if (us != null) {
                User user = User.getUser(_context);
                JSONObject userJSON = new JSONObject(us);

                if (!userJSON.isNull("email")) {
                    user.setEmail(userJSON.getString("email"));
                }
                if (!userJSON.isNull("mobile")) {
                    user.setMobile(userJSON.getString("mobile"));
                }

                if (!userJSON.isNull("pic")) {
                    user.setPicPath(userJSON.getString("pic"));
                }
                if (!userJSON.isNull("token")) {
                    user.setToken(userJSON.getString("token"));
                }

                if (!userJSON.isNull("id")) {
                    user.setId(userJSON.getString("id"));
                }

                if (!userJSON.isNull("name")) {
                    user.setName(userJSON.getString("name"));
                }

                if (!userJSON.isNull("name1")) {
                    user.setName(userJSON.getString("name1"));
                }

                if (!userJSON.isNull("name2")) {
                    user.setName(userJSON.getString("name2"));
                }
                if (!userJSON.isNull("picPath")) {
                    user.setPicPath(userJSON.getString("picPath"));
                }
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public void setUserInfo() {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(USER_INFO_PREFERS, 0).edit();
            editor.putString(USER_INFO, new JSONObject(new Gson().toJson(User.getUser(_context))).toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public void getAudioSetting() {
        try {
            String Audio = _context.getSharedPreferences(AUDIO_SETTING_PREFERS, 0).getString(AUDIO_SETTING_INFO, null);
            if (Audio != null) {
                AudioSetting audioSetting = AudioSetting.getAudioSetting(_context);
                JSONObject audioJSON = new JSONObject(Audio);
                Log.d("TAG SEEK", "getAudioInfo : " + audioJSON);

                if (!audioJSON.isNull("VoiceSelection")) {
                    audioSetting.setVoiceSelection(audioJSON.getString("VoiceSelection"));
                }

                if (!audioJSON.isNull("LangSelection")) {
                    audioSetting.setLangSelection(fromString(audioJSON.getString("LangSelection")));
                }

                if (!audioJSON.isNull("AccentSelection")) {
                    audioSetting.setAccentSelection(audioJSON.getString("AccentSelection"));
                }

                if (!audioJSON.isNull("VoiceSpeed")) {
                    audioSetting.setVoiceSpeed(audioJSON.getInt("VoiceSpeed"));
                    Log.d("TAG", " json Audio VoiceSpeed 1 " + audioJSON.getInt("VoiceSpeed"));
                } else {
                    Log.d("TAG", " json Audio VoiceSpeed 2 ");
                    audioSetting.setVoiceSpeed(1);
                }
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public Locale fromString(String locale) {
        String parts[] = locale.split("_", -1);
        if (parts.length == 1) return new Locale(parts[0]);
        else if (parts.length == 2
                || (parts.length == 3 && parts[2].startsWith("#")))
            return new Locale(parts[0], parts[1]);
        else return new Locale(parts[0], parts[1], parts[2]);
    }

    public void setAudioSetting() {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(AUDIO_SETTING_PREFERS, 0).edit();
            editor.putString(AUDIO_SETTING_INFO, new JSONObject(new Gson().toJson(AudioSetting.getAudioSetting(_context))).toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public ArrayList<String> toGetPDFList() {
        try {
            String us = _context.getSharedPreferences(PDF_LIST, 0).getString(PDF_LIST_INFO, null);
            if (us != null) {
                return new ArrayList<>(Arrays.asList(us.trim().replace("[", "").replace("]", "").split(",")));
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    public void toSetPDFFileList(ArrayList<String> list, boolean status) {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(PDF_LIST, 0).edit();
            if (status) {
                editor.clear();
            }
            editor.putString(PDF_LIST_INFO, list.toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public ArrayList<String> toGetImageList() {
        try {
            String us = _context.getSharedPreferences(IMAGE_LIST, 0).getString(IMAGE_LIST_INFO, null);
            if (us != null) {
                return new ArrayList<>(Arrays.asList(us.trim().replaceAll("\\s+", "").replace("[", "").replace("]", "").split(",")));
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    public void toSetImageFileList(ArrayList<String> list) {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(IMAGE_LIST, 0).edit();
            editor.putString(IMAGE_LIST_INFO, list.toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public ArrayList<String> populateSelectedSearch() {
        try {
            String s = _context.getSharedPreferences(WEB_PREFERENCES, 0).getString(WEB_LINKS, null);
            if (s != null) {
                return new ArrayList<>(Arrays.asList(s.trim().replaceAll("\\s+", "").replace("[", "").replace("]", "").split(",")));
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            return null;
        }
    }

    public void setSearchResult(List<String> selectedSearch) {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(WEB_PREFERENCES, 0).edit();
            editor.putString(WEB_LINKS, selectedSearch.toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }
}