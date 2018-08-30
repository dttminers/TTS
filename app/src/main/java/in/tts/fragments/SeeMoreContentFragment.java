package in.tts.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import java.util.Objects;

import in.tts.R;
import in.tts.adapters.ImageAdapterGallery;
import in.tts.adapters.PdfListAdapter;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class SeeMoreContentFragment extends Fragment {

    private RecyclerView recyclerView;

    private LinearLayout mll;

    private FloatingActionButton fab;

    private PrefManager prefManager;

    private boolean status = false;

    private OnFragmentInteractionListener mListener;

    public SeeMoreContentFragment() {
        // Required empty public constructor
    }

    public static SeeMoreContentFragment newInstance() {
        return new SeeMoreContentFragment();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            mll = Objects.requireNonNull(getActivity()).findViewById(R.id.llCustom_loader12000);

            recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.rvListSeeMore);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(true);

            fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        toExit(false);
                        if (status) {
                            mListener.setCurrentViewPagerItem(4);
                        } else {
                            mListener.setCurrentViewPagerItem(1);
                        }
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

            if (getArguments() != null) {
                Log.d("TAG_Blank", "DATA : 1  " + getArguments().getBoolean("STATUS"));
                if (getArguments().getBoolean("STATUS")) {
                    Log.d("TAG_Blank", "DATA : 2 ");
                    // Images
                    if (prefManager.toGetImageListRecent() != null) {
                        Log.d("TAG_Blank", "DATA : 3 ");
                        if (prefManager.toGetImageListRecent().size() > 0) {
                            status = true;
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
                } else {
                    Log.d("TAG_Blank", "DATA : 7 ");
                    // PDf
                    if (prefManager.toGetPDFListRecent() != null) {
                        Log.d("TAG_Blank", "DATA : 8 ");
                        if (prefManager.toGetPDFListRecent().size() > 0) {
                            Log.d("TAG_Blank", "DATA : 9 ");
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(new PdfListAdapter(getContext(), prefManager.toGetPDFListRecent()));
                            toChangeViews();
                        } else {
                            Log.d("TAG_Blank", "DATA : 10 ");
                            toExit(true);
                        }
                    } else {
                        Log.d("TAG_Blank", "DATA : 11 ");
                        toExit(true);
                    }
                }
            } else

            {
                Log.d("TAG_Blank", "DATA : 12 ");
                // Pdf
                if (prefManager.toGetPDFListRecent() != null) {
                    Log.d("TAG_Blank", "DATA : 13 ");
                    if (prefManager.toGetPDFListRecent().size() > 0) {
                        Log.d("TAG_Blank", "DATA : 14 ");
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new ImageAdapterGallery(getContext(), prefManager.toGetPDFListRecent()));
                        toChangeViews();
                    } else {
                        Log.d("TAG_Blank", "DATA : 15 ");
                        toExit(true);
                    }
                } else {
                    Log.d("TAG_Blank", "DATA : 16 ");
                    toExit(true);
                }
            }
        } catch (Exception | Error e) {
            Log.d("TAG_Blank", "DATA : 17 ");
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            toExit(true);
        }
    }

    @SuppressLint("RestrictedApi")
    private void toChangeViews() {
        try {
            recyclerView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            mll.setVisibility(View.GONE);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    private void toExit(boolean status) {
        try {
            if (status) {
                CommonMethod.toDisplayToast(getContext(), " No Data Found ");
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(getActivity())
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .remove(SeeMoreContentFragment.this)
                            .commit();
                }
            }, 1000);
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void setCurrentViewPagerItem(int i);
    }
}
