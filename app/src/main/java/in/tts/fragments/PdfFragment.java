package in.tts.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

    private MyBooksFragment tab1;
    private EBookFragment tab2;

    private String[] tabHomeText = new String[]{"My Books", "Free eBooks"};

    public PdfFragment() {
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
            CommonMethod.setAnalyticsData(getContext(), "DocTab", "Doc Pdf", null);

            tabLayout = getActivity().findViewById(R.id.tabsub);
            viewPager = getActivity().findViewById(R.id.viewpagersub);

            viewPager.setAdapter(new SectionsPagerAdapter(getChildFragmentManager()));
            tabLayout.setupWithViewPager(viewPager);

            tabLayout.getTabAt(0).setText(tabHomeText[0]).select();
            tabLayout.getTabAt(1).setText(tabHomeText[1]);


            viewPager.setCurrentItem(1, true);
            LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(Color.GRAY);
            drawable.setSize(1, 1);

            linearLayout.setDividerPadding(10);
            linearLayout.setDividerDrawable(drawable);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return tab1 = MyBooksFragment.newInstance();
                case 1:
                    return tab2 = EBookFragment.newInstance();
                default:
                    return tab1 = MyBooksFragment.newInstance();
            }
        }

        public int getCount() {
            return tabHomeText.length;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        viewPager.setCurrentItem(1, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.setCurrentItem(0, true);
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonMethod.toReleaseMemory();
    }
}