package in.tts.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import in.tts.R;

public class PDFHomePage extends PagerAdapter {

    private ArrayList<File> l;
    private Context context;

    public PDFHomePage(Context ctx, ArrayList<File> list) {
        context = ctx;
        l = list;
    }

    @Override
    public int getCount() {
        Log.d("TAG ", " count "+ l.size());
        return l.size();
    }


    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup vg = null;
        try {
            vg = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.layout_books_item, container, false);
            ImageView iv = vg.findViewById(R.id.ivBi);
            iv.setBackgroundColor(toGetRandomColor());
            container.addView(vg);
        }catch (Exception| Error e){
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
        return(30.9f);
    }
}
