package in.tts.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.TableLayout;

import com.google.firebase.perf.metrics.AddTrace;

import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class PdfFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;

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
        tabLayout.addTab(tabLayout.newTab().setText("A"));
        tabLayout.addTab(tabLayout.newTab().setText("B"));

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

