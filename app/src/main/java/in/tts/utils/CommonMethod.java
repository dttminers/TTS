package in.tts.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.firebase.analytics.FirebaseAnalytics;

public class CommonMethod {

    public static void setAnalyticsData(Context context, String id, String name, String contentType) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}