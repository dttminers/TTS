package in.tts.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.activities.LoginActivity;
import in.tts.activities.MainActivity;
import in.tts.utils.CommonMethod;

public class LoginFragment extends Fragment {
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    @AddTrace(name = "onCreateLoginFragment", enabled = true)
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CommonMethod.setAnalyticsData(getContext(), "MainTab", "Login", null);
        try {
            getActivity().findViewById(R.id.llSignUp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(getContext(), LoginActivity.class).putExtra("LOGIN", "register"));
                        getActivity().finish();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }
                }
            });
            getActivity().findViewById(R.id.txtSkipLogin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}