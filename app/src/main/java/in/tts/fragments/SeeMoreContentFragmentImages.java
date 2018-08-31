package in.tts.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Objects;

import in.tts.R;
import in.tts.activities.ImageOcrActivity;
import in.tts.adapters.ImageAdapterGallery;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;
import in.tts.utils.FilePath;

public class SeeMoreContentFragmentImages extends Fragment {

    private RecyclerView recyclerView;

    private LinearLayout mll;

    private FloatingActionButton fab;

    private TextView tv;

    private PrefManager prefManager;

    private OnFragmentInteractionListener mListener;

    public SeeMoreContentFragmentImages() {
        // Required empty public constructor
    }

    public static SeeMoreContentFragmentImages newInstance() {
        return new SeeMoreContentFragmentImages();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_see_more_content, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (PrefManager.AddedRecentImage){
            setLoadData();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            mll = Objects.requireNonNull(getActivity()).findViewById(R.id.llCustom_loader12000);
            tv = Objects.requireNonNull(getActivity()).findViewById(R.id.tvNoData);
            tv.setText(getString(R.string.str_no_image_viewed));

            recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.rvListSeeMore);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(true);

            fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        toExit(false);
//                        if (status) {
//                            mListener.setCurrentViewPagerItem(4);
                            toBrowseImageFromDevice();
//                        } else {
//                        /**/    toBrowsePdfFromDevice();
//                            mListener.setCurrentViewPagerItem(1);
//                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                        toExit(true);
                    }
                }
            });

            prefManager = new PrefManager(getContext());

        } catch (Exception | Error e) {
            Log.d("TAG_Blank", "DATA : 17 ");
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            toExit(true);
        }
    }

    private void toBrowsePdfFromDevice() {
        try {
            CommonMethod.toCallLoader(getContext(), "Loading...");
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 210);
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }

    }

    private void toBrowseImageFromDevice() {
        try {
            CommonMethod.toCallLoader(getContext(), "Loading...");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), 220);
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d("TAG ", " See onActivityResult " + requestCode + ":" + resultCode + ":" + data.getExtras() + ": " + data.getData() + ":   " + data.getData().getPath());
            String path = FilePath.getPath(getContext(), data.getData());
            Log.d("TAG", "See File Name " + path);
            if (path != null) {
//                if (status) {
                    try {
                        CommonMethod.toCallLoader(getContext(), "Loading...");
                        Intent intent = new Intent(getContext(), ImageOcrActivity.class);
                        intent.putExtra("PATH", path);
                        Objects.requireNonNull(getContext()).startActivity(intent);
                        CommonMethod.toCloseLoader();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                        CommonMethod.toDisplayToast(getContext(), "Unable to fetch Pdf");
                    }
//                } else {
//                    try {
//                        CommonMethod.toCallLoader(getContext(), "Loading...");
//                        Intent intent = new Intent(getContext(), PdfShowingActivity.class);
//                        intent.putExtra("file", path);
//                        Objects.requireNonNull(getContext()).startActivity(intent);
//                        CommonMethod.toCloseLoader();
//                    } catch (Exception | Error e) {
//                        e.printStackTrace();
//                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//                        Crashlytics.logException(e);
//                        FirebaseCrash.report(e);
//                        CommonMethod.toDisplayToast(getContext(), "Unable to fetch Pdf");
//                    }
//                }
            } else {
                CommonMethod.toDisplayToast(getContext(), "Select File is not Pdf");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }


    @SuppressLint("RestrictedApi")
    private void toChangeViews() {
        try {
            recyclerView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            mll.setVisibility(View.GONE);
            tv.setVisibility(View.GONE);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    @SuppressLint("RestrictedApi")
    private void toExit(boolean status) {
        try {
//            if (status) {
//                CommonMethod.toDisplayToast(getContext(), " No Data Found ");
//            }
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Objects.requireNonNull(getActivity())
//                            .getSupportFragmentManager()
//                            .beginTransaction()
//                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
//                            .remove(SeeMoreContentFragment.this)
//                            .commit();
//                }
//            }, 1000);

            recyclerView.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            mll.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
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
//        status = loadData;
        PrefManager.CurrentPage = 4;
        toLoadMore();
    }

    private void toLoadMore() {
        try {
//            if (getArguments() != null) {
//            Log.d("TAG_Blank", "DATA : 1  " + getArguments().getBoolean("STATUS"));
//                if (getArguments().getBoolean("STATUS")) {
//            if (status) {
                Log.d("TAG_Blank", "DATA : 2 ");
                // Images
                if (prefManager.toGetImageListRecent() != null) {
                    Log.d("TAG_Blank", "DATA : 3 ");
                    if (prefManager.toGetImageListRecent().size() > 0) {
//                        status = true;
                        Log.d("TAG_Blank", "DATA : 4 ");
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                        recyclerView.setAdapter(new ImageAdapterGallery(getContext(), prefManager.toGetImageListRecent()));
                        toChangeViews();
                    } else {
                        Log.d("TAG_Blank", "DATA : 5 ");
                        toExit(true);
                    }
                } else {
                    Log.d("TAG_Blank", "DATA : 6 ");
                    toExit(true);
                }
//            } else {
//                Log.d("TAG_Blank", "DATA : 7 ");
//                // PDf
//                if (prefManager.toGetPDFListRecent() != null) {
//                    Log.d("TAG_Blank", "DATA : 8 ");
//                    if (prefManager.toGetPDFListRecent().size() > 0) {
//                        Log.d("TAG_Blank", "DATA : 9 ");
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                        recyclerView.setAdapter(new PdfListAdapter(getContext(), prefManager.toGetPDFListRecent()));
//                        toChangeViews();
//                    } else {
//                        Log.d("TAG_Blank", "DATA : 10 ");
//                        toExit(true);
//                    }
//                } else {
//                    Log.d("TAG_Blank", "DATA : 11 ");
//                    toExit(true);
//                }
//            }
//            } else
//
//            {
//                Log.d("TAG_Blank", "DATA : 12 ");
//                // Pdf
//                if (prefManager.toGetPDFListRecent() != null) {
//                    Log.d("TAG_Blank", "DATA : 13 ");
//                    if (prefManager.toGetPDFListRecent().size() > 0) {
//                        Log.d("TAG_Blank", "DATA : 14 ");
//                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                        recyclerView.setAdapter(new ImageAdapterGallery(getContext(), prefManager.toGetPDFListRecent()));
//                        toChangeViews();
//                    } else {
//                        Log.d("TAG_Blank", "DATA : 15 ");
//                        toExit(true);
//                    }
//                } else {
//                    Log.d("TAG_Blank", "DATA : 16 ");
//                    toExit(true);
//                }
//            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void setCurrentViewPagerItem(int i);
    }
}
