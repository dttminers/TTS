package in.tts.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
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

        myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);

        ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence text =  editText.getText().subSequence(editText.getSelectionStart(), editText.getSelectionEnd());

                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getContext(), "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        ivPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ClipData text = myClipboard.getPrimaryClip();
//                ClipData.Item item = text.getItemAt(0);
//
//                String text1 = item.getText().toString();
//                editText.setText(text1);

                if(myClipboard.hasPrimaryClip()) {
                    ClipData.Item item = myClipboard.getPrimaryClip().getItemAt(0);
                    CharSequence ptext = item.getText();
                    editText.setText(ptext);
                }
                Toast.makeText(getContext(), "Text Pasted", Toast.LENGTH_SHORT).show();
            }
        });

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setBackgroundColor(getResources().getColor(R.color.transparent));
                frameLayout.setVisibility(View.VISIBLE);
            }
        });

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getContext().getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, myClip);
                getActivity().startActivity(Intent.createChooser(sharingIntent, "Share"));
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


    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        menu.removeItem(android.R.id.cut);

        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.clipboard, menu);
        return true;
    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }


    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copy:
                copyText();
                mode.finish();
                return true;
            case R.id.action_paste:
                pasteText();
                mode.finish();
                return true;
            default:
                return false;
        }

    }


    private void copyText() {
        ClipboardManager clipboardManager = (ClipboardManager)
                getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        CharSequence selectedTxt =  editText.getText().subSequence(editText.getSelectionStart(), editText.getSelectionEnd());
        ClipData clipData = ClipData.newPlainText("zoftino text view", selectedTxt);
        clipboardManager.setPrimaryClip(clipData);
    }

    private void pasteText() {
        ClipboardManager clipboardManager = (ClipboardManager)
                getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        if(clipboardManager.hasPrimaryClip()) {
            ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);

            CharSequence ptext = item.getText();
            editText.setText(ptext);
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.actionSearch);
        item.setVisible(false);
    }


}



