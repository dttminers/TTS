package in.tts.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import in.tts.R;

public class CommonMethod {
    private static AlertDialog dialog;
    private static TextView tvMsg;

    public static int dpToPx(int dp, Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static boolean isValidEmail(String email) {
        return Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(email).matches();
    }

    public static boolean isValidPassword(String str) {
        //Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character:
        return Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}").matcher(str).matches();
    }

    public static String firstLetterCaps(String myString) {
        return myString.substring(0, 1).toUpperCase() + myString.substring(1);
    }

    public static void setAnalyticsData(Context context, String id, String name, String contentType) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        bundle.putString("DeviceId",  Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Map<String, String> articleParams = new HashMap<>();
        articleParams.put("PAGE", id);
        articleParams.put("SUB_PAGE", name);
        articleParams.put("DATA", contentType);
        articleParams.put("DeviceId", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
        FlurryAgent.logEvent("Article_Read", articleParams, true);
    }

    public static void toCallLoader(Context context, String msg) {
        try {
            Log.d("TAG", " LOADER stxt : " + msg);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (inflater != null) {
                View view = inflater.inflate(R.layout.custom_loader, null, false);

                alertDialogBuilder.setView(view);
                alertDialogBuilder.setTitle(null);
                alertDialogBuilder.setCancelable(false);

                dialog = alertDialogBuilder.create();
                dialog.setCancelable(false);
                dialog.show();

                tvMsg = view.findViewById(R.id.txtLoaderMsg);
                tvMsg.setText(msg);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public static void toCloseLoader() {
        try {
            if (dialog != null && dialog.isShowing()) {
                if (tvMsg != null) {
                    Log.d("TAG", " LOADER ctxt : " + tvMsg.getText().toString());
                }
                dialog.dismiss();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public static void toReleaseMemory() {
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public static boolean isSignedIn(Context context) {
        try {
            Log.d("TAG ", "signed Google : " + (GoogleSignIn.getLastSignedInAccount(context) != null));
            Log.d("TAG ", "signed Facebook 1 :" + AccessToken.isCurrentAccessTokenActive());
            Log.d("TAG ", "signed Facebook 2 :" + AccessToken.getCurrentAccessToken());
            Log.d("TAG ", "signed Facebook 3 :" + Profile.getCurrentProfile());
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return false;
    }

    public static void toDisplayToast(Context context, String str) {
        try {
            if (context != null) {
                if (str != null) {
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public static void toSetTitle(ActionBar supportActionBar, Context context, String title) {
        try {
            supportActionBar.show();
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(context, R.drawable.ic_left_white_24dp));

            if (title != null) {
                supportActionBar.setTitle(title);
            } else {
                supportActionBar.setTitle(context.getResources().getString(R.string.app_name));
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }
}