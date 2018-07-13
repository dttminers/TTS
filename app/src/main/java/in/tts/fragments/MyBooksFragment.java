package in.tts.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
            CommonMethod.setAnalyticsData(getContext(), "MainTab", "PDF Document", null);

            lv_pdf = getActivity().findViewById(R.id.lv_pdf);
            mLoading = getActivity().findViewById(R.id.pbPdf);
            mTvLblRecent = getActivity().findViewById(R.id.txtRecent);

            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            fn_permission();
            lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(getContext(), PdfActivity.class);
                    Intent intent = new Intent(getContext(), PdfReadersActivity.class);
                    intent.putExtra("position", i);
                    startActivity(intent);
                    Log.d("TAG", "pdf Position" + i);
                }
            });
        } catch (Exception| Error e){
            e.printStackTrace();
        }
    }

    private void fn_permission() {
        try {
            Log.d("TAG", " pdf doc permission ");
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
            Log.d("TAG", " pdf doc getpdf ");
            boolean_permission = true;
            if ((AppData.fileList.size() == 0)) {
                Log.d("TAG", " pdf doc getpdf 1");
                getfile(dir);
            } else {
                fileList = AppData.fileList;
                Log.d("TAG", " pdf doc getpdf 2 ");
            }
            obj_adapter = new PDFAdapter(getContext(), fileList);
            lv_pdf.setAdapter(obj_adapter);

//            mTvLblRecent.setVisibility(View.VISIBLE);
//            lv_pdf.setVisibility(View.VISIBLE);
//            mLoading.setVisibility(View.GONE);
        } catch (Exception | Error e) {
            e.printStackTrace();
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
        Log.d("TAG", " pdf doc count " + fileList.size());
        return fileList;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG ", " pdf doc permission " + requestCode + " : " + permissions + " : " + grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toGetPDF();
            } else {
                Toast.makeText(getContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", " pdf doc resume" + fileList.size());
        fn_permission();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TAG", " pdf doc pause " + fileList.size());
        CommonMethod.toReleaseMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", " pdf doc destroy" + fileList.size());
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
}