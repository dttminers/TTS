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
import java.util.Objects;

import in.tts.R;
import in.tts.adapters.PdfListAdapter;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

public class PdfListFragment extends Fragment {

    private RecyclerView recyclerView;
    private PdfListAdapter pdfListAdapter;

    private TextView tv;
    private RelativeLayout rl;
    private ProgressBar mProgress;

    private ArrayList<String> list = new ArrayList<>();

    private boolean status = false;

    private int pStatus = 0;
    private Handler handler = new Handler();

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
        try {
            CommonMethod.toReleaseMemory();

            tv = Objects.requireNonNull(getActivity()).findViewById(R.id.tv12);
            rl = Objects.requireNonNull(getActivity()).findViewById(R.id.rlLoader12);

            recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.rvList12);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public void onButtonPressed(Uri uri) {
        try {
            if (mListener != null) {
                mListener.onFragmentInteraction(uri);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            CommonMethod.toReleaseMemory();
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public void setLoadData() {
        try {
            if (!status) {
                Resources res = getResources();
                Drawable drawable = res.getDrawable(R.drawable.circular);
                mProgress = Objects.requireNonNull(getActivity()).findViewById(R.id.circularProgressbar12);
                mProgress.setProgress(0);
                mProgress.setSecondaryProgress(100);
                mProgress.setMax(100);
                mProgress.setProgressDrawable(drawable);

                pdfListAdapter = new PdfListAdapter(getContext(), list);
                new toGet().execute();
                try {
//                    Log.d("TAG", " countsd 1" + status);
//                    for (int i = 0; i < 100; i++) {
//                        Log.d("TAG", " countsd 2" + i + status);
//                        tv.setText(String.valueOf(i) + "%");
////                        i = i+10;
//                    }
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            while (pStatus < 100) {
                                pStatus += 1;

                                handler.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        mProgress.setProgress(pStatus);
                                        tv.setText(pStatus + "%");

                                    }
                                });
                                try {
                                    // Sleep for 200 milliseconds.
                                    // Just to display the progress slowly
                                    Thread.sleep(16); //thread will take approx 3 seconds to finish
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
//                            rl.setVisibility(View.GONE);
                        }
                    }).start();
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                    CommonMethod.toCloseLoader();
                    Crashlytics.logException(e);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    private class toGet extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (new PrefManager(getContext()).toGetPDFList() != null && new PrefManager(getContext()).toGetPDFList().size() != 0) {
                    list = new PrefManager(getContext()).toGetPDFList();
                }
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                CommonMethod.toCloseLoader();
                Crashlytics.logException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                pdfListAdapter = new PdfListAdapter(getContext(), list);
                recyclerView.setAdapter(pdfListAdapter);
                pdfListAdapter.notifyDataSetChanged();
                rl.setVisibility(View.GONE);
                status = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
                    }
                }, 2000);
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                CommonMethod.toCloseLoader();
                Crashlytics.logException(e);
            }
        }
    }

    public void getFile(final File dir) {
        try {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        File listFile[] = dir.listFiles();
                        if (listFile != null && listFile.length > 0) {
                            for (int i = 0; i < listFile.length; i++) {
                                if (listFile[i].isDirectory()) {
                                    getFile(listFile[i]);
                                } else {
                                    boolean booleanpdf = false;
                                    if (listFile[i].getName().endsWith(".pdf")) {
                                        for (int j = 0; j < list.size(); j++) {
                                            if (list.get(j).equals(listFile[i].getPath())) {
                                                booleanpdf = true;
                                            }
                                        }
                                        if (booleanpdf) {
                                            booleanpdf = false;
                                        } else {
                                            Log.d("TAG", " count  2:" + list.size() + ":" + pdfListAdapter.getItemCount());
                                            if (!list.contains(listFile[i].getPath().trim().replaceAll("\\s", "%20"))) {
                                                list.add(listFile[i].getPath().trim().replaceAll("\\s", "%20"));
                                                Log.d("TAG", " count  3:" + list.size() + ":" + pdfListAdapter.getItemCount());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        CommonMethod.toCloseLoader();
                        Crashlytics.logException(e);
                    }
                }
            });
            Log.d("TAG", " count  5:" + list.size() + ":" + pdfListAdapter.getItemCount());
            new PrefManager(getContext()).toSetPDFFileList(list);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    pdfListAdapter.notifyDataSetChanged();
                    Log.d("TAG", " count  7:" + list.size() + ":" + pdfListAdapter.getItemCount());
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            mListener = null;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
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