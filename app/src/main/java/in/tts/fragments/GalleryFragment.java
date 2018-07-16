package in.tts.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.ImageAdapterGallery;

import in.tts.model.AppData;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetImages;

public class GalleryFragment extends Fragment {

    private  ArrayList<String> fileName = new ArrayList<>();
    public GalleryFragment() {
    }

    @Nullable
    @Override
    @AddTrace(name = "onCreateGalleryFragment", enabled = true)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fn_permissions();
    }

    private void fn_permissions() {
        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
//            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
//            }
        } else {
            toGetData();
        }
    }

    private void toGetData() {
        try {
            CommonMethod.toCallLoader(getContext(), "Loading");
            RecyclerView recyclerView = getActivity().findViewById(R.id.rvGallery);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//            if (AppData.fileName == null) {
            if (getActivity() != null) {
                recyclerView.setAdapter(new ImageAdapterGallery(getActivity(), getAllShownImagesPath(getActivity())));
            }
//            } else {
//                recyclerView.setAdapter(new ImageAdapterGallery(getActivity(), AppData.fileName));
//            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toCloseLoader();
        }
    }
    public ArrayList<String> getAllShownImagesPath(final Activity activity) {
        try {
            Cursor cursor;

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
//            AppData.fileName = fileName;

        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
        return fileName;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toGetData();
            } else {
                Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
            }
        } else {
            fn_permissions();
        }
    }

    private OnFragmentInteractionListener mListener;

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}