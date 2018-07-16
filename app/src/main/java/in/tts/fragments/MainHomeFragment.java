package in.tts.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import in.tts.R;
import in.tts.adapters.CustomPagerAdapter;
import in.tts.adapters.PDFHomePage;
import in.tts.adapters.PDFHomePageImages;
import in.tts.model.AppData;
import in.tts.utils.CommonMethod;

public class MainHomeFragment extends Fragment {

    // Main View Pager
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ImageView imageView;
    private ImageView ivLeft, ivRight;

    private int currentImage = 0;
    private int mResources[] = {R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5};

    private ArrayList<File> fileList;
    private ArrayList<String> fileName;

    // Other data
    private LinearLayout ll;
    private View view, view1;


    private OnFragmentInteractionListener mListener;

    public MainHomeFragment() {
        // Required empty public constructor
    }

    public static MainHomeFragment newInstance(String param1, String param2) {
        return new MainHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            toBindViews();
            toBindTopBanners();
            fn_permission();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }


    private void fn_permission() {
        try {
            Log.d("TAG", " pdf permission ");
            if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
//                    Log.d("TAG", "Home0131 ");
//                } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
                Log.d("TAG", "Home0231 ");
//                }
            } else {
                Log.d("TAG", "Home0331 ");
//                toGetPDF();
                toSetOtherData();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }

    }

    private void toBindTopBanners() throws Exception, Error {
        mViewPager.setAdapter(new CustomPagerAdapter(mResources, getContext()));
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void toBindViews() throws Exception, Error {
        fileList = new ArrayList<>();
        fileName = new ArrayList<>();

        imageView = getActivity().findViewById(R.id.imageView);
        ivLeft = getActivity().findViewById(R.id.imageViewLeft1);
        ivRight = getActivity().findViewById(R.id.imageViewRight1);
        tabLayout = getActivity().findViewById(R.id.tlHomePage);
        mViewPager = getActivity().findViewById(R.id.vpHomePage);

        ll = getActivity().findViewById(R.id.llData);

        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Increase Counter to move to next Image
                if (currentImage == 0) {
                    currentImage = mResources.length - 1;
                    mViewPager.setCurrentItem(currentImage);
                } else {
                    currentImage--;
                    mViewPager.setCurrentItem(currentImage);
                }
            }
        });

        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentImage == mResources.length - 1) {
                    currentImage = 0;
                    mViewPager.setCurrentItem(currentImage);
                } else {
                    currentImage++;
                    mViewPager.setCurrentItem(currentImage);
                }
            }
        });
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toSetOtherData();
            } else {
                Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void toSetOtherData() {
        try {
            CommonMethod.toCallLoader(getContext(), "Loading....");
            new ToSetPDF().execute();
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            CommonMethod.toCloseLoader();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void setCurrentViewPagerItem(int i);
    }

    private class ToSetPDF extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            getfile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
            getAllShownImagesPath(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            toBindRecentPdf();
            toBindRecentImages();
            CommonMethod.toCloseLoader();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d("TaG", " Values " + Arrays.toString(values));
        }
    }

    private void toBindRecentImages() {
        try {
            if (view1 != null) {
                ll.removeView(view1);
            }
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                view1 = inflater.inflate(R.layout.layout_home_page_recent_items1, null, false);

                TextView tvHeader = view1.findViewById(R.id.tvRecent);
                TextView tvSeeMore = view1.findViewById(R.id.tvSeeMore);

                ViewPager vpDeals = view1.findViewById(R.id.vpRecentItem);

                tvHeader.setText("Recent Images");
                tvSeeMore.setText("See More");

                vpDeals.setClipToPadding(false);
                vpDeals.setOffscreenPageLimit(3);
                vpDeals.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                vpDeals.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
//                pdfHomePageImages = new PDFHomePageImages(getContext(), fileName);
                vpDeals.setAdapter(new PDFHomePageImages(getContext(), fileName));
//                pdfHomePageImages.notifyDataSetChanged();

                tvSeeMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            mListener.setCurrentViewPagerItem(4);
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                            CommonMethod.toCloseLoader();
                            Crashlytics.logException(e);
                        }
                    }
                });

                ll.addView(view1);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    private void toBindRecentPdf() {
        try {
            if (view != null) {
                ll.removeView(view);
            }
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                view = inflater.inflate(R.layout.layout_home_page_recent_items, null, false);

                TextView tvHeader = view.findViewById(R.id.tvRecent);
                TextView tvSeeMore = view.findViewById(R.id.tvSeeMore);

                ViewPager vpDeals = view.findViewById(R.id.vpRecentItem);

                tvHeader.setText("Recent PDF");
                tvSeeMore.setText("See More");

                tvSeeMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            mListener.setCurrentViewPagerItem(1);
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                            CommonMethod.toCloseLoader();
                            Crashlytics.logException(e);
                        }
                    }
                });

                vpDeals.setClipToPadding(true);
                vpDeals.setOffscreenPageLimit(3);
                vpDeals.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                vpDeals.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
//                pdfHomePage = new PDFHomePage(getContext(), list);
                vpDeals.setAdapter(new PDFHomePage(getContext(), fileList));
//                pdfHomePage.notifyDataSetChanged();

                ll.addView(view);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public ArrayList<File> getfile(final File dir) {
        Log.d("TAG ", " PATH : "+ dir );

        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getfile(listFile[i]);
                } else {
                    boolean booleanpdf = false;
                    if (listFile[i].getName().endsWith(".pdf")) {
                        for (int j = 0; j < fileList.size(); j++) {
                            if (fileList.get(j).getName().equals(listFile[i].getName())) {
                                booleanpdf = true;
                            } else {
                            }
                        }
                        if (booleanpdf) {
                            booleanpdf = false;
                        } else {
                            fileList.add(listFile[i]);
                        }
                    }
                }
            }
        }
        Log.d("TAG", " pdf count " + fileList.size());
        return fileList;
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
}
