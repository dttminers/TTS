package in.tts.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
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
import in.tts.fragments.HomePageFragment;
import in.tts.fragments.MakeYourOwnReadFragment;
import in.tts.fragments.PdfFragment;
import in.tts.fragments.TutorialFragment;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetImages;
import in.tts.utils.ToGetPdfFiles;

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

//            PrefManager prefManager = new PrefManager(this);
//            if (prefManager.isFirstTimeLaunch()) {
//                startActivity(new Intent(MainActivity.this, TutorialPageActivity.class));
//            }

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

            setCurrentViewPagerItem(2);

            fn_permission();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void fn_permission() {
        try {
            Log.d("TAG", " pdf permission ");
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
//                    Log.d("TAG", "Home0131 ");
//                } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
                Log.d("TAG", "Home0231 ");
//                }
            } else {
                Log.d("TAG", "Home0331 ");
//                toGetPDF();
                ToGetPdfFiles.getfile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
                ToGetImages.getAllShownImagesPath(MainActivity.this);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", " Main onActivityResult " + resultCode + ":" + requestCode + " :");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentrepalce);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", "Home013 " + requestCode + ":" + permissions + ":" + grantResults);
        setCurrentViewPagerItem(2);
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentrepalce);
//        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.d("TAG", "Home031 ");
//            } else {
//                Log.d("TAG", "Home021 ");
//                Toast.makeText(MainActivity.this, "Please allow the permission", Toast.LENGTH_LONG).show();
//            }
//        } else {
//            Log.d("TAG", "Home011 ");
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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