

package in.tts.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.tts.R;

public class GridViewAdapter extends BaseAdapter {

    // Declare variables
    private Context activity;
    private String[] filepath;
    private String[] filename;

    private static LayoutInflater inflater = null;

    public GridViewAdapter(Context a, String[] fpath, String[] fname) {
        activity = a;
        filepath = fpath;
        filename = fname;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return filepath.length;

    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.image_itm, null);
        // Locate the TextView in gridview_item.xml
        TextView text = (TextView) vi.findViewById(R.id.text);
        // Locate the ImageView in gridview_item.xml
        ImageView image = (ImageView) vi.findViewById(R.id.image);

        // Set file name to the TextView followed by the position
        text.setText(filename[position]);

        // Decode the filepath with BitmapFactory followed by the position
        Bitmap bmp = BitmapFactory.decodeFile(filepath[position]);

        // Set the decoded bitmap into ImageView
        image.setImageBitmap(bmp);
        return vi;
    }
}


//
//import android.app.Activity;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//import in.tts.R;
//import in.tts.model.ImageItem;
//
//public class GridViewAdapter extends ArrayAdapter {
//    private Context context;
//    private int layoutResourceId;
//    private ArrayList data = new ArrayList();
//
//    public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
//        super(context, layoutResourceId, data);
//        this.layoutResourceId = layoutResourceId;
//        this.context = context;
//        this.data = data;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View row = convertView;
//        ViewHolder holder = null;
//
//        if (row == null) {
//            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//            row = inflater.inflate(layoutResourceId, parent, false);
//            holder = new ViewHolder();
//            holder.imageTitle = (TextView) row.findViewById(R.id.text);
//            holder.image = (ImageView) row.findViewById(R.id.image);
//            row.setTag(holder);
//        } else {
//            holder = (ViewHolder) row.getTag();
//        }
//
////        ImageItem item = data.get(position);
////        holder.imageTitle.setText(item.getTitle());
////        holder.image.setImageBitmap(item.getImage());
//        return row;
//    }
//
//    static class ViewHolder {
//        TextView imageTitle;
//        ImageView image;
//    }
//}
