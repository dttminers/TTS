package in.tts.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

public class AppPermissions {

    private static int REQUEST_PERMISSIONS = 1;
    private static boolean per = false;

    public static boolean toAsk(Context context, final Activity activity, final String permission) {
        try {
            Log.d("TAG", " Permission for " + permission + ":" + per);
            per = false;

            if ((ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
//                    Log.d("TAG", "Allow ask 1 " + permission);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setTitle("Need Permission");
//                    builder.setMessage("This app needs permission.");
//                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            ActivityCompat.requestPermissions(activity, new String[]{permission}, 101);
//                            per = true;
//                        }
//                    });
//                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            per = false;
//                        }
//                    });
//                    builder.show();
                    return per;
                } else {
                    Log.d("TAG", "Allow ask 2 " + permission);
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_PERMISSIONS);
                    return true;
                }
            } else {
                Log.d("TAG", "Already Allowed " + permission);
                return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return false;
    }
}