package in.tts.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import in.tts.R;

public class PDFHomePageImages extends PagerAdapter {
    ArrayList<String> l;
    Context context;

    public PDFHomePageImages(Context ctx, ArrayList<String> list) {
        context = ctx;
        l = list;
    }

    @Override
    public int getCount() {
        Log.d("TAG", " count images " + l.size());
        return l.size();
    }


    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        ViewGroup vg = null;
        try {
            vg = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.image_item, container, false);
            ImageView iv = vg.findViewById(R.id.ivItem);
            iv.setBackgroundColor(toGetRandomColor());
            Picasso.get().load("file://" + l.get(position).replaceAll("\\s", "%20")).into(iv);
            container.addView(vg);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
        return vg;
    }

    public int toGetRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(200), rnd.nextInt(200), rnd.nextInt(200));
    }
}

