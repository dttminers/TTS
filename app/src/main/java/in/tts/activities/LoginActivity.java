package in.tts.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.fragments.LoginFragment;
import in.tts.fragments.RegisterFragment;
import in.tts.utils.CommonMethod;

public class LoginActivity extends AppCompatActivity {

    @Override
    @AddTrace(name = "onCreateLoginActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CommonMethod.setAnalyticsData(LoginActivity.this, "MainTab", "Login", null);

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

    private void replaceMainTabsFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentrepalce, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }
}