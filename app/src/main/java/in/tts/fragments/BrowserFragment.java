package in.tts.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.activities.BrowserActivity;
import in.tts.model.Browser;
import in.tts.utils.CommonMethod;
import in.tts.utils.TouchImageView;

public class BrowserFragment extends Fragment {
    private EditText editText;
    private Button button;
    private ImageView ivBookmark1, ivBookmark2, ivBookmark3, ivRecent1,ivRecent2, ivRecent3, ivRecent4, ivRecent5, ivRecent6;

    public BrowserFragment() {
    }


    @Override
    @AddTrace(name = "onCreateBrowserFragment", enabled = true)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_browser, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonMethod.setAnalyticsData(getContext(), "MainTab", "Browser", null);
        editText = getActivity().findViewById(R.id.edtBrowser);
        button = getActivity().findViewById(R.id.btnSearch);
            // Bookmarks.........
        ivBookmark1 = getActivity().findViewById(R.id.wikipedia);
        ivBookmark2 = getActivity().findViewById(R.id.ivAmazone);
        ivBookmark3 = getActivity().findViewById(R.id.ivXerox);
        // Recent Tabs....

        ivRecent1 = getActivity().findViewById(R.id.ivRecent1);
        ivRecent2 = getActivity().findViewById(R.id.ivRecent2);
        ivRecent3 = getActivity().findViewById(R.id.ivRecent3);
        ivRecent4 = getActivity().findViewById(R.id.ivRecent4);
        ivRecent5 = getActivity().findViewById(R.id.ivRecent5);
        ivRecent6 = getActivity().findViewById(R.id.ivRecent6);

        // Bookmark page...................
        ivBookmark1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(getContext(), BrowserActivity.class)
                                .putExtra("url", "https://www.wikipedia.org/"));
            }
        });

        ivBookmark2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(getContext(), BrowserActivity.class)
                                .putExtra("url", "https://www.amazon.in"));
            }
        });

        ivBookmark3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(getContext(), BrowserActivity.class)
                                .putExtra("url", "https://www.xerox.com/"));
            }
        });

        // Recent page ......

        ivRecent3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(getContext(), BrowserActivity.class)
                                .putExtra("url", "https://www.wikipedia.org/"));
            }
        });

        ivRecent4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(getContext(), BrowserActivity.class)
                                .putExtra("url", "https://www.wikipedia.org/"));
            }
        });

        ivRecent5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(getContext(), BrowserActivity.class)
                                .putExtra("url", "https://www.wikipedia.org/"));
            }
        });

        ivRecent6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(getContext(), BrowserActivity.class)
                                .putExtra("url", "https://www.wikipedia.org/"));
            }
        });


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
        try {
            CommonMethod.setAnalyticsData(getContext(), "MainTab", "Browser", null);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
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
        try {
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            }
        } catch (Exception | Error e) {
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
    }
}