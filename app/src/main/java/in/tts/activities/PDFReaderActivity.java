package in.tts.activities;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.tts.R;
import in.tts.fragments.DocumentsFragment;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class PDFReaderActivity extends AppCompatActivity {

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ImageView image;
    private Button btnPrevious;
    private Button btnNext;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreader);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle("Audio Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_left_white_24dp));
        } else {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        // Retain view references.
        image = findViewById(R.id.image);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);

        //set buttons event
        btnPrevious.setOnClickListener(onActionListener(-1)); //previous button clicked
        btnNext.setOnClickListener(onActionListener(1)); //next button clicked

        try {
            openRenderer();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Fragment", "Error occurred!");
            Log.e("Fragment", e.getMessage());
            Toast.makeText(PDFReaderActivity.this, " Not able to Read PDF ", Toast.LENGTH_SHORT).show();
//            activity.finish();
        }

        int index = 0;
        // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
        if (null != savedInstanceState) {
            index = savedInstanceState.getInt("current_page", 0);
        }
        showPage(index);
    }

    @Override
    public void onDestroy() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != currentPage) {
            outState.putInt("current_page", currentPage.getIndex());
        }
    }


    private void openRenderer() throws IOException {
        // Reading a PDF file from the assets directory.
//        fileDescriptor = activity.getAssets().openFd("canon_in_d.pdf").getParcelFileDescriptor();

        position = getIntent().getIntExtra("position", -1);
        fileDescriptor = ParcelFileDescriptor.open(DocumentsFragment.fileList.get(position), ParcelFileDescriptor.MODE_READ_ONLY);

        Log.d("TAG", " " + DocumentsFragment.fileList.get(position).getName() + " : " + position);

        // This is the PdfRenderer we use to render the PDF.
        pdfRenderer = new PdfRenderer(fileDescriptor);
    }

    /**
     * Closes PdfRenderer and related resources.
     */
    private void closeRenderer() throws IOException {
        if (null != currentPage) {
            currentPage.close();
        }
        pdfRenderer.close();
        fileDescriptor.close();
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
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        // showing bitmap to an imageview
        image.setImageBitmap(bitmap);
        updateUIData();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    private void updateUIData() {
        int index = currentPage.getIndex();
        int pageCount = pdfRenderer.getPageCount();
        btnPrevious.setEnabled(0 != index);
        btnNext.setEnabled(index + 1 < pageCount);
//        setTitle(getString(R.string.app_name, index + 1, pageCount));
    }

    private View.OnClickListener onActionListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (i < 0) {//go to previous page
                    showPage(currentPage.getIndex() - 1);
                } else {
                    showPage(currentPage.getIndex() + 1);
                }
            }
        };
    }
}
