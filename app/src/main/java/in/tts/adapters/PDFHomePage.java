package in.tts.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import in.tts.R;
import in.tts.fragments.MyBooksFragment;

public class PDFHomePage extends PagerAdapter {

    private ArrayList<File> list;
    private Context context;

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;

    public PDFHomePage(Context ctx, ArrayList<File> listfile) {
        context = ctx;
        list = listfile;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size() < 10 ? list.size() : 10;
        } else {
            return 0;
        }
    }

    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup vg = null;
        try {
            vg = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.layout_books_item, container, false);
            ImageView iv = vg.findViewById(R.id.ivBi);
//            iv.setBackgroundColor(toGetRandomColor());

            fileDescriptor = ParcelFileDescriptor.open(list.get(position), ParcelFileDescriptor.MODE_READ_ONLY);

            pdfRenderer = new PdfRenderer(fileDescriptor);

//            if (pdfRenderer.getPageCount() <= index) {
//                return;
//            }
            // Make sure to close the current page before opening another one.
            if (null != currentPage) {
                currentPage.close();
            }
            //open a specific page in PDF file
            currentPage = pdfRenderer.openPage(0);
            // Important: the destination bitmap must be ARGB (not RGB).
            Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
            // Here, we render the page onto the Bitmap.
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            iv.setImageBitmap(bitmap);
            container.addView(vg);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
        return vg;
    }

    private int toGetRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(200), rnd.nextInt(200), rnd.nextInt(200));
    }

    @Override
    public float getPageWidth(int position) {
        return (0.29f);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

