package in.tts.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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

import java.io.File;

import in.tts.R;
import in.tts.adapters.CustomPagerAdapter;
import in.tts.adapters.PDFHomePage;
import in.tts.adapters.PDFHomePageImages;
import in.tts.model.PrefManager;
import in.tts.utils.AppPermissions;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetImages;
import in.tts.utils.ToGetPdfFiles;

public class MainHomeFragment extends Fragment {

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

    private PrefManager prefManager;

    private OnFragmentInteractionListener mListener;

    public MainHomeFragment() {
    }

    public static MainHomeFragment newInstance(String param1, String param2) {
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
            toBindViews();
            toBindTopBanners();
            AppPermissions.toCheckPermissionRead(getContext(), getActivity(), MainHomeFragment.this, null, null,false);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            Log.d("TAG", " ERROR1 " + e.getMessage());
        }
    }

    private void toBindViews() throws Exception, Error {
        if (getActivity() != null) {
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

            prefManager = new PrefManager(getContext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toSetSomeData();
                } else {
                    Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void toSetSomeData() {
        try {
            if (prefManager.toGetPDFList() != null) {
                if (prefManager.toGetPDFList().size() != 0) {
                    toBindRecentPdf();
                } else {
                    CommonMethod.toDisplayToast(getContext(), "No PDF Found");
//                    ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), MainHomeFragment.this);
                }
            } else {
                ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), MainHomeFragment.this);
            }

            if (prefManager.toGetImageList() != null) {
                if (prefManager.toGetImageList().size() != 0) {
                    toBindRecentImages();
                } else {
                    CommonMethod.toDisplayToast(getContext(), "No Images Found");
                    prefManager.toSetImageFileList(ToGetImages.getAllShownImagesPath(getActivity()));
                }
            } else {
                prefManager.toSetImageFileList(ToGetImages.getAllShownImagesPath(getActivity()));
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void toBindTopBanners() throws Exception, Error {
        mViewPager.setAdapter(new CustomPagerAdapter(mResources, getContext()));
        tabLayout.setupWithViewPager(mViewPager);
    }

    // To Bind views
    private void toBindRecentImages() {
        try {
            if (view1 != null) {
                ll.removeView(view1);
            }
            if (prefManager.toGetImageList().size() != 0) {
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
                        vpDeals.setAdapter(new PDFHomePageImages(getContext(), prefManager.toGetImageList()));

                        tvSeeMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    mListener.setCurrentViewPagerItem(4);
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                    Crashlytics.logException(e);
                                }
                            }
                        });

                        ll.addView(view1);
                    }
                }
            } else {
                ToGetImages.getAllShownImagesPath(getActivity());
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            Log.d("TAG", " ERROR4" + e.getMessage());
        }
    }

    private void toBindRecentPdf() {
        try {
            if (view != null) {
                ll.removeView(view);
            }
            if (prefManager.toGetPDFList().size() != 0) {
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
                                    CommonMethod.toCloseLoader();
                                    Crashlytics.logException(e);
                                    Log.d("TAG", " ERROR5 " + e.getMessage());
                                }
                            }
                        });

                        vpDeals.setClipToPadding(true);
                        vpDeals.setOffscreenPageLimit(10);
                        vpDeals.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                        vpDeals.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                        vpDeals.setAdapter(new PDFHomePage(getContext(), prefManager.toGetPDFList()));
                        ll.addView(view);
                    }
                }
            } else {
                ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), MainHomeFragment.this);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            Log.d("TAG", " ERROR6 " + e.getMessage());
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
            Crashlytics.logException(e);
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
