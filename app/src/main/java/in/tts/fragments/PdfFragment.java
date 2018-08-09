package in.tts.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
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
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.util.Objects;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class PdfFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private MyBooksListFragment tab1;
    private EBookFragment tab2;

    private String[] tabHomeText = new String[]{"My Books", "Library"};

    public PdfFragment() {
    }

    private OnFragmentInteractionListener mListener;

    public static PdfFragment newInstance() {
        return new PdfFragment();
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

    public void setLoadData() {
        try {
            Log.d("Tag", "tab2 setLoadData " + viewPager.getCurrentItem());
            if (viewPager != null) {
                setCurrentViewPagerItem(viewPager.getCurrentItem());
            } else {
                setCurrentViewPagerItem(0);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    @AddTrace(name = "onCreatePdfFragment", enabled = true)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

            tabLayout.getTabAt(0).setText(tabHomeText[0]);
            tabLayout.getTabAt(1).setText(tabHomeText[1]);

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
//                    viewPager.setCurrentItem(tab.getPosition());
                    setCurrentViewPagerItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }

            });
//            viewPager.setCurrentItem(0, true);

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    public void setCurrentViewPagerItem(int i) {
        try {
            if (tabLayout != null) {
                Objects.requireNonNull(tabLayout.getTabAt(i)).select();
            }
            if (viewPager != null) {
                viewPager.setCurrentItem(i);
            }
            switch (i) {
                case 0:
                    if (tab1 != null) {
                        tab1.setLoadData();
                    }
                    break;
                case 1:
                    if (tab2 != null) {
                        tab2.setLoadData();
                    }
                    break;
                default:
                    if (tabLayout != null) {
                        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                    }
                    if (tab1 != null) {
                        tab1.setLoadData();
                    }
                    break;
            }
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return tab1 = MyBooksListFragment.newInstance();
                case 1:
                    return tab2 = EBookFragment.newInstance();
                default:
                    return tab1 = MyBooksListFragment.newInstance();
            }
        }

        public int getCount() {
            return tabHomeText.length;
        }
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