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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.adapters.ImageAdapterGallery;
import in.tts.utils.CommonMethod;

public class GalleryFragment extends Fragment {

    //    private GridView gallery;
    private ArrayList<String> images;
    private RecyclerView recyclerView;

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
//        gallery = getActivity().findViewById(R.id.gridView);
        recyclerView = getActivity().findViewById(R.id.rvGallery);
        per();
        toGetData();
    }

    private void per() {
        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
            }
        } else {
            toGetData();
        }
    }

    private void toGetData() {
        try {
            CommonMethod.toCallLoader(getContext(), "Loading");
//            gallery.setAdapter(new ImageAdapter(getActivity()));
//            gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//
//                    if (null != images && !images.isEmpty()) {
//                        startActivity(new Intent(getContext(), ImageOcrActivity.class).putExtra("PATH", images.get(position)));
////                        Toast.makeText(getContext(), "position " + position + " " + images.get(position), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(new ImageAdapterGallery(getActivity(), getAllShownImagesPath(getActivity())));
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toCloseLoader();
        }
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(absolutePathOfImage);
        }
        Log.d("TAG", " DATa " + listOfAllImages.size()+":"+listOfAllImages);
        CommonMethod.toCloseLoader();
        if (listOfAllImages.size() == 0){
            Toast.makeText(getContext(),"Image not found",Toast.LENGTH_SHORT).show();
        }
        return listOfAllImages;
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
        }
    }
}

/*
    private class ImageAdapter extends BaseAdapter {

        private Activity context;

        public ImageAdapter(Activity localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
        }

        public int getCount() {
            Log.d("TAG", " count images " + images.size());
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                int size = width / 3;

                picturesView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size-10));
//                picturesView.setPadding(5, 5, 5, 5);

            } else {
                picturesView = (ImageView) convertView;
            }
//            Glide.with(context).load(images.get(position)).into(picturesView);
            Log.d("TAG","Images " + images.get(position) + " : " + position);
            Picasso.get().load("file://"+images.get(position).replaceAll("\\s", "%20")).into(picturesView);
            return picturesView;
        }
* */