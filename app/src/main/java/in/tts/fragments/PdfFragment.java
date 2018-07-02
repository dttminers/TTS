package in.tts.fragments;

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
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;

import in.tts.R;

public class PdfFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;

    public PdfFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tabLayout = getActivity().findViewById(R.id.tabsub);

        viewPager = getActivity().findViewById(R.id.viewpagersub);

        viewPager.setAdapter(new PagerAdapter(getFragmentManager()));
        tabLayout.addTab(tabLayout.newTab().setText("A"));
        tabLayout.addTab(tabLayout.newTab().setText("B"));

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("My Book");
        tabLayout.getTabAt(1).setText("Free eBooks");
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

