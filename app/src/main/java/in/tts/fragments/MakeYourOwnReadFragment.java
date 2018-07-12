package in.tts.fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import java.util.Locale;

import in.tts.R;
import in.tts.utils.CommonMethod;

import static android.content.ClipDescription.CREATOR;
import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MakeYourOwnReadFragment extends Fragment {

    private EditText editText;
    private TextToSpeech t1;
    private ImageView ivCopy, ivPaste, ivShare, ivSpeak;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    // class member variable to save the X,Y coordinates
    private float[] lastTouchDownXY = new float[2];


    public MakeYourOwnReadFragment() {
        // Required empty public constructor
    }

    @Override

    @AddTrace(name = "onCreateMakeYourOurReadFragment", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_make_your_own_read, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            setHasOptionsMenu(true);
            CommonMethod.setAnalyticsData(getContext(), "MainTab", "MakeYourRead", null);
            editText = getActivity().findViewById(R.id.edMakeRead);
            // the purpose of the touch listener is just to store the touch X,Y coordinates
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    // save the X,Y coordinates
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        lastTouchDownXY[0] = event.getX();
                        lastTouchDownXY[1] = event.getY();
                        Log.d("TAG", " points " + lastTouchDownXY[0] + lastTouchDownXY[1]);
                    }

                    // let the touch event pass on to whoever needs it
                    return false;
                }
            });
            editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    popup();
                    return false;
                }
            });

            if (android.os.Build.VERSION.SDK_INT < 11) {
                editText.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                                                    ContextMenu.ContextMenuInfo menuInfo) {
                        // TODO Auto-generated method stub
                        menu.clear();
                    }
                });
            } else {
                editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode mode) {
                        // TODO Auto-generated method stub

                    }

                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        // TODO Auto-generated method stub
                        return false;
                    }

                    public boolean onActionItemClicked(ActionMode mode,
                                                       MenuItem item) {
                        // TODO Auto-generated method stub
                        return false;
                    }
                });
            }


        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void popup() {

        try {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View customView = inflater.inflate(R.layout.customise_clipboard, null);

            final PopupWindow popupWindow = new PopupWindow(
                    customView,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            // Set an elevation value for popup window
            // Call requires API level 21
            if (Build.VERSION.SDK_INT >= 21) {
                popupWindow.setElevation(5.0f);
            }
            popupWindow.setFocusable(true);

            ivCopy = customView.findViewById(R.id.ivCopy);
            ivPaste = customView.findViewById(R.id.ivPaste);
            ivShare = customView.findViewById(R.id.ivShare);
            ivSpeak = customView.findViewById(R.id.ivSpeak);

            myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);

            ivCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence text = editText.getText();//.subSequence(editText.getSelectionStart(), editText.getSelectionEnd());
                    myClip = ClipData.newPlainText("text", text);
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(getContext(), "Text Copied : " + text, Toast.LENGTH_SHORT).show();
                }
            });

            ivPaste.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myClipboard.hasPrimaryClip()) {
                        ClipData.Item item = myClipboard.getPrimaryClip().getItemAt(0);
                        String ptext = item.getText().toString();
                        editText.setText(ptext);
                        Toast.makeText(getContext(), "Text Pasted : " + ptext, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getContext().getString(R.string.app_name));
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, myClipboard.getPrimaryClip().getItemAt(0).getText().toString());
                    Log.d("TAG", "SHARE : " + sharingIntent.getExtras());
                    getContext().startActivity(Intent.createChooser(sharingIntent, "Share"));
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

            Log.d("TAG", " Touch " + lastTouchDownXY[0] + " : " + lastTouchDownXY[1] + " :" + customView.getMeasuredHeight() + " : " + (lastTouchDownXY[1] - customView.getMeasuredHeight()));
            popupWindow.setOutsideTouchable(true);
            customView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.NO_GRAVITY, (int) lastTouchDownXY[0], (int) lastTouchDownXY[1] + customView.getMeasuredHeight());
            //(int)event.getX(), (int)event.getY() - customView.getMeasuredHeight());

            getActivity().getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }

        CommonMethod.toReleaseMemory();
        super.onPause();
    }

    private void copyText() {
        ClipboardManager clipboardManager = (ClipboardManager)
                getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        CharSequence selectedTxt = editText.getText().subSequence(editText.getSelectionStart(), editText.getSelectionEnd());
        ClipData clipData = ClipData.newPlainText("zoftino text view", selectedTxt);
        clipboardManager.setPrimaryClip(clipData);
    }

    private void pasteText() {
        ClipboardManager clipboardManager = (ClipboardManager)
                getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager.hasPrimaryClip()) {
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