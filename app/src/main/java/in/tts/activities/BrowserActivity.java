package in.tts.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

import static java.security.AccessController.getContext;

public class BrowserActivity extends AppCompatActivity {

    private ProgressBar superProgressBar;
    private WebView superWebView;
    private RelativeLayout rl;
    private PrefManager prefManager;
    private List<String> linkList;
    private View menuBookMark;
    private CheckBox cbMenu;
    String historyUrl = "";


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            checkInternetConnection();
            setContentView(R.layout.activity_browser);

            prefManager = new PrefManager(BrowserActivity.this);

            if (getIntent() != null) {
                superProgressBar = findViewById(R.id.myProgressBar);
                superWebView = findViewById(R.id.myWebView);
                rl = findViewById(R.id.llBrowser);

                superProgressBar.setMax(100);

                if (getIntent().getStringExtra("Data") != null) {
                    superWebView.loadUrl("https://www.google.co.in/search?q="
                            + getIntent().getStringExtra("Data")
                            + "&oq=df&aqs=chrome..69i57j69i60l3j0l2.878j0j7&sourceid=chrome&ie=UTF-8");
                } else if (getIntent().getStringExtra("url") != null) {
                    superWebView.loadUrl(getIntent().getStringExtra("url"));
                } else {
                    superWebView.loadUrl("https://www.google.co.in");
                }
                superWebView.getSettings().setJavaScriptEnabled(true);
                superWebView.getSettings().setSupportZoom(true);
                superWebView.getSettings().setBuiltInZoomControls(true);
                superWebView.getSettings().setDisplayZoomControls(true);
                superWebView.getSettings().setLoadWithOverviewMode(true);
                superWebView.getSettings().setUseWideViewPort(true);
                superWebView.clearCache(true);
//                superWebView.clearHistory();
                superWebView.setHorizontalScrollBarEnabled(true);
            }

            superWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    startOfHistory();
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
                        linkList = prefManager.populateSelectedSearch();
                        toUpdateBookMarkIcon();
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
//                    ImageView iv = new ImageView(BrowserActivity.this);
//                    iv.setImageBitmap(icon);
//                    rl.addView(iv);

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

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void checkInternetConnection() {
        try {
            if (CommonMethod.isOnline(BrowserActivity.this)) {
                new BrowserActivity();
            } else {
                CommonMethod.toDisplayToast(BrowserActivity.this,getResources().getString(R.string.lbl_no_check_internet));
            }
        } catch (Exception | Error e) {
            e.printStackTrace(); Crashlytics.logException(e);

        }
    }

    public boolean startOfHistory() {
        try {
            Log.d("TAGWEB", "WEB Histroy");
            WebBackForwardList currentList = superWebView.copyBackForwardList();
            Log.d("TAGWEB", " Web " + currentList.getCurrentItem() + ":" + currentList.getSize() + ":" + currentList.getCurrentItem());
            for (int i = 0; i < currentList.getSize(); i++) {
                WebHistoryItem item = currentList.getItemAtIndex(i);
                Log.d("TAGWEB", " web item " + item.getTitle());
                if (item != null) { // Null-fence in case they haven't called loadUrl yet (CB-2458)
                    String url = item.getUrl();
                    String currentUrl = superWebView.getUrl();
                    Log.d("TAGWEB", i + ". The current URL is: " + currentUrl);
                    Log.d("TAGWEB", i + ". The URL at item 0 is:" + url);
//                    return currentUrl.equals(url);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return false;
    }

    private void toUpdateBookMarkIcon() {
        try {
            if (linkList != null) {
                if (linkList.contains(superWebView.getUrl())) {
                    cbMenu.setChecked(true);
                } else {
                    cbMenu.setChecked(false);
                }
            } else {
                linkList = new ArrayList<>();
                cbMenu.setChecked(false);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater myMenuInflater = getMenuInflater();
        myMenuInflater.inflate(R.menu.browser_menu, menu);

        menuBookMark = menu.findItem(R.id.menuBookmark).getActionView();
        cbMenu = menuBookMark.findViewById(R.id.cbBookmark);
        cbMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbMenu.isChecked()) {
                    toChangeData(true);
                } else {
                    toChangeData(false);
                }
            }
        });

        linkList = prefManager.populateSelectedSearch();
        if (linkList != null) {
            if (linkList.contains(superWebView.getUrl())) {
                menu.getItem(0).setIcon(R.drawable.ic_bookmark_black_24dp);
            } else {
                menu.getItem(0).setIcon(R.drawable.ic_bookmark_border_black_24dp);
            }
        } else {
            linkList = new ArrayList<>();
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

            case R.id.menuBookmarksList:
                startActivity(new Intent(BrowserActivity.this, BookmarkActivity.class));
                break;

            case R.id.menuReload:
                superWebView.reload();
                break;

            case R.id.menuBookmark:
                toChangeData(true);
                break;

        }
        return true;
    }

    private void toChangeData(boolean b) {
        try {
            String message;
            if (!b) {
                linkList.remove(superWebView.getUrl().trim().replaceAll("\\s+", "%20"));
                message = "Bookmark Removed";
            } else {
                linkList.add(superWebView.getUrl().trim().replaceAll("\\s+", "%20"));
                message = "Bookmarked";
            }
            prefManager.setSearchResult(linkList);
            Snackbar snackbar = Snackbar.make(rl, message, Snackbar.LENGTH_LONG);
            snackbar.show();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
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

/*
*  @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

        if(url.toLowerCase().contains("/favicon.ico")) {
            try {
                return new WebResourceResponse("image/png", null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    @SuppressLint("NewApi")
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        if(!request.isForMainFrame() && request.getUrl().getPath().endsWith("/favicon.ico")) {
            try {
                return new WebResourceResponse("image/png", null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    */