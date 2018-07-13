package in.tts.model;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;
import android.util.Log;

import io.fabric.sdk.android.Fabric;

public class ReadIt extends Application {

    public void onCreate() {
        super.onCreate();
        try {
            FirebaseAnalytics.getInstance(this);
            AppEventsLogger.activateApp(this);
            Fabric.with(this, new Crashlytics());
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onLowMemory() {
        super.onLowMemory();
        Log.d("TAG", "onLowMemory");
    }

    public void onTerminate() {
        super.onTerminate();
    }

    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
