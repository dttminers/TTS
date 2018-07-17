package in.tts.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.activities.BrowserActivity;
import in.tts.activities.MainActivity;
import in.tts.utils.CommonMethod;

public class BrowserFragment extends Fragment {
    private EditText editText;
    private Button button;

    public BrowserFragment() {
    }

    @Override
    @AddTrace(name = "onCreateBrowserFragment", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browser, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonMethod.setAnalyticsData(getContext(), "MainTab", "Browser", null);
        editText = getActivity().findViewById(R.id.edtBrowser);
        button = getActivity().findViewById(R.id.btnSearch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText != null && editText.getText().toString().trim().length() != 0) {
                    startActivity(
                            new Intent(getContext(), BrowserActivity.class)
                                    .putExtra("Data", editText.getText().toString().trim()));
                    CommonMethod.toDisplayToast(getContext(), "To " + editText.getText().toString().trim());
                } else {
                    CommonMethod.toDisplayToast(getContext(), "To Data Found");
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }

    private OnFragmentInteractionListener mListener;

    public static BrowserFragment newInstance() {
        return new BrowserFragment();
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