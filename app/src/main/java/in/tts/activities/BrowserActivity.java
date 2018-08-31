package in.tts.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.classes.TTS;
import in.tts.model.BookmarkModel;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class BrowserActivity extends AppCompatActivity {

    private ProgressBar superProgressBar;
    private WebView superWebView;
    private RelativeLayout rl, rlPb;
    private PrefManager prefManager;
    private List<BookmarkModel> list;
    private View menuBookMark;
    private CheckBox cbMenu;
    private TTS tts;
    private String historyUrl = "";
    private String webTitle = "";
    private MenuItem menuSpeak;
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
            PrefManager.ActivityCount = +1;
            checkInternetConnection();
            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), BrowserActivity.this, getString(R.string.app_name));
            }

            prefManager = new PrefManager(BrowserActivity.this);
            if (getIntent() != null) {
                superProgressBar = findViewById(R.id.myProgressBar);
                superWebView = findViewById(R.id.myWebView);
                rl = findViewById(R.id.llBrowser);
                rlPb = findViewById(R.id.rlPb);

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

                superWebView.setLongClickable(false);
                superWebView.setOnLongClickListener(null);
                superWebView.setHapticFeedbackEnabled(false);
                superWebView.getSettings().setJavaScriptEnabled(true);
                superWebView.getSettings().setSupportZoom(true);
                superWebView.getSettings().setBuiltInZoomControls(true);
                superWebView.getSettings().setDisplayZoomControls(true);
                superWebView.getSettings().setLoadWithOverviewMode(true);
                superWebView.getSettings().setUseWideViewPort(true);
                superWebView.clearCache(true);
                superWebView.setHorizontalScrollBarEnabled(true);
            }

            superWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageStarted(WebView view, final String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    try {
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
                        if (menuSpeak != null) {
                            menuSpeak.setVisible(true);
                        }
                        toUpdateBookMarkIcon();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                superWebView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                        currentPosition = i1;
//                        Log.d("TAG_WEB", "onScrollChange " + i + ":" + i1 + ":" + i2 + ":" + i3 + ":" + view.getHeight() + ":" + view.getWidth() + currentPosition);
                    }
                });
            }

            superWebView.setWebChromeClient(new WebChromeClient() {

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
            });

            superWebView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    try {
                        Log.d("TAG_WEB ", " onDownloadStart : " + url + "\n :" + userAgent + "\n: " + contentDisposition + "\n : " + mimetype + "\n :" + contentLength);
                        Uri myUri = Uri.parse(url);
                        Intent superIntent = new Intent(Intent.ACTION_VIEW);
                        superIntent.setData(myUri);
                        startActivity(superIntent);
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

            // Get clipboard manager object.
            Object clipboardService = getSystemService(CLIPBOARD_SERVICE);
            final ClipboardManager clipboardManager = (ClipboardManager) clipboardService;
            superWebView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ClipData clipData = ClipData.newPlainText("", "");
                        clipboardManager.setPrimaryClip(clipData);
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
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

    @Override
    public void onActionModeStarted(final android.view.ActionMode mode) {
        try {
            Log.d("TAG_WEB", "onActionModeStarted :  " + mode.isTitleOptional());
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
                                                Log.d("TAG_WEB", "SelectedText:" + value);
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
//                                    Log.d("TAG_List ", " toUpdateBookMarkIcon : " + list.get(i).getPageUrl() + ":" + (list.get(i).getPageUrl().equals(superWebView.getUrl().trim().replaceAll("\\s+", "%20"))));
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
            menuSpeak = menu.findItem(R.id.menuSpeakBrowser);
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
                case R.id.menuSpeakBrowser:
                    try {
                        setBackPosition = currentPosition;
                        toGetTextFromCurrentScreen();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                    break;

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
//            Log.d("TAG_WEB", " toGetTextFromBitmap " + bitmap.getHeight() + ":" + bitmap.isRecycled() + ":" + currentPosition + ":" + setBackPosition);
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
//                Log.d("TAG_WEB", " getpath " + imageFile.getAbsolutePath());
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
//            Log.d("TAG_WEB", " data10 " + stringBuilder.toString().length() + ":" + stringBuilder.toString());


            superWebView.post(new Runnable() {
                public void run() {
//                    Log.d("TAG_WEB", " data11 " + superWebView.getContentHeight() + ":" + superWebView.getScale() + ":" + superWebView.getScrollY());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (superWebView.getContentHeight() * superWebView.getScale() >= superWebView.getScrollY()) {
//                                Log.d("TAG_WEB", " data12 " + (int) superWebView.getHeight());

                                superWebView.scrollBy(0, (int) superWebView.getHeight());
                                toGetTextFromCurrentScreen();

                            } else {
//                                Log.d("TAG_WEB", " data13 " + currentPosition + ":" + setBackPosition);
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
            Log.d("WEB", "toSpeakWebPage  " + stringBuilder.toString().length() + ":" + stringBuilder.toString());
            if (text.trim().length() > 0) {
//                tts = new TTS(BrowserActivity.this);
                if (tts != null) {
                    if (tts.isSpeaking()) {
                        tts.toStop();
                        tts.toShutDown();
                    }
                }
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
            String message;
            if (!b) {
                list.remove(new BookmarkModel(superWebView.getTitle(), superWebView.getUrl().trim().replaceAll("\\s+", "%20"), CommonMethod.BitmapToString(superWebView.getFavicon())));
                message = "Bookmark Removed";
            } else {
                list.add(new BookmarkModel(superWebView.getTitle(), superWebView.getUrl().trim().replaceAll("\\s+", "%20"), CommonMethod.BitmapToString(superWebView.getFavicon())));
                message = "Bookmarked";
            }
            prefManager.saveList(list);
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
        try {
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }
            if (superWebView.canGoForward()) {
                superWebView.goForward();
            } else {
                Toast.makeText(this, "Can't go further!", Toast.LENGTH_SHORT).show();
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
                    startActivity(new Intent(BrowserActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
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