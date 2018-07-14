package in.tts.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;

import in.tts.R;
import in.tts.model.AppData;
import in.tts.model.PrefManager;
import in.tts.model.User;
import in.tts.utils.ToGetImages;
import in.tts.utils.ToGetPdfFiles;

public class TutorialPageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TextView skip;
    private int[] layouts;
    private PrefManager prefManager;


    @Override
    @AddTrace(name = "onCreateTutorialPageActivityActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_tutorial_page);

            // Checking for first time launch - before calling setContentView()
            prefManager = new PrefManager(this);
            if (!prefManager.isFirstTimeLaunch()) {
                launchHomeScreen();
                finish();
            }

            // Making notification bar transparent
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }


            viewPager = findViewById(R.id.view_pager);
            skip = findViewById(R.id.skip);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchHomeScreen();
                }
            });

            // layouts of all welcome sliders
            // add few more layouts if you want
            layouts = new int[]{
                    R.layout.tutorial_1,
                    R.layout.tutorial_2,
                    R.layout.tutorial_3,
                    R.layout.tutorial_5,
                    R.layout.tutorial_4
            };
            // making notification bar transparent
            changeStatusBarColor();

            viewPager.setAdapter(new MyViewPagerAdapter());
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

//            fn_permission();

        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        prefManager.getUserInfo();
        if (User.getUser(TutorialPageActivity.this).getId() != null) {
            startActivity(new Intent(TutorialPageActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(TutorialPageActivity.this, LoginActivity.class).putExtra("LOGIN", "login"));
        }
        finish();

    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
//            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
//                 last page. make button text to GOT IT
//                btnNext.setText(getString(R.string.start));
//                btnSkip.setVisibility(View.GONE);
                skip.setVisibility(View.GONE);
            } else {
                // still pages are left
//                btnNext.setText(getString(R.string.next));
//                btnSkip.setVisibility(View.VISIBLE);
                skip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void toChangeScreen(View view) {
        // checking for last page
        // if last page home screen will be launched
        int current = getItem(+1);
        if (current < layouts.length) {
            // move to next screen
            viewPager.setCurrentItem(current);
        } else {
            launchHomeScreen();
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private void fn_permission() {
        try {
            Log.d("TAG", " pdf permission ");
            if ((ContextCompat.checkSelfPermission(TutorialPageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(TutorialPageActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                Log.d("TAG", "Home0231 ");
            } else {
                Log.d("TAG", "count Home0331 ");
                if (AppData.fileList == null) {
//                    if (AppData.fileList.size() == 0) {
                    ToGetPdfFiles.getfile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
//                    }
                }
                if (AppData.fileName == null) {
                    ToGetImages.getAllShownImagesPath(TutorialPageActivity.this);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
