package in.tts.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import in.tts.activities.MediaFile;
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

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        ViewGroup vg = null;
        try {
            vg = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.image_item, container, false);
            ImageView iv = vg.findViewById(R.id.ivItem);
            Log.d("TAG", " IMAGES " + position +":"+ l.get(position).trim().replaceAll("\\s+", "%20"));
            Picasso.get()
                    .load("file://" + l.get(position).trim().replaceAll("\\s+", "%20"))
                    .placeholder(R.color.light3)
                    .error(R.color.grey)
                    .resize(250,250)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(iv);

            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        CommonMethod.toCallLoader(context, "Loading...");
                        context.startActivity(new Intent(context, ImageOcrActivity.class).putExtra("PATH", l.get(position)));
                        CommonMethod.toCloseLoader();
//                        context.startActivity(new Intent(context, MediaFile.class));
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                        CommonMethod.toCloseLoader();
                    }
                }
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            CommonMethod.toCloseLoader();
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