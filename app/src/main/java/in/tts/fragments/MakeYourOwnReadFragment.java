package in.tts.fragments;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.perf.metrics.AddTrace;

import java.util.Locale;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class MakeYourOwnReadFragment extends Fragment {

    private EditText editText;
    private TextToSpeech t1;
    private Button b1;

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
        b1=getActivity().findViewById(R.id.button);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
        });


        t1=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        final ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener( new ClipboardManager.OnPrimaryClipChangedListener() {
            public void onPrimaryClipChanged() {
                String toSpeak = clipboard.getText().toString();
//                String toSpeak = editText.getText().toString();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

            }
        });
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }
}



