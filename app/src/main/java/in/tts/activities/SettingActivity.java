package in.tts.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.text.DecimalFormat;

import in.tts.R;
import in.tts.model.User;
import in.tts.utils.CommonMethod;

public class SettingActivity extends AppCompatActivity {

    private RelativeLayout rlLogout;
    private ProgressBar pbData, pbStorage;
    private TextView tvUsed, tvFree;

    @Override
    @AddTrace(name = "onCreateSettingActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_setting);
            CommonMethod.setAnalyticsData(SettingActivity.this, "MainTab", "Setting", null);

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), SettingActivity.this, getString(R.string.str_title_settings));
            } else {
                getSupportActionBar().setTitle(R.string.app_name);
            }

            rlLogout = findViewById(R.id.rlLogout);
            pbData = findViewById(R.id.pbSetting1);
            pbStorage = findViewById(R.id.pbSetting2);
            tvFree = findViewById(R.id.tvFree);
            tvUsed = findViewById(R.id.tvUsed);

            tvUsed.setVisibility(View.VISIBLE);

            final float totalSpace = DeviceMemory.getInternalStorageSpace();
            final float occupiedSpace = DeviceMemory.getInternalUsedSpace();
            final float freeSpace = DeviceMemory.getInternalFreeSpace();
            final DecimalFormat outputFormat = new DecimalFormat("#.##");

            Log.d("TAG","Storage occu"  +occupiedSpace);
            Log.d("TAG","Storage total" +totalSpace);

            if (null != pbStorage) {
                pbStorage.setMax((int) totalSpace);
                pbStorage.setProgress((int)occupiedSpace);
            }
            if (null != tvUsed) {
                tvUsed.setText(outputFormat.format(occupiedSpace) + " MB");
            }

            if (null != tvFree) {
                tvFree.setText(outputFormat.format(freeSpace) + " MB");
            }

            if (User.getUser(SettingActivity.this).getId() != null) {
                rlLogout.setVisibility(View.VISIBLE);
            } else {
                rlLogout.setVisibility(View.GONE);
            }

            rlLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        FirebaseAuth.getInstance().signOut();
                        doExit();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    }
                }
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public static class DeviceMemory {

        public static float getInternalStorageSpace() {
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            //StatFs statFs = new StatFs("/data");
            float total = ((float)statFs.getBlockCount() * statFs.getBlockSize()) / 1048576;
            return total;
        }

        public static float getInternalFreeSpace() {
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            //StatFs statFs = new StatFs("/data");
            float free  = ((float)statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
            return free;
        }

        public static float getInternalUsedSpace() {
            StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
            //StatFs statFs = new StatFs("/data");
            float total = ((float)statFs.getBlockCount() * statFs.getBlockSize()) / 1048576;
            float free  = ((float)statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
            float busy  = total - free;
            return busy;
        }
    }

    private void doExit() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setPositiveButton(getResources().getString(R.string.str_lbl_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alertDialog.setNegativeButton(getResources().getString(R.string.str_lbl_no), null);
            alertDialog.setMessage(getResources().getString(R.string.str_lbl_logout_from_app));
            alertDialog.setTitle(getResources().getString(R.string.app_name));
            alertDialog.show();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
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
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}