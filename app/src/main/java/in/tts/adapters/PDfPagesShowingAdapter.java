package in.tts.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.PdfShowingActivity;
import in.tts.utils.TouchImageView;

public class PDfPagesShowingAdapter extends PagerAdapter {

    private ArrayList<Bitmap> list = new ArrayList<>();
    private Context context;
    private TouchImageView pageView;
    private TextView tvPageNumber;

    public PDfPagesShowingAdapter(Context mContext, ArrayList<Bitmap> listBitmap) {
        try {
            context = mContext;
            list = listBitmap;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        try {
            ViewGroup vg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.pdf_page, container, false);
            pageView = vg.findViewById(R.id.pages);
            tvPageNumber = vg.findViewById(R.id.txtPageNumber);

            pageView.setImageBitmap(list.get(position));
            tvPageNumber.setText(String.valueOf(position + 1));//+ " / " + list.size());

            container.addView(vg);
            return vg;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}