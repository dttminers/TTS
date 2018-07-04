package in.tts.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class MakeYourOwnReadFragment extends Fragment {

    private EditText editText;

    public MakeYourOwnReadFragment() {
        // Required empty public constructor
    }

    @Override

    @AddTrace(name = "onCreateMakeYourOurReadFragment", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_your_own_read, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CommonMethod.setAnalyticsData(getContext(), "MainTab", "MakeYourRead", null);

        editText = getActivity().findViewById(R.id.edMakeRead);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                editText.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });
    }
}

