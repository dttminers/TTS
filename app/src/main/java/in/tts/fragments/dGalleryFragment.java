//package in.tts.fragments;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import java.io.File;
//import java.util.ArrayList;
//
//import in.tts.R;
//
//public class GallersyFragment extends Fragment {
//
//    AsyncTaskLoadFiles myAsyncTaskLoadFiles;
//
//    public class AsyncTaskLoadFiles extends AsyncTask<Void, String, Void> {
//
//        File targetDirector;
//        ImageAdapter myTaskAdapter;
//
//        public AsyncTaskLoadFiles(ImageAdapter adapter) {
//            myTaskAdapter = adapter;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//
//            String targetPath = ExternalStorageDirectoryPath + "/test/";
//            targetDirector = new File(targetPath);
//            myTaskAdapter.clear();
//
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            File[] files = targetDirector.listFiles();
//            for (File file : files) {
//                publishProgress(file.getAbsolutePath());
//                if (isCancelled()) break;
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(String... values) {
//            myTaskAdapter.add(values[0]);
//            super.onProgressUpdate(values);
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            myTaskAdapter.notifyDataSetChanged();
//            super.onPostExecute(result);
//        }
//
//    }
//
//    public class ImageAdapter extends BaseAdapter {
//
//        private Context mContext;
//        ArrayList<String> itemList = new ArrayList<String>();
//
//        public ImageAdapter(Context c) {
//            mContext = c;
//        }
//
//        void add(String path) {
//            itemList.add(path);
//        }
//
//        void clear() {
//            itemList.clear();
//        }
//
//        void remove(int index) {
//            itemList.remove(index);
//        }
//
//        @Override
//        public int getCount() {
//            return itemList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            // TODO Auto-generated method stub
//            return itemList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ImageView imageView;
//            if (convertView == null) { // if it's not recycled, initialize some
//                // attributes
//                imageView = new ImageView(mContext);
//                imageView.setLayoutParams(new GridView.LayoutParams(220, 220));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                imageView.setPadding(8, 8, 8, 8);
//            } else {
//                imageView = (ImageView) convertView;
//            }
//
//            Bitmap bm = decodeSampledBitmapFromUri(itemList.get(position), 220,
//                    220);
//
//            imageView.setImageBitmap(bm);
//            return imageView;
//        }
//
//        public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
//                                                 int reqHeight) {
//
//            Bitmap bm = null;
//            // First decode with inJustDecodeBounds=true to check dimensions
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(path, options);
//
//            // Calculate inSampleSize
//            options.inSampleSize = calculateInSampleSize(options, reqWidth,
//                    reqHeight);
//
//            // Decode bitmap with inSampleSize set
//            options.inJustDecodeBounds = false;
//            bm = BitmapFactory.decodeFile(path, options);
//
//            return bm;
//        }
//
//        public int calculateInSampleSize(
//
//                BitmapFactory.Options options, int reqWidth, int reqHeight) {
//            // Raw height and width of image
//            final int height = options.outHeight;
//            final int width = options.outWidth;
//            int inSampleSize = 1;
//
//            if (height > reqHeight || width > reqWidth) {
//                if (width > height) {
//                    inSampleSize = Math.round((float) height
//                            / (float) reqHeight);
//                } else {
//                    inSampleSize = Math.round((float) width / (float) reqWidth);
//                }
//            }
//
//            return inSampleSize;
//        }
//    }
//    ImageAdapter myImageAdapter;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_gallery, container, false);
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
////        setContentView(R.layout.activity_main);
//
//        final GridView gridview = (GridView) getActivity().findViewById(R.id.gridView);
//        myImageAdapter = new ImageAdapter(getContext());
//        gridview.setAdapter(myImageAdapter);
//
//        /*
//         * Move to asyncTaskLoadFiles String ExternalStorageDirectoryPath =
//         * Environment .getExternalStorageDirectory() .getAbsolutePath();
//         *
//         * String targetPath = ExternalStorageDirectoryPath + "/test/";
//         *
//         * Toast.makeText(getApplicationContext(), targetPath,
//         * Toast.LENGTH_LONG).show(); File targetDirector = new
//         * File(targetPath);
//         *
//         * File[] files = targetDirector.listFiles(); for (File file : files){
//         * myImageAdapter.add(file.getAbsolutePath()); }
//         */
//        myAsyncTaskLoadFiles = new AsyncTaskLoadFiles(myImageAdapter);
//        myAsyncTaskLoadFiles.execute();
//
//        gridview.setOnItemClickListener(myOnItemClickListener);
//
////        Button buttonReload = (Button)getContext().findViewById(R.id.reload);
////        buttonReload.setOnClickListener(new OnClickListener(){
////
////            @Override
////            public void onClick(View arg0) {
//
//                //Cancel the previous running task, if exist.
//                myAsyncTaskLoadFiles.cancel(true);
//
//                //new another ImageAdapter, to prevent the adapter have
//                //mixed files
//                myImageAdapter = new ImageAdapter(getContext());
//                gridview.setAdapter(myImageAdapter);
//                myAsyncTaskLoadFiles = new AsyncTaskLoadFiles(myImageAdapter);
//                myAsyncTaskLoadFiles.execute();
////            }});
//
//    }
//
//    AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position,
//                                long id) {
//            String prompt = "remove " + (String) parent.getItemAtPosition(position);
//            Toast.makeText(getContext(), prompt, Toast.LENGTH_SHORT)
//                    .show();
//
//            myImageAdapter.remove(position);
//            myImageAdapter.notifyDataSetChanged();
//
//        }
//    };
//
//}
//
//
//
////
////
////import android.app.Activity;
////import android.content.ActivityNotFoundException;
////import android.content.DialogInterface;
////import android.content.Intent;
////import android.database.Cursor;
////import android.graphics.Bitmap;
////import android.net.Uri;
////import android.os.Bundle;
////import android.os.Environment;
////import android.provider.MediaStore;
////import android.support.annotation.Nullable;
////import android.support.v4.app.Fragment;
////import android.support.v7.app.AlertDialog;
////import android.util.Log;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.AdapterView;
////import android.widget.GridView;
////import android.widget.ImageView;
////import android.widget.Toast;
////
////import com.facebook.internal.Utility;
////
////import java.io.File;
////
////import in.tts.R;
////import in.tts.adapters.GridViewAdapter;
////
////import static android.app.Activity.RESULT_OK;
////
////
/////**
//// * A simple {@link Fragment} subclass.
//// */
////public class GalleryFragment extends Fragment {
////    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
////    Uri picUri;
////
////    // Declare variables
////    private String[] FilePathStrings;
////    private String[] FileNameStrings;
////    private File[] listFile;
////    GridView grid;
////    GridViewAdapter adapter;
////    File file;
////
////    public GalleryFragment() {
////        // Required empty public constructor
////    }
////
////
////    @Override
////    public View onCreateView(LayoutInflater inflater, ViewGroup container,
////                             Bundle savedInstanceState) {
////        // Inflate the layout for this fragment
////        return inflater.inflate(R.layout.fragment_gallery, container, false);
////
////    }
////
////    @Override
////    public void onCreate(@Nullable Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
//////Toast.makeText(getContext(), " Unable to Display Data", Toast.LENGTH_SHORT).show();
////        //        galleryIntent();
//////        display();
//////        Intent i = new Intent(
//////                Intent.ACTION_PICK,
//////                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//////
//////        startActivityForResult(i, 1);
////    }
////
////    private void display() {
////        // Check for SD Card
////        if (!Environment.getExternalStorageState().equals(
////                Environment.MEDIA_MOUNTED)) {
////            Toast.makeText(getContext(), "Error! No SDCARD Found!", Toast.LENGTH_LONG)
////                    .show();
////        } else {
////            // Locate the image folder in your SD Card
////            file = new File(Environment.getExternalStorageDirectory()
////                    + File.separator + "SDImageTutorial");
////            // Create a new folder if no folder named SDImageTutorial exist
////            file.mkdirs();
////        }
////
////        if (file.isDirectory()) {
////            listFile = file.listFiles();
////            // Create a String array for FilePathStrings
////            FilePathStrings = new String[listFile.length];
////            // Create a String array for FileNameStrings
////            FileNameStrings = new String[listFile.length];
////
////            for (int i = 0; i < listFile.length; i++) {
////                // Get the path of the image file
////                FilePathStrings[i] = listFile[i].getAbsolutePath();
////                // Get the name image file
////                FileNameStrings[i] = listFile[i].getName();
////            }
////        }
////
////        // Locate the GridView in gridview_main.xml
////        grid = getActivity().findViewById(R.id.gridView);
////        Log.d("TAG", " FILEPAth " + FileNameStrings.length + " : "+ FilePathStrings.length);
////        // Pass String arrays to LazyAdapter Class
////        adapter = new GridViewAdapter(getContext(), FilePathStrings, FileNameStrings);
////        // Set the LazyAdapter to the GridView
////        grid.setAdapter(adapter);
////
////        // Capture gridview item click
////        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view,
////                                    int position, long id) {
////
//////                Intent i = new Intent(GalleryFragment.this, ViewImage.class);
//////                // Pass String arrays FilePathStrings
//////                i.putExtra("filepath", FilePathStrings);
//////                // Pass String arrays FileNameStrings
//////                i.putExtra("filename", FileNameStrings);
//////                // Pass click position
//////                i.putExtra("position", position);
//////                startActivity(i);
////            }
////
////        });
////    }
////
////
//////    @Override
//////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//////        super.onActivityResult(requestCode, resultCode, data);
//////
//////        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
//////            Uri selectedImage = data.getData();
//////            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//////
//////            Cursor cursor = getContentResolver().query(selectedImage,
//////                    filePathColumn, null, null, null);
//////            cursor.moveToFirst();
//////
//////            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//////            String picturePath = cursor.getString(columnIndex);
//////            cursor.close();
//////
//////            ImageView imageView = (ImageView) getActivity().findViewById(R.id.imgView);
//////
//////            Bitmap bmp = null;
//////            try {
//////                bmp = getBitmapFromUri(selectedImage);
//////            } catch (IOException e) {
//////                // TODO Auto-generated catch block
//////                e.printStackTrace();
//////            }
//////            imageView.setImageBitmap(bmp);
//////
//////        }
////
////    private void cameraIntent() {
////        try {
////            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////            String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
////            File imageFile = new File(imageFilePath);
////            picUri = Uri.fromFile(imageFile); // convert path to Uri
////            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
////            startActivityForResult(intent, REQUEST_CAMERA);
////        } catch (Exception | Error e) {
////            e.printStackTrace();
////        }
////    }
////
////    private void galleryIntent() {
////        try {
////            Intent intent = new Intent();
////            intent.setType("image/*");
////            intent.setAction(Intent.ACTION_GET_CONTENT);
////            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
////        } catch (Exception | Error e) {
////            e.printStackTrace();
////        }
////    }
////
////    @Override
////    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        try {
////            if (resultCode == RESULT_OK) {
////                if (requestCode == SELECT_FILE)
////                    onSelectFromGalleryResult(data);
////                else if (requestCode == REQUEST_CAMERA)
////                    onCaptureImageResult(data);
////                else if (requestCode == 3){
////                    // get the returned data
////                    Bundle extras = data.getExtras();
////                    Log.d("hmapp", " crop " + extras);
////                    // get the cropped bitmap
////                    Bitmap thePic = (Bitmap) extras.get("data");
////                    Log.d("hmapp", " crop " + thePic + " ; " + thePic.getByteCount());
//////                    CommonFunctions.toSaveImages(thePic, "HM_PP", true, getContext(), getActivity());
//////                    mCivUpeProfile.setImageBitmap(thePic);
////                }
////            }
////        } catch (Exception | Error e) {
////            e.printStackTrace();
////        }
////    }
////
////    private void onCaptureImageResult(Intent data) {
////        try {
////            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//////            mCivUpeProfile.setImageBitmap(thumbnail);
////            CropImage(picUri);
//////            CommonFunctions.toSaveImages(thumbnail, "HMC", true, getContext(), getActivity());
////        } catch (Exception | Error e) {
////            e.printStackTrace();
////        }
////    }
////
////    private void onSelectFromGalleryResult(Intent data) {
////        if (data != null) {
////            try {
////                Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
//////                mCivUpeProfile.setImageBitmap(bm);
////                CropImage(data.getData());
//////                CommonFunctions.toSaveImages(bm, "HMG", true, getContext(), getActivity());
////            } catch (Exception | Error e) {
////                e.printStackTrace();
////            }
////        }
////    }
////
////    private void CropImage(Uri picUri) {
////        try {
////            Log.d("Hmapp", " Crop image " + picUri);
////            //call the standard crop action intent (the user device may not support it)
////            Intent cropIntent = new Intent("com.android.camera.action.CROP");
////            //indicate image type and Uri
////            cropIntent.setDataAndType(picUri, "image/*");
////            //set crop properties
////            cropIntent.putExtra("crop", "true");
////            //indicate aspect of desired crop
////            cropIntent.putExtra("aspectX", 1);
////            cropIntent.putExtra("aspectY", 1);
////            //indicate output X and Y
////            cropIntent.putExtra("outputX", 256);
////            cropIntent.putExtra("outputY", 256);
////            //retrieve data on return
////            cropIntent.putExtra("return-data", true);
////            //start the activity - we handle returning in onActivityResult
////            startActivityForResult(cropIntent, 3);
////        } catch (ActivityNotFoundException e) {
//////            Toast.makeText(this, "Your device is not supportting the crop action", Toast.LENGTH_SHORT);
////        }
////    }
////
////
////
////
////
////}
