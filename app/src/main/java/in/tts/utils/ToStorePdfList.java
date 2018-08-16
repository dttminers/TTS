package in.tts.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.tts.model.PdfModel;
import in.tts.model.PrefManager;

public class ToStorePdfList {

    private static ArrayList<String> fileList = new ArrayList<>();
    private static List<PdfModel> list = new ArrayList<>();
    private static DatabaseHelper db;

    @AddTrace(name = "onGetPDF", enabled = true)
    public static void getFile(final File dir, final Context context) {
        try {
            db = new DatabaseHelper(context);
            list.addAll(db.getAllNotes());
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
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
                                        fileList.add(fileList.size() - 1, listFile[i].getPath().trim().replaceAll("\\s", "%20"));
                                        // inserting note in db and getting
                                        // newly inserted note id
                                        if (!list.contains(listFile[i].getPath().trim())) {
                                            long id = db.insertNote(listFile[i].getPath().trim(), String.valueOf(new Date(listFile[i].lastModified())));
                                            Log.d("TAG", "ToStorePdfList PDF " + id);
                                        }
                                    }
                                }
                            }
                        }
                        new PrefManager(context).toSetPDFFileList(fileList);
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
}
