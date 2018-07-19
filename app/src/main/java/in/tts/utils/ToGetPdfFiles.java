package in.tts.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;
import java.util.ArrayList;

import in.tts.fragments.MainHomeFragment;
import in.tts.model.PrefManager;

public class ToGetPdfFiles {

    static ArrayList<String> fileList = new ArrayList<>();
    static boolean status = false;

    @AddTrace(name = "onGetPDF", enabled = true)
    public static void getFile(final File dir, final Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                status = true;
                File listFile[] = dir.listFiles();
                if (listFile != null && listFile.length > 0) {
                    for (int i = 0; i < listFile.length; i++) {
                        if (listFile[i].isDirectory()) {
                            getFile(listFile[i], context);
                        } else {
                            boolean booleanpdf = false;
                            if (listFile[i].getName().endsWith(".pdf")) {
                                for (int j = 0; j < fileList.size(); j++) {
//                                    if (fileList.get(j).getName().equals(listFile[i].getName())) {
                                    if (fileList.get(j).equals(listFile[i].getPath())) {
                                        booleanpdf = true;
                                    } else {
                                    }
                                }
                                if (booleanpdf) {
                                    booleanpdf = false;
                                } else {
                                    if (fileList.size() < 11) {
                                        fileList.add(listFile[i].getPath());
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    Log.d("TAG", "DATA PDF " + fileList.size());
                    new PrefManager(context).toSetPDFFileList(fileList, false);
                }
            }
        });
        status = false;
    }

    public static boolean isRunning() {
        Log.d("TAG", "DATA PDF isRunning : " + status);
        return status;
    }
}