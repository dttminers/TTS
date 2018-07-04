package in.tts.activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
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
            getSupportActionBar().show();
            getSupportActionBar().setTitle("Help");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_backword));
        } else {
            getSupportActionBar().setTitle(R.string.app_name);
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
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
