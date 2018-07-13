package in.tts.utils;

import android.util.Log;

import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;
import java.util.ArrayList;

import in.tts.model.AppData;

public class ToGetPdfFiles {

    @AddTrace(name = "onGetPDF", enabled = true)
    public static ArrayList<File> getfile(File dir) {
        ArrayList<File> fileList = new ArrayList<>();
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
        return  AppData.fileList = fileList;
    }
}
