package in.tts.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import org.json.JSONObject;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IMAGE_LIST = "IMAGE_LIST";
    private static final String IMAGE_LIST_INFO = "IMAGE_LIST_INFO";

    private static final String PDF_LIST = "PDF_LIST";
    private static final String PDF_LIST_INFO = "PDF_LIST_INFO";

    private static final String USER_INFO = "USER_INFO";
    private static final String USER_INFO_PREFERS = "USER_INFO_PREFERS";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

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
                Log.d("TAG", "getUserinfo " + userJSON);

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
            e.printStackTrace();
        }
    }

    public void setUserInfo() {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(USER_INFO_PREFERS, 0).edit();
            editor.putString(USER_INFO, new JSONObject(new Gson().toJson(User.getUser(_context))).toString());
            editor.apply();
            editor.commit();
            Log.d("TAG", "getUserinfo " + new JSONObject(new Gson().toJson(User.getUser(_context))).toString());
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public ArrayList<String> toGetPDFList() {
        try {
            String us = _context.getSharedPreferences(PDF_LIST, 0).getString(PDF_LIST_INFO, null);
            if (us != null) {
                return new ArrayList<String>(Arrays.asList(us.replace("[", "").replace("]", "").split(",")));
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return null;
        }
    }

    public void toSetPDFFileList(ArrayList<String> list) {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(PDF_LIST, 0).edit();
            editor.putString(PDF_LIST_INFO, list.toString());
            editor.apply();
            editor.commit();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public ArrayList<String> toGetImageList() {
        try {
            String us = _context.getSharedPreferences(IMAGE_LIST, 0).getString(IMAGE_LIST_INFO, null);
            if (us != null) {
                return new ArrayList<String>(Arrays.asList(us.replace("[", "").replace("]", "").split(",")));
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return null;
        }
    }

    public void toSetImageFileList(ArrayList<String> list) {
        try {
            SharedPreferences.Editor editor = _context.getSharedPreferences(IMAGE_LIST, 0).edit();
            editor.putString(IMAGE_LIST_INFO, list.toString());
            editor.apply();
            editor.commit();
            Log.d("TAG", "toSetImageFileList " + list.size());
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

}