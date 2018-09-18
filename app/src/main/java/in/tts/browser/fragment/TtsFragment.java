package in.tts.browser.fragment;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import in.tts.R;
import in.tts.activities.HomeActivity;
import in.tts.model.AudioSetting;
import in.tts.model.PrefManager;
import in.tts.services.AudioPlayerBroadcastReceiver;
import in.tts.utils.CommonMethod;

@SuppressLint("RestrictedApi")
public class TtsFragment extends Fragment {

    public static final String TAG = TtsFragment.class.getSimpleName();

    private RelativeLayout rlParent, rlChild1, rlChild11, rlChild21, rlChild22, rlRate, rlPitch;
    private ImageView ivSetting, ivPrevious, ivPlayPause, ivNext, ivClose;
    private TextView tvLblRate, tvLblPitch, tvLblLanguage, tvLblVoice;
    private SeekBar sbRate, sbPitch;
    private ProgressBar pbLanguage, pbVoice;
    private ListView listView;
    //    private FloatingActionButton fabBack;
    private ImageView fabBack;

    private TextToSpeech tts;
    private AudioSetting audioSetting;

    private ArrayList<String> voiceNameList = new ArrayList<>(), localeNameList = new ArrayList<>();
    private ArrayList<Locale> localeList;
    private ArrayList<Voice> voiceList;

    private int count = 0, sub_count = 0, status = 1, resume = 0;
    private String[] a, text;
    private boolean isPaused = false, isVoiceList = false;

    private OnFragmentInteractionListener mListener;

    public TtsFragment() {
    }

