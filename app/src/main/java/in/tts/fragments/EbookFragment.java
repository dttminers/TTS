package in.tts.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics; import com.flurry.android.FlurryAgent; import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class EbookFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    public EbookFragment() {
        // Required empty public constructor
    }

    @Override
    @AddTrace(name = "onCreateEbookFragment", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        CommonMethod.toCloseLoader();
        return inflater.inflate(R.layout.fragment_ebook, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.setAnalyticsData(getContext(), "DocTab", "Doc Free eBooks", null);
        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonMethod.toReleaseMemory();
    }

    public static EbookFragment newInstance() {
        return new EbookFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
            }
        } catch (Exception | Error e) {
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
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