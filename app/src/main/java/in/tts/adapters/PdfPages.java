package in.tts.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.PdfReadersActivity;
import in.tts.utils.TouchImageView;

public class PdfPages extends RecyclerView.Adapter<PdfPages.ViewHolder> {

    private ArrayList<Bitmap> pages;
    private Context context;

    public PdfPages(Context ctx, ArrayList<Bitmap> list) {
        context = ctx;
        pages = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.pdf_page, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        try {
            viewHolder.pageView.setImageBitmap(pages.get(i));
            viewHolder.tvPageNumber.setText((i+1) + " / " + pages.size());
//            Log.d("TAG", " Page " + i+ ":"+ pages.size());
//            Toast.makeText(context, "Page " + i + "/" + pages.size(), Toast.LENGTH_SHORT).show();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TouchImageView pageView;
        private TextView tvPageNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pageView = itemView.findViewById(R.id.pages);
            tvPageNumber = itemView.findViewById(R.id.txtPageNumber);
        }
    }
}
