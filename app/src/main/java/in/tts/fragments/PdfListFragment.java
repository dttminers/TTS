package in.tts.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.tts.R;
import in.tts.adapters.PdfListModelAdapter;
import in.tts.model.PdfModel;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;
import in.tts.utils.DatabaseHelper;

public class PdfListFragment extends Fragment {

    private PdfListModelAdapter mAdapter;
    private List<PdfModel> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
//    private DatabaseHelper db;

    int pStatus = 0;
    private Handler handler = new Handler();
    TextView tv;
    private RelativeLayout rl;

    private static ArrayList<PdfModel> fileList = new ArrayList<>();
    private static ArrayList<String> list = new ArrayList<>();


    public boolean status = false;

    private OnFragmentInteractionListener mListener;

    public PdfListFragment() {
        // Required empty public constructor
    }

    public static PdfListFragment newInstance() {
        return new PdfListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonMethod.toReleaseMemory();
        tv = getActivity().findViewById(R.id.tv12);
        rl = getActivity().findViewById(R.id.rlLoader12);
        recyclerView = getActivity().findViewById(R.id.rvList12);

        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        final ProgressBar mProgress = (ProgressBar) getActivity().findViewById(R.id.circularProgressbar12);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);
        }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CommonMethod.toReleaseMemory();
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setLoadData() {
        if (!status){
            new toGet().execute();
        }
    }

    private class toGet extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tv.setText(String.valueOf(values));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            db = new DatabaseHelper(getContext());
//            notesList.addAll(db.getAllNotes());
            mAdapter = new PdfListModelAdapter(getContext(), fileList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            rl.setVisibility(View.GONE);

            new PrefManager(getContext()).toSetPDFFileList(list, true);
        }
    }

    public void getFile(final File dir) {
        try {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    File listFile[] = dir.listFiles();
                    if (listFile != null && listFile.length > 0) {
                        for (int i = 0; i < listFile.length; i++) {
                            if (listFile[i].isDirectory()) {
                                getFile(listFile[i]);
                            } else {
                                boolean booleanpdf = false;
                                if (listFile[i].getName().endsWith(".pdf")) {
                                    for (int j = 0; j < fileList.size(); j++) {
                                        if (fileList.get(j).equals(listFile[i].getPath())) {
                                            booleanpdf = true;
                                        }
                                    }
                                    if (booleanpdf) {
                                        booleanpdf = false;
                                    } else {
                                        list.add(listFile[i].getPath());
                                        fileList.add(new PdfModel(fileList.size(), listFile[i].getPath(), String.valueOf(new Date(listFile[i].lastModified())), String.valueOf(listFile[i].getTotalSpace())));
//                                        long id = db.insertNote(listFile[i].getPath().trim(), String.valueOf(new Date(listFile[i].lastModified())));
//                                        Log.d("TAG", "ToStorePdfList PDF " + id);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (pStatus < 100) {
//                    pStatus += 1;
//
//                    handler.post(new Runnable() {
//
//                        @Override
//                        public void run() {
////                            mProgress.setProgress(pStatus);
////                            tv.setText(pStatus + "%");
//
//                        }
//                    });
//                    try {
//                        // Sleep for 200 milliseconds.
//                        // Just to display the progress slowly
//                        Thread.sleep(16); //thread will take approx 3 seconds to finish
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }).start();
//    }
//        CommonMethod.toReleaseMemory();
//        if (!status) {
//            Resources res = getResources();
//            Drawable drawable = res.getDrawable(R.drawable.circular);
//            final ProgressBar mProgress = (ProgressBar) getActivity().findViewById(R.id.circularProgressbar);
//            mProgress.setProgress(0);   // Main Progress
//            mProgress.setSecondaryProgress(100); // Secondary Progress
//            mProgress.setMax(100); // Maximum Progress
//            mProgress.setProgressDrawable(drawable);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    db = new DatabaseHelper(getContext());
//
//                    notesList.addAll(db.getAllNotes());
//
//                    mAdapter = new PdfListModelAdapter(getContext(), notesList);
//                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//                    recyclerView.setLayoutManager(mLayoutManager);
//                    recyclerView.setItemAnimator(new DefaultItemAnimator());
////        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
//                    recyclerView.setAdapter(mAdapter);
//                }
//            }, 100);
//
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    while (pStatus < 100) {
//                        pStatus += 1;
//
//                        handler.post(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                mProgress.setProgress(pStatus);
//                                tv.setText(pStatus + "%");
//                                if (pStatus == 100){
//                                    rl.setVisibility(View.GONE);
//                                }
//                            }
//                        });
////                        try {
////                            // Sleep for 200 milliseconds.
////                            // Just to display the progress slowly
////                            Thread.sleep(16); //thread will take approx 3 seconds to finish
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
//                    }
//
//                }
//            }).start();
//            CommonMethod.toReleaseMemory();
//        }
//    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onStop() {
        super.onStop();
        CommonMethod.toReleaseMemory();
    }
}
