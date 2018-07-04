package in.tts.adapters;

import android.content.Context;
import android.graphics.Movie;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

import in.tts.R;
import in.tts.model.AudioRecent;

public class RecentVoiceAdapter extends RecyclerView.Adapter<RecentVoiceAdapter.ViewHolder> {
    private Context context;
    private List<AudioRecent> AudioList;

    public RecentVoiceAdapter(Context ctx, List<AudioRecent> AudioList) {
        context = ctx;
        this.AudioList = AudioList;
    }


    @NonNull
    @Override
    public RecentVoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recent_voice_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecentVoiceAdapter.ViewHolder viewHolder, int i) {

        AudioRecent audioRecent = AudioList.get(i);
        viewHolder.Filename.setText(audioRecent.getFilename());
        viewHolder.FileSize.setText(audioRecent.getFileSize());
    }

    @Override
    public int getItemCount() {
        return AudioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Filename, FileSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Filename = (TextView) itemView.findViewById(R.id.tv_file_name);
            FileSize = (TextView) itemView.findViewById(R.id.tv_file_size);
        }
    }
}
