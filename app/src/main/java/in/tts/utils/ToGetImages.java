package in.tts.utils;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class ToGetImages {

    static ArrayList<String> imageFile = new ArrayList<>();
    public static ArrayList<String> getAllShownImagesPath(Activity activity) {

        // Find the last picture
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        if (activity != null) {
            Cursor cursor =
                    activity.getContentResolver()
                            .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                    null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String imageLocation = cursor.getString(1);
                    imageFile.add(imageLocation);
                    Log.d("TAG", "File Name " + imageLocation);
                }
                Log.d("TAG", "Count Image Files " + imageFile.size());
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return imageFile;
    }

//    public static ArrayList<String> getAllShownImagesPath(final Activity activity) {
//        final ArrayList<String> imageFile = new ArrayList<>();
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                Cursor cursor;
//
//                int column_index_data, column_index_folder_name;
//
//                String absolutePathOfImage = null;
//
//                Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//                String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
//
//                cursor = activity.getContentResolver().query(uri, projection, null, null, null);
//
//                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//                column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//
//                while (cursor.moveToNext()) {
//                    absolutePathOfImage = cursor.getString(column_index_data);
//                    imageFile.add(absolutePathOfImage);
//                }
//
//                Log.d("TAG", " DATA " + imageFile.size() + ":" + imageFile);
//            }
//        });
//        return AppData.imageFile = imageFile;
//    }
}