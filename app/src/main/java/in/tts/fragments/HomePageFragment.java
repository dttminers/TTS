package in.tts.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class HomePageFragment extends Fragment {
    ViewPager mViewPager;
    TabLayout tabLayout;
    ImageView imageView;
    ImageView ivLeft, ivRight;
    CustomPagerAdapter mCustomPagerAdapter;
    int currentImage = 0;
    int mResources[] = {R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5};

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    @AddTrace(name = "onCreateHomePageFragment", enabled = true)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonMethod.setAnalyticsData(getContext(), "MainTab", "HomePage", null);
        try {
            imageView = getActivity().findViewById(R.id.imageView);
            ivLeft = getActivity().findViewById(R.id.imageViewLeft);
            ivRight = getActivity().findViewById(R.id.imageViewRight);
            tabLayout = getActivity().findViewById(R.id.tlHomePage);
            mViewPager = getActivity().findViewById(R.id.vpHomePage);
            mCustomPagerAdapter = new CustomPagerAdapter(mResources, getContext());
            mViewPager.setAdapter(mCustomPagerAdapter);
            tabLayout.setupWithViewPager(mViewPager);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Increase Counter to move to next Image
                if (currentImage == 0) {
                    currentImage = mResources.length-1;
                    mViewPager.setCurrentItem(currentImage);
                } else {
                    currentImage--;
                    mViewPager.setCurrentItem(currentImage);
                }
            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentImage == mResources.length-1) {
                    currentImage = 0;
                    mViewPager.setCurrentItem(currentImage);
                } else {
                    currentImage++;
                    mViewPager.setCurrentItem(currentImage);
                }
            }
        });
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        int mResources[];

        public CustomPagerAdapter(int mResources[], Context context) {
            mContext = context;
            this.mResources = mResources;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mResources[position]);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }
}