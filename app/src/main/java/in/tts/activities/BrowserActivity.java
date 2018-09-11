package in.tts.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.classes.ScreenshotTask;
import in.tts.classes.TTS;
import in.tts.model.BookmarkModel;
import in.tts.model.PrefManager;
import in.tts.utils.BrowserUnit;
import in.tts.utils.CommonMethod;
import in.tts.utils.HelperUnit;
import in.tts.utils.slidinguppanel.SlidingUpPanelLayout;

public class BrowserActivity extends AppCompatActivity {

    //PARENT
    private SlidingUpPanelLayout mLayout;

    //MAIN CONTENT
    private RelativeLayout rl;

    private ProgressBar superProgressBar;
    private WebView superWebView;

    private RelativeLayout rlNoInternet;
    private TextView tvNoInternet;

    private RelativeLayout rlPb;

    // SLIDING LAYOUT
    private LinearLayout dragView;
    // Level 1
    private LinearLayout llLevel1;
    private ImageView ivPrevious, ivForward, ivMore, ivHome;
    // Level 2
    private LinearLayout llLevel2;
    private ImageView ivBookmark, ivHistory, ivSpeak, ivReload;

    private PrefManager prefManager;

    private List<BookmarkModel> list;
    private View menuBookMark;
    private CheckBox cbMenu;

    private TTS tts;

    private String historyUrl = "";
    private String webTitle = "";

    private int currentPosition = 0, setBackPosition = 0;

    private StringBuilder stringBuilder;

