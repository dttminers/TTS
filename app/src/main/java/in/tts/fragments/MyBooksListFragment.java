package in.tts.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.ImageAdapterGallery;
import in.tts.adapters.PdfListAdapter;
import in.tts.utils.CommonMethod;

public class MyBooksListFragment extends Fragment {

    private ArrayList<String> file;
    private PdfListAdapter pdfListAdapter;

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
            if (getActivity() != null) {
                CommonMethod.toCallLoader(getContext(), "Loading");
                file = new ArrayList<>();
                pdfListAdapter = new PdfListAdapter(getActivity(), file);

                getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));

                RecyclerView recyclerView = getActivity().findViewById(R.id.rvList);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(pdfListAdapter);
                pdfListAdapter.notifyDataSetChanged();
                CommonMethod.toCloseLoader();
//                CommonMethod.toCloseLoader();
            }
            CommonMethod.toCloseLoader();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            CommonMethod.toCloseLoader();
        }
    }

    public void getFile(final File dir) {
        try {
//            CommonMethod.toCallLoader(getContext(), "Fetching files ");

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
                                        file.add(listFile[i].getPath());
                                        pdfListAdapter.notifyItemChanged(file.size(), file);
                                    }
                                }
                            }
                        }
                    }
                }
            });
            Log.d("TAG", " pdf count " + file.size());
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            CommonMethod.toCloseLoader();
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
