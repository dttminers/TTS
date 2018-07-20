package in.tts.activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics; import com.flurry.android.FlurryAgent; import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.adapters.RecentVoiceAdapter;
import in.tts.model.AudioRecent;
import in.tts.utils.CommonMethod;

public class RecentVoiceActivity extends AppCompatActivity {

    private List<AudioRecent> AudioListData ;
    @Override
    @AddTrace(name = "onCreateAudioSettingActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_voice);
        CommonMethod.setAnalyticsData(RecentVoiceActivity.this, "MainTab", "Recent Voice", null);

        if (getSupportActionBar() != null) {
            CommonMethod.toSetTitle(getSupportActionBar(), RecentVoiceActivity.this, getString(R.string.str_title_recent_voice));
        }


        AudioListData = new ArrayList<>();
        AudioListData.add(new AudioRecent("AUD3458", "1.08 MB"));
        AudioListData.add(new AudioRecent("AUD7858", "2.25 MB"));
        AudioListData.add(new AudioRecent("AUD6958", "1.25 MB"));
        AudioListData.add(new AudioRecent("AUD8558", "2.25 MB"));
        AudioListData.add(new AudioRecent("AUD3458", "2.55 MB"));
        AudioListData.add(new AudioRecent("AUD5800", "6.25 MB"));
        AudioListData.add(new AudioRecent("AUD6558", "2.25 MB"));
        AudioListData.add(new AudioRecent("AUD2558", "8.25 MB"));


        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycleView);
        RecentVoiceAdapter mAdapter = new RecentVoiceAdapter(RecentVoiceActivity.this, AudioListData);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
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