    private SparseArray<TextBlock> items;
    private Frame imageFrame;
    private TextRecognizer textRecognizer;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_browser);

            CommonMethod.setAnalyticsData(BrowserActivity.this, "Activity", "BrowserActivity", null);

            prefManager = new PrefManager(BrowserActivity.this);
            PrefManager.ActivityCount = +1;

            toBindViews();

            toSetListener();

            checkInternetConnection();

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), BrowserActivity.this, getString(R.string.app_name));
            }

            toSetUrl();

            toSetWebView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                superWebView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                        currentPosition = i1;
                    }
                });
            }
            totoDisplayBottomMessage(true, getString(R.string.str_read_specific_selected_text));
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toBindViews() {
        try {
            mLayout = findViewById(R.id.sliding_layout);

            rl = findViewById(R.id.rlBrowser);

            superProgressBar = findViewById(R.id.myProgressBar);
            superWebView = findViewById(R.id.myWebView);

            rlNoInternet = findViewById(R.id.rlNoInternetConnection);
            tvNoInternet = findViewById(R.id.tvNoInternetConnection);

            rlPb = findViewById(R.id.rlPb);

            dragView = findViewById(R.id.dragView);

            llLevel1 = findViewById(R.id.ll_bottom_sheet_head1);

            ivPrevious = findViewById(R.id.ivPrevious);
            ivForward = findViewById(R.id.ivForward);
            ivMore = findViewById(R.id.ivMore);
            ivHome = findViewById(R.id.ivHome);

            llLevel2 = findViewById(R.id.ll_bottom_sheet_content);

            ivBookmark = findViewById(R.id.ivBookmark);
            ivHistory = findViewById(R.id.ivHistory);
            ivSpeak = findViewById(R.id.ivBrowserSpeak);
            ivReload = findViewById(R.id.ivBrowserReload);

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetListener() {
        try {
            ivPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ScreenshotTask(BrowserActivity.this, superWebView);
//                    onBackPressed();
                    toCloseBottomOptions();
                }
            });


            ivForward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoForward();
                    toCloseBottomOptions();
                }
            });

            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                        toCloseBottomOptions();
                    } else {
                        toOpenBottomOptions();
                    }
                }
            });

            ivReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (superWebView != null) {
                        superWebView.reload();
                        toCloseBottomOptions();
                    }
                }
            });

            ivHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toCloseBottomOptions();
                    toExit();
                }
            });

            ivBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toCloseBottomOptions();
                    startActivity(new Intent(BrowserActivity.this, BookmarkActivity.class));
                }
            });

            ivHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toCloseBottomOptions();
                    startActivity(new Intent(BrowserActivity.this, BookmarkActivity.class));
                }
            });
            ivSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        toCloseBottomOptions();
                        setBackPosition = currentPosition;
                        toGetTextFromCurrentScreen();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });
            mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View panel, float slideOffset) {
                    Log.d("TAG_WEB", "onPanelSlide, offset " + slideOffset);
                }

                @Override
                public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                    Log.d("TAG_WEB", "onPanelStateChanged " + newState);
                }
            });
            mLayout.setFadeOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                        toCloseBottomOptions();
                    } else {
                        toOpenBottomOptions();
                    }
                }
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toOpenBottomOptions() {
        try {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            ivMore.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_down_black_24dp));
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toCloseBottomOptions() {
        try {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            ivMore.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_drop_up_black_24dp));
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toExit() {
        try {
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (PrefManager.ActivityCount <= 1) {
                        if (PrefManager.CurrentPage != 0) {
                            startActivity(new Intent(BrowserActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        } else {
                            finish();
                        }
                    } else {
                        finish();
                    }
                }
            }, 500);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void totoDisplayBottomMessage(boolean status, final String message) {
        try {
            if (status) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(rl, message, Snackbar.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                            Crashlytics.logException(e);
                            FirebaseCrash.report(e);
                        }
                    }
                }, 1000);
            } else {
                Snackbar.make(rl, message, Snackbar.LENGTH_LONG).show();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetUrl() {
        try {
            if (getIntent() != null) {
                superProgressBar.setMax(100);
                if (getIntent().getStringExtra("Data") != null) {
                    superWebView.loadUrl("https://www.google.co.in/search?q="
                            + getIntent().getStringExtra("Data")
                            + "&oq=df&aqs=chrome..69i57j69i60l3j0l2.878j0j7&sourceid=chrome&ie=UTF-8");
                } else if (getIntent().getStringExtra("url") != null) {
                    superWebView.loadUrl(getIntent().getStringExtra("url"));
                } else if (getIntent().getData() != null) {
                    superWebView.loadUrl("" + getIntent().getData());
                } else {
                    superWebView.loadUrl("https://www.google.co.in");
                }
            } else {
                superWebView.loadUrl("https://www.google.co.in");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetWebView() {
        try {
            superWebView.setLongClickable(false);

            superWebView.setOnLongClickListener(null);
            superWebView.setHapticFeedbackEnabled(false);

            superWebView.getSettings().setJavaScriptEnabled(true);
            superWebView.getSettings().setSupportZoom(true);
            superWebView.getSettings().setBuiltInZoomControls(true);
            superWebView.getSettings().setDisplayZoomControls(true);
            superWebView.getSettings().setLoadWithOverviewMode(true);
            superWebView.getSettings().setUseWideViewPort(true);
            superWebView.getSettings().setDomStorageEnabled(true);
            superWebView.getSettings().setBlockNetworkImage(false);

            superWebView.clearCache(true);
            superWebView.setHorizontalScrollBarEnabled(true);

            superWebView.setWebViewClient(webViewClient());
            superWebView.setWebChromeClient(webChromeClient());

            superWebView.setDownloadListener(downloadListener());

//            // For Fetching Text
//            superWebView.evaluateJavascript("(function(){return window.document.body.outerHTML})();",
//                    new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String html) {
//                            Log.d("TAG", " onReceiveValue " + html);
//
//                        }
//                    });

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private DownloadListener downloadListener() {
        return new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                try {
                    Log.d("TAG_WEB ", " onDownloadStart : " + url + "\n :" + userAgent + "\n: " + contentDisposition + "\n : " + mimeType + "\n :" + contentLength);

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    request.setMimeType(mimeType);
                    //------------------------COOKIE!!------------------------
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    //------------------------COOKIE!!------------------------
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading file...");
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    CommonMethod.toDisplayToast(getApplicationContext(), "Downloading File");


                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                }
            }
        };
    }

    private WebChromeClient webChromeClient() {
        return new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                try {
                    try {
                        if (tts != null) {
                            tts.toStop();
                            tts.toShutDown();
                        }
                    } catch (Exception | Error e) {
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    }
                    superProgressBar.setVisibility(View.VISIBLE);
                    superProgressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        superProgressBar.setVisibility(View.GONE);
                        list = prefManager.loadList();
                    } else {
                        superProgressBar.setVisibility(View.VISIBLE);
                    }
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                try {
                    webTitle = title;
                    if (getSupportActionBar() != null) {
                        CommonMethod.toSetTitle(getSupportActionBar(), BrowserActivity.this, title);
                    }
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                }
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
        };
    }

    private WebViewClient webViewClient() {
        return new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, final String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try {
                    if (cbMenu != null) {
                        cbMenu.setChecked(false);
                    }
                    if (tts != null) {
                        if (tts.isSpeaking()) {
                            tts.toStop();
                            tts.toShutDown();
                        }
                    }
                    stringBuilder = new StringBuilder();

                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                }
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);
                try {
//                        if (menuSpeak != null) {
//                            menuSpeak.setVisible(true);
//                        }
                    toUpdateBookMarkIcon();
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                }
            }

            @SuppressLint("NewApi")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("TAG_WEB ", " shouldOverrideUrlLoading :1 " + url);
//                    if (url.contains(".jpg")) {
//
//                        DownloadManager mdDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                        DownloadManager.Request request = new DownloadManager.Request(
//                                Uri.parse(url));
//                        File destinationFile = new File(
//                                Environment.getExternalStorageDirectory(),
//                                getFileName(url));
//                        Log.d("TAG_WEB", " File Name : " + destinationFile.getName());
//                        request.setDescription("Downloading ...");
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                        request.setDestinationUri(Uri.fromFile(destinationFile));
//                        mdDownloadManager.enqueue(request);
//
//
//                        return true;
//                    } else {
//                        Log.d("TAG_WEB ", " shouldOverrideUrlLoading :2 " + url);
//                    }
                return super.shouldOverrideUrlLoading(view, url);
            }

//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                    return super.shouldOverrideUrlLoading(view, request);
//                }
        };
    }


    public String getFileName(String url) {
        String filenameWithoutExtension = "";
        filenameWithoutExtension = String.valueOf(System.currentTimeMillis()
                + ".jpg");
        return filenameWithoutExtension;
    }

    @Override
    public void onActionModeStarted(final android.view.ActionMode mode) {
        try {
            //Log.d("TAG_WEB", "onActionModeStarted :  " + mode.isTitleOptional());
            mode.getMenu().clear();
            Menu menus = mode.getMenu();
            mode.getMenuInflater().inflate(R.menu.highlight, menus);
            MenuItem menu1 = menus.findItem(R.id.menu_read);
            menu1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    try {
                        if (superWebView != null) {
                            superWebView.evaluateJavascript("window.getSelection().toString()",
                                    new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            try {
                                                //Log.d("TAG_WEB", "SelectedText:" + value);
                                                if (value != null) {
                                                    if (value.trim().length() > 0) {
                                                        toSpeakFromWebPage(value);
                                                    }
                                                }
                                            } catch (Exception | Error e) {
                                                e.printStackTrace();
                                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                                Crashlytics.logException(e);
                                                FirebaseCrash.report(e);
                                            }
                                        }
                                    });
                        }
                        mode.finish();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }

                    return false;
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }

        super.onActionModeStarted(mode);
    }


    private void checkInternetConnection() {
        try {
            if (CommonMethod.isOnline(BrowserActivity.this)) {
                new BrowserActivity();
            } else {

                CommonMethod.toDisplayToast(BrowserActivity.this, getResources().getString(R.string.lbl_no_check_internet));
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toUpdateBookMarkIcon() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
//                                    //Log.d("TAG_List ", " toUpdateBookMarkIcon : " + list.get(i).getPageUrl() + ":" + (list.get(i).getPageUrl().equals(superWebView.getUrl().trim().replaceAll("\\s+", "%20"))));
                                    if (list.get(i).getPageUrl().equals(superWebView.getUrl().trim().replaceAll("\\s+", "%20"))) {
                                        cbMenu.setChecked(true);
                                    } else {
                                        cbMenu.setChecked(false);
                                    }
                                }
                            } else {
                                list = new ArrayList<>();
                                cbMenu.setChecked(false);
                            }
                        }
                    });
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    Crashlytics.logException(e);
                    FirebaseCrash.report(e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.browser_menu, menu);
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
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            tts = new TTS(BrowserActivity.this);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.menuBookmark:
                    toChangeData(true);
                    break;

                case android.R.id.home:
                    onBackPressed();
                    break;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return true;
    }

    private void toGetTextFromCurrentScreen() {
        try {
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            toGetTextFromBitmap(bitmap);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toGetTextFromBitmap(Bitmap bitmap) {
        try {
//            //Log.d("TAG_WEB", " toGetTextFromBitmap " + bitmap.getHeight() + ":" + bitmap.isRecycled() + ":" + currentPosition + ":" + setBackPosition);
            rlPb.setVisibility(View.VISIBLE);

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), BrowserActivity.this, getString(R.string.str_fetching_data));
            }

//            try {
//                Date now = new Date();
//                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
//                File dirPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/READ_IT/IMAGE/");
//                if (!dirPath.exists()) {
//                    dirPath.mkdirs();
//                }
//                File imageFile = new File(dirPath.getAbsolutePath() + File.separator + "aksh_" + now + ".jpg");
//                //Log.d("TAG_WEB", " getpath " + imageFile.getAbsolutePath());
//                FileOutputStream fos = new FileOutputStream(imageFile);
//                if (fos != null) {
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                    fos.flush();
//                    fos.close();
//                }
//            } catch (Exception | Error e) {
//                e.printStackTrace();
//                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//                Crashlytics.logException(e);
//                FirebaseCrash.report(e);
//            }

            textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

            imageFrame = new Frame.Builder().setBitmap(bitmap).build();

            items = textRecognizer.detect(imageFrame);

            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
            //Log.d("TAG_WEB", " data10 " + stringBuilder.toString().length() + ":" + stringBuilder.toString());


            superWebView.post(new Runnable() {
                public void run() {
                    //Log.d("TAG_WEB", " data11 " + superWebView.getContentHeight() + ":" + superWebView.getScale() + ":" + superWebView.getScrollY());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (superWebView.getContentHeight() * superWebView.getScale() >= superWebView.getScrollY()) {
                                //Log.d("TAG_WEB", " data12 " + (int) superWebView.getHeight());

                                superWebView.scrollBy(0, (int) superWebView.getHeight());
                                toGetTextFromCurrentScreen();

                            } else {
                                //Log.d("TAG_WEB", " data13 " + currentPosition + ":" + setBackPosition);
                                superWebView.scrollTo(0, setBackPosition);
                                if (getSupportActionBar() != null) {
                                    CommonMethod.toSetTitle(getSupportActionBar(), BrowserActivity.this, webTitle);
                                }

                                rlPb.setVisibility(View.GONE);
                                toSpeakFromWebPage(stringBuilder.toString());
                            }

                        }
                    }, 100);
                }
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
            rlPb.setVisibility(View.GONE);
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    private void toSpeakFromWebPage(String text) {
        try {
            CommonMethod.toCloseLoader();
            //Log.d("WEB", "toSpeakWebPage  " + stringBuilder.toString().length() + ":" + stringBuilder.toString());
            if (text.trim().length() > 0) {
//                tts = new TTS(BrowserActivity.this);
                if (tts != null) {
                    if (tts.isSpeaking()) {
                        tts.toStop();
                        tts.toShutDown();
                    }
                }
//                Speaker speaker = new Speaker(getApplication());
//                speaker.play(text);
//                tts = new TTS(BrowserActivity.this);
                tts.SpeakLoud(text.replaceAll("Fetching data...", "\\s"));//, "AUD_Web" + superWebView.getTitle() + System.currentTimeMillis());
                CommonMethod.toDisplayToast(BrowserActivity.this, "Sound will play...");
//                tts.toSaveAudioFile(text.replaceAll("&nbsp;", "\\s"), "AUD_Web" + superWebView.getTitle() + System.currentTimeMillis());
            } else {
                CommonMethod.toDisplayToast(BrowserActivity.this, " Unable to fetch data ");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
        }
    }

    private void toChangeData(boolean b) {
        try {
            if (!b) {
                list.remove(new BookmarkModel(superWebView.getTitle(), superWebView.getUrl().trim().replaceAll("\\s+", "%20"), CommonMethod.BitmapToString(superWebView.getFavicon())));
                totoDisplayBottomMessage(false, "Bookmark Removed");
            } else {
                list.add(new BookmarkModel(superWebView.getTitle(), superWebView.getUrl().trim().replaceAll("\\s+", "%20"), CommonMethod.BitmapToString(superWebView.getFavicon())));
                totoDisplayBottomMessage(false, "Bookmarked");
            }
            prefManager.saveList(list);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void GoForward() {
        try {
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }
            if (superWebView.canGoForward()) {
                superWebView.goForward();
            } else {
                CommonMethod.toDisplayToast(BrowserActivity.this, "Can't go further!");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }

            if (superWebView.canGoBack()) {
                superWebView.goBack();
            } else if (PrefManager.ActivityCount <= 1) {
                if (PrefManager.CurrentPage != 0) {
                    startActivity(new Intent(BrowserActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    finish();
                }
            } else {
                finish();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onPause() {
        try {
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        super.onDestroy();
    }
}


/*


//            menuSpeak = menu.findItem(R.id.menuSpeakBrowser);
//                case R.id.menuSpeakBrowser:
//                    try {
//                        setBackPosition = currentPosition;
//                        toGetTextFromCurrentScreen();
//                    } catch (Exception | Error e) {
//                        e.printStackTrace();
//                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
//                        Crashlytics.logException(e);
//                        FirebaseCrash.report(e);
//                    }
//                    break;

//                case R.id.menuBack:
//                    onBackPressed();
//                    break;
//
//                case R.id.menuForward:
//                    GoForward();
//                    break;
//
//                case R.id.menuBookmarksList:
//                    startActivity(new Intent(BrowserActivity.this, BookmarkActivity.class));
//                    break;
//
//                case R.id.menuReload:
//                    superWebView.reload();
//                    break;



////                        Uri myUri = Uri.parse(url);
////                        Intent superIntent = new Intent(Intent.ACTION_VIEW);
////                        superIntent.setData(myUri);
////                        startActivity(superIntent);
//                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//                        request.allowScanningByMediaScanner();
//
//                        request.setNotificationVisibility(
//                                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//
////                        request.setDestinationInExternalPublicDir(
////                                Environment.DIRECTORY_DOWNLOADS,    //Download folder
////                                "download");                        //Name of file
//
//
//                        DownloadManager dm = (DownloadManager) getSystemService(
//                                DOWNLOAD_SERVICE);
//
//                        dm.enqueue(request);
        webView.setDownloadListener(new DownloadListener()
        {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
            {
                //for downloading directly through download manager
                Request request = new Request(Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "download");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
            }
        });

        In the above code, if you don’t have sdcard on emulator/device, then you can use internal storage to store a file, by commenting this line.

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "download");

        2) Downloading a file using a web browser opened from android webview
        This will send an intent to web browser and web browser will take care of downloading the file. It will internally use download manager to download a file.

        webView.setDownloadListener(new DownloadListener()
        {
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
            {
                //download file using web browser
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        */
//            superWebView.setDownloadListener(new DownloadListener() {
//
//                @Override
//                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                    DownloadManager.Request request = new DownloadManager.Request(
//                            Uri.parse(url));
//
//                    request.allowScanningByMediaScanner();
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Name of your downloadble file goes here, example: Mathematics II ");
//                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                    dm.enqueue(request);
//                    Toast.makeText(getApplicationContext(), "Downloading File", //To notify the Client that the file is being downloaded
//                            Toast.LENGTH_LONG).show();
//
//                }
//            });
//
//    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
