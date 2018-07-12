package in.tts.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
import in.tts.adapters.PDFAdapter;
import in.tts.adapters.PDFHomePage;
import in.tts.adapters.PDFHomePageImages;
import in.tts.utils.CommonMethod;

public class HomePageFragment extends Fragment {
    ViewPager mViewPager;
    TabLayout tabLayout;
    ImageView imageView;
    ImageView ivLeft, ivRight;
    CustomPagerAdapter mCustomPagerAdapter;
    LinearLayout ll;
    int currentImage = 0;
    int mResources[] = {R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5};
    //    int mResources[] = {R.drawable.bg_main, R.drawable.bg_main, R.drawable.bg_main, R.drawable.bg_main, R.drawable.bg_main};
    boolean boolean_permission;
    private File dir;

    public static ArrayList<File> fileList = new ArrayList<>();
    public static ArrayList<String> fileName = new ArrayList<>();
    public static int REQUEST_PERMISSIONS = 1;


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

            mCustomPagerAdapter = new CustomPagerAdapter(mResources, getContext());
            mViewPager.setAdapter(mCustomPagerAdapter);
            tabLayout.setupWithViewPager(mViewPager);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

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
        toSetData();
    }

    private void toSetData() {
        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        fn_permission();
    }

    private void fn_permission() {

        try {
            Log.d("TAG", " pdf permission ");
            if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
                }
            } else {
                toGetPDF();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }

    }

    private void toGetPDF() {
        try {
            Log.d("TAG", " pdf getpdf ");
            boolean_permission = true;
            getfile(dir);
            getAllShownImagesPath(getActivity());

            if (fileList.size() != 0) {
                toBindDealProductData(fileList, "Recent PDF ", "See More");
            }

            if (fileName.size() != 0) {
                toBindDealProductDataimages(fileName, "Recent Images ", "See More");
            }
//            toBindDealProductData(fileList, "Recent PDF ", "See More");
//            toBindDealProductDataimages(getAllShownImagesPath(getActivity()), "Recent Images ", "See More");
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void toBindDealProductDataimages(ArrayList<String> fileList, String header, String see_more) {
        try {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                View view = inflater.inflate(R.layout.layout_home_page_recent_items, null, false);

                TextView tvHeader = (TextView) view.findViewById(R.id.tvRecent);
                TextView tvHeaderTimer = (TextView) view.findViewById(R.id.tvSeeMore);

                ViewPager vpDeals = (ViewPager) view.findViewById(R.id.vpRecentItem);

                tvHeader.setText(header);

                vpDeals.setClipToPadding(false);
                vpDeals.setPadding(15, 0, 15, 0);
                vpDeals.setOffscreenPageLimit(4);
                vpDeals.setPageMargin(10);
                vpDeals.setAdapter(new PDFHomePageImages(getContext(), fileList));

                ll.addView(view);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            fileName.add(absolutePathOfImage);
        }
        Log.d("TAG", " DATa " + fileName.size() + ":" + fileName);
        return fileName;
    }

    @AddTrace(name = "onGetPDF", enabled = true)
    public ArrayList<File> getfile(File dir) {
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


    private void toBindDealProductData(ArrayList<File> list, String header, String viewAll) {
        try {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                View view = inflater.inflate(R.layout.layout_home_page_recent_items, null, false);

                TextView tvHeader = (TextView) view.findViewById(R.id.tvRecent);
                TextView tvHeaderTimer = (TextView) view.findViewById(R.id.tvSeeMore);

                ViewPager vpDeals = (ViewPager) view.findViewById(R.id.vpRecentItem);

                tvHeader.setText(header);

                vpDeals.setClipToPadding(false);
//                vpDeals.setPadding(15, 0, 15, 0);
                vpDeals.setOffscreenPageLimit(4);
                vpDeals.setPageMargin(10);
                vpDeals.setAdapter(new PDFHomePage(getContext(), list));

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
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toGetPDF();
            } else {
                Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        int mResources[];

        public CustomPagerAdapter(int mResources[], Context context) {
            mContext = context;
            this.mResources = mResources;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mResources[position]);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }
}