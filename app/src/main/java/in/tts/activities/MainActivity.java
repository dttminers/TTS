package in.tts.activities;

import android.content.Intent;
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

import in.tts.fragments.BrowserFragment;
import in.tts.fragments.DocumentsFragment;
import in.tts.fragments.GalleryFragment;
import in.tts.R;
import in.tts.fragments.LoginFragment;
import in.tts.fragments.RegisterFragment;
import in.tts.model.PrefManager;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else {
            getSupportActionBar().setTitle(R.string.app_name);
        }


        PrefManager prefManager = new PrefManager(this);
        Log.d("TAG", "prefManager " + prefManager.isFirstTimeLaunch());
        if (prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
        }

        tabLayout = findViewById(R.id.tabs);
        replacePage(new DocumentsFragment());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        replacePage(new BrowserFragment());
                        break;
                    case 1:
                        replacePage(new RegisterFragment());
                        break;
                    case 2:
                        replacePage(new LoginFragment());
                        break;
                    case 3:
                        replacePage(new GalleryFragment());
                        break;
                    default:
                        replacePage(new DocumentsFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
//                        showPopup(findViewById(R.id.actionSetting));
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

            case R.id.actionSetting:
                showPopup(findViewById(R.id.actionSetting));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void replacePage(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(fragment.getClass().getName())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .commit();
    }
}