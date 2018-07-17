package in.tts.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class BrowserActivity extends AppCompatActivity {

    ProgressBar superProgressBar;
    WebView superWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

            case R.id.menuReload:
                superWebView.reload();

                break;

        }
        return true;
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
