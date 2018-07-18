package in.tts.activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class BrowserActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    ProgressBar superProgressBar;
    WebView superWebView;

    String current_page_url ;

        // Bookmark
    public static final String PREFERENCES = "PREFERENCES_NAME";
    public static final String WEB_LINKS = "links";
    public static final String WEB_TITLE = "title";


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent() != null) {
            Intent i = getIntent();
            String URL_DATA = i.getStringExtra("Data");
            CommonMethod.toDisplayToast(BrowserActivity.this, "ToDATa " + URL_DATA);


            superProgressBar = findViewById(R.id.myProgressBar);
            superWebView = findViewById(R.id.myWebView);

            superProgressBar.setMax(100);

            superWebView.loadUrl("https://www.google.co.in/search?q=" + URL_DATA + "&oq=df&aqs=chrome..69i57j69i60l3j0l2.878j0j7&sourceid=chrome&ie=UTF-8");
            superWebView.getSettings().setJavaScriptEnabled(true);
            superWebView.getSettings().setSupportZoom(true);
            superWebView.getSettings().setBuiltInZoomControls(true);
            superWebView.getSettings().setDisplayZoomControls(true);
            superWebView.getSettings().setLoadWithOverviewMode(true);
            superWebView.getSettings().setUseWideViewPort(true);
            superWebView.clearCache(true);
            superWebView.clearHistory();
            superWebView.setHorizontalScrollBarEnabled(true);
        }

        Log.d("TAG","URL" + superWebView.getUrl());
        superWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        superWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                superProgressBar.setVisibility(View.VISIBLE);
                superProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    superProgressBar.setVisibility(View.GONE);

                } else {
                    superProgressBar.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
//                superImageView.setImageBitmap(icon);

            }
        });

        superWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri myUri = Uri.parse(url);
                Intent superIntent = new Intent(Intent.ACTION_VIEW);
                superIntent.setData(myUri);
                startActivity(superIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenuInflater = getMenuInflater();
        myMenuInflater.inflate(R.menu.browser, menu);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        String links = sharedPreferences.getString(WEB_LINKS, null);

        if (links != null) {

            Gson gson = new Gson();
            ArrayList<String> linkList = gson.fromJson(links, new TypeToken<ArrayList<String>>() {
            }.getType());

            if (linkList.contains(superWebView.getUrl())) {
                menu.getItem(0).setIcon(R.drawable.ic_bookmark_black_24dp);
            } else {
                menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_black_24dp);
            }
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_black_24dp);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuBack:
                onBackPressed();
                break;

            case R.id.menuForward:
                GoForward();
                break;

            case R.id.bookmarks:
                startActivity(new Intent(BrowserActivity.this, BookmarkActivity.class));
                break;

            case R.id.menuReload:
                superWebView.reload();

            case R.id.menuBookmark:
                current_page_url = superWebView.getUrl();
               toChangeData();

                break;

        }
        return true;
    }

    private void toChangeData() {
        try {
            String message;

            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            String jsonLink = sharedPreferences.getString(WEB_LINKS, null);
            String jsonTitle = sharedPreferences.getString(WEB_TITLE, null);


            if (jsonLink != null && jsonTitle != null) {

                Gson gson = new Gson();
                ArrayList<String> linkList = gson.fromJson(jsonLink, new TypeToken<ArrayList<String>>() {
                }.getType());

                ArrayList<String> titleList = gson.fromJson(jsonTitle, new TypeToken<ArrayList<String>>() {
                }.getType());

                if (linkList.contains(current_page_url)) {
                    linkList.remove(current_page_url);
                    titleList.remove(superWebView.getTitle().trim());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                    editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                    editor.apply();


                    message = "Bookmark Removed";

                } else {
                    linkList.add(current_page_url);
                    titleList.add(superWebView.getTitle().trim());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                    editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                    editor.apply();

                    message = "Bookmarked";
                }
            } else {

                ArrayList<String> linkList = new ArrayList<>();
                ArrayList<String> titleList = new ArrayList<>();
                linkList.add(current_page_url);
                titleList.add(superWebView.getTitle());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(WEB_LINKS, new Gson().toJson(linkList));
                editor.putString(WEB_TITLE, new Gson().toJson(titleList));
                editor.apply();

                message = "Bookmarked";
            }

            Snackbar snackbar = Snackbar.make(linearLayout, message, Snackbar.LENGTH_LONG);
            snackbar.show();
        } catch (Exception| Error e){
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void GoForward() {
        if (superWebView.canGoForward()) {
            superWebView.goForward();
        } else {
            Toast.makeText(this, "Can't go further!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (superWebView.canGoBack()) {
            superWebView.goBack();
        } else {
            finish();
        }
    }
}
