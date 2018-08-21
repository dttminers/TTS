package in.tts.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Locale;

import in.tts.R;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

@SuppressLint("StaticFieldLeak")
public class ClipBoard {
    private static TextView tvCopy, tvPaste, tvShare, tvSpeak;
    private static ImageView ivCopy, ivPaste, ivShare, ivSpeak;
    private static ClipboardManager myClipboard;
    private static ClipData myClip;
    private TextToSpeech t1;
    // class member variable to save the X,Y coordinates
    private static float[] lastTouchDownXY = new float[2];
//    private static String text;


    @SuppressLint("ClickableViewAccessibility")
    public static void ToPopup(final Context context, final Activity activity, final EditText editText) {
        try {
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


            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
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

            myClipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

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

//                    CharSequence text = editText.getText();//.subSequence(editText.getSelectionStart(), editText.getSelectionEnd());
//                    myClip = ClipData.newPlainText("text", text);
//                    myClipboard.setPrimaryClip(myClip);
//                    Toast.makeText(getContext(), "Text Copied : " + text, Toast.LENGTH_SHORT).show();
                        String text = editText.getText().toString().trim();
                        Log.d("TAG", " copy " + text + ":" + text.substring(editText.getSelectionStart(), editText.getSelectionEnd()));
                        if (text.length() != 0) {
                            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Clip", text);
                            Toast.makeText(getApplicationContext(), "Text Copied to Clipboard", Toast.LENGTH_SHORT).show();
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Nothing to Copy", Toast.LENGTH_SHORT).show();
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
//                        editText.setText(ptext);
                            int cursorPosition = editText.getSelectionStart();
                            CharSequence enteredText = editText.getText().toString();
                            CharSequence cursorToEnd = enteredText.subSequence(cursorPosition, enteredText.length());
                            editText.setText(ptext);
//                            Log.d("TAG paste", " paste " + editText.getText().toString() + ":" + ptext + ":" + cursorPosition + ":" + cursorToEnd + ":" + ptext.substring(cursorPosition, ptext.length()));
                        }
                    }
                }

            });

            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", " Speak " + editText.getText().toString());
                    tvShare.setVisibility(View.VISIBLE);
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, myClipboard.getPrimaryClip().getItemAt(0).getText().toString());
                    context.startActivity(Intent.createChooser(sharingIntent, "Share"));
                }
            });

            ivSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.d("TAG", " Speak " + editText.getText().toString());
                        TTS tts = new TTS(context);
                        tts.SpeakLoud(editText.getText().toString(),"AUD_Clip"+System.currentTimeMillis());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            popupWindow.setOutsideTouchable(true);
            customView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.NO_GRAVITY, (int) (lastTouchDownXY[0] - 10), ((int) lastTouchDownXY[1] + customView.getMeasuredHeight() + 170));
            //(int)event.getX(), (int)event.getY() - customView.getMeasuredHeight());

            activity.getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
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

//    t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//        @Override
//        public void onInit(int status) {
//            if(status != TextToSpeech.ERROR) {
//                t1.setLanguage(Locale.UK);
//            }
//        }
//    });

    public void onPause() {
        if (t1 != null) {
            t1.stop();
            t1.shutdown();
        }
    }
}
