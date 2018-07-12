package in.tts.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.fragments.BrowserFragment;
import in.tts.fragments.GalleryFragment;
import in.tts.fragments.HomePageFragment;
import in.tts.fragments.LoginFragment;
import in.tts.fragments.MakeYourOwnReadFragment;
import in.tts.fragments.PdfFragment;
import in.tts.fragments.RegisterFragment;
import in.tts.utils.CommonMethod;

public class LoginActivity extends AppCompatActivity {

    @Override
    @AddTrace(name = "onCreateLoginActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            String Status = getIntent().getStringExtra("LOGIN");
            if (Status == null) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login1", null);
                replaceMainTabsFragment(new LoginFragment());
            } else if (Status.toLowerCase().equals("login")) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login2", null);
                replaceMainTabsFragment(new LoginFragment());
            } else if (Status.toLowerCase().equals("login1")) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login2", null);
                replaceMainTabsFragment(new HomePageFragment());
            } else if (Status.toLowerCase().equals("login2")) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login2", null);
                replaceMainTabsFragment(new PdfFragment());
            } else if (Status.toLowerCase().equals("login3")) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login2", null);
                replaceMainTabsFragment(new BrowserFragment());
            } else if (Status.toLowerCase().equals("login4")) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login2", null);
                replaceMainTabsFragment(new MakeYourOwnReadFragment());
            } else if (Status.toLowerCase().equals("login5")) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login2", null);
                replaceMainTabsFragment(new GalleryFragment());
            } else if (Status.toLowerCase().equals("register")) {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Register", null);
                replaceMainTabsFragment(new RegisterFragment());
            } else {
                CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login3", null);
                replaceMainTabsFragment(new LoginFragment());
            }
        } catch (Exception | Error e2) {
            e2.printStackTrace();
            Crashlytics.logException(e2);
        }
    }

    private void replaceMainTabsFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentrepalce, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", " fb login result " + resultCode + ":" + requestCode + " :");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentrepalce);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}