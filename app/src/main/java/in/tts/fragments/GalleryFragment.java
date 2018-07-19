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
import in.tts.utils.CommonMethod;

public class GalleryFragment extends Fragment {

    private ArrayList<String> imageFile;
    private ImageAdapterGallery imageAdapterGallery;

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
        CommonMethod.toCloseLoader();
    }

    @Override
    public void onStart() {
        super.onStart();
        CommonMethod.toReleaseMemory();
        try {
            fn_permission();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                toGetData();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void toGetData() {
        try {
            CommonMethod.toCallLoader(getContext(), "Loading");
            if (getActivity() != null) {
                imageFile = new ArrayList<>();
                imageAdapterGallery = new ImageAdapterGallery(getActivity(), imageFile);

                getAllShownImagesPath(getActivity());
                RecyclerView recyclerView = getActivity().findViewById(R.id.rvGallery);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerView.setAdapter(imageAdapterGallery);
                imageAdapterGallery.notifyDataSetChanged();
            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            CommonMethod.toCloseLoader();
        }
    }

    public void getAllShownImagesPath(Activity activity) {
        try{
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
                    imageAdapterGallery.notifyItemChanged(imageFile.size(), imageFile);
                    Log.d("TAG", "File Name " + imageLocation);
                }
                Log.d("TAG", "Count Image Files " + imageFile.size());
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            CommonMethod.toCloseLoader();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toGetData();
            } else {
                Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
            }
        } else {
            toGetData();
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
        try {
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
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