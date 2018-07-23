package in.tts.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.PdfReadersActivity;
import in.tts.utils.CommonMethod;

public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> list;

    public PdfListAdapter(Context _context, ArrayList<String> _list) {
        context = _context;
        list = _list;
    }

    @NonNull
    @Override
    public PdfListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_pdf, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PdfListAdapter.ViewHolder viewHolder, int i) {
        try {
            Log.d("TAG", "Bind " + i + ":" + list.size());
            viewHolder.mtv.setText(new File(list.get(i)).getName());
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mtv;
        private RelativeLayout mRl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                mtv = itemView.findViewById(R.id.tv_name);
                mRl = itemView.findViewById(R.id.mRlPdfListItem);
                mRl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            CommonMethod.toCallLoader(context, "Loading...");
                            Intent intent = new Intent(context, PdfReadersActivity.class);
                            intent.putExtra("file", list.get(getAdapterPosition()));
                            context.startActivity(intent);
                        CommonMethod.toCloseLoader();
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                            Crashlytics.logException(e);
                            FirebaseCrash.report(e);
                        }
                    }
                });

            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
        }
    }
}
