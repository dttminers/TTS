package in.tts.fragments;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.ImageAdapterGallery;
import in.tts.adapters.PdfListAdapter;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class MyBooksListFragment extends Fragment {

    private ArrayList<String> file;
    private PdfListAdapter pdfListAdapter;
    private PrefManager prefManager;
    //    ProgressBar mProgress;
    RecyclerView recyclerView;
    //    RelativeLayout rl;
    LinearLayout rl;

    int pStatus = 0;
    private Handler handler = new Handler();
//    TextView tv;

    private OnFragmentInteractionListener mListener;

    public MyBooksListFragment() {
        // Required empty public constructor
    }


    public static MyBooksListFragment newInstance() {
        return new MyBooksListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //CommonMethod.toCloseLoader();
        return inflater.inflate(R.layout.fragment_my_books_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        CommonMethod.toReleaseMemory();
        try {
            prefManager = new PrefManager(getContext());
            file = new ArrayList<>();

//            tv = (TextView) getActivity().findViewById(R.id.tv);
//            mProgress = (ProgressBar) getActivity().findViewById(R.id.circularProgressbar);
            recyclerView = getActivity().findViewById(R.id.rvList);
            rl = getActivity().findViewById(R.id.llCustom_loader);

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

            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.circular_progress_bar);

//            mProgress.setProgress(0);   // Main Progress
//            mProgress.setSecondaryProgress(100); // Secondary Progress
//            mProgress.setMax(100); // Maximum Progress
//            mProgress.setProgressDrawable(drawable);
            if (getActivity() != null) {

                if (prefManager.toGetPDFList() != null && prefManager.toGetPDFList().size() != 0) {
                    file = prefManager.toGetPDFList();
                }
                pdfListAdapter = new PdfListAdapter(getActivity(), file);


                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


                recyclerView.setAdapter(pdfListAdapter);
                pdfListAdapter.notifyDataSetChanged();
                getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));


//                getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
                rl.setVisibility(View.GONE);
                pdfListAdapter.notifyItemChanged(file.size(), file);
            }
            //CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            //CommonMethod.toCloseLoader();
        }
    }

    public void getFile(final File dir) {
        try {
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    while (pStatus < 100) {
//                        pStatus += 1;
//
//                        handler.post(new Runnable() {
//
//                            @Override
//                            public void run() {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("TAG", " FILE ::: " + dir);
                    File listFile[] = dir.listFiles();
                    if (listFile != null && listFile.length > 0) {
                        for (int i = 0; i < listFile.length; i++) {
                            if (listFile[i].isDirectory()) {
                                getFile(listFile[i]);
                            } else {
                                boolean booleanpdf = false;
                                if (listFile[i].getName().endsWith(".pdf")) {
                                    for (int j = 0; j < file.size(); j++) {
//                                    if (fileList.get(j).getName().equals(listFile[i].getName())) {
                                        if (file.get(j).equals(listFile[i].getPath())) {
                                            booleanpdf = true;
                                        } else {
                                        }
                                    }
                                    if (booleanpdf) {
                                        booleanpdf = false;
                                    } else {
                                        if (!prefManager.toGetPDFList().contains(listFile[i].getPath().trim())) {
                                            file.add(listFile[i].getPath());
//                                            prefManager.toSetPDFFileList(file);
                                        }
                                    }
                                }
                            }


                        }
                    }
                    Log.d("TAG", " pdf count " + file.size());

//                                mProgress.setProgress(pStatus);
//                                tv.setText(pStatus + "%");

                }


            });
//                        Log.d("TAG", " END");
//                        try {
//                            // Sleep for 200 milliseconds.
//                            // Just to display the progress slowly
//                            Thread.sleep(16); //thread will take approx 3 seconds to finish
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();


        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
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
