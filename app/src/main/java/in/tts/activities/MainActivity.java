package in.tts.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.fragments.BrowserFragment;
import in.tts.fragments.GalleryFragment;
import in.tts.R;
import in.tts.fragments.HomePageFragment;
import in.tts.fragments.LoginFragment;
import in.tts.fragments.MakeYourOwnReadFragment;
import in.tts.fragments.PdfFragment;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    @AddTrace(name = "onCreateMainActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            toSetTitle(getResources().getString(R.string.app_name));

            PrefManager prefManager = new PrefManager(this);
            if (prefManager.isFirstTimeLaunch()) {
                startActivity(new Intent(MainActivity.this, TutorialActivity.class));
            }

            tabLayout = findViewById(R.id.tabs);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    setCurrentViewPagerItem(tab.getPosition());
                    CommonMethod.setAnalyticsData(MainActivity.this, "MainTab", "HomePage", null);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            setCurrentViewPagerItem(2);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", " fb  main result " + resultCode + ":" + requestCode + " :");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionCamera:
                startActivity(new Intent(MainActivity.this, CameraOcrActivity.class));
                break;

            case R.id.actionSearch:
                break;

            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;

            case R.id.audio_settings:
                startActivity(new Intent(MainActivity.this, AudioSettingActivity.class));
                break;

            case R.id.recent_audios:
                startActivity(new Intent(MainActivity.this, RecentVoiceActivity.class));
                break;

            case R.id.our_other_apps:
                startActivity(new Intent(MainActivity.this, OurOtherAppActivity.class));
                break;

            case R.id.help:
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toSetTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
//            getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_left_pink_arrow));
            if (title != null) {
                if (title.length() > 0) {
                    getSupportActionBar().setTitle(firstLetterCaps(title));
                } else {
                    getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                }
            } else {
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
            }
        }
    }

    public static String firstLetterCaps(String myString) {
        return myString.substring(0, 1).toUpperCase() + myString.substring(1);
    }

    public void replacePage(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commitAllowingStateLoss();
    }


    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (tabLayout.getSelectedTabPosition() != 2) {
                setCurrentViewPagerItem(2);
            } else {
                doExit();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void setCurrentViewPagerItem(int i) {
        tabLayout.getTabAt(i).select();
        switch (i) {
            case 0:
                toSetTitle(getString(R.string.str_title_browse_it));
                replacePage(new BrowserFragment());
                break;
            case 1:
                toSetTitle(getString(R.string.str_title_docs));
                replacePage(new PdfFragment());
                break;
            case 2:
                toSetTitle(getString(R.string.app_name));
                replacePage(new HomePageFragment());
                break;
            case 3:
                toSetTitle(getString(R.string.str_title_make_your_own_read));
                replacePage(new MakeYourOwnReadFragment());
                break;
            case 4:
                toSetTitle(getString(R.string.str_title_images));
                replacePage(new GalleryFragment());
                break;
            default:
                toSetTitle(getString(R.string.app_name));
                tabLayout.getTabAt(2).select();
                replacePage(new HomePageFragment());
                break;
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
            alertDialog.setMessage(getResources().getString(R.string.str_lbl_exit_from_app));
            alertDialog.setTitle(getResources().getString(R.string.app_name));
            alertDialog.show();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }

    @Override
    protected void onStart() {
        super.onStart();
        CommonMethod.toReleaseMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethod.toReleaseMemory();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        CommonMethod.toReleaseMemory();
    }
}