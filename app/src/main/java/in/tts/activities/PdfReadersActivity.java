package in.tts.activities;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.PdfPages;
import in.tts.utils.CommonMethod;

public class PdfReadersActivity extends AppCompatActivity {

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private StringBuilder stringBuilder;
    ArrayList<Bitmap> list = new ArrayList<Bitmap>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_readers);
        try {
            CommonMethod.toCallLoader(PdfReadersActivity.this, "Loading...");
            if (getIntent().getExtras() != null) {
                if (getIntent().getStringExtra("file") != null) {
                    File file = new File( getIntent().getStringExtra("file").trim());

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().show();
                        if (file.getName() != null) {
                            getSupportActionBar().setTitle(file.getName());
                        } else {
                            getSupportActionBar().setTitle(R.string.app_name);
                        }
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setDisplayShowHomeEnabled(true);
                        getSupportActionBar().setDisplayShowTitleEnabled(true);
                        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_left_white_24dp));
                    }
                    stringBuilder = new StringBuilder();
                    fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

                    pdfRenderer = new PdfRenderer(fileDescriptor);
                    for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                        showPage(i);
                    }

                    if (list.size() > 0) {
                        RecyclerView rv = findViewById(R.id.rv);
                        rv.setLayoutManager(new LinearLayoutManager(PdfReadersActivity.this));
                        rv.setItemAnimator(new DefaultItemAnimator());
                        rv.setAdapter(new PdfPages(PdfReadersActivity.this, list));
                    }
                    CommonMethod.toCloseLoader();
                } else {
                    CommonMethod.toCloseLoader();
                    CommonMethod.toDisplayToast(PdfReadersActivity.this, "Can't Open Pdf File");
                    finish();
                }
            } else {
                CommonMethod.toCloseLoader();
                CommonMethod.toDisplayToast(PdfReadersActivity.this, "Can't Open Pdf File");
                finish();
            }
//            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    private void toGetData(Bitmap bitmap) {
        try {
            stringBuilder.append("next page...");
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = textRecognizer.detect(imageFrame);
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
            Log.d("TAG", " Final DATA " + stringBuilder);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        try {
            if (pdfRenderer.getPageCount() <= index) {
                return;
            }
            // Make sure to close the current page before opening another one.
            if (null != currentPage) {
                currentPage.close();
            }
            //open a specific page in PDF file
            currentPage = pdfRenderer.openPage(index);
            // Important: the destination bitmap must be ARGB (not RGB).
            Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
            // Here, we render the page onto the Bitmap.
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            // showing bitmap to an imageview
            list.add(bitmap);
//            toGetData(bitmap);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                default:
                    return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}