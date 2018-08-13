package in.tts.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import in.tts.R;
import in.tts.adapters.PdfListAdapter;
import in.tts.utils.CommonMethod;

public class MyBooksListFragment extends Fragment {

    private ArrayList<String> file;
    private PdfListAdapter pdfListAdapter;
    private RecyclerView recyclerView;
    private LinearLayout llCustom_loader;
    public boolean status = false;
    long start;
    private OnFragmentInteractionListener mListener;

    public MyBooksListFragment() {
    }

    public static MyBooksListFragment newInstance() {
        return new MyBooksListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_books_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.toReleaseMemory();
            file = new ArrayList<>();
            recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.rvList);
            llCustom_loader = Objects.requireNonNull(getActivity()).findViewById(R.id.llCustom_loader);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        CommonMethod.toReleaseMemory();

    }

    @Override
    public void onStart() {
        super.onStart();
        CommonMethod.toReleaseMemory();
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                toGetData();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void toGetData() {
        try {
            if (getActivity() != null) {
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);

                pdfListAdapter = new PdfListAdapter(getActivity(), file);
                recyclerView.setAdapter(pdfListAdapter);
                pdfListAdapter.notifyDataSetChanged();

                if (!status) {
//                    start = System.currentTimeMillis();
                    getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
//                    new toGet().execute();
//                    Log.d("TAGCount", "END : " + System.currentTimeMillis());
//                    long elapsed = System.currentTimeMillis() - start;
//                    Log.d("TAGCount", "TOTAL :  " + elapsed);
//                    System.out.println("total time (ms) : " + elapsed);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void getFile(final File dir) {
        try {
//            Log.d("TAGCount", "START : " + start);
            getActivity().runOnUiThread(new Runnable() {
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
                                    for (int j = 0; j < file.size(); j++) {
                                        if (file.get(j).equals(listFile[i].getPath())) {
                                            booleanpdf = true;
                                        } else {
                                        }
                                    }
                                    if (booleanpdf) {
                                        booleanpdf = false;
                                    } else {
//                                        Log.d("TAGCount ", " file_list " + file.size());
                                        file.add(listFile[i].getPath());
                                        pdfListAdapter.notifyItemRangeInserted(file.size(), file.size());
//                                        pdfListAdapter.notifyItemChanged(file.size(), file);
                                        pdfListAdapter.notifyDataSetChanged();
                                        if (file.size() > 0) {
                                            llCustom_loader.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
            status = true;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
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

    public void setLoadData() {
        fn_permission();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

//    private class toGet extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            long elapsed = System.currentTimeMillis() - start;
//            System.out.println("total time (ms) : " + elapsed);
//            recyclerView.setAdapter(pdfListAdapter);
//            pdfListAdapter.notifyDataSetChanged();
//            status = true;
//            llCustom_loader.setVisibility(View.GONE);
//        }
//    }

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