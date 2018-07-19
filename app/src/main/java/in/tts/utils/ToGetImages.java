package in.tts.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import in.tts.model.PrefManager;

public class ToGetImages {

    static ArrayList<String> imageFile = new ArrayList<>();
    static boolean status;

    public static void getAllShownImagesPath(final Activity activity, final Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                status = true;
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
                        while (cursor.moveToNext() && imageFile.size() < 11) {
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
                Log.d("TAG", "Data imaget " + imageFile.size());
                new PrefManager(context).toSetImageFileList(imageFile);
                Log.d("TAG", "Data image " + imageFile.size() +":"+ new PrefManager(context).toGetImageList());
            }
        });
//        return imageFile;
        status = false;
    }

    public static boolean isRunning(){
        Log.d("TAG", "Data image isRunning : " + status);
        return status;
    }
}