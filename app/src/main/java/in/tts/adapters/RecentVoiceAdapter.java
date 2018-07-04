package in.tts.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;

import in.tts.R;

public class RecentVoiceAdapter extends RecyclerView.Adapter<RecentVoiceAdapter.ViewHolder> {
    private Context context;

    public RecentVoiceAdapter(Context ctx) {
        context = ctx;
    }


    @NonNull
    @Override
    public RecentVoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recent_voice_item,viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecentVoiceAdapter.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
