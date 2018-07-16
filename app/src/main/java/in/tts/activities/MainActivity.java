package in.tts.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;

import in.tts.fragments.BrowserFragment;
import in.tts.fragments.GalleryFragment;
import in.tts.R;
import in.tts.fragments.MainHomeFragment;
import in.tts.fragments.MakeYourOwnReadFragment;
import in.tts.fragments.PdfFragment;
import in.tts.model.AppData;
import in.tts.utils.AppPermissions;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetImages;
import in.tts.utils.ToGetPdfFiles;

public class MainActivity extends AppCompatActivity implements
        BrowserFragment.OnFragmentInteractionListener,
        PdfFragment.OnFragmentInteractionListener,
        MainHomeFragment.OnFragmentInteractionListener,
        MakeYourOwnReadFragment.OnFragmentInteractionListener,
        GalleryFragment.OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    @AddTrace(name = "onCreateMainActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            CommonMethod.toCloseLoader();
            setContentView(R.layout.activity_main);
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            toSetTitle(getResources().getString(R.string.app_name));

            tabLayout = findViewById(R.id.tabs);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    setCurrentViewPagerItem(tab.getPosition());
                    CommonMethod.setAnalyticsData(MainActivity.this, "MainTab", "Page " + tab.getPosition() + 1, null);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });


//            if (
                    AppPermissions.toAsk(MainActivity.this, MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
//                    )
//            {
//                ToGetPdfFiles.getfile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
//                ToGetImages.getAllShownImagesPath(MainActivity.this);
//
//            }
//            AppPermissions.toAsk(MainActivity.this, MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            AppPermissions.toAsk(MainActivity.this, MainActivity.this, Manifest.permission.CAMERA);
            setCurrentViewPagerItem(0);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Log.d("TAG", " Main onActivityResult " + resultCode + ":" + requestCode + " :");
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentrepalce);
            fragment.onActivityResult(requestCode, resultCode, data);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", "Main onRequestPermissionsResult : " + requestCode + ":" + permissions + ":" + grantResults);
//        setCurrentViewPagerItem(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
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
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return super.onOptionsItemSelected(item);
    }

    private void toSetTitle(String title) {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);

                if (title != null) {
                    if (title.length() > 0) {
                        getSupportActionBar().setTitle(CommonMethod.firstLetterCaps(title));
                    } else {
                        getSupportActionBar().setTitle(CommonMethod.firstLetterCaps(getResources().getString(R.string.app_name)));
                    }
                } else {
                    getSupportActionBar().setTitle(CommonMethod.firstLetterCaps(getResources().getString(R.string.app_name)));
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void replacePage(Fragment fragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitAllowingStateLoss();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }


    public void onBackPressed() {
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

    public void setCurrentViewPagerItem(int i) {
        try {
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
                    replacePage(new MainHomeFragment());
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
                    replacePage(new MainHomeFragment());
                    break;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
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
        setCurrentViewPagerItem(2);
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

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}