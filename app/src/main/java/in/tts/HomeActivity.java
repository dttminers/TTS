package in.tts;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        walkdir(Environment.getRootDirectory());
    }

    public void walkdir(File dir) {
        Log.d("TAG", " DIR " + dir);
        String pdfPattern = ".pdf";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            Log.d("TAG", "FILE " + Arrays.toString(listFile));
            for (File aListFile : listFile) {

                if (aListFile.isDirectory()) {
                    walkdir(aListFile);
                } else {
                    if (aListFile.getName().endsWith(pdfPattern)) {
                        //Do what ever u want
                        Log.d("TAG", "IS PDF  " + aListFile.getName() + ":" + aListFile.canWrite());
                    } else {
                        Log.d("TAG", "NO PDF  " + aListFile.getName() + ":" + aListFile.canWrite());
                    }
                }
            }
        } else {
            Log.d("TAG", "NO PDF  ");
        }
    }
}
