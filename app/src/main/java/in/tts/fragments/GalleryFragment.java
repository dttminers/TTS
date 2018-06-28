package in.tts.fragments;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.internal.Utility;

import java.io.File;

import in.tts.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends Fragment {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Uri picUri;


    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleryIntent();
    }

    private void cameraIntent() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
            File imageFile = new File(imageFilePath);
            picUri = Uri.fromFile(imageFile); // convert path to Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void galleryIntent() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
                else if (requestCode == 3){
                    // get the returned data
                    Bundle extras = data.getExtras();
                    Log.d("hmapp", " crop " + extras);
                    // get the cropped bitmap
                    Bitmap thePic = (Bitmap) extras.get("data");
                    Log.d("hmapp", " crop " + thePic + " ; " + thePic.getByteCount());
//                    CommonFunctions.toSaveImages(thePic, "HM_PP", true, getContext(), getActivity());
//                    mCivUpeProfile.setImageBitmap(thePic);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void onCaptureImageResult(Intent data) {
        try {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//            mCivUpeProfile.setImageBitmap(thumbnail);
            CropImage(picUri);
//            CommonFunctions.toSaveImages(thumbnail, "HMC", true, getContext(), getActivity());
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
//                mCivUpeProfile.setImageBitmap(bm);
                CropImage(data.getData());
//                CommonFunctions.toSaveImages(bm, "HMG", true, getContext(), getActivity());
            } catch (Exception | Error e) {
                e.printStackTrace();
            }
        }
    }

    private void CropImage(Uri picUri) {
        try {
            Log.d("Hmapp", " Crop image " + picUri);
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 3);
        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, "Your device is not supportting the crop action", Toast.LENGTH_SHORT);
        }
    }





}
