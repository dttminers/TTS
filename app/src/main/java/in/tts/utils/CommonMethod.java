package in.tts.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import in.tts.R;

public class CommonMethod {
    private static AlertDialog dialog;


    public static void setAnalyticsData(Context context, String id, String name, String contentType) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
    public static void toCallLoader(Context context, String msg) {
        try {
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
        } catch (Exception|Error e) {
            e.printStackTrace(); Crashlytics.logException(e);
        }
    }

    public static void toCloseLoader() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception|Error e) {
            e.printStackTrace(); Crashlytics.logException(e);
        }
    }

}