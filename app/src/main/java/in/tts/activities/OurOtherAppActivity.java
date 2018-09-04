package in.tts.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.BuildConfig;
import in.tts.R;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class OurOtherAppActivity extends AppCompatActivity {

    @Override
    @AddTrace(name = "onCreateOurOtherAppActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_our_other_app);
            PrefManager.ActivityCount =+1;
            CommonMethod.setAnalyticsData(OurOtherAppActivity.this, "MainTab", "OurOtherApp", null);

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), OurOtherAppActivity.this, getString(R.string.str_title_our_other_apps));
            }
//        //Log.d("TAG", "VERSION_APP " + BuildConfig.VERSION_CODE + " : " + BuildConfig.VERSION_NAME);
            CommonMethod.toDisplayToast(OurOtherAppActivity.this, BuildConfig.VERSION_CODE + " : " + BuildConfig.VERSION_NAME);
        } catch (Exception|Error e){
            e.printStackTrace();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                default:
                    return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
