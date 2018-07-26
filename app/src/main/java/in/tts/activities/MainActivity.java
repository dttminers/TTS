package in.tts.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.perf.metrics.AddTrace;

import java.util.Arrays;

import in.tts.classes.ToSetMore;
import in.tts.fragments.BrowserFragment;
import in.tts.fragments.GalleryFragment;
import in.tts.R;
import in.tts.fragments.MainHomeFragment;
import in.tts.fragments.MakeYourOwnReadFragment;
import in.tts.fragments.PdfFragment;
import in.tts.utils.CommonMethod;

public class MainActivity extends AppCompatActivity implements
        BrowserFragment.OnFragmentInteractionListener,
        PdfFragment.OnFragmentInteractionListener,
        MainHomeFragment.OnFragmentInteractionListener,
        MakeYourOwnReadFragment.OnFragmentInteractionListener,
        GalleryFragment.OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private FirebaseAnalytics mFirebaseAnalytics;
    private MakeYourOwnReadFragment makeYourOwnReadFragment;

    @Override
    @AddTrace(name = "onCreateMainActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
//            com.pdftron.demo.utils.AppUtils.initializePDFNetApplication(getApplicationContext(), "YydfqYlnS6eoAcc4_UCuL55yUO-jl9CdAVHunX2E02O0F1G2mFUGh_n2K7iTmq4fp3GSqkKfpJsKD7nBEYSAMTIylBfFw0lwFDjzfvtAJMyL6ZLhXCKqtbWUVsW00-_p5bE7arUaZfVkc7-CraqEeAI29SZdrlxYnwHEoxZwJxdNRJ1EuBmxN9YR5m8NuQAkbGJdCfhP9aS8bkuEEp_mHT9rGnJDjiuV7WfhG5m2ZhfnFtPF2YvqHJJ0wDNq64KXsmhQWtmYC2pr7m1M61DxNcc7kV0FcNoj8MaefT0SD-MSL6QgZOulGusreWEWsmNXMB0vY7Er9Nr8nE1hXBZ74OFKCJyvb9T6EPselHVn7-6QgvUC6LbuLzaRfJXD4XqJ6V68tM9imjdDvwTJ0lqyTNR_56GM-vywobmbaXb4QHxtPYq1uJgBppTjDVupK3VN");
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            toSetTitle(getResources().getString(R.string.app_name));

//            PDFNet.initialize(getApplicationContext(), R.raw.pdfnet, "YydfqYlnS6eoAcc4_UCuL55yUO-jl9CdAVHunX2E02O0F1G2mFUGh_n2K7iTmq4fp3GSqkKfpJsKD7nBEYSAMTIylBfFw0lwFDjzfvtAJMyL6ZLhXCKqtbWUVsW00-_p5bE7arUaZfVkc7-CraqEeAI29SZdrlxYnwHEoxZwJxdNRJ1EuBmxN9YR5m8NuQAkbGJdCfhP9aS8bkuEEp_mHT9rGnJDjiuV7WfhG5m2ZhfnFtPF2YvqHJJ0wDNq64KXsmhQWtmYC2pr7m1M61DxNcc7kV0FcNoj8MaefT0SD-MSL6QgZOulGusreWEWsmNXMB0vY7Er9Nr8nE1hXBZ74OFKCJyvb9T6EPselHVn7-6QgvUC6LbuLzaRfJXD4XqJ6V68tM9imjdDvwTJ0lqyTNR_56GM-vywobmbaXb4QHxtPYq1uJgBppTjDVupK3VN");

            tabLayout = findViewById(R.id.tabs);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    CommonMethod.toCallLoader(MainActivity.this, "Please wait23000");
                    setCurrentViewPagerItem(tab.getPosition());
                    CommonMethod.toCloseLoader();
                    CommonMethod.setAnalyticsData(MainActivity.this, "MainTab", "Page " + tab.getPosition() + 1, null);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
            makeYourOwnReadFragment = new MakeYourOwnReadFragment();
            fn_permission();
//            CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
            if (getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT) == null) {
                setCurrentViewPagerItem(2);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("EXTRA_PROCESS_TEXT", getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString());
                makeYourOwnReadFragment.setArguments(bundle);
                setCurrentViewPagerItem(3);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
//            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
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
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", "Main onRequestPermissionsResult : " + requestCode + ":" + Arrays.toString(permissions) + ":" + Arrays.toString(grantResults));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ToSetMore.MenuOptions(MainActivity.this, item);
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
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    public void replacePage(Fragment fragment) {
        try {
            CommonMethod.toCloseLoader();
            CommonMethod.toCallLoader(this, "Please wait000");

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commitAllowingStateLoss();
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }


    public void onBackPressed() {
        try {
            if (tabLayout.getSelectedTabPosition() != 2) {
                setCurrentViewPagerItem(2);
            } else {
                doExit();
            }
//            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    public void setCurrentViewPagerItem(int i) {
        try {

            tabLayout.getTabAt(i).select();
//            CommonMethod.toCloseLoader();
            switch (i) {
                case 0:
                    toSetTitle(getString(R.string.str_title_browse_it));
                    replacePage(new BrowserFragment());
                    CommonMethod.toCloseLoader();
                    break;
                case 1:
                    toSetTitle(getString(R.string.str_title_docs));
                    replacePage(new PdfFragment());
                    CommonMethod.toCloseLoader();
                    break;
                case 2:
                    CommonMethod.toCloseLoader();
                    toSetTitle(getString(R.string.app_name));
                    replacePage(new MainHomeFragment());
                    break;
                case 3:
                    toSetTitle(getString(R.string.str_title_make_your_own_read));
                    replacePage(makeYourOwnReadFragment);
                    CommonMethod.toCloseLoader();
                    break;
                case 4:
                    toSetTitle(getString(R.string.str_title_images));
                    replacePage(new GalleryFragment());
                    CommonMethod.toCloseLoader();
                    break;
                default:
                    CommonMethod.toCloseLoader();
                    toSetTitle(getString(R.string.app_name));
                    tabLayout.getTabAt(2).select();
                    replacePage(new MainHomeFragment());
                    break;
            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
        CommonMethod.toCloseLoader();
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
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
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
        CommonMethod.toCloseLoader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonMethod.toReleaseMemory();
        CommonMethod.toCloseLoader();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        CommonMethod.toReleaseMemory();
        CommonMethod.toCloseLoader();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}