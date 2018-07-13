package in.tts.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class EBookFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    public EBookFragment() {
        // Required empty public constructor
    }

    @Override
    @AddTrace(name = "onCreateEbookFragment", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ebook, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonMethod.setAnalyticsData(getContext(), "MainTab", "Ebook", null);
        Log.d("TAG", " pdf ebook" + savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TAG", " pdf ebook 1");
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", " pdf ebook 2 ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", " pdf ebook 3 ");
    }

    public static EBookFragment newInstance() {
        return new EBookFragment();
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
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