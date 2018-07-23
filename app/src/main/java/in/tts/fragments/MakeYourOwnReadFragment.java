package in.tts.fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ActionMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.util.Locale;

import in.tts.R;
import in.tts.utils.CommonMethod;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class MakeYourOwnReadFragment extends Fragment {

    private EditText editText;
    private TextToSpeech t1;
    private TextView tvCopy, tvPaste, tvShare, tvSpeak;
    private ImageView ivCopy, ivPaste, ivShare, ivSpeak;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    int sdk = android.os.Build.VERSION.SDK_INT;

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

            // EditText.getSelectionStart()
            // https://stackoverflow.com/questions/3609174/android-insert-text-into-edittext-at-current-position

//            int start = Math.max(myEditText.getSelectionStart(), 0);
//            int end = Math.max(myEditText.getSelectionEnd(), 0);
//            myEditText.getText().replace(Math.min(start, end), Math.max(start, end),
//                    textToInsert, 0, textToInsert.length());
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

                        menu.clear();
                    }
                });
            } else {
                editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode mode) {
                    }

                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        return false;
                    }
                });
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
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

            tvCopy = customView.findViewById(R.id.tv_copy);
            tvPaste = customView.findViewById(R.id.tv_paste);
            tvShare = customView.findViewById(R.id.tv_share);
            tvSpeak = customView.findViewById(R.id.tv_speak);

            myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);

            ivCopy.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    tvCopy.setVisibility(View.VISIBLE);
                    tvPaste.setVisibility(View.GONE);
                    tvShare.setVisibility(View.GONE);
                    tvSpeak.setVisibility(View.GONE);
                    return false;
                }
            });

            ivPaste.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    tvCopy.setVisibility(View.GONE);
                    tvPaste.setVisibility(View.VISIBLE);
                    tvShare.setVisibility(View.GONE);
                    tvSpeak.setVisibility(View.GONE);
                    return false;
                }
            });

            ivShare.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    tvCopy.setVisibility(View.GONE);
                    tvPaste.setVisibility(View.GONE);
                    tvShare.setVisibility(View.VISIBLE);
                    tvSpeak.setVisibility(View.GONE);
                    return false;
                }
            });

            ivSpeak.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    tvCopy.setVisibility(View.GONE);
                    tvPaste.setVisibility(View.GONE);
                    tvShare.setVisibility(View.GONE);
                    tvSpeak.setVisibility(View.VISIBLE);
                    return false;
                }
            });

            ivCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    CharSequence text = editText.getText();//.subSequence(editText.getSelectionStart(), editText.getSelectionEnd());
//                    myClip = ClipData.newPlainText("text", text);
//                    myClipboard.setPrimaryClip(myClip);
//                    Toast.makeText(getContext(), "Text Copied : " + text, Toast.LENGTH_SHORT).show();

                    String CopyText = editText.getText().toString();
                    if (CopyText.length() != 0) {
                        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(CopyText);
                            Toast.makeText(getApplicationContext(), "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();

                        } else {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Clip", CopyText);
                            Toast.makeText(getApplicationContext(), "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
                            clipboard.setPrimaryClip(clip);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Nothing to Copy", Toast.LENGTH_SHORT).show();
                    }
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

//                    String pasteText;
//
//                    // TODO Auto-generated method stub
//                    if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB){
//                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//                        pasteText = clipboard.getText().toString();
//                        editText.setText(pasteText);
//
//                    }else{
//
//                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//                        if(clipboard.hasPrimaryClip()== true){
//                            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(1);
//                            pasteText = item.getText().toString();
//                            editText.setText(pasteText);
//                        }else{
//                            Toast.makeText(getApplicationContext(), "Nothing to Paste", Toast.LENGTH_SHORT).show();
//                        }
//                    }

            });

            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvShare.setVisibility(View.VISIBLE);
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
                    Log.d("Tag", " Speak : " + toSpeak);
                    Toast.makeText(getContext(), toSpeak, Toast.LENGTH_SHORT).show();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            });

            Log.d("TAG", " Touch " + lastTouchDownXY[0] + " : " + lastTouchDownXY[1] + " :" + customView.getMeasuredHeight() + " : " + (lastTouchDownXY[1] + customView.getMeasuredHeight()));
            popupWindow.setOutsideTouchable(true);
            customView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.NO_GRAVITY, (int) (lastTouchDownXY[0] - 10), ((int) lastTouchDownXY[1] + customView.getMeasuredHeight() + 170));
            //(int)event.getX(), (int)event.getY() - customView.getMeasuredHeight());

            getActivity().getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
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
        try {
            if (getActivity() != null) {
                ClipboardManager clipboardManager = (ClipboardManager)
                        getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                CharSequence selectedTxt = editText.getText().subSequence(editText.getSelectionStart(), editText.getSelectionEnd());
                ClipData clipData = ClipData.newPlainText("zoftino text view", selectedTxt);
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void pasteText() {
        try {
            if (getActivity() != null) {
                ClipboardManager clipboardManager = (ClipboardManager)
                        getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
                    ClipData.Item item = clipboardManager.getPrimaryClip().getItemAt(0);

                    CharSequence ptext = item.getText();
                    editText.setText(ptext);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        try {
            MenuItem item = menu.findItem(R.id.actionSearch);
            if (item != null) {
                item.setVisible(false);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private OnFragmentInteractionListener mListener;

    public static MakeYourOwnReadFragment newInstance() {
        return new MakeYourOwnReadFragment();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}