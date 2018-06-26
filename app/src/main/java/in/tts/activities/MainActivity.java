package in.tts.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import in.tts.BrowserFragment;
import in.tts.CameraFragment;
import in.tts.DocumentsFragment;
import in.tts.GalleryFragment;
import in.tts.R;


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
        }else{
            getSupportActionBar().setTitle(R.string.app_name);
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
                        replacePage(new DocumentsFragment());
                        break;
                    case 2:
                        replacePage(new CameraFragment());
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

