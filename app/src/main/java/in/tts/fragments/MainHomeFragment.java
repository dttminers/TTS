package in.tts.fragments;

import android.Manifest;
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
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.CustomPagerAdapter;
import in.tts.adapters.PDFHomePage;
import in.tts.adapters.PDFHomePageImages;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class MainHomeFragment extends Fragment {

    private NestedScrollView nsv;
    private ProgressBar mLoading;

    // Main View Pager
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ImageView imageView;
    private ImageView ivLeft, ivRight;

    private int currentImage = 0;
    private int mResources[] = {R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5};

    // Other data
    private LinearLayout ll;
    private View view, view1;

    //pdf
    private ArrayList<String> pdfFile;
    private PDFHomePage pdfHomePage;

    // images
    private ArrayList<String> imageFile;
    private PDFHomePageImages pdfHomePageImages;

    PrefManager prefManager;

    private OnFragmentInteractionListener mListener;

    public MainHomeFragment() {
    }

    public static MainHomeFragment newInstance() {
        return new MainHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonMethod.toCloseLoader();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CommonMethod.toCloseLoader();
        return inflater.inflate(R.layout.fragment_main_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.toCloseLoader();
            toBindViews();
            fn_permission();
            toBindTopBanners();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                toSetSomeData();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toBindViews() throws Exception, Error {
        if (getActivity() != null) {
            prefManager = new PrefManager(getContext());

            mLoading = getActivity().findViewById(R.id.progressBar100);
            nsv = getActivity().findViewById(R.id.nsv);
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

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            CommonMethod.toCloseLoader();
            if (requestCode == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toSetSomeData();
                } else {
                    Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    public void toSetSomeData() {
        try {
//            CommonMethod.toCloseLoader();
//            Log.d("TAG ", " Data :  " + prefManager.toGetPDFList().size() + ":" + prefManager.toGetImageList().size());
            if (prefManager.toGetPDFList() == null) {
//                pdfFile = ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
                pdfFile = new ArrayList<>();
                getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
            } else {
                pdfFile = prefManager.toGetPDFList();
            }
            pdfHomePage = new PDFHomePage(getContext(), pdfFile);
            toBindRecentPdf();

            if (prefManager.toGetImageList() == null) {
//                imageFile = ToGetImages.getAllShownImagesPath(getActivity());
                imageFile = new ArrayList<>();
                getAllShownImagesPath();
            } else {
                imageFile = prefManager.toGetImageList();
            }
            pdfHomePageImages = new PDFHomePageImages(getContext(), imageFile);
            toBindRecentImages();
            CommonMethod.toCloseLoader();
            nsv.setVisibility(View.VISIBLE);
            mLoading.setVisibility(View.GONE);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    private void toBindTopBanners() throws Exception, Error {
        mViewPager.setAdapter(new CustomPagerAdapter(mResources, getContext()));
        tabLayout.setupWithViewPager(mViewPager);
    }

    // To Bind views
    public void getFile(final File dir) {
        try {
            File listFile[] = dir.listFiles();
            if (listFile != null && listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {
                    if (listFile[i].isDirectory()) {
                        getFile(listFile[i]);
                    } else {
                        boolean booleanpdf = false;
                        if (listFile[i].getName().endsWith(".pdf")) {
                            for (int j = 0; j < pdfFile.size(); j++) {
//                                    if (fileList.get(j).getName().equals(listFile[i].getName())) {
                                if (pdfFile.get(j).equals(listFile[i].getPath())) {
                                    booleanpdf = true;
                                } else {
                                }
                            }
                            if (booleanpdf) {
                                booleanpdf = false;
                            } else {
                                if (pdfFile.size() < 11) {
                                    pdfFile.add(listFile[i].getPath());
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    private void toBindRecentPdf() {
        try {
//            CommonMethod.toCloseLoader();
            if (view != null) {
                ll.removeView(view);
            }
            if (pdfFile.size() != 0) {
                if (getContext() != null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    if (inflater != null) {
                        view = inflater.inflate(R.layout.layout_home_page_recent_items, null, false);

                        TextView tvHeader = view.findViewById(R.id.tvRecent);
                        TextView tvSeeMore = view.findViewById(R.id.tvSeeMore);

                        ViewPager vpDeals = view.findViewById(R.id.vpRecentItem);

                        tvHeader.setText("Recent PDF");
                        tvSeeMore.setText("See More");

                        tvSeeMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    mListener.setCurrentViewPagerItem(1);
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                    CommonMethod.toCloseLoader();
                                    Crashlytics.logException(e);
                                    FirebaseCrash.report(e);
                                    Log.d("TAG", " ERROR5 " + e.getMessage());
                                }
                            }
                        });

                        vpDeals.setClipToPadding(true);
                        vpDeals.setOffscreenPageLimit(10);
                        vpDeals.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                        vpDeals.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                        vpDeals.setAdapter(pdfHomePage);
                        ll.addView(view);
                    }
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    public void getAllShownImagesPath() {
        try {
//            CommonMethod.toCloseLoader();
            String[] projection = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.MIME_TYPE
            };
            if (getActivity() != null) {
                Cursor cursor =
                        getActivity().getContentResolver()
                                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
                if (cursor != null) {
                    while (cursor.moveToNext() && imageFile.size() < 11) {
                        String imageLocation = cursor.getString(1);
                        imageFile.add(imageLocation);
                        Log.d("TAG", "File Name " + imageLocation);
                    }
                    Log.d("TAG", "Count Image Files " + imageFile.size());
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    private void toBindRecentImages() {
        try {
//            CommonMethod.toCloseLoader();
            if (view1 != null) {
                ll.removeView(view1);
            }
            if (imageFile.size() != 0) {
                if (getContext() != null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    if (inflater != null) {
                        view1 = inflater.inflate(R.layout.layout_home_page_recent_items1, null, false);

                        TextView tvHeader = view1.findViewById(R.id.tvRecent);
                        TextView tvSeeMore = view1.findViewById(R.id.tvSeeMore);

                        ViewPager vpDeals = view1.findViewById(R.id.vpRecentItem);

                        tvHeader.setText("Recent Images");
                        tvSeeMore.setText("See More");

                        vpDeals.setClipToPadding(false);
                        vpDeals.setOffscreenPageLimit(10);
                        vpDeals.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                        vpDeals.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                        vpDeals.setAdapter(pdfHomePageImages);
                        pdfHomePageImages.notifyDataSetChanged();

                        tvSeeMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    mListener.setCurrentViewPagerItem(4);
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                    Crashlytics.logException(e);
                                    FirebaseCrash.report(e);
                                }
                            }
                        });

                        ll.addView(view1);
                    }
                }
            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
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
        try {
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void setCurrentViewPagerItem(int i);
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