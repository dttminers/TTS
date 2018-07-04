//package in.tts.activities;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.pdf.PdfRenderer;
//import android.os.ParcelFileDescriptor;
//import android.support.annotation.NonNull;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.crashlytics.android.Crashlytics;
//
//import java.lang.reflect.Array;
//import java.util.ArrayList;
//
//import in.tts.R;
//import in.tts.adapters.PdfPages;
//import in.tts.adapters.RecentVoiceAdapter;
//import in.tts.fragments.DocumentsFragment;
//import in.tts.utils.TouchImageView;
//
//public class PdfReadersActivity extends AppCompatActivity {
//
//
//    private ParcelFileDescriptor fileDescriptor;
//    private PdfRenderer pdfRenderer;
//    private PdfRenderer.Page currentPage;
//    private int position = -1;
//    ArrayList<Bitmap> list = new ArrayList<Bitmap>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pdf_readers);
//        try {
//
//            position = getIntent().getIntExtra("position", -1);
//            fileDescriptor = ParcelFileDescriptor.open(DocumentsFragment.fileList.get(position), ParcelFileDescriptor.MODE_READ_ONLY);
//
//            Log.d("TAG", " hg " + DocumentsFragment.fileList.get(position).getName() + " : " + position);
//
//            // This is the PdfRenderer we use to render the PDF.
//            pdfRenderer = new PdfRenderer(fileDescriptor);
//
////            int index = 0;
//            // If there is a savedInstanceState (screen orientations, etc.), we restore the page index.
////            if (null != savedInstanceState) {
////                index = savedInstanceState.getInt("current_page", 0);
////            }
////            showPage(index);
//            Log.d("TAG", " pages " + pdfRenderer.getPageCount());
//            for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
//                showPage(i);
//            }
//
//            if (list.size() > 0) {
//                RecyclerView rv = findViewById(R.id.rv);
//                rv.setLayoutManager(new LinearLayoutManager(PdfReadersActivity.this));
//                rv.setItemAnimator(new DefaultItemAnimator());
//                rv.setAdapter(new PdfPages(PdfReadersActivity.this, list));
//            }
//        } catch (Exception | Error e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void showPage(int index) {
//        if (pdfRenderer.getPageCount() <= index) {
//            return;
//        }
////        Log.d("TAG", " pages 1 " + index + ":" + currentPage.getIndex());
//        // Make sure to close the current page before opening another one.
//        if (null != currentPage) {
////            Log.d("TAG", " pages 2 " + currentPage.getIndex() + ":" + index);
//            currentPage.close();
//        }
//        //open a specific page in PDF file
//        currentPage = pdfRenderer.openPage(index);
//        Log.d("TAG", " pages 3 " + index + ":" + currentPage.getIndex());
//        // Important: the destination bitmap must be ARGB (not RGB).
//        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
//        // Here, we render the page onto the Bitmap.
//        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//        // showing bitmap to an imageview
////        image.setImageBitmap(bitmap);
//        list.add(bitmap);
////        updateUIData();
//    }
//
//
//
//    /**
//     * Updates the state of 2 control buttons in response to the current page index.
//     */
////    private void updateUIData() {
//////        int index = currentPage.getIndex();
////        int pageCount = pdfRenderer.getPageCount();
//////        btnPrevious.setEnabled(0 != index);
//////        btnNext.setEnabled(index + 1 < pageCount);
//////        setTitle(getString(R.string.app_name, index + 1, pageCount));
////
////
////        for (int i =0 ; i< pageCount; i++){
////        }
////    }
//}
