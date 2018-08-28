package in.tts.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class BlankFragment extends Fragment {

    private RecyclerView recyclerView;

    private LinearLayout mll;

    private PrefManager prefManager;

    private OnFragmentInteractionListener mListener;

    public BlankFragment() {
        // Required empty public constructor
    }

    public static BlankFragment newInstance() {
        return new BlankFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            mll = Objects.requireNonNull(getActivity()).findViewById(R.id.llCustom_loader12000);

            recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.rvListSeeMore);

            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setHasFixedSize(true);


            prefManager = new PrefManager(getContext());

            if (getArguments() != null) {
                if (getArguments().getBoolean("STATUS")) {
                    // Images
                    if (prefManager.toGetImageListRecent() != null) {
                        if (prefManager.toGetImageListRecent().size() > 0) {
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                            recyclerView.setAdapter(new ImageAdapterGallery(getContext(), prefManager.toGetImageListRecent()));
                            toChangeViews();
                        } else {
                            toExit();
                        }
                    } else {
                        toExit();
                    }
                } else {
                    // PDf
                    if (prefManager.toGetPDFListRecent() != null) {
                        if (prefManager.toGetPDFListRecent().size() > 0) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(new ImageAdapterGallery(getContext(), prefManager.toGetPDFListRecent()));
                            toChangeViews();
                        } else {
                            toExit();
                        }
                    } else {
                        toExit();
                    }
                }
            } else {
                // Pdf
                if (prefManager.toGetPDFListRecent() != null) {
                    if (prefManager.toGetPDFListRecent().size() > 0) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new ImageAdapterGallery(getContext(), prefManager.toGetPDFListRecent()));
                        toChangeViews();
                    } else {
                        toExit();
                    }
                } else {
                    toExit();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    private void toChangeViews() {
        try {
            recyclerView.setVisibility(View.VISIBLE);
            mll.setVisibility(View.GONE);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    private void toExit() {
        try {
            CommonMethod.toDisplayToast(getContext(), " No Data Found ");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(BlankFragment.this).commit();
                }
            }, 1500);
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
    }
}
