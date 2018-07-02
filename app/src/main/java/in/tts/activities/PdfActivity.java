package in.tts.activities;

import in.tts.R;
import in.tts.fragments.DocumentsFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.util.List;

public class PdfActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    String TAG = "PdfActivity";
    int position = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_pdf);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
                getSupportActionBar().setTitle(R.string.app_name);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_backward));
            } else {
                getSupportActionBar().setTitle(R.string.app_name);
            }

            init();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Toast.makeText(PdfActivity.this, " Error in Displaying Pdf ", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        try {
            pdfView = (PDFView) findViewById(R.id.pdfView);
            position = getIntent().getIntExtra("position", -1);
            displayFromSdcard();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Toast.makeText(PdfActivity.this, " Error in Displaying Pdf ", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayFromSdcard() {
        try {
            pdfFileName = DocumentsFragment.fileList.get(position).getName();

            pdfView.fromFile(DocumentsFragment.fileList.get(position))
                    .defaultPage(pageNumber)
                    .enableSwipe(true)

                    .swipeHorizontal(false)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .load();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Toast.makeText(PdfActivity.this, " Error in Displaying Pdf ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        try {
            pageNumber = page;
//        setTitle(String.format(" % s % s / % s",pdfFileName, page + 1, pageCount));
            setTitle(DocumentsFragment.fileList.get(position).getName());
        } catch (Exception | Error e) {
            e.printStackTrace();
            Toast.makeText(PdfActivity.this, " Error in Displaying Pdf ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void loadComplete(int nbPages) {
        try {
            PdfDocument.Meta meta = pdfView.getDocumentMeta();
            printBookmarksTree(pdfView.getTableOfContents(), " -");
        } catch (Exception | Error e) {
            e.printStackTrace();
            Toast.makeText(PdfActivity.this, " Error in Displaying Pdf ", Toast.LENGTH_SHORT).show();
        }

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        try {
            for (PdfDocument.Bookmark b : tree) {

                Log.e(TAG, String.format(" % s % s, p % d", sep, b.getTitle(), b.getPageIdx()));

                if (b.hasChildren()) {
                    printBookmarksTree(b.getChildren(), sep + " -");
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Toast.makeText(PdfActivity.this, " Error in Displaying Pdf ", Toast.LENGTH_SHORT).show();
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        try {
            super.onBackPressed();
            finish();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Toast.makeText(PdfActivity.this, " Error in Displaying Pdf ", Toast.LENGTH_SHORT).show();
        }
    }
}


/*

import java.io.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.util.*;

public class PDFTest {

 public static void main(String[] args){
 PDDocument pd;
 BufferedWriter wr;
 try {
         File input = new File("C:\\Invoice.pdf");  // The PDF file from where you would like to extract
         File output = new File("C:\\SampleText.txt"); // The text file where you are going to store the extracted data
         pd = PDDocument.load(input);
         System.out.println(pd.getNumberOfPages());
         System.out.println(pd.isEncrypted());
         pd.save("CopyOfInvoice.pdf"); // Creates a copy called "CopyOfInvoice.pdf"
         PDFTextStripper stripper = new PDFTextStripper();
         stripper.setStartPage(3); //Start extracting from page 3
         stripper.setEndPage(5); //Extract till page 5
         wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
         stripper.writeText(pd, wr);
         if (pd != null) {
             pd.close();
         }
        // I use close() to flush the stream.
        wr.close();
 } catch (Exception e){
         e.printStackTrace();
        }
     }
}
 */
