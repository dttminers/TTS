package in.tts.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.util.Linkify;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.tts.R;

public class HelperUnit {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_1 = 1234;

    public static void grantPermissionsStorage(final Activity activity) {

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int hasWRITE_EXTERNAL_STORAGE = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.toast_permission_title)
                            .setMessage(R.string.toast_permission_sdCard)
                            .setPositiveButton(activity.getString(R.string.app_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            })
                            .setNegativeButton(activity.getString(R.string.app_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            }
        }
    }

    public static void grantPermissionsLoc(final Activity activity) {

        if (android.os.Build.VERSION.SDK_INT >= 23) {

            int hasACCESS_FINE_LOCATION = activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(activity)
                            .setTitle(R.string.toast_permission_title)
                            .setMessage(R.string.toast_permission_loc)
                            .setPositiveButton(activity.getString(R.string.app_ok), new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQUEST_CODE_ASK_PERMISSIONS_1);
                                }
                            })
                            .setNegativeButton(activity.getString(R.string.app_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            }
        }
    }

    public static void setTheme(Context activity) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        if (sp.getBoolean("sp_darkUI", false)) {
            activity.setTheme(R.style.AppTheme);
        } else {
            activity.setTheme(R.style.AppTheme_dark);
        }
    }

    public static SpannableString textSpannable(String text) {
        SpannableString s;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            s = new SpannableString(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            s = new SpannableString(Html.fromHtml(text));
        }

        Linkify.addLinks(s, Linkify.WEB_URLS);
        return s;
    }

    public static String secString(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        } else {
            return string.replaceAll("'", "\'\'");
        }
    }

    public static String fileName(String url) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        String domain = Uri.parse(url).getHost().replace("www.", "").trim();

        return domain.replace(".", "_").trim() + "_" + currentTime.trim();
    }
}