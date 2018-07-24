package in.tts.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.ImageOcrActivity;
import in.tts.utils.CommonMethod;

public class PDFHomePageImages extends PagerAdapter {
    private ArrayList<String> l;
    private Context context;

    public PDFHomePageImages(Context ctx, ArrayList<String> list) {
        context = ctx;
        l = list;
    }

    @Override
    public int getCount() {
        if (l != null) {
            return l.size() < 10 ? l.size() : 10;
        } else {
            return 0;
        }
    }

    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ViewGroup vg = null;
        try {
            vg = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.image_item, container, false);
            ImageView iv = vg.findViewById(R.id.ivItem);
            Log.d("TAG", " IMAGES " + position + ("file://" + l.get(position).trim().replaceAll("\\s+", "%20")));
            Picasso.get()
                    .load("file://" + l.get(position).trim().replaceAll("\\s+", "%20"))
                    .resize(300, 300)
                    .into(iv);

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethod.toCallLoader(context, "Loading...");
                        context.startActivity(new Intent(context, ImageOcrActivity.class).putExtra("PATH", l.get(position)));
                        CommonMethod.toCloseLoader();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        container.addView(vg);
        return vg;
    }

    @Override
    public float getPageWidth(int position) {
        return (0.3f);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}