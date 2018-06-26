package in.tts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;



public class BrowserFragment extends Fragment {


    public BrowserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browser, container, false);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WebView webView = getActivity().findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("http://www.google.com");

        TextView tv = getActivity().findViewById(R.id.tvurl);
        tv.setText(webView.getUrl());
        Log.d("Tag", " f " +webView.getProgress());
    }
}