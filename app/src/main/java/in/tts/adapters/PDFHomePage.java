package in.tts.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.PdfReadersActivity;
import in.tts.utils.CommonMethod;

public class PDFHomePage extends PagerAdapter {

    private ArrayList<String> list;
    private Context context;

    private ParcelFileDescriptor fileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private File file;

    public PDFHomePage(Context ctx, ArrayList<String> listfile) {
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
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ViewGroup vg = null;
        try {
            vg = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.layout_books_item, container, false);

            CardView cv = vg.findViewById(R.id.cvBi);
            ImageView iv = vg.findViewById(R.id.ivBi);

            file = new File(list.get(position).trim());
            if (file.exists()) {
                fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                pdfRenderer = new PdfRenderer(fileDescriptor);
                if (null != currentPage) {
                    currentPage.close();
                }
                currentPage = pdfRenderer.openPage(0);
                Bitmap bitmap = Bitmap.createBitmap(250, 300, Bitmap.Config.ARGB_8888);
                currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                iv.setImageBitmap(bitmap);

                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            CommonMethod.toCallLoader(context, "Loading...");
                            Intent intent = new Intent(context, PdfReadersActivity.class);
                            intent.putExtra("file", list.get(position));
                            context.startActivity(intent);
                            CommonMethod.toCloseLoader();
                            Log.d("TAG", "PDF FILE" + list.get(position)+":"+ file.getAbsolutePath());
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                            Crashlytics.logException(e);
                            FirebaseCrash.report(e);
                        }
                    }
                });
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        container.addView(vg);
        return vg;
    }

    @Override
    public float getPageWidth(int position) {
        return (0.29f);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}