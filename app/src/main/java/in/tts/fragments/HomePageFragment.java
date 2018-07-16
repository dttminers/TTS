package in.tts.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.MainActivity;
import in.tts.adapters.CustomPagerAdapter;
import in.tts.adapters.PDFAdapter;
import in.tts.adapters.PDFHomePage;
import in.tts.adapters.PDFHomePageImages;
import in.tts.model.AppData;
import in.tts.utils.CommonMethod;
import in.tts.utils.ToGetImages;
import in.tts.utils.ToGetPdfFiles;

public class HomePageFragment extends Fragment {

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ImageView imageView;
    private ImageView ivLeft, ivRight;
    private LinearLayout ll;
    private int currentImage = 0;
    private int mResources[] = {R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5};
    boolean boolean_permission;
    private File dir;
    private View view, view1;

    public static int REQUEST_PERMISSIONS = 1;
    private static ArrayList<File> fileList;
    private static ArrayList<String> fileName;

    // Adapters
    private CustomPagerAdapter customPagerAdapter;
    private PDFHomePage pdfHomePage;
    private PDFHomePageImages pdfHomePageImages;

    private OnFragmentInteractionListener mListener;

    public static HomePageFragment newInstance() {
        return new HomePageFragment();
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

        void setCurrentViewPagerItem(int i);
    }

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    @AddTrace(name = "onCreateHomePageFragment", enabled = true)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonMethod.setAnalyticsData(getContext(), "MainTab", "HomePage", null);
        try {

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

            customPagerAdapter = new CustomPagerAdapter(mResources, getContext());
            mViewPager.setAdapter(customPagerAdapter);
            tabLayout.setupWithViewPager(mViewPager);
            customPagerAdapter.notifyDataSetChanged();

            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
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
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
//                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
                Log.d("TAG", "Home0231 ");
//                }
            } else {
                Log.d("TAG", "Home0331 ");
//                toGetPDF();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

    }

    private void toGetPDF() {
        try {
            CommonMethod.toCallLoader(getContext(), "Loading...");
            boolean_permission = true;
            if (AppData.fileList != null) {
                if (AppData.fileList.size() != 0) {
                    Log.d("TAG", " ppk 1 " + AppData.fileList.size());
                    toBindDealProductData(AppData.fileList);
                } else {
                    Log.d("TAG", " ppk 2 ");
                    fileList = new ArrayList<>();
                    getfile(dir);
                }
            } else {
                Log.d("TAG", " ppk 3 ");
                fileList = new ArrayList<>();
                getfile(dir);
                toBindDealProductData(AppData.fileList);
            }

            if (AppData.fileName != null) {
                toBindDealProductDataImages(AppData.fileName);
            } else {
                fileName = new ArrayList<>();
                ToGetImages.getAllShownImagesPath(getActivity());
                toBindDealProductDataImages(fileName);
            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }


    public ArrayList<File> getfile(final File dir) {
        try {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
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
            Log.d("TAG", "home pdf count " + fileList.size());
            AppData.fileList = fileList;

//            }
//        });
//        toBindDealProductData(AppData.fileList);

        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return fileList;
    }

    public ArrayList<String> getAllShownImagesPath(final Activity activity) {
        try {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
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
            AppData.fileName = fileName;

//            }
//        });
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return fileName;
    }

    private void toBindDealProductDataImages(ArrayList<String> fileList) {
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
                pdfHomePageImages = new PDFHomePageImages(getContext(), fileList);
                vpDeals.setAdapter(pdfHomePageImages);
                pdfHomePageImages.notifyDataSetChanged();

                tvSeeMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            mListener.setCurrentViewPagerItem(4);
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }
                    }
                });

                ll.addView(view1);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void toBindDealProductData(ArrayList<File> list) {
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
                            Crashlytics.logException(e);
                        }
                    }
                });

                vpDeals.setClipToPadding(true);
                vpDeals.setOffscreenPageLimit(3);
                vpDeals.setPadding(CommonMethod.dpToPx(5, getActivity()), 0, CommonMethod.dpToPx(10, getActivity()), 0);
                vpDeals.setPageMargin(CommonMethod.dpToPx(10, getActivity()));
                pdfHomePage = new PDFHomePage(getContext(), list);
                vpDeals.setAdapter(pdfHomePage);
                pdfHomePage.notifyDataSetChanged();

                ll.addView(view);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == REQUEST_PERMISSIONS) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toGetPDF();
                    Log.d("TAG", "Home0311 ");
                } else {
                    Log.d("TAG", "Home0312 ");
                    Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("TAG", "Home0313 ");
//                fn_permission();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }
}