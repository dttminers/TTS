package in.tts.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.File;

import in.tts.activities.CameraOcrActivity;
import in.tts.fragments.GalleryFragment;
import in.tts.fragments.MainHomeFragment;
import in.tts.fragments.MyBooksFragment;
import in.tts.model.PrefManager;

public class AppPermissions {

    public static void toCheckPermissionRead(Context context, Activity activity, MainHomeFragment mainHomeFragment, MyBooksFragment myBooksFragment, GalleryFragment galleryFragment, boolean b) {
        try {
            PrefManager prefManager = new PrefManager(context);
            if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                if (!b) {
                    Log.d("TAG", "APPpermission 11 ");
                    if (mainHomeFragment != null) {
                        Log.d("TAG", "APPpermission 12 ");
                        mainHomeFragment.toSetSomeData();
                    } else if (myBooksFragment != null) {
                        Log.d("TAG", "APPpermission 13 ");
                        myBooksFragment.toSetView();
                    } else if (galleryFragment != null){
                        Log.d("TAG", "APPpermission 131 ");
                        galleryFragment.toGetData();
                    } else {
                        Log.d("TAG", "APPpermission 14 ");
                        toSetData(context, activity, prefManager);
                    }
                } else {
                    Log.d("TAG", "APPpermission 135 ");
                    prefManager.toSetPDFFileList(ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), null));
                    prefManager.toSetImageFileList(ToGetImages.getAllShownImagesPath(activity));
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private static void toSetData(Context context, Activity activity, PrefManager prefManager) {
        try {
            if (prefManager.toGetPDFList() != null) {
                if (prefManager.toGetPDFList().size() == 0) {
                    Log.d("TAG", "APPpermission 21 ");
                    CommonMethod.toDisplayToast(context, "No PDF Found");
                } else {
                    Log.d("TAG", "APPpermission 22 ");
                    ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), null);
                }
            } else {
                Log.d("TAG", "APPpermission 23 ");
                prefManager.toSetPDFFileList(ToGetPdfFiles.getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), null));
            }
            if (prefManager.toGetImageList() != null) {
                if (prefManager.toGetImageList().size() == 0) {
                    prefManager.toSetImageFileList(ToGetImages.getAllShownImagesPath(activity));
                }
            } else {
                prefManager.toSetImageFileList(ToGetImages.getAllShownImagesPath(activity));
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public static void toCheckPermissionWrite(Context context, Activity activity) {
        try {
            if ((ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public static void toCheckPermissionCamera(Context context, Activity activity, CameraOcrActivity cameraOcrActivity) {
        try {
            if ((ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 3);
            } else {
                if (cameraOcrActivity != null) {
                    cameraOcrActivity.startCameraSource();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}