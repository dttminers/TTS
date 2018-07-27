package in.tts.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.model.AudioModel;
import in.tts.utils.CommonMethod;

public class RecentVoiceAdapter extends RecyclerView.Adapter<RecentVoiceAdapter.ViewHolder> {
    private Context context;
    //    ArrayList<String> audioList;
//    private List<AudioModel> audioList;
    public ArrayList<AudioModel> audioList = new ArrayList<>();
    public ArrayList<AudioModel> selected_usersList = new ArrayList<>();
//    Context mContext;

    public RecentVoiceAdapter(Context ctx, ArrayList<AudioModel> user_list, ArrayList<AudioModel> list) {
        context = ctx;
        audioList = user_list;
        selected_usersList = list;
    }

    @NonNull
    @Override
    public RecentVoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recent_voice_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecentVoiceAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        try {
//            File file = new File(audioList.get(i));
            File file = new File(audioList.get(i).getText());
            viewHolder.filename.setText(file.getName());
            viewHolder.fileSize.setText(CommonMethod.getFileSize(file));
            Log.d("TAG", "Audio : " + audioList.get(i).isSelected());
            if (selected_usersList.contains(audioList.get(i)))
                viewHolder.ivSelected.setVisibility(View.VISIBLE);
//                holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_selected_state));
            else
                viewHolder.ivSelected.setVisibility(View.GONE);
//                holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_normal_state));

//            viewHolder.ivSelected.setVisibility(audioList.get(i).isSelected() ? View.VISIBLE : View.INVISIBLE);
//            viewHolder.ivSelected.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    audioList.get(i).setSelected(!audioList.get(i).isSelected());
//                    Log.d("TAG", "Audio : " + audioList.get(i).isSelected());
//                    viewHolder.ivSelected.setVisibility(audioList.get(i).isSelected() ? View.VISIBLE : View.INVISIBLE);
//                }
//            });
//            viewHolder.ivSelected.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    audioList.get(i).setSelected(!audioList.get(i).isSelected());
//                    Log.d("TAG", "Audio : " + audioList.get(i).isSelected());
//                    viewHolder.ivSelected.setVisibility(audioList.get(i).isSelected() ? View.VISIBLE : View.INVISIBLE);
//                    return false;
//                }
//            });
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

    public void setData() {
//        audioList.get(i).setSelected(!audioList.get(i).isSelected());
//        Log.d("TAG", "Audio : " + audioList.get(i).isSelected());
//        viewHolder.ivSelected.setVisibility(audioList.get(i).isSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView filename, fileSize;
        public ImageView ivSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                filename = itemView.findViewById(R.id.tv_file_name);
                fileSize = itemView.findViewById(R.id.tv_file_size);
                ivSelected = itemView.findViewById(R.id.ivSelected);
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
        }
    }

}

