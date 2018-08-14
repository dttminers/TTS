package in.tts.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;
import java.util.ArrayList;

import in.tts.model.PrefManager;

public class ToGetPdfFiles {

    static ArrayList<String> fileList = new ArrayList<>();
    static boolean status = false;
    static PrefManager prefManager;

    @AddTrace(name = "onGetPDF", enabled = true)
    public static void getFile(final File dir, final Context context) {
        try {
            prefManager = new PrefManager(context);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    if (prefManager.toGetPDFList() != null) {
                        fileList = prefManager.toGetPDFList();
                    }
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
                                        if (fileList.get(j).equals(listFile[i].getPath())) {
                                            booleanpdf = true;
                                        }
                                    }
                                    if (booleanpdf) {
                                        booleanpdf = false;
                                    } else {
//                                        if (fileList.size() < 10) {
                                            if (!fileList.contains(listFile[i].getPath().trim().replaceAll("\\s", "%20"))) {
                                                fileList.add(fileList.size() - 1, listFile[i].getPath().trim().replaceAll("\\s", "%20"));
                                            }
//                                        } else {
//                                            break;
//                                        }
                                    }
                                }
                            }
                        }
                        prefManager.toSetPDFFileList(fileList, false);
                    }
                }
            });
            status = false;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public static boolean isRunning() {
        return status;
    }
}