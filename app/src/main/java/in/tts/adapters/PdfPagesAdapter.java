package in.tts.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics; import com.flurry.android.FlurryAgent; import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

import in.tts.R;
import in.tts.utils.TouchImageView;

public class PdfPagesAdapter extends RecyclerView.Adapter<PdfPagesAdapter.ViewHolder> {

    private ArrayList<Bitmap> pages;
    private Context context;


    public PdfPagesAdapter(Context ctx, ArrayList<Bitmap> list) {
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
            e.printStackTrace(); FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e); FirebaseCrash.report(e);
        }
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TouchImageView pageView;
        public TextView tvPageNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pageView = itemView.findViewById(R.id.pages);
            tvPageNumber = itemView.findViewById(R.id.txtPageNumber);
        }
    }
}
