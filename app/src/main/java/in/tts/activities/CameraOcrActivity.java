package in.tts.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import in.tts.R;
import in.tts.browser.fragment.TtsFragment;
import in.tts.utils.CommonMethod;

public class CameraOcrActivity extends AppCompatActivity implements TtsFragment.OnFragmentInteractionListener {

    private SurfaceView mCameraView;
    private TextView mTextView;
    private CameraSource mCameraSource;

    private RelativeLayout mRl;
    private ImageView ivCamera, ivSpeak;
    private StringBuilder stringBuilder;

    Camera camera;
    SurfaceHolder surfaceHolder;

//    PictureCallback rawCallback;
//    ShutterCallback shutterCallback;
//    PictureCallback jpegCallback;

    private static final String TAG = "MainActivity";
    private static final int requestPermissionID = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_camera_ocr);
            mCameraView = findViewById(R.id.surfaceView);
//            surfaceHolder = surfaceView.getHolder();
//            surfaceHolder.addCallback(this);
//            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            mTextView = findViewById(R.id.text_view);

            mRl = findViewById(R.id.rlCameraOcr);
            ivCamera = findViewById(R.id.ivCameraOcr);
            ivSpeak = findViewById(R.id.ivSpeakCameraOcr);

            ivCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    getBitmap();
                    //take the picture
                    camera.takePicture(null, null, new PictureCallback() {

                                public void onPictureTaken(byte[] data, Camera camera) {
                                    try {
                                        Date now = new Date();
                                        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
                                        File dirPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/READ_IT/IMAGE/");
                                        if (!dirPath.exists()) {
                                            dirPath.mkdirs();
                                        }
                                        File imageFile = new File(dirPath.getAbsolutePath() + File.separator + "/Camera_" + now + ".jpg");
                                        //Log.d("TAG_WEB", " getpath " + imageFile.getAbsolutePath());
                                        FileOutputStream outStream = new FileOutputStream(imageFile);
//                    FileOutputStream outStream = null;

//                    try {

//                        outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));

                                        outStream.write(data);

                                        outStream.close();

                                        Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);

//                                    } catch (FileNotFoundException e) {
//
//                                        e.printStackTrace();

                                    } catch (Exception | Error e) {

                                        e.printStackTrace();

//                                    } finally {

                                    }

                                    CommonMethod.toDisplayToast(CameraOcrActivity.this, "Pictured Saved ");
                                    refreshCamera();

                                }

                            }
                    );

                }
            });

            ivSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openTtsFragment(stringBuilder.toString());
                }
            });

