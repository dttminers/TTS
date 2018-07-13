package in.tts.activities;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.adapters.PdfPages;
import in.tts.fragments.DocumentsFragment;
import in.tts.utils.CommonMethod;

public class PdfReadersActivity extends AppCompatActivity {

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private int position = -1;
    ArrayList<Bitmap> list = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_readers);
        try {
            position = getIntent().getIntExtra("position", -1);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
                if (DocumentsFragment.fileList.get(position).getName() != null) {
                    getSupportActionBar().setTitle(DocumentsFragment.fileList.get(position).getName());
                } else {
                    getSupportActionBar().setTitle(R.string.app_name);
                }
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_left_white_24dp));
            }

            fileDescriptor = ParcelFileDescriptor.open(DocumentsFragment.fileList.get(position), ParcelFileDescriptor.MODE_READ_ONLY);

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
            toGetPDFText();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

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
    }

    public void toGetPDFText() {
        try {
//            File sdcard = Environment.getExternalStorageDirectory();

//Get the text file

//            File file = new File(sdcard, "myfolder/anskey.txt");

//ob.pathh
            //Read text from file

            StringBuilder text = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(new File(DocumentsFragment.fileList.get(position).getName())));
            String line = null;
            //int i=0;
            List<String> lines = new ArrayList<String>();

            while ((line = br.readLine()) != null) {
                lines.add(line);
//       arr[i]=line;
//       i++;
                text.append(line);
                text.append('\n');
            }
            Log.d("Tag", " text : " + text);
            Toast.makeText(PdfReadersActivity.this, "TEXT " + text.length(), Toast.LENGTH_LONG).show();
            //arr = lines.toArray(new String[lines.size()]);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
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



