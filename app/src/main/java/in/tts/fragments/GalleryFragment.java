package in.tts.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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

import in.tts.R;
import in.tts.adapters.ImageAdapterGallery;

import in.tts.model.PrefManager;
import in.tts.utils.AppPermissions;
import in.tts.utils.CommonMethod;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetImages;

public class GalleryFragment extends Fragment {

    private PrefManager prefManager;

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

        prefManager = new PrefManager(getContext());
//        fn_permissions();
        AppPermissions.toCheckPermissionRead(getContext(), getActivity(), null, null, GalleryFragment.this, false);
    }

//    private void fn_permissions() {
//        try {
//            if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
////            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
////            } else {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
////            }
//            } else {
//                toGetData();
//            }
//        } catch (Exception | Error e) {
//            e.printStackTrace();
//            Crashlytics.logException(e);
//        }
//    }

    public void toGetData() {
        try {
            CommonMethod.toCallLoader(getContext(), "Loading");
            if (getActivity() != null) {
                RecyclerView recyclerView = getActivity().findViewById(R.id.rvGallery);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                if (prefManager.toGetImageList() == null) {
                    Log.d("TAG", "Gallery 10 ");
                    prefManager.toSetImageFileList(ToGetImages.getAllShownImagesPath(getActivity()));
                    recyclerView.setAdapter(new ImageAdapterGallery(getActivity(), prefManager.toGetImageList()));
                } else {
                    Log.d("TAG", "Gallery 11 ");
                    recyclerView.setAdapter(new ImageAdapterGallery(getActivity(), prefManager.toGetImageList()));
                }
            }
            Log.d("TAG", "Gallery 12 ");
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            CommonMethod.toCloseLoader();
        }
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
        try{
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