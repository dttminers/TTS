package in.tts.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.tts.R;
import in.tts.activities.LoginActivity;
import in.tts.activities.MainActivity;

public class RegisterFragment extends Fragment {


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            getActivity().findViewById(R.id.txtAlreadyAccountReg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        startActivity(new Intent(getContext(), LoginActivity.class).putExtra("LOGIN", "login"));
                        getActivity().finish();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }
            });
            getActivity().findViewById(R.id.txtSkipRegisterReg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().finish();
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }
}
