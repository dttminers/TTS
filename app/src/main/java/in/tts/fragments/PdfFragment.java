package in.tts.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

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

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class PdfFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public PdfFragment() {
    }

    @Override
    @AddTrace(name = "onCreatePdfFragment", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.setAnalyticsData(getContext(), "MainTab", "PdfFragment", null);
            tabLayout = getActivity().findViewById(R.id.tabsub);
            viewPager = getActivity().findViewById(R.id.viewpagersub);
            toSetData();
        } catch (Exception |Error e){
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("TAG", " pdf position " + position);
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


//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "";
//        }
    }

    public void toSetData(){
        try{
            viewPager.setAdapter(new PagerAdapter(getFragmentManager()));
            tabLayout.setupWithViewPager(viewPager);

            tabLayout.getTabAt(0).setText("My Book").select();
            tabLayout.getTabAt(1).setText("Free eBooks");

            LinearLayout linearLayout = (LinearLayout)tabLayout.getChildAt(0);
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.GRAY);
            drawable.setSize(1, 1);
            linearLayout.setDividerPadding(10);
            linearLayout.setDividerDrawable(drawable);
        } catch (Exception| Error e){
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "pdf onResume ll");
        toSetData();
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }
}