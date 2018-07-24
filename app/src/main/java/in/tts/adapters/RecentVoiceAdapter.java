package in.tts.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class RecentVoiceAdapter extends RecyclerView.Adapter<RecentVoiceAdapter.ViewHolder> {
    private Context context;
    ArrayList<String> audioList;

    public RecentVoiceAdapter(Context ctx, ArrayList<String> list) {
        context = ctx;
        audioList = list;
    }

    @NonNull
    @Override
    public RecentVoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recent_voice_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecentVoiceAdapter.ViewHolder viewHolder, int i) {
        try {
            File file = new File(audioList.get(i));
            viewHolder.filename.setText(file.getName());
            viewHolder.fileSize.setText(CommonMethod.getFileSize(file));
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView filename, fileSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                filename = itemView.findViewById(R.id.tv_file_name);
                fileSize = itemView.findViewById(R.id.tv_file_size);
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
        }
    }
}
