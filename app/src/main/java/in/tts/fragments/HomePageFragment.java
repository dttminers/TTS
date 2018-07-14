package in.tts.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;


import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.CustomPagerAdapter;
import in.tts.adapters.PDFHomePage;
import in.tts.adapters.PDFHomePageImages;
import in.tts.model.AppData;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetImages;
import in.tts.utils.ToGetPdfFiles;

public class HomePageFragment extends Fragment {
    ViewPager mViewPager;
    TabLayout tabLayout;
    ImageView imageView;
    ImageView ivLeft, ivRight;
    LinearLayout ll;
    int currentImage = 0;
    int mResources[] = {R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5};
    boolean boolean_permission;
    private File dir;
    private View view, view1;

    public static int REQUEST_PERMISSIONS = 1;
    public static ArrayList<File> fileList;
    public static ArrayList<String> fileName;


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
            ivLeft = getActivity().findViewById(R.id.imageViewLeft1);
            ivRight = getActivity().findViewById(R.id.imageViewRight1);
            tabLayout = getActivity().findViewById(R.id.tlHomePage);
            mViewPager = getActivity().findViewById(R.id.vpHomePage);

            ll = getActivity().findViewById(R.id.llData);

            ivLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Increase Counter to move to next Image
                    if (currentImage == 0) {
                        currentImage = mResources.length - 1;
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
                    if (currentImage == mResources.length - 1) {
                        currentImage = 0;
                        mViewPager.setCurrentItem(currentImage);
                    } else {
                        currentImage++;
                        mViewPager.setCurrentItem(currentImage);
                    }
                }
            });

            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            fn_permission();

            mViewPager.setAdapter(new CustomPagerAdapter(mResources, getContext()));
            tabLayout.setupWithViewPager(mViewPager);

        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

    }

    private void fn_permission() {
        try {
            Log.d("TAG", " pdf permission ");
            if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
                    Log.d("TAG", "Home1 ");
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
                    Log.d("TAG", "Home2 ");
                }
            } else {
                Log.d("TAG", "Home3 ");
                toGetPDF();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

    }

    private void toGetPDF() {
        try {
            CommonMethod.toCallLoader(getContext(), "Loading...");
            boolean_permission = true;
            if (AppData.fileList != null) {
                Log.d("TAG", " ppk 1 ");
                toBindDealProductData(AppData.fileList);
            } else {
                Log.d("TAG", " ppk 2 ");
                fileList = new ArrayList<>();
                toBindDealProductData(ToGetPdfFiles.getfile(dir));
            }

            if (AppData.fileName != null) {
                toBindDealProductDataImages(AppData.fileName);
            } else {
                fileName = new ArrayList<>();
                toBindDealProductDataImages(ToGetImages.getAllShownImagesPath(getActivity()));
            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    public void toBindDealProductDataImages(ArrayList<String> fileList) {
        try {
            if (view1 != null) {
                ll.removeView(view1);
            }
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                view1 = inflater.inflate(R.layout.layout_home_page_recent_items1, null, false);

                TextView tvHeader = view1.findViewById(R.id.tvRecent);
                TextView tvSeeMore = view1.findViewById(R.id.tvSeeMore);

                ViewPager vpDeals = view1.findViewById(R.id.vpRecentItem);

                tvHeader.setText("Recent Images");
                tvSeeMore.setText("See More");

                vpDeals.setClipToPadding(false);
                vpDeals.setOffscreenPageLimit(3);
                vpDeals.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                vpDeals.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                vpDeals.setAdapter(new PDFHomePageImages(getContext(), fileList));

                ll.addView(view1);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void toBindDealProductData(ArrayList<File> list) {
        try {
            if (view != null) {
                ll.removeView(view);
            }
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                view = inflater.inflate(R.layout.layout_home_page_recent_items, null, false);

                TextView tvHeader = view.findViewById(R.id.tvRecent);
                TextView tvSeeMore = view.findViewById(R.id.tvSeeMore);

                ViewPager vpDeals = view.findViewById(R.id.vpRecentItem);

                tvHeader.setText("Recent PDF");
                tvSeeMore.setText("See More");

                vpDeals.setClipToPadding(true);
                vpDeals.setOffscreenPageLimit(3);
                vpDeals.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                vpDeals.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                vpDeals.setAdapter(new PDFHomePage(getContext(), list));

                ll.addView(view);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", "Home01 " + requestCode + ":" + permissions + ":" + grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toGetPDF();
                Log.d("TAG", "Home31 ");
            } else {
                Log.d("TAG", "Home21 ");
                Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d("TAG", "Home11 ");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }
}