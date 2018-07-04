package in.tts.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class PdfFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    MediaPlayer mMediaPlayer;

    public PdfFragment() {
        // Required empty public constructor
    }


    @Override

    @AddTrace(name = "onCreatePdfFragment", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CommonMethod.setAnalyticsData(getContext(), "MainTab", "PdfFragment", null);

        tabLayout = getActivity().findViewById(R.id.tabsub);

        viewPager = getActivity().findViewById(R.id.viewpagersub);

        viewPager.setAdapter(new PagerAdapter(getFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("My Book");
        tabLayout.getTabAt(1).setText("Free eBooks");

        LinearLayout linearLayout = (LinearLayout)tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.GRAY);
        drawable.setSize(1, 1);
        linearLayout.setDividerPadding(10);
        linearLayout.setDividerDrawable(drawable);

//        String speakTextTxt = "Hello world";
//        HashMap<String, String> myHashRender = new HashMap<String, String>();
//        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, speakTextTxt);
//
//        String exStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        Log.d("MainActivity", "exStoragePath : "+exStoragePath);
//        File appTmpPath = new File(exStoragePath + "/sounds/");
//        boolean isDirectoryCreated = appTmpPath.mkdirs();
//        Log.d("MainActivity", "directory "+appTmpPath
//                +" is created : "+isDirectoryCreated);
//        String tempFilename = "tmpaudio.wav";
//        String tempDestFile = appTmpPath.getAbsolutePath()
//                + File.separator + tempFilename;
//        Log.d("MainActivity", "tempDestFile : "+tempDestFile);
////        new MySpeech(speakTextTxt);
//
//        HashMap<String, String> myHashRender1 = new HashMap();
//        String wakeUpText = "Are you up yet?";
////        String destFileName = "/sdcard/myAppCache/wakeUp.wav";
////        String destFileName = Environment.getDownloadCacheDirectory() + "wakeUp.wav";
//        String destFileName = "/sdcard/DDD/11j.wav";
//        Log.d("TTS", " dest : " + destFileName);
//        myHashRender1.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, wakeUpText);
//        TextToSpeech mTts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int i) {
//                Log.d("TTS", " i " + i);
//            }
//        });
//        mTts.synthesizeToFile(wakeUpText, myHashRender1, destFileName);
//
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer = MediaPlayer.create(getContext(),android.R.drawable.ic_media_play);
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.start();
//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mMediaPlayer.stop();
//            }
//        });
    }

    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new DocumentsFragment();
                case 1:
                    return new EbookFragment();
                default:
                    return new DocumentsFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }
}