//            jpegCallback =

            startCameraSource();
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
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
                mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        try {

                            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(CameraOcrActivity.this,
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
                    }

                    /**
                     * Detect all the text from camera using TextBlock and the values into a stringBuilder
                     * which will then be set to the textView.
                     * */
                    @Override
                    public void receiveDetections(Detector.Detections<TextBlock> detections) {
                        final SparseArray<TextBlock> items = detections.getDetectedItems();
                        if (items.size() != 0) {

                            mTextView.post(new Runnable() {
                                @Override
                                public void run() {
                                    stringBuilder = new StringBuilder();
                                    for (int i = 0; i < items.size(); i++) {
                                        TextBlock item = items.valueAt(i);
                                        stringBuilder.append(item.getValue());
                                        stringBuilder.append("\n");
                                    }
                                    mTextView.setText(stringBuilder.toString());
                                    mTextView.setVisibility(View.VISIBLE);
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

    public void getBitmap() {

        Bitmap bitmap = null;
        try {
//            int w = params.getPreviewSize().width;
//            int h = params.getPreviewSize().height;
//            int format = params.getPreviewFormat();
//            YuvImage image = new YuvImage(data, format, w, h, null);
//
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            Rect area = new Rect(0, 0, w, h);
//            image.compressToJpeg(area, 50, out);
//            Bitmap bm = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
//            View v1 = getWindow().getDecorView().getRootView();
//            v1.setDrawingCacheEnabled(true);
//            bitmap = Bitmap.createBitmap(v1.getDrawingCache());
//            v1.setDrawingCacheEnabled(false);

            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
            File dirPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/READ_IT/IMAGE/");
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
            File imageFile = new File(dirPath.getAbsolutePath() + File.separator + "/Camera_" + now + ".jpg");
            //Log.d("TAG_WEB", " getpath " + imageFile.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(imageFile);
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }

            startActivity(new Intent(CameraOcrActivity.this, ImageOcrActivity.class).putExtra("PATH", imageFile.getAbsolutePath()));
            finish();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        param.setPreviewSize(352, 288);
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {

        }
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // stop preview and release camera
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    protected void onPause() {
        try {
            closeTtsFragment();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            closeTtsFragment();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void openTtsFragment(String text) {
        try {
            Log.d("TAG_ Web", "fdddf31" + ":" + text.length());
            Bundle bundle = new Bundle();
            bundle.putString("Speak", text);
            TtsFragment ttsFragment = new TtsFragment();
            ttsFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_close_exit, R.anim.activity_open_enter, R.anim.activity_close_exit)
                    .replace(R.id.ttsFrameCamera, ttsFragment, TtsFragment.TAG).commit();
            setTtsFragmentVisibility(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeTtsFragment() {
        try {
            Log.d("TAG_ Web", "fdddf21" + ":");
            setTtsFragmentVisibility(false);
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(TtsFragment.TAG);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTtsFragmentVisibility(final boolean visible) {
        try {
            Log.d("TAG_ Web", "fdddf11" + visible + ":");
            final View ttsFrame = findViewById(R.id.ttsFrameCamera);
            if (visible) {
                ttsFrame.animate().alpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        ttsFrame.setVisibility(View.VISIBLE);
                        Log.d("TAG_ Web", "fdddf12" + visible + ":");
                    }
                }).start();
            } else {
                ttsFrame.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ttsFrame.setVisibility(View.GONE);
                        Log.d("TAG_ Web", "fdddf13" + visible + ":");
                    }
                }).start();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }
}
/*


    private CameraSource.PictureCallback mPicture = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes) {
            int orientation = Exif.getOrientation(bytes);
            Bitmap   bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            switch(orientation) {
                case 90:
                    bitmapPicture= rotateImage(bitmap, 90);

                    break;
                case 180:
                    bitmapPicture= rotateImage(bitmap, 180);

                    break;
                case 270:
                    bitmapPicture= rotateImage(bitmap, 270);

                    break;
                case 0:
                    // if orientation is zero we don't need to rotate this

                default:
                    break;
            }
            //write your code here to save bitmap
        }

    }

};
public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),   source.getHeight(), matrix,
        true);
        }

        */

//
//    ExifInterface exif = new ExifInterface();
//    Matrix matrix = new Matrix();
//try {
//        exif.readExif(context.getContentResolver().openInputStream(fileUri), ExifInterface.Options.OPTION_ALL);
//        ExifTag tag = exif.getTag(ExifInterface.TAG_ORIENTATION);
//        int orientation = tag.getValueAsInt(1);
//        switch (orientation) {
//        case 3: /* 180° */
//        matrix.postRotate(180);
//        break;
//        case 6: /*  90° */
//        matrix.postRotate(90);
//        break;
//        case 8: /* 270° */
//        matrix.postRotate(-90);
//        break;
//        }
//        } catch (IOException e) {
//        Log.i("INFO","expected behaviour: IOException");
//        //not every picture comes from the phone, should that be the case,
//        // we can't get exif tags anyway, since those aren't being transmitted
//        // via http (atleast I think so. I'd need to save the picture on the SD card to
//        // confirm that and I don't want to do that)
//        } catch(NullPointerException e){
//        Log.i("INFO","expected behaviour: NullPointerException");
//        //same as above, not every picture comes from the phone
//        }


//https://examples.javacodegeeks.com/android/core/ui/surfaceview/android-surfaceview-example/