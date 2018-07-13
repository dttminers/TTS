package in.tts.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

import in.tts.R;
import in.tts.classes.TTS;
import in.tts.utils.CommonMethod;

public class CameraOcrActivity extends AppCompatActivity {

    private SurfaceView mCameraView;
    //    private TextView mTextView;
    private CameraSource mCameraSource;
    private RelativeLayout mRl;
    private View view;
    private TextRecognizer textRecognizer;
    private StringBuilder stringBuilder;
    private TTS tts;

    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
                getSupportActionBar().setTitle(getString(R.string.app_name));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_left_white_24dp));
            }

            setContentView(R.layout.activity_camera_ocr);

            mCameraView = findViewById(R.id.surfaceView);
            mRl = findViewById(R.id.rlCamera);
            tts = new TTS(CameraOcrActivity.this);

            startCameraSource();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode != requestPermissionID) {
                Log.d(TAG, "Got unexpected permission result: " + requestCode);
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                return;
            }

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mCameraSource.start(mCameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                default:
                    return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void startCameraSource() {
        try {
            //Create the TextRecognizer
            final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

            if (!textRecognizer.isOperational()) {
                Log.w(TAG, "Detector dependencies not loaded yet");
            } else {

                //Initialize camerasource to use high resolution and set Autofocus on.
                mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setAutoFocusEnabled(true)
                        .setRequestedFps(2.0f)
                        .build();

                /**
                 * Add call back to SurfaceView and check if camera permission is granted.
                 * If permission is granted we can start our cameraSource and pass it to surfaceView
                 */
                mCameraView.getHolder()
                        .addCallback(
                                new SurfaceHolder.Callback() {
                                    @Override
                                    public void surfaceCreated(SurfaceHolder holder) {
                                        try {

                                            if (ActivityCompat
                                                    .checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(
                                                        CameraOcrActivity.this,
                                                        new String[]{Manifest.permission.CAMERA},
                                                        requestPermissionID);
                                                return;
                                            }
                                            mCameraSource.start(mCameraView.getHolder());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                    }

                                    @Override
                                    public void surfaceDestroyed(SurfaceHolder holder) {
                                        mCameraSource.stop();
                                    }
                                });

                //Set the TextRecognizer's Processor.
                textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                    @Override
                    public void release() {
                        mRl.removeView(view);
                    }

                    /**
                     * Detect all the text from camera using TextBlock and the values into a stringBuilder
                     * which will then be set to the textView.
                     * */
                    public void receiveDetections(Detector.Detections<TextBlock> detections) {
                        final SparseArray<TextBlock> items = detections.getDetectedItems();
                        if (items.size() != 0) {

//                            mTextView.post(new Runnable() {
//                                @Override
//                                public void run() {
                            stringBuilder = new StringBuilder();
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
//                                    mTextView.setText(stringBuilder.toString());
//                                    Toast.makeText(CameraOcrActivity.this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
                            toSetView();
                        }
                    }
                });
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void toSetView() {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                view = inflater.inflate(R.layout.layout_ocr_image, null, false);

                final TextView tvImgOcr = view.findViewById(R.id.tvImgOcr);
                ImageView ivSpeak = view.findViewById(R.id.ivSpeak);
                ImageView ivReload = view.findViewById(R.id.ivReload);

                tvImgOcr.setText(stringBuilder);

                ivReload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        new toGetImage().execute();
                        startCameraSource();
                        tvImgOcr.setText("");
                    }
                });

                ivSpeak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tts.SpeakLoud(stringBuilder.toString());
                    }
                });
//                mRl.addView(view);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // or wrap_content
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.setMargins(
                        CommonMethod.dpToPx(10, CameraOcrActivity.this),
                        CommonMethod.dpToPx(10, CameraOcrActivity.this),
                        CommonMethod.dpToPx(10, CameraOcrActivity.this),
                        CommonMethod.dpToPx(10, CameraOcrActivity.this)
                );

                mRl.addView(view, layoutParams);
            }

        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }
}