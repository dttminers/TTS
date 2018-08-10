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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
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

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.util.Locale;
import java.util.Objects;

import in.tts.R;
import in.tts.model.AudioSetting;
import in.tts.utils.CommonMethod;
import in.tts.utils.KeyBoard;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class MakeYourOwnReadFragment extends Fragment {

    private EditText editText;
    private TextToSpeech t1;
    private TextView tvCopy, tvPaste, tvShare, tvSpeak;
    private ImageView ivCopy, ivPaste, ivShare, ivSpeak;
    private ClipboardManager myClipboard;

    // class member variable to save the X,Y coordinates
    private float[] lastTouchDownXY = new float[2];

    public MakeYourOwnReadFragment() {
        // Required empty public constructor
    }

    @Override

    @AddTrace(name = "onCreateMakeYourOurReadFragment", enabled = true)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_make_your_own_read, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.setAnalyticsData(Objects.requireNonNull(getContext()), "MainTab", "MakeYourRead", null);
            editText = Objects.requireNonNull(getActivity()).findViewById(R.id.edMakeRead);

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        KeyBoard.hideKeyboard(getActivity());
                    } else {
                        KeyBoard. openKeyboard(getActivity());
                    }
                }
            });


            // the purpose of the touch listener is just to store the touch X,Y coordinates
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    // save the X,Y coordinates
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        lastTouchDownXY[0] = event.getX();
                        lastTouchDownXY[1] = event.getY();
                    }

                    // let the touch event pass on to whoever needs it
                    return false;
                }
            });

            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KeyBoard.openKeyboard(getActivity());
                }
            });
            editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    popup();
                    return false;
                }
            });

//            if (android.os.Build.VERSION.SDK_INT < 11) {
                editText.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.clear();
                    }
                });
//            } else {
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
//            }
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
            LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getContext()).getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") View customView = inflater.inflate(R.layout.customise_clipboard, null);

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

            if (getActivity() != null) {
                myClipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
            }

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
                    if (editText != null) {
                        String text = editText.getText().toString().trim();
                        if (text.length() != 0) {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Clip", text);
                            CommonMethod.toDisplayToast(getApplicationContext(), "Text Copied");
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            }
                        } else {
                            CommonMethod.toDisplayToast(getApplicationContext(), "Nothing to Copy");
                        }
                    }
                }
            });

            ivPaste.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (myClipboard.hasPrimaryClip()) {
                        ClipData.Item item = myClipboard.getPrimaryClip().getItemAt(0);
                        String ptext = item.getText().toString();
                        if (editText != null) {
                            int cursorPosition = editText.getSelectionStart();
                            CharSequence enteredText = editText.getText().toString();
                            CharSequence cursorToEnd = enteredText.subSequence(cursorPosition, enteredText.length());
                            editText.setText(ptext);
                        }
                    }
                }
            });

            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvShare.setVisibility(View.VISIBLE);
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getContext().getString(R.string.app_name));
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, myClipboard.getPrimaryClip().getItemAt(0).getText().toString());
                    getContext().startActivity(Intent.createChooser(sharingIntent, "Share"));
                }
            });

            t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(AudioSetting.getAudioSetting(getContext()).getLangSelection() != null ? AudioSetting.getAudioSetting(getContext()).getLangSelection() : Locale.US);
                    }
                }
            });

            ivSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String toSpeak = editText.getText().toString();
                        CommonMethod.toDisplayToast(getContext(), toSpeak);
                        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        try {
            CommonMethod.toReleaseMemory();
            if (menu != null) {
                MenuItem search = menu.findItem(R.id.actionSearch);
                if (search != null) {
                    search.setVisible(false);
                }

                MenuItem speak = menu.findItem(R.id.actionSpeak);
                if (speak != null) {
                    speak.setVisible(true);
                    speak.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (editText.getText().toString().trim().length() > 0) {
                                if (t1 != null) {
                                    if (!t1.isSpeaking()) {
                                        t1.speak(editText.getText().toString().trim(), TextToSpeech.QUEUE_FLUSH, null);
                                    } else {
                                        t1.speak(editText.getText().toString().trim(), TextToSpeech.QUEUE_ADD, null);
                                    }
                                } else {
                                    t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                                        @Override
                                        public void onInit(int status) {
                                            if (status != TextToSpeech.ERROR) {
                                                t1.setLanguage(AudioSetting.getAudioSetting(getContext()).getLangSelection() != null ? AudioSetting.getAudioSetting(getContext()).getLangSelection() : Locale.US);
                                            }
                                        }
                                    });
                                    if (!t1.isSpeaking()) {
                                        t1.speak(editText.getText().toString().trim(), TextToSpeech.QUEUE_FLUSH, null);
                                    } else {
                                        t1.speak(editText.getText().toString().trim(), TextToSpeech.QUEUE_ADD, null);
                                    }
                                }
                            } else {
                                CommonMethod.toDisplayToast(getContext(), " No data to read");
                            }
                            return false;
                        }
                    });
                }
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
            CommonMethod.toReleaseMemory();
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
        CommonMethod.toReleaseMemory();
    }

    public void setLoadData() {
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

    }

    @Override
    public void onStart() {
        super.onStart();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        CommonMethod.toReleaseMemory();
    }

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        if (editText != null) {
            editText.setText("");
        }
        CommonMethod.toReleaseMemory();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        if (editText != null) {
            editText.setText("");
        }
        CommonMethod.toReleaseMemory();
    }
}