    public static TtsFragment newInstance() {
        return new TtsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            if (getArguments() != null) {
                toBindViews();
                if (getArguments().getString("Speak") != null) {
                    if (tts != null) {
                        if (tts.isSpeaking()) {
                            tts.stop();
                        }
                        tts.shutdown();
                    }
                    String text = getArguments().getString("Speak");
                    Log.d("Tts_TAG", "toSpeak : " + text.length() + " : \n +++ " + text + "\n +++");
                    Log.d("Tts_TAG", "Count 1 : " + text.split("\\.|\\?|\\!").length + " : " + count);
                    a = text.split("\\.|\\?|\\!");
                    Log.d("Tts_TAG", "Count 2 : " + a.length + " : " + count);
                    toSpeak(count, sub_count);

                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

//    private void toSetText(String text) {
//        try {
//
//            toTtsSpeak(text);
//                            if (text.length() >= 3990) {
//
//                                int textLength = text.length();
//                                count = textLength / 3990 + ((textLength % 3990 == 0) ? 0 : 1);
//                                int start = 0;
//                                int end = text.indexOf(" ", 3990);
//
//                                for (int i = 1; i <= count; i++) {
//                                    texts.add(text.substring(start, end));
//                                    start = end;
//                                    if ((start + 3990) < textLength) {
//                                        end = text.indexOf(" ", start + 3990);
//                                    } else {
//                                        end = textLength;
//                                    }
//                                }
//
//                                for (start = 0; start < texts.size(); start++) {
//                                    CommonMethod.toCloseLoader();
//                                    tts.speak(texts.get(start), tts.isSpeaking() ? TextToSpeech.QUEUE_ADD : TextToSpeech.QUEUE_FLUSH, null, text);
//                                }
//                            } else {
//                                tts.speak(text, tts.isSpeaking() ? TextToSpeech.QUEUE_ADD : TextToSpeech.QUEUE_FLUSH, null, text);
//                                CommonMethod.toCloseLoader();
//                            }

//                            String str = "This is how I tried to split a paragraph into a sentence. But, there is a problem. My paragraph includes dates like Jan.13, 2014 , words like U.S and numbers like 2.2. They all got split by the above code.";
//
//                            Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
//                            Matcher reMatcher = re.matcher(str);
//                            while (reMatcher.find()) {
//                                System.out.println(reMatcher.group());
//                            }
//                            String[] sentenceHolder = titleAndBodyContainer.split("(?i)(?<=[.?!])\\S+(?=[a-z])");
//
//        } catch (Exception | Error e) {
//            e.printStackTrace();
//        }
//    }

    private void toSpeak(final int _count, final int _sub_count) {
        try {

            tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    try {
//                        Log.d("TTS_TAG ", " ini " + audioSetting.getSelectedPitch() + ":" + audioSetting.getSelectedRate());
                        if (status != TextToSpeech.SUCCESS) {
                            CommonMethod.toDisplayToast(getContext(), "Unable to read");
                        } else {
                            audioSetting = AudioSetting.getAudioSetting(getContext());
                            if (audioSetting.getSelectedVoice() != null) {
                                tts.setVoice(audioSetting.getSelectedVoice());
                            }

                            if (audioSetting.getSelectedLanguage() != null) {
                                tts.setLanguage(audioSetting.getSelectedLanguage());
                            }

                            if (audioSetting.getSelectedPitch() != -1) {
                                tts.setPitch(audioSetting.getSelectedPitch());
                            }

                            if (audioSetting.getSelectedRate() != -1) {
                                tts.setSpeechRate(audioSetting.getSelectedRate());
                            }

                            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                @Override
                                public void onStart(String s) {
                                    try {
                                        Log.d("Tts_TAG", "onStart : " + s + ":" + s.length() + ":" + count + ":" + sub_count + ":" + text.length);
                                    } catch (Exception | Error e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onDone(String s) {
                                    try {
                                        Log.d("Tts_TAG", "onDone : " + s + ":" + s.length() + ":" + count + ":" + sub_count + ":" + text.length);
                                        if (text.length > sub_count) {
                                            count = count + 1;
                                            if (count < a.length) {
                                                toTtsSpeak(a[count], sub_count);
                                            } else {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ivPlayPause.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                                                    }
                                                });
                                            }
                                        } else {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ivPlayPause.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                                                }
                                            });
                                            cancelNotification(getContext());
                                        }
                                    } catch (Exception | Error e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(String s) {
                                    try {
                                        Log.d("Tts_TAG", "onError : " + s + ":" + s.length() + ":" + count + ":" + sub_count + ":" + text.length);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (ivPlayPause != null) {
                                                    ivPlayPause.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                                                }
                                            }
                                        });

                                    } catch (Exception | Error e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            Log.d("Tts_TAG", "Count 0 :" + count);
                            toTtsSpeak(a[_count], _sub_count);
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }

            }, "com.google.android.tts");
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toTtsSpeak(String str, int _sub_count) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ivPlayPause.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_pause_grey_24dp));
                }
            });
            text = str.split("\\s+");
            Log.d("Tts_TAG", "Count 01 :" + count + ":" + str + ":" + text.length + ":" + sub_count + ":" + _sub_count);
            for (int i = _sub_count; i < text.length; i++) {
                Log.d("Tts_TAG", "ttsSpeak " + i + " : " + text[i] + ":" + sub_count + " : " + count + ":" + _sub_count);
                sub_count = i;
                tts.speak(text[i], tts.isSpeaking() ? TextToSpeech.QUEUE_ADD : TextToSpeech.QUEUE_FLUSH, null, text[i]);
            }
            setNotification();
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public void setNotification() {
        try {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(ns);

            @SuppressWarnings("deprecation")
            Notification notification = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());

            RemoteViews notificationView = new RemoteViews(getActivity().getPackageName(), R.layout.notification_mediacontroller);

            //the intent that is started when the notification is clicked (works)
            Intent notificationIntent = new Intent(getContext(), HomeActivity.class);
            Log.d("Tts_TAG", " pendingNotificationIntent ");
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent, 0);

            notification.contentView = notificationView;
            notification.contentIntent = pendingNotificationIntent;
            notification.flags |= Notification.FLAG_NO_CLEAR;

            //this is the intent that is supposed to be called when the button is clicked
            Intent switchIntent = new Intent(getContext(), AudioPlayerBroadcastReceiver.class);
            switchIntent.setAction("PlayPause");
            Log.d("Tts_TAG", " pendingSwitchIntent ");
            PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(getContext(), 0, switchIntent, 0);


            //this is the intent that is supposed to be called when the button is clicked
            Intent closeIntent = new Intent(getContext(), AudioPlayerBroadcastReceiver.class);
            Log.d("Tts_TAG", "  closeSwitchIntent ");
            switchIntent.setAction("Close");
            PendingIntent closeSwitchIntent = PendingIntent.getBroadcast(getContext(), 0, closeIntent, 0);

            //this is the intent that is supposed to be called when the button is clicked
            Intent homeIntent = new Intent(getContext(), HomeActivity.class);
            Log.d("Tts_TAG", "  closeSwitchIntent ");
//            switchIntent.setAction("Close");
            PendingIntent homeSwitchIntent = PendingIntent.getBroadcast(getContext(), 0, homeIntent, 0);

            notificationView.setTextViewText(R.id.tvTtsName, "Reading Web Page");

            notificationView.setOnClickPendingIntent(R.id.ivTtsPlayPause, pendingSwitchIntent);

            notificationView.setOnClickPendingIntent(R.id.ivTtsClose, closeSwitchIntent);

            notificationView.setOnClickPendingIntent(R.id.ivAppIcon, homeSwitchIntent);

            notificationManager.notify(1, notification);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }


    public void cancelNotification(Context ctx) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ivPlayPause != null) {
                        ivPlayPause.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_play_arrow_grey_24dp));
                    }
                }
            });
            Log.d("Tts_TAG", " Cancel : ");
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
            if (nMgr != null) {
                nMgr.cancel(1);
            }
            if (tts != null) {
                if (tts.isSpeaking()) {
                    tts.stop();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void toBindViews() {
        try {
            rlParent = Objects.requireNonNull(Objects.requireNonNull(getActivity())).findViewById(R.id.rl_main_tts_fragment);
            rlChild1 = Objects.requireNonNull(getActivity()).findViewById(R.id.rl_tts_main_player);
            rlChild11 = Objects.requireNonNull(getActivity()).findViewById(R.id.rl_tts_player);
            rlChild21 = Objects.requireNonNull(getActivity()).findViewById(R.id.rl_tts_overflow_menu1);
            rlChild22 = Objects.requireNonNull(getActivity()).findViewById(R.id.rl_tts_overflow_content_parent);
            rlRate = Objects.requireNonNull(getActivity()).findViewById(R.id.rl_tts_rate_parent);
            rlPitch = Objects.requireNonNull(getActivity()).findViewById(R.id.rl_tts_pitch_parent);

            ivSetting = Objects.requireNonNull(getActivity()).findViewById(R.id.iv_tts_settings);
            ivPrevious = Objects.requireNonNull(getActivity()).findViewById(R.id.iv_tts_prev);
            ivPlayPause = Objects.requireNonNull(getActivity()).findViewById(R.id.iv_tts_toggle);
            ivNext = Objects.requireNonNull(getActivity()).findViewById(R.id.iv_tts_next);
            ivClose = Objects.requireNonNull(getActivity()).findViewById(R.id.iv_tts_close);

            tvLblRate = Objects.requireNonNull(getActivity()).findViewById(R.id.tv_tts_rate_title);
            tvLblPitch = Objects.requireNonNull(getActivity()).findViewById(R.id.tv_tts_pitch_title);
            tvLblLanguage = Objects.requireNonNull(getActivity()).findViewById(R.id.tv_tts_option_language);
            tvLblVoice = Objects.requireNonNull(getActivity()).findViewById(R.id.tv_tts_option_voices);

            sbRate = Objects.requireNonNull(getActivity()).findViewById(R.id.sb_tts_rate);
            sbPitch = Objects.requireNonNull(getActivity()).findViewById(R.id.sb_tts_pitch);

            pbLanguage = Objects.requireNonNull(getActivity()).findViewById(R.id.pb_tts_option_language);
            pbVoice = Objects.requireNonNull(getActivity()).findViewById(R.id.pb_tts_option_voices);

            listView = Objects.requireNonNull(getActivity()).findViewById(R.id.lv_tts_values_list);

            fabBack = Objects.requireNonNull(getActivity()).findViewById(R.id.fab_back);



            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.d("Tts_TAG", " onItemClick : " + i + " : " + l + " : " + isVoiceList + " : ");
                    if (!isVoiceList) {
                        tts.setLanguage(localeList.get(i));
                        audioSetting = AudioSetting.getAudioSetting(getContext());
                        audioSetting.setSelectedLanguage(localeList.get(i));
                        toSpeak(count, sub_count);
                    } else {
                        tts.setVoice(voiceList.get(i));
                        audioSetting = AudioSetting.getAudioSetting(getContext());
                        audioSetting.setSelectedVoice(voiceList.get(i));
                        toSpeak(count, sub_count);
                    }

                    new PrefManager(getContext()).setAudioSetting();
                    if (status == 1) {
                        player1();
                    } else if (status == 2) {
                        player2();
                    } else {
                        player1();
                    }
                }
            });

            fabBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (status == 1) {
                        player1();
                    } else if (status == 2) {
                        player2();
                    } else {
                        player1();
                    }
                }
            });

            ivSetting.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    player2();
                }
            });

            tvLblLanguage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        localeList = new ArrayList<>(tts.getAvailableLanguages());
                        Log.d("Tts_TAG", "localeList : " + localeList);

                        for (int i = 0; i < localeList.size(); i++) {
//                            Log.d("Tts_TAG", "localeList " + i + ": " + localeList.get(i).getDisplayName()
//                                    + " : " + localeList.get(i).getCountry()
//                                    + " : " + localeList.get(i).getDisplayLanguage()
//                                    + " : " + localeList.get(i).getDisplayScript()
//                                    + " : " + localeList.get(i).getLanguage()
//                                    + " : " + localeList.get(i).getDisplayVariant()
//                                    + " : " + localeList.get(i).getISO3Country()
//                                    + " : " + localeList.get(i).getISO3Language()
//                                    + " : " + localeList.get(i).getScript()
//                                    + " : " + localeList.get(i).toLanguageTag()
//                                    + " : " + localeList.get(i).getExtensionKeys()
//                            );
                            localeNameList.add(localeList.get(i).getDisplayLanguage());
                        }
                        isVoiceList = false;
                        listView.setAdapter(new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, localeNameList));
                        player3();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }
            });

            tvLblVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        voiceList = new ArrayList<>(tts.getVoices());
                        Log.d("Tts_TAG", "voiceList : " + voiceList);
                        for (int i = 0; i < voiceList.size(); i++) {
//                            Log.d("Tts_TAG", "voiceList " + i
//                                    + " : " + voiceList.get(i).getName()
//                                    + " : " + voiceList.get(i).getLatency()
//                                    + " : " + voiceList.get(i).getFeatures()
//                                    + " : " + voiceList.get(i).getLocale()
//                                    + " : " + voiceList.get(i).getLocale().getLanguage()
//                                    + " : " + voiceList.get(i).getLocale().getDisplayLanguage()
//                            );
                            voiceNameList.add(voiceList.get(i).getLocale().getDisplayLanguage() + " ( " + voiceList.get(i).getName() + " ) ");
                        }
                        isVoiceList = true;
                        listView.setAdapter(new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1, voiceNameList));
                        player3();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }
            });

            ivPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.d("Tts_TAG", " ivPrevious " + count + a.length);
                        count = count - 2;
                        if (count > 0) {
                            toTtsSpeak(a[count], sub_count);
                            CommonMethod.toDisplayToast(getContext(), "You have jumped forward");
                        } else {
                            if (a != null) {
                                CommonMethod.toDisplayToast(getContext(), "You have jumped to start");
                                count = 0;
                                toTtsSpeak(a[count], sub_count);
                            } else {
                                count = count + 2;
                                CommonMethod.toDisplayToast(getContext(), "You have jumped to start ..... ");
                            }
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }
            });

            ivNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.d("Tts_TAG", " ivNext " + count + a.length);
                        count = count + 2;
                        if (count < a.length) {
                            toTtsSpeak(a[count], sub_count);
                            CommonMethod.toDisplayToast(getContext(), "You have jumped backward");
                        } else {
                            count = count - 2;
                            CommonMethod.toDisplayToast(getContext(), "You have jumped backward ..... ");
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }
            });

            ivPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.d("Tts_TAG", " ivPlayPause " + count);
                        if (tts.isSpeaking() || !isPaused) {
                            isPaused = true;
                            resume = count;
                            tts.stop();
                            CommonMethod.toDisplayToast(getContext(), "Paused");
                        } else {
                            if (resume != 0 && resume < count) {
                                if (a != null) {
                                    count = resume;
                                    sub_count = 0;
                                    toTtsSpeak(a[count], sub_count);
                                    isPaused = false;
                                    CommonMethod.toDisplayToast(getContext(), "Resume");
                                } else {
                                    count = 0;
                                    sub_count = 0;
                                    toSpeak(count, sub_count);
                                    CommonMethod.toDisplayToast(getContext(), "Resume1");
                                }
                            } else {
                                count = 0;
                                sub_count = 0;
                                toSpeak(count, sub_count);
                                CommonMethod.toDisplayToast(getContext(), "Resume");
                            }
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }
            });

            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (tts != null) {
                            if (tts.isSpeaking()) {
                                tts.stop();
                            }
                            tts.shutdown();
                        }
                        mListener.closeTtsFragment();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }
            });

            sbRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    try {
                        Log.d("Tts_TAG", "onProgressChanged Rate : " + progress + " : " + b + " : " + count);
                        SpannableStringBuilder builder = new SpannableStringBuilder(getString(R.string.tts_rate));
                        builder.append(" - ")
                                .append(String.valueOf(progress))
                                .append("%")
                                .setSpan(new ForegroundColorSpan(Color.parseColor("#9e9e9e")),
                                        builder.length(),
                                        builder.length(),
                                        33);

                        tvLblRate.setText(builder);

                        audioSetting = AudioSetting.getAudioSetting(getContext());
                        audioSetting.setSelectedRate(progress);

                        new PrefManager(getContext()).setAudioSetting();

                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.d("Tts_TAG", "onStartTrackingTouch Rate: " + seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    try {
                        Log.d("Tts_TAG", "onStopTrackingTouch Rate: " + seekBar.getProgress());
                        tts.setSpeechRate((float) seekBar.getProgress());

                        toSpeak(count, sub_count);
                        toTtsSpeak(a[count], sub_count);

                        audioSetting = AudioSetting.getAudioSetting(getContext());
                        audioSetting.setSelectedPitch(seekBar.getProgress());

                        new PrefManager(getContext()).setAudioSetting();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }
            });

            sbPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    try {
                        Log.d("Tts_TAG", "onProgressChanged Pitch : " + progress + " : " + b + " : " + count);
                        SpannableStringBuilder builder = new SpannableStringBuilder(getString(R.string.tts_pitch));
                        builder.append(" - ")
                                .append(String.valueOf(progress))
                                .append("%")
                                .setSpan(new ForegroundColorSpan(
                                                Color.parseColor("#9e9e9e")),
                                        builder.length(),
                                        builder.length(),
                                        33);

                        tvLblPitch.setText(builder);

                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    Log.d("Tts_TAG", "onStartTrackingTouch Pitch: " + seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    try {
                        Log.d("Tts_TAG", "onStopTrackingTouch Pitch: " + seekBar.getProgress());

                        tts.setSpeechRate((float) seekBar.getProgress());

                        toSpeak(count, sub_count);
                        toTtsSpeak(a[count], sub_count);

                        audioSetting = AudioSetting.getAudioSetting(getContext());
                        audioSetting.setSelectedPitch(seekBar.getProgress());

                        new PrefManager(getContext()).setAudioSetting();

                    } catch (Exception | Error e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void player1() {
        try {
            status = 0;
            rlChild11.setVisibility(View.VISIBLE);
            rlChild22.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            fabBack.setVisibility(View.GONE);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void player2() {
        try {
            status = 1;
            rlChild11.setVisibility(View.GONE);
            rlChild22.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            fabBack.setVisibility(View.VISIBLE);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void player3() {
        try {
            status = 2;
            rlChild11.setVisibility(View.GONE);
            rlChild22.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            fabBack.setVisibility(View.VISIBLE);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static void toStop(Context context) {
        Log.d("Tts_TAG", " toStop : ");
//        cancelNotification(context);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void closeTtsFragment();
    }

    @Override
    public void onPause() {
        try {
            cancelNotification(getContext());
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        try {
            cancelNotification(getContext());
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}