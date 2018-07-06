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
import in.tts.fragments.LoginFragment;
import in.tts.fragments.RegisterFragment;
import in.tts.utils.CommonMethod;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout relativeLayoutFb;
    private LoginButton loginButton;

    @Override
    @AddTrace(name = "onCreateLoginActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login", null);

//        relativeLayoutFb = findViewById(R.id.rlFacebookLogin);
//        loginButton = findViewById(R.id.login_button);

        try {
            String Status = getIntent().getStringExtra("LOGIN");
            if (Status == null) {
                replaceMainTabsFragment(new LoginFragment());
            } else if (Status.toLowerCase().equals("login")) {
                replaceMainTabsFragment(new LoginFragment());
            } else if (Status.toLowerCase().equals("register")) {
                replaceMainTabsFragment(new RegisterFragment());
            } else {
                replaceMainTabsFragment(new LoginFragment());
            }
        } catch (Exception | Error e2) {
            e2.printStackTrace();
            Crashlytics.logException(e2);
        }
    }

//    public void onClick(View v) {
//        if (v == relativeLayoutFb) {
//            loginButton.performClick();
//        }
//    }


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