package in.tts.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.text.DateFormat;
import java.util.Date;

import in.tts.R;
import in.tts.classes.TTS;
import in.tts.utils.CommonMethod;

public class CameraOcrActivity extends AppCompatActivity {

    private SurfaceView mCameraView;
    private CameraSource mCameraSource;
    private RelativeLayout mRlCamera, mRlOCRData;
    private TextView tvImgOcr;
    private ImageView ivSpeak, ivReload;
    private View view;
    private TextRecognizer textRecognizer;
    private StringBuilder stringBuilder;
    private TTS ttsFile;

    private static final String TAG = "CameraOcrActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), CameraOcrActivity.this, "");
            }

            setContentView(R.layout.activity_camera_ocr);

            ttsFile = new TTS(CameraOcrActivity.this);
            mCameraView = findViewById(R.id.surfaceView);
            mRlCamera = findViewById(R.id.rlCamera);
            mRlOCRData = findViewById(R.id.rlOcrData);

            // layout_ocr_image
            tvImgOcr = findViewById(R.id.tvImgOcr);
            ivSpeak = findViewById(R.id.ivSpeak);
            ivReload = findViewById(R.id.ivReload);
            view = findViewById(R.id.view101);

            toHideLayout();

            ivReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvImgOcr.setText("");
                    fn_permission();
                }
            });

            ivSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        ttsFile.SpeakLoud(stringBuilder.toString());
                        ttsFile.toSaveAudioFile(stringBuilder.toString());
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // or wrap_content
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.setMargins(
                    CommonMethod.dpToPx(10, this),
                    CommonMethod.dpToPx(10, this),
                    CommonMethod.dpToPx(10, this),
                    CommonMethod.dpToPx(10, this)
            );

            fn_permission();

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        toHideLayout();
        fn_permission();
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(CameraOcrActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(CameraOcrActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(CameraOcrActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(CameraOcrActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(CameraOcrActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(CameraOcrActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                startCameraSource();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toHideLayout() {
        try {
            mRlOCRData.setVisibility(View.GONE);
            tvImgOcr.setVisibility(View.GONE);
            ivReload.setVisibility(View.GONE);
            ivReload.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toShowLayout() {
        try {
            mRlOCRData.setVisibility(View.VISIBLE);
            tvImgOcr.setVisibility(View.VISIBLE);
            ivReload.setVisibility(View.VISIBLE);
            ivReload.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
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
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
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
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void startCameraSource() {
        try {
            //Create the TextRecognizer
            textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

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

                /*
                 * Add call back to SurfaceView and check if camera permission is granted.
                 * If permission is granted we can start our cameraSource and pass it to surfaceView
                 */
                mCameraView.getHolder()
                        .addCallback(
                                new SurfaceHolder.Callback() {
                                    @Override
                                    public void surfaceCreated(SurfaceHolder holder) {
                                        try {

                                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                ActivityCompat.requestPermissions(
                                                        CameraOcrActivity.this,
                                                        new String[]{Manifest.permission.CAMERA},
                                                        requestPermissionID);
                                                return;
                                            }
                                            mCameraSource.start(mCameraView.getHolder());
                                        } catch (Exception | Error e) {
                                            e.printStackTrace();
                                            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
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
                    }

                    /*
                     * Detect all the text from camera using TextBlock and the values into a stringBuilder
                     * which will then be set to the textView.
                     * */
                    public void receiveDetections(Detector.Detections<TextBlock> detections) {
                        final SparseArray<TextBlock> items = detections.getDetectedItems();
                        if (items.size() != 0) {

                            tvImgOcr.post(new Runnable() {
                                @Override
                                public void run() {
                                    stringBuilder = new StringBuilder();
                                    for (int i = 0; i < items.size(); i++) {
                                        TextBlock item = items.valueAt(i);
                                        stringBuilder.append(item.getValue());
                                        stringBuilder.append("\n");
                                    }
                                    tvImgOcr.setText(stringBuilder.toString().trim());
                                    toShowLayout();
                                }
                            });
                        }
                    }
                });
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public Bitmap getBitmap() {
        mCameraView.setDrawingCacheEnabled(true);
        mCameraView.buildDrawingCache(true);
        final Bitmap bitmap = Bitmap.createBitmap( mCameraView.getDrawingCache() );
        mCameraView.setDrawingCacheEnabled(false);
        mCameraView.destroyDrawingCache();
        return bitmap;
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            ttsFile.toStop();
            if (mRlOCRData != null) {
                mRlOCRData.setVisibility(View.GONE);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ttsFile.toStop();
            if (mRlOCRData != null) {
                mRlOCRData.setVisibility(View.GONE);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }
}