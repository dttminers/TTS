package in.tts.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
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

import in.tts.R;

public class CommonMethod {
    private static AlertDialog dialog;

    public static int dpToPx(int dp, Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String firstLetterCaps(String myString) {
        return myString.substring(0, 1).toUpperCase() + myString.substring(1);
    }

    public static void setAnalyticsData(Context context, String id, String name, String contentType) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public static void toCallLoader(Context context, String msg) {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
//            } else {
//                toCloseLoader();
            }
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

                TextView tvMsg = view.findViewById(R.id.txtLoaderMsg);
                tvMsg.setText(msg);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public static void toCloseLoader() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
//            } else {
//                toCloseLoader();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
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
        } catch (Exception| Error e){
            e.printStackTrace();
            Crashlytics.logException(e);
        }
         return false;
    }

    public static void toDisplayToast(Context context, String str) {
        try {
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

//    public static void signInSilently(Context context) {
//        GoogleSignInClient signInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
//        signInClient.silentSignIn().addOnCompleteListener(context,
//                new OnCompleteListener<GoogleSignInAccount>() {
//                    @Override
//                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
//                        if (task.isSuccessful()) {
//                            // The signed in account is stored in the task's result.
//                            GoogleSignInAccount signedInAccount = task.getResult();
//                        } else {
//                            // Player will need to sign-in explicitly using via UI
//                        }
//                    }
//                });
//    }
}