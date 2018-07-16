package in.tts.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.PdfReadersActivity;
import in.tts.adapters.PDFAdapter;
import in.tts.model.AppData;
import in.tts.utils.CommonMethod;

public class MyBooksFragment extends Fragment {

    private ListView lv_pdf;

    private ProgressBar mLoading;
    private TextView mTvLblRecent;

    private PDFAdapter obj_adapter;
    boolean boolean_permission;
    private File dir;

    public static ArrayList<File> fileList = new ArrayList<File>();
    public static int REQUEST_PERMISSIONS = 1;

    private OnFragmentInteractionListener mListener;

    public MyBooksFragment() {
        // Required empty public constructor
    }

    public static MyBooksFragment newInstance() {
        return new MyBooksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_books, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            CommonMethod.setAnalyticsData(getContext(), "DocTab", "Doc MyBooks", null);

            lv_pdf = getActivity().findViewById(R.id.lv_pdf);
            mTvLblRecent = getActivity().findViewById(R.id.txtRecent);

            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            fn_permission();
            lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    CommonMethod.toCallLoader(getContext(), "Loading...");
                    Intent intent = new Intent(getContext(), PdfReadersActivity.class);
                    intent.putExtra("position", i);
                    intent.putExtra("name", fileList.get(i).getName());
                    getContext().startActivity(intent);
                    CommonMethod.toCloseLoader();
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//                if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
//                } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
//                }
            } else {
//                toGetPDF();
                CommonMethod.toCallLoader(getContext(), "Loading...");
                new toGetPDFData().execute();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void toGetPDF() {
        try {
//            CommonMethod.toCallLoader(getContext(), "Loading");
            boolean_permission = true;
            if ((AppData.fileList.size() == 0)) {
//                CommonMethod.toCallLoader(getContext(), "Loading...");
                getfile(dir);
            } else {
                fileList = AppData.fileList;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            CommonMethod.toCloseLoader();
        }
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
        Log.d("Tag", " mybooks3 " + fileList.size());
        return fileList;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CommonMethod.toCallLoader(getContext(), "Loading...");
                new toGetPDFData().execute();

            } else {
                Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CommonMethod.toReleaseMemory();
        fn_permission();
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
    }

    private class toGetPDFData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("Tag", " mybooks1 " + fileList.size());
            toGetPDF();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CommonMethod.toCloseLoader();
            Log.d("Tag", " mybooks2 " + fileList.size());
            obj_adapter = new PDFAdapter(getContext(), fileList);
            lv_pdf.setAdapter(obj_adapter);
        }
    }
}