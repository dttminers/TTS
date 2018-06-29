package in.tts.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import com.bumptech.glide.Glide;

import in.tts.R;

public class GalleryFragment extends Fragment {

    /**
     * The images.
     */
    private ArrayList<String> images;

    public GalleryFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GridView gallery = (GridView) getActivity().findViewById(R.id.gridView);

        gallery.setAdapter(new ImageAdapter(getActivity()));

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                if (null != images && !images.isEmpty())
//                    Toast.makeText(getContext(), "position " + position + " " + images.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * The Class ImageAdapter.
     */
    private class ImageAdapter extends BaseAdapter {

        /**
         * The context.
         */
        private Activity context;

        /**
         * Instantiates a new image adapter.
         *
         * @param localContext the local context
         */
        public ImageAdapter(Activity localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                int size = width / 3;

                picturesView.setLayoutParams(new GridView.LayoutParams(size, size));
                picturesView.setPadding(2, 2, 2, 2);

            } else {
                picturesView = (ImageView) convertView;
            }

            Glide.with(context).load(images.get(position))
//                    .placeholder(R.
// .light)
//                    .centerCrop()
                    .into(picturesView);

            return picturesView;
        }

        /**
         * Getting All Images Path.
         *
         * @param activity the activity
         * @return ArrayList with images Path
         */
        private ArrayList<String> getAllShownImagesPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name;
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            String absolutePathOfImage = null;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);

                listOfAllImages.add(absolutePathOfImage);
            }
            return listOfAllImages;
        }
    }
}
//    /**
//     * Cursor used to access the results from querying for images on the SD card.
//     */
//    private Cursor cursor;
//    /*
//     * Column index for the Thumbnails Image IDs.
//     */
//    private int columnIndex;
//
//    public GalleryFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_gallery, container, false);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        // Set up an array of the Thumbnail Image ID column we want
//        String[] projection = {MediaStore.Images.Thumbnails._ID};
//        // Create the cursor pointing to the SDCard
//        cursor = getActivity().managedQuery( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
//                projection, // Which columns to return
//                null,       // Return all rows
//                null,
//                MediaStore.Images.Thumbnails.IMAGE_ID);
//        // Get the column index of the Thumbnails Image ID
//        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
//        GridView sdcardImages = (GridView) getActivity().findViewById(R.id.gridView);
//        sdcardImages.setAdapter(new ImageAdapter(getContext()));
//        // Set up a click listener
//        sdcardImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView parent, View v, int position, long id) {
//                // Get the data location of the image
//                String[] projection = {MediaStore.Images.Media.DATA};
//                cursor = getActivity().managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        projection, // Which columns to return
//                        null,       // Return all rows
//                        null,
//                        null);
//                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                cursor.moveToPosition(position);
//                // Get image filename
//                String imagePath = cursor.getString(columnIndex);
//                // Use this path to do further processing, i.e. full screen display
//            }
//        });
//    }
//    /**
//     * Adapter for our image files.
//     */
//    private class ImageAdapter extends BaseAdapter {
//        private Context context;
//        public ImageAdapter(Context localContext) {
//            context = localContext;
//        }
//        public int getCount() {
//            Log.d("TAG", " Count  " + cursor.getCount());
//            return cursor.getCount();
//        }
//        public Object getItem(int position) {
//            return position;
//        }
//        public long getItemId(int position) {
//            return position;
//        }
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ImageView picturesView;
//            if (convertView == null) {
//                picturesView = new ImageView(context);
//                // Move cursor to current position
//                cursor.moveToPosition(position);
//                // Get the current value for the requested column
//                int imageID = cursor.getInt(columnIndex);
//                // Set the content of the image based on the provided URI
//                picturesView.setImageURI(Uri.withAppendedPath(
//                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));
//                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                picturesView.setPadding(8, 8, 8, 8);
//                picturesView.setLayoutParams(new GridView.LayoutParams(100, 100));
//            }
//            else {
//                picturesView = (ImageView)convertView;
//            }
//            return picturesView;
//        }
//    }
//
//}
