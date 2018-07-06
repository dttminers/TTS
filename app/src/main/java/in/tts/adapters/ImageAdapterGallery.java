package in.tts.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.tts.R;

public class ImageAdapterGallery extends RecyclerView.Adapter<ImageAdapterGallery.ViewHolder> {
    private Context context;
    private ArrayList<String> images;

    public ImageAdapterGallery(Context activity, ArrayList<String> imagesGallery) {
        context = activity;
        images = imagesGallery;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.image_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        try {
            Picasso.get().load("file://" + images.get(i).replaceAll("\\s", "%20")).into(viewHolder.picturesView);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView picturesView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            picturesView = itemView.findViewById(R.id.ivItem);
        }
    }
}
