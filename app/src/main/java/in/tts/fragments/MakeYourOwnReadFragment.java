package in.tts.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.perf.metrics.AddTrace;

import java.util.Locale;

import in.tts.R;
import in.tts.utils.CommonMethod;

import static android.content.Context.CLIPBOARD_SERVICE;

public class MakeYourOwnReadFragment extends Fragment {

    private EditText editText;
    private TextToSpeech t1;
    private ImageView ivCopy, ivPaste, ivShare, ivSpeak;
    private ClipboardManager myClipboard;
    private ClipData myClip;


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
        setHasOptionsMenu(true);

        CommonMethod.setAnalyticsData(getContext(), "MainTab", "MakeYourRead", null);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myView = inflater.inflate(R.layout.customise_clipboard, null);
        ;
        RelativeLayout relativeLayout = getActivity().findViewById(R.id.rlMakeRead);
        final FrameLayout frameLayout = getActivity().findViewById(R.id.flMakeRead);
        frameLayout.addView(myView);

        editText = getActivity().findViewById(R.id.edMakeRead);
        ivCopy = getActivity().findViewById(R.id.ivCopy);
        ivPaste = getActivity().findViewById(R.id.ivPaste);
        ivShare = getActivity().findViewById(R.id.ivShare);
        ivSpeak = getActivity().findViewById(R.id.ivSpeak);

        myClipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);

        ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = editText.getText().toString();

                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getContext(), "Text Copied",
                        Toast.LENGTH_SHORT).show();
            }
        });

        ivPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData text = myClipboard.getPrimaryClip();
                ClipData.Item item = text.getItemAt(0);

                String text1 = item.getText().toString();
                editText.setText(text1);

                Toast.makeText(getContext(), "Text Pasted",
                        Toast.LENGTH_SHORT).show();
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setBackgroundColor(getResources().getColor(R.color.transparent));
                frameLayout.setVisibility(View.VISIBLE);
            }
        });

        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        ivSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = editText.getText().toString();
                Toast.makeText(getContext(), toSpeak, Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.actionSearch);
        item.setVisible(false);
    }
}