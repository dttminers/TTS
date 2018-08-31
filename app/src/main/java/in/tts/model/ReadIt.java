package in.tts.model;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.flurry.android.FlurryAgent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import in.tts.R;
import in.tts.services.ClipboardMonitorService;
import io.fabric.sdk.android.Fabric;

//import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
//import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

public class ReadIt extends Application {

    public void onCreate() {
        super.onCreate();
        try {

            // Show the contents of the clipboard history.
            startService(new Intent(this, ClipboardMonitorService.class));

            new FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .build(this, getString(R.string.str_flurry_app));

            FirebaseAnalytics.getInstance(this);
            AppEventsLogger.activateApp(this);
            Fabric.with(this, new Crashlytics());

            final Fabric fabric = new Fabric.Builder(this)
                    .kits(new Crashlytics())
                    .debuggable(true)
                    .build();
            Fabric.with(fabric);

        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onLowMemory() {
        super.onLowMemory();
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
