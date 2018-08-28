package in.tts.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.activities.BookmarkActivity;
import in.tts.activities.BrowserActivity;
import in.tts.model.BookmarkModel;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.ViewHolder> {
    private Context context;
    private List<BookmarkModel> bookmarkModelList;

    public BookMarkAdapter(Context _context, List<BookmarkModel> _bookmarkModelList) {
        context = _context;
        bookmarkModelList = _bookmarkModelList;
    }

    @NonNull
    @Override
    public BookMarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.bookmark_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookMarkAdapter.ViewHolder viewHolder, int position) {
        try {
            viewHolder.tv_title.setText(bookmarkModelList.get(position).getPageTitle());
            viewHolder.tv_link.setText(bookmarkModelList.get(position).getPageUrl());
            viewHolder.iv_favicon.setImageBitmap(CommonMethod.StringToBitmap(bookmarkModelList.get(position).getPageFavicon()));
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public int getItemCount() {
        return bookmarkModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_link;
        ImageView iv_favicon;
        RelativeLayout rl;

        public ViewHolder(@NonNull View view) {
            super(view);
            try {
                rl = view.findViewById(R.id.rlbm);
                tv_title = view.findViewById(R.id.title);
                tv_link = view.findViewById(R.id.link);
                iv_favicon = view.findViewById(R.id.ivFavicon);

                rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent in = new Intent(context, BrowserActivity.class);
                            in.putExtra("url", bookmarkModelList.get(getAdapterPosition()).getPageUrl());
                            context.startActivity(in);
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                            Crashlytics.logException(e);
                            FirebaseCrash.report(e);
                        }
                    }
                });

                rl.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        deleteBookmark(getAdapterPosition());
                        return true;
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

    private void deleteBookmark(final int position) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle("DELETE")
                    .setMessage("Confirm that you want to delete this bookmark?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                bookmarkModelList.remove(position);
                                new PrefManager(context).saveList(bookmarkModelList);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                                notifyItemRangeChanged(position, bookmarkModelList.size());
                                dialogInterface.dismiss();
                            } catch (Exception | Error e) {
                                e.printStackTrace();
                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e);
                                FirebaseCrash.report(e);
                            }
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }
}