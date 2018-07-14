package in.tts.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.tts.R;

public class PDFHomePageImages extends PagerAdapter {
    private ArrayList<String> l;
    private Context context;

    public PDFHomePageImages(Context ctx, ArrayList<String> list) {
        context = ctx;
        l = list;
    }

    @Override
    public int getCount() {
        return l.size() < 10 ? l.size() : 10;
    }

    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup vg = null;
        try {
            vg = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.image_item, container, false);
            ImageView iv = vg.findViewById(R.id.ivItem);
            Picasso.get()
                    .load("file://" + l.get(position).replaceAll("\\s+", "%20"))
                    .resize(250,250)
                    .into(iv);
            container.addView(vg);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
        return vg;
    }

    @Override
    public float getPageWidth(int position) {
        return (0.3f);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}