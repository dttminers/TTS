package in.tts.adapters;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.tts.R;
import in.tts.activities.PdfShowingActivity;
import in.tts.model.PdfModel;
import in.tts.utils.CommonMethod;

public class PdfListModelAdapter extends RecyclerView.Adapter<PdfListModelAdapter.MyViewHolder> {

    private Context context;
    private File file;
    private List<PdfModel> notesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mtv, mtv1, mtv2;
        private LinearLayout mLl;

        public MyViewHolder(View view) {
            super(view);
            try {
                mtv = itemView.findViewById(R.id.tv_name);
                mtv1 = itemView.findViewById(R.id.tv_duration);
                mtv2 = itemView.findViewById(R.id.tv_remaining);

                mLl = itemView.findViewById(R.id.mLlPdfListItem);

                mLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            CommonMethod.toCallLoader(context, "Loading...");
                            Intent intent = new Intent(context, PdfShowingActivity.class);
//                            Intent intent = new Intent(context, PdfReadersActivity.class);
//                            Intent intent = new Intent(context, AfterFileSelected.class);
                            intent.putExtra("file", notesList.get(getAdapterPosition()).getName());
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


    public PdfListModelAdapter(Context context, List<PdfModel> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pdf, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        try {
            file = new File(notesList.get(position).getName().trim().replaceAll("%20", " "));
            Log.d("TAG", " count  onBindViewHolder " + position + notesList.get(position).getName()+ ":" +notesList.get(position).getCreatedOn() + ":" + file.getName() + ":" + file.getAbsolutePath());
            viewHolder.mtv.setText(file.getName());
            viewHolder.mtv1.setText(CommonMethod.getFileSize(file));
            viewHolder.mtv2.setText(String.valueOf(new PdfReader(file.getAbsolutePath()).getNumberOfPages()) + " Pages");
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }
}
