package in.tts.fragments;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfRenderer;
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

import java.io.File;
import java.util.ArrayList;

import android.support.design.widget.TabLayout;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.perf.metrics.AddTrace;

import in.tts.R;
import in.tts.activities.MainActivity;
import in.tts.activities.PDFReaderActivity;
//import in.tts.activities.PdfActivity;
import in.tts.activities.PdfReadersActivity;
import in.tts.adapters.PDFAdapter;
import in.tts.utils.CommonMethod;

public class DocumentsFragment extends Fragment {

    private ListView lv_pdf;
    private ProgressBar mLoading;
    private TextView mTvLblRecent;

    private PDFAdapter obj_adapter;
    boolean boolean_permission;
    private File dir;

    public static ArrayList<File> fileList = new ArrayList<File>();
    public static int REQUEST_PERMISSIONS = 1;

    public DocumentsFragment() {
        // Required empty public constructor
    }

    @Override
    @AddTrace(name = "onCreateDocuments", enabled = true)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_documents, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CommonMethod.setAnalyticsData(getContext(), "MainTab", "Document", null);
//        Toast.makeText(getContext(), " Unable to Display Data", Toast.LENGTH_SHORT).show();
        init();
    }

    private void init() {
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
//
                Log.e("Position", i + "");
            }
        });
    }

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
        Log.d("TAG", " count " + fileList);
        return fileList;
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }
        } else {
            toGetPDF();
        }
    }

    private void toGetPDF() {
        try {
//            if (fileList.size() == 0) {
//                Toast.makeText(getContext(), " No Files Found", Toast.LENGTH_SHORT).show();
//            } else {
                boolean_permission = true;
                getfile(dir);
                obj_adapter = new PDFAdapter(getContext(), fileList);
                lv_pdf.setAdapter(obj_adapter);

                mTvLblRecent.setVisibility(View.VISIBLE);
                lv_pdf.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.GONE);
//            }
        } catch (Exception | Error e) {
            e.printStackTrace();
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
}
//    public ArrayList<File> getfile(File dir) {
//        File listFile[] = dir.listFiles();
//        if (listFile != null && listFile.length > 0) {
//            for (int i = 0; i < listFile.length; i++) {
//
//                if (listFile[i].isDirectory()) {
//                    fileList.add(listFile[i]);
//                    getfile(listFile[i]);
//
//                } else {
//                    if (listFile[i].getName().endsWith(".pdf")
//                            || listFile[i].getName().endsWith(".xls")
//                            || listFile[i].getName().endsWith(".jpg")
//                            || listFile[i].getName().endsWith(".jpeg")
//                            || listFile[i].getName().endsWith(".png")
//                            || listFile[i].getName().endsWith(".doc"))
//                    {
//                        fileList.add(listFile[i]);
//                        mAttachmentList.add(new AttachmentModel(listFile[i].getName()));
//                    }
//                }
//            }
//        }
//        return fileList;
//    }
//
//    private void setAdapter()
//    {
//        AttachmentAdapter itemsAdapter = new AttachmentAdapter(AttachmentFileList.this);
//        ArrayList<AttachmentModel> list = new ArrayList<>();
//        itemsAdapter.setData(mAttachmentList);
//        mListView.setAdapter(itemsAdapter);
//    }

