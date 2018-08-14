package in.tts.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.tts.R;
import in.tts.adapters.PdfListAdapter;
import in.tts.adapters.PdfListModelAdapter;
import in.tts.model.PdfModel;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;
import in.tts.utils.DatabaseHelper;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ScanDeviceActivity extends AppCompatActivity {

    private PdfListModelAdapter mAdapter;
    private ArrayList<PdfModel> notesList = new ArrayList<>();
    //    private ArrayList<String> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseHelper db;


    int pStatus = 0;
    private Handler handler = new Handler();
    TextView tv;
    private RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device);
        if (getSupportActionBar() != null) {
            CommonMethod.toSetTitle(getSupportActionBar(), ScanDeviceActivity.this, getString(R.string.str_scan_device));
        }


        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

      /*  ObjectAnimator animation = ObjectAnimator.ofInt(mProgress, "progress", 0, 100);
        animation.setDuration(50000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();*/
        tv = findViewById(R.id.tv121);
        recyclerView = findViewById(R.id.rvList121);
        rl = findViewById(R.id.rlLoader);

        db = new DatabaseHelper(getApplicationContext());
        notesList.addAll(db.getAllNotes());
//        notesList = new PrefManager(ScanDeviceActivity.this).toGetPDFList();
        mAdapter = new PdfListModelAdapter(getApplicationContext(), notesList);
//        notesList= new PrefManager(ScanDeviceActivity.this).toGetPDFList();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
//        recyclerView.setAdapter(new PdfListAdapter(ScanDeviceActivity.this, notesList, null));
        recyclerView.setAdapter(mAdapter);

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (pStatus < 100) {
                    pStatus += 1;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            mProgress.setProgress(pStatus);
                            tv.setText(pStatus + "%");

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(16); //thread will take approx 3 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                rl.setVisibility(View.GONE);
            }
        }).start();
    }
}
