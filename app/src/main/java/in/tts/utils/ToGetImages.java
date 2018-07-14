package in.tts.utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import in.tts.fragments.HomePageFragment;
import in.tts.model.AppData;

public class ToGetImages {

    public static ArrayList<String> getAllShownImagesPath(Activity activity) {

        Cursor cursor;

        ArrayList<String> fileName = new ArrayList<>();

        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;

        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            fileName.add(absolutePathOfImage);
        }

        Log.d("TAG", " DATA " + fileName.size() + ":" + fileName);
        return AppData.fileName = fileName;
    }
}