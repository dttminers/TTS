package in.tts.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics; import com.flurry.android.FlurryAgent; import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class HelpActivity extends AppCompatActivity {

    @Override
    @AddTrace(name = "onCreateHelpActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        CommonMethod.setAnalyticsData(HelpActivity.this, "MainTab", "Help", null);

        if (getSupportActionBar() != null) {
            CommonMethod.toSetTitle(getSupportActionBar(), HelpActivity.this, getString(R.string.str_title_help));
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
