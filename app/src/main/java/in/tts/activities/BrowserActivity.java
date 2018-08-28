package in.tts.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.tts.R;
import in.tts.classes.ClipBoard;
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
            checkInternetConnection();
            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), BrowserActivity.this, getString(R.string.app_name));
            }

            if (getIntent() != null) {
                if (getIntent().getData() != null) {
                    Log.d("TAG_WEB", " Data 1 " + getIntent().getData());
                }
                if (getIntent().getAction() != null) {
                    Log.d("TAG_WEB", " Data 2 " + getIntent().getAction());
                }
                if (getIntent().getExtras() != null) {
                    Log.d("TAG_WEB", " Data 3 " + getIntent().getExtras());
                }
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
                        Log.d("TAG_WEB", "onScrollChange " + i + ":" + i1 + ":" + i2 + ":" + i3 + ":" + view.getHeight() + ":" + view.getWidth() + currentPosition);
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
//                            linkList = prefManager.populateSelectedSearch();
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

            superWebView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        ClipBoard.ToPopup(BrowserActivity.this, BrowserActivity.this, null);
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
                                    Log.d("TAG_List ", " toUpdateBookMarkIcon : " + list.get(i).getPageUrl() + ":" + (list.get(i).getPageUrl().equals(superWebView.getUrl().trim().replaceAll("\\s+", "%20"))));
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

//            list = prefManager.loadList();
//            if (list != null) {
//                BookmarkModel model = new BookmarkModel(superWebView.getTitle(), superWebView.getUrl().trim().replaceAll("\\s+", "%20"), CommonMethod.BitmapToString(superWebView.getFavicon()));
//                Log.d("TAG_List ", " onCreateOptionsMenu : " + model.toString() + ":" + list.contains(model));
//                if (list.contains(model)) {
//                    menu.getItem(1).setIcon(R.drawable.ic_bookmark_black_24dp);
//                } else {
//                    menu.getItem(1).setIcon(R.drawable.ic_bookmark_border_black_24dp);
//                }
//            } else {
//                list = new ArrayList<>();
//                menu.getItem(1).setIcon(R.drawable.ic_bookmark_border_black_24dp);
//            }


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
//            tts = new TTS(BrowserActivity.this);
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
                        if (tts != null) {
                            tts.toStop();
                            tts.toShutDown();
                        }
//                        tts = new TTS(BrowserActivity.this);
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
            Log.d("TAG_WEB", " toGetTextFromBitmap " + bitmap.getHeight() + ":" + bitmap.isRecycled() + ":" + currentPosition + ":" + setBackPosition);
            rlPb.setVisibility(View.VISIBLE);

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), BrowserActivity.this, getString(R.string.str_fetching_data));
            }

            try {
                Date now = new Date();
                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
                File dirPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/READ_IT/IMAGE/");
                if (!dirPath.exists()) {
                    dirPath.mkdirs();
                }
                File imageFile = new File(dirPath.getAbsolutePath() + File.separator + "aksh_" + now + ".jpg");
                Log.d("TAG_WEB", " getpath " + imageFile.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(imageFile);
                if (fos != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                }
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }

            textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

            imageFrame = new Frame.Builder().setBitmap(bitmap).build();

            items = textRecognizer.detect(imageFrame);

            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
            Log.d("TAG_WEB", " data10 " + stringBuilder.toString().length() + ":" + stringBuilder.toString());


            superWebView.post(new Runnable() {
                public void run() {
                    Log.d("TAG_WEB", " data11 " + superWebView.getContentHeight() + ":" + superWebView.getScale() + ":" + superWebView.getScrollY());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (superWebView.getContentHeight() * superWebView.getScale() >= superWebView.getScrollY()) {
                                Log.d("TAG_WEB", " data12 " + (int) superWebView.getHeight());

                                superWebView.scrollBy(0, (int) superWebView.getHeight());
                                toGetTextFromCurrentScreen();

                            } else {
                                Log.d("TAG_WEB", " data13 " + currentPosition + ":" + setBackPosition);
                                superWebView.scrollTo(0, setBackPosition);
                                if (getSupportActionBar() != null) {
                                    CommonMethod.toSetTitle(getSupportActionBar(), BrowserActivity.this, webTitle);
                                }

                                rlPb.setVisibility(View.GONE);
                                toSpeakFromWebPage();
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

    private void toSpeakFromWebPage() {
        try {
            CommonMethod.toCloseLoader();
            Log.d("WEB", "toSpeakWebPage  " + stringBuilder.toString().length() + ":" + stringBuilder.toString());
            if (stringBuilder.toString().trim().length() > 0) {
//                tts = new TTS(BrowserActivity.this);

                tts.SpeakLoud(stringBuilder.toString().replaceAll("Fetching data...", "\\s"));//, "AUD_Web" + superWebView.getTitle() + System.currentTimeMillis());
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
                Log.d("TAG", "Added : " + superWebView.getFavicon().getHeight() + ":" + superWebView.getFavicon().getWidth() + ":" + superWebView.getFavicon().getGenerationId());
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

/*
AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Document doc = Jsoup.connect(url).get();
                                Log.d("WEB", "Doc " + doc + ":lll\n" + doc.getAllElements());
                                Log.d("WEB", "Doc 1" + doc.getElementsByTag("body"));
                                Elements newsHeadlines = doc.getElementsByAttribute("value1");
                                Log.d("WEB", "Ele  " + newsHeadlines.text());
//                        String ip = newsHeadlines[0].text().split("**")[1];
//                                String text = "<B>I don't want this to be bold<\\B>";
//                                System.out.println(text);
                               String text = doc.getElementsByTag("body").toString().replaceAll("\\<.*?\\>", "");
//                                System.out.println(text);
                                Log.d("WEB", "text  " + text);


                                URL url1 = new URL(url);
                                URLConnection yc = url1.openConnection();
                                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                                String inputLine;
                                StringBuilder builder = new StringBuilder();
                                while ((inputLine = in.readLine()) != null)
                                    builder.append(inputLine.trim());
                                in.close();
                                String htmlPage = builder.toString();
                                Log.d("WEB", "text1  " + htmlPage);
                                String versionNumber = htmlPage.replaceAll("\\<.*?>","");
                                Log.d("WEB", "text2  " + versionNumber);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    linkList = prefManager.populateSelectedSearch();
            if (linkList != null) {
                if (linkList.contains(superWebView.getUrl())) {
                    menu.getItem(1).setIcon(R.drawable.ic_bookmark_black_24dp);
                } else {
                    menu.getItem(1).setIcon(R.drawable.ic_bookmark_border_black_24dp);
                }
            } else {
                linkList = new ArrayList<>();
                menu.getItem(1).setIcon(R.drawable.ic_bookmark_border_black_24dp);
            }
                    */