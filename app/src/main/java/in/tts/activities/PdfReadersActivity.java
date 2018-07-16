package in.tts.activities;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.adapters.PdfPages;
import in.tts.model.AppData;
import in.tts.utils.CommonMethod;

public class PdfReadersActivity extends AppCompatActivity {

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private int position = -1;
    //    private Bitmap bitmap;
    private StringBuilder stringBuilder;
    ArrayList<Bitmap> list = new ArrayList<Bitmap>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_readers);
        try {
            CommonMethod.toCallLoader(PdfReadersActivity.this, "Loading...");
            position = getIntent().getIntExtra("position", -1);
            String name = getIntent().getStringExtra("name");
            String file = getIntent().getStringExtra("file");
            stringBuilder = new StringBuilder();
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
//                if (MyBooksFragment.fileList.get(position).getName() != null) {
//                    getSupportActionBar().setTitle(MyBooksFragment.fileList.get(position).getName());
                if (name != null) {
                    getSupportActionBar().setTitle(name);
                } else {
                    getSupportActionBar().setTitle(R.string.app_name);
                }
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_left_white_24dp));
            }

//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//            fileDescriptor = ParcelFileDescriptor.open(MyBooksFragment.fileList.get(position), ParcelFileDescriptor.MODE_READ_ONLY);
            fileDescriptor = ParcelFileDescriptor.open(new File(file), ParcelFileDescriptor.MODE_READ_ONLY);

            pdfRenderer = new PdfRenderer(fileDescriptor);
            for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                showPage(i);
            }
//                    } catch (Exception | Error e) {
//                        e.printStackTrace();
////                        CommonMethod.toCloseLoader();
//                        Crashlytics.logException(e);
//                    }
//                }
//            });
            CommonMethod.toCloseLoader();
            if (list.size() > 0) {
                RecyclerView rv = findViewById(R.id.rv);
                rv.setLayoutManager(new LinearLayoutManager(PdfReadersActivity.this));
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(new PdfPages(PdfReadersActivity.this, list));
//                CommonMethod.toCloseLoader();
            }
//            toGetPDFText();
            CommonMethod.toCloseLoader();
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

//            textBlocks = textRecognizer.detect(imageFrame);
//            for (int i = 0; i < textBlocks.size(); i++) {
//                textBlock = textBlocks.get(textBlocks.keyAt(i));
//                imageText = textBlock.getValue();                   // return string
//                Log.d("TAG", " Result : " + i + ":" + imageText);
//            }
//            Log.d("TAG", " Result Final: " + imageText);

            SparseArray<TextBlock> items = textRecognizer.detect(imageFrame);
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
            Log.d("TAG", " Final DATA " + stringBuilder);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
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
        toGetData(bitmap);
    }

//    public void toGetPDFText() {
//        try {
////            File sdcard = Environment.getExternalStorageDirectory();
////Get the text file
////            File file = new File(sdcard, "myfolder/anskey.txt");
////ob.pathh
//            //Read text from file
//
//            StringBuilder text = new StringBuilder();
//            BufferedReader br = new BufferedReader(new FileReader(new File(AppData.fileList.get(position).getPath())));
//            String line = null;
//            //int i=0;
//            List<String> lines = new ArrayList<String>();
//
//            while ((line = br.readLine()) != null) {
//                lines.add(line);
//                text.append(line);
//                text.append('\n');
//            }
//            Log.d("Tag", " text : " + text);
////            Toast.makeText(PdfReadersActivity.this, "TEXT " + text.length(), Toast.LENGTH_LONG).show();
////            arr =
//            Log.d("Tag", " text : " + lines.toArray(new String[lines.size()]));
//        } catch (Exception | Error e) {
//            e.printStackTrace();
//            Crashlytics.logException(e);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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



