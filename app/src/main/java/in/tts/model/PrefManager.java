package in.tts.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import org.json.JSONObject;
import com.google.gson.Gson;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String USER_INFO = "USER_INFO";
    private static final String USER_INFO_PREFERS = "USER_INFO_PREFERS";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
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


    public static void getUserInfo(Context context) {
        try {
            String us = context.getSharedPreferences(USER_INFO_PREFERS, 0).getString(USER_INFO, null);
            if (us != null) {
                User user = User.getUser(context);
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
            e.printStackTrace();
        }
    }

    public static void setUserInfo(Context context) {
        try {
            SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO_PREFERS, 0).edit();
            editor.putString(USER_INFO, new JSONObject(new Gson().toJson(User.getUser(context))).toString());
            editor.apply();
            editor.commit();
            return;
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

}
