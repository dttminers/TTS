package in.tts.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.net.Uri;
import android.widget.Toast;

import in.tts.fragments.BrowserFragment;
import in.tts.fragments.DocumentsFragment;
import in.tts.fragments.GalleryFragment;
import in.tts.R;
import in.tts.fragments.HomePageFragment;
import in.tts.fragments.LoginFragment;
import in.tts.fragments.MakeYourOwnReadFragment;
import in.tts.fragments.RegisterFragment;
import in.tts.model.PrefManager;
import in.tts.utils.FloatingViewService;
import in.tts.utils.FloatingWidgetService;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;

    /*  Permission request code to draw over other apps  */
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            toSetTitle(getResources().getString(R.string.app_name), true);

            PrefManager prefManager = new PrefManager(this);
            if (prefManager.isFirstTimeLaunch()) {
                startActivity(new Intent(MainActivity.this, TutorialActivity.class));
            }

            tabLayout = findViewById(R.id.tabs);
            replacePage(new DocumentsFragment());
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    setCurrentViewPagerItem(tab.getPosition());

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
        }
    }


    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.submenu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings:
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                        break;

                    case R.id.audio_settings:
                        startActivity(new Intent(MainActivity.this, AudioSettingActivity.class));
                        break;
                    case R.id.our_other_apps:
                        startActivity(new Intent(MainActivity.this, OurOtherAppActivity.class));
                        break;

                    case R.id.help:
                        startActivity(new Intent(MainActivity.this, HelpActivity.class));
                        break;
                    default:
                        break;

                }
                return true;
            }
        });
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
                break;

            case R.id.actionSearch:

                break;

//            case R.id.actionSetting:
//                showPopup(findViewById(R.id.actionSetting));
//                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toSetTitle(String title, boolean b) {
        if (!b) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_backword));
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
    }

    public static String firstLetterCaps(String myString) {

        return myString.substring(0, 1).toUpperCase() + myString.substring(1);
    }

    public void replacePage(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commit();
    }


    public void onBackPressed() {
        super.onBackPressed();
        try {
            Log.d("Tag", " g " + tabLayout.getSelectedTabPosition());
//            if (tabLayout != null) {
            if (tabLayout.getSelectedTabPosition() != 2) {
                setCurrentViewPagerItem(2);
            } else {
                doExit();
            }
//            } else if (tabLayout.getSelectedTabPosition() == 0) {
//                doExit();
//            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void setCurrentViewPagerItem(int i) {
        tabLayout.getTabAt(i).select();
        switch (i) {

            case 0:
                toSetTitle("Browser It", true);
                replacePage(new BrowserFragment());

                break;
            case 1:
                toSetTitle("Docs", true);
                replacePage(new DocumentsFragment());
                break;
            case 2:
                toSetTitle("Read It", true);
                replacePage(new HomePageFragment());
                break;
            case 3:
                toSetTitle("Make Your Own Read", true);
                replacePage(new MakeYourOwnReadFragment());
                break;
            case 4:
                toSetTitle("Images", true);
                replacePage(new GalleryFragment());
                break;
            default:
                toSetTitle("Read It", true);
                replacePage(new HomePageFragment());
                break;
        }
    }

    private void doExit() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setPositiveButton(getResources().getString(R.string.lbl_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alertDialog.setNegativeButton(getResources().getString(R.string.lbl_no), null);
            alertDialog.setMessage(getResources().getString(R.string.lbl_str_exit_from_app));
            alertDialog.setTitle(getResources().getString(R.string.app_name));
            alertDialog.show();
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack();
//        } else {
//            super.onBackPressed();
//        }
//
//    }
}