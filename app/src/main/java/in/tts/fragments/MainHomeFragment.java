package in.tts.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.Objects;

import in.tts.R;
import in.tts.adapters.CustomPagerAdapter;
import in.tts.adapters.HomePageRecentImages;
import in.tts.adapters.HomePageRecentPdf;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class MainHomeFragment extends Fragment {

    private NestedScrollView nsv;

    // Main View Pager
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    private ImageView imageView;
    private ImageView ivLeft, ivRight;

    private int currentImage = 0;
    private int mResources[] = {R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5};

    //pdf
    private ArrayList<String> pdfFile;
    private HomePageRecentPdf pdfHomePage;

    private TextView tvHeaderPDF, tvSeeMorePDF, tvNoRecentPDF;
    private ViewPager vpRecentPDF;

    // images
    private ArrayList<String> imageFile;
    private HomePageRecentImages pdfHomePageImages;

    private TextView tvHeader, tvSeeMore, tvNoRecentImage;
    private ViewPager vpRecentImage;

    private boolean status = false;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.toReleaseMemory();
            toBindViews();
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                toSetData();
            }
            CommonMethod.toReleaseMemory();
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
            nsv = getActivity().findViewById(R.id.nsv);

            imageView = getActivity().findViewById(R.id.imageView);

            ivLeft = getActivity().findViewById(R.id.imageViewLeft1);
            ivRight = getActivity().findViewById(R.id.imageViewRight1);

            tabLayout = getActivity().findViewById(R.id.tlHomePage);

            mViewPager = getActivity().findViewById(R.id.vpHomePage);

            ivLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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

            // Pdf

            tvHeaderPDF = Objects.requireNonNull(getActivity()).findViewById(R.id.tvRecentPdf);
            tvSeeMorePDF = Objects.requireNonNull(getActivity()).findViewById(R.id.tvSeeMorePdf);
            tvNoRecentPDF = Objects.requireNonNull(getActivity()).findViewById(R.id.tvNoRecentPDF);

            vpRecentPDF = Objects.requireNonNull(getActivity()).findViewById(R.id.vpRecentItemPdf);

            tvHeaderPDF.setText("Recent PDF");
            tvSeeMorePDF.setText("See More");

            tvSeeMorePDF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (pdfFile.size() > 0) {
                            toCallSeeMore(false);
                        } else {
                            CommonMethod.toDisplayToast(getContext(), " No data");
                        }

//                        mListener.setCurrentViewPagerItem(1);
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

            // images
            tvHeader = Objects.requireNonNull(getActivity()).findViewById(R.id.tvRecent);
            tvSeeMore = Objects.requireNonNull(getActivity()).findViewById(R.id.tvSeeMore);
            tvNoRecentImage = Objects.requireNonNull(getActivity()).findViewById(R.id.tvNoRecentImage);

            vpRecentImage = Objects.requireNonNull(getActivity()).findViewById(R.id.vpRecentItem);

            tvHeader.setText("Recent Images");
            tvSeeMore.setText("See More");

            tvSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (imageFile.size() > 0) {
                            toCallSeeMore(true);
                        } else {
                            CommonMethod.toDisplayToast(getContext(), " No data");
                        }
//                        mListener.setCurrentViewPagerItem(4);
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });
        }
    }

    private void toCallSeeMore(boolean status) {
        try {
            BlankFragment blankFragment = new BlankFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("STATUS", status);
            blankFragment.setArguments(bundle);
//            View view = Objects.requireNonNull(getActivity()).findViewById(R.id.rlHomePage);
//            view.setVisibility(View.VISIBLE);
            mListener.setVisible();
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.rlHomePage, blankFragment).commit();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toSetDisplay();
                } else {
                    CommonMethod.toDisplayToast(getContext(), "Please allow the permission");
                }
            }
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetData() {
        try {
            if (!status) {
                toSetDisplay();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetDisplay() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                toBindTopBanners();
                                toSetRecentPdf();
                                toSetRecentImages();
                            } catch (Exception | Error e) {
                                e.printStackTrace();
                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e);
                                FirebaseCrash.report(e);
                            }
                        }
                    });
                }
            }).start();
            status = true;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetRecentPdf() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toBindRecentPdf();
                        }
                    });
                }
            }, 100);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetRecentImages() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toBindRecentImages();
                        }
                    });
                }
            }, 100);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toBindTopBanners() throws Exception, Error {
        mViewPager.setAdapter(new CustomPagerAdapter(mResources, getContext()));
        tabLayout.setupWithViewPager(mViewPager);
        CommonMethod.toReleaseMemory();
    }

    private void toBindRecentPdf() {
        try {
            pdfFile = new ArrayList<>();
            if (prefManager.toGetPDFListRecent() != null && prefManager.toGetPDFListRecent().size() != 0) {
                pdfFile = prefManager.toGetPDFListRecent();
                pdfHomePage = new HomePageRecentPdf(getContext(), pdfFile);
            }

            if (pdfFile.size() != 0) {
                if (getContext() != null) {
                    vpRecentPDF.setClipToPadding(true);
                    vpRecentPDF.setOffscreenPageLimit(10);
                    vpRecentPDF.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                    vpRecentPDF.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                    vpRecentPDF.setAdapter(pdfHomePage);
                    vpRecentPDF.setVisibility(View.VISIBLE);
                    tvNoRecentPDF.setVisibility(View.GONE);
                }
            } else {
                vpRecentPDF.setVisibility(View.GONE);
                tvNoRecentPDF.setVisibility(View.VISIBLE);
            }
            getActivity().findViewById(R.id.rlRecentItemPdfMain).setVisibility(View.VISIBLE);
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
        }
    }

    private void toBindRecentImages() {
        try {
            imageFile = new ArrayList<>();
            if (prefManager.toGetImageListRecent() != null) {
                imageFile = prefManager.toGetImageListRecent();
                pdfHomePageImages = new HomePageRecentImages(getContext(), imageFile);
            }
            if (imageFile.size() != 0) {
                if (getContext() != null) {
                    vpRecentImage.setClipToPadding(false);
                    vpRecentImage.setOffscreenPageLimit(10);
                    vpRecentImage.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                    vpRecentImage.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                    vpRecentImage.setAdapter(pdfHomePageImages);
                    vpRecentImage.setVisibility(View.VISIBLE);
                    tvNoRecentImage.setVisibility(View.GONE);
                }
            } else {
                vpRecentImage.setVisibility(View.GONE);
                tvNoRecentImage.setVisibility(View.VISIBLE);
            }
            getActivity().findViewById(R.id.rlRecentItemPdfMain).setVisibility(View.VISIBLE);
            CommonMethod.toReleaseMemory();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toReleaseMemory();
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
            CommonMethod.toReleaseMemory();
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
        CommonMethod.toReleaseMemory();
        mListener = null;
    }

    public void setLoadData() {
        if (!status) {
            fn_permission();
        }
        Log.d("TAG ", " main  " + PrefManager.AddedRecentImage + ":" + PrefManager.AddedRecentPDF);
        if (PrefManager.AddedRecentImage || PrefManager.AddedRecentPDF) {
            toSetData();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void setCurrentViewPagerItem(int i);

        void setVisible();
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

    @Override
    public void onResume() {
        super.onResume();
        if (PrefManager.AddedRecentImage || PrefManager.AddedRecentPDF) {
            toSetData();
        }
        CommonMethod.toReleaseMemory();
    }
}