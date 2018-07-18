package in.tts.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.BookmarkActivity;
import in.tts.utils.CommonMethod;

public class BookmarkListAdapter extends ArrayAdapter<File> {
    Context context;
    ViewHolder viewHolder;
    ArrayList<String> list;


    public BookmarkListAdapter(BookmarkActivity _context, ArrayList<String> _list) {
        super(_context, R.layout.bookmark_item);
        context = _context;
        list = _list;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (list.size() > 0) {
            return list.size();
        } else {
            return 1;
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            CommonMethod.toCloseLoader();
            view = LayoutInflater.from(getContext()).inflate(R.layout.bookmark_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_title = (TextView) view.findViewById(R.id.title);
            viewHolder.tv_link = (TextView) view.findViewById(R.id.link);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();

        }

        viewHolder.tv_title.setText(list.get(position));
        viewHolder.tv_link.setText(list.get(position));
        return view;

    }

    public class ViewHolder {
        TextView tv_title, tv_link;
    }

}
