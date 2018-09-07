package in.tts.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.crash.FirebaseCrash;

import java.util.ArrayList;

import in.tts.R;
import in.tts.classes.TTS;
import in.tts.classes.ToSetMore;
import in.tts.model.PrefManager;
import in.tts.utils.CameraUtils;
import in.tts.utils.CommonMethod;
import in.tts.utils.FilePath;
import in.tts.utils.ToCheckFileExists;

public class ImageOcrActivity extends AppCompatActivity {

    private String photoPath, name;
    private Bitmap bitmap;
    private TextRecognizer textRecognizer;
    private Frame imageFrame;
    private SparseArray<TextBlock> items;
    private TextBlock item;
    private StringBuilder stringBuilder;
    private View view;

    private RelativeLayout mRl;
    private TTS tts;

    private TextView tvImgOcr;
    private ImageView ivSpeak, ivReload, mIvOcr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_image_ocr);

            PrefManager.ActivityCount = +1;

            if (getIntent().getStringExtra("PATH") != null) {
                photoPath = getIntent().getStringExtra("PATH");
            } else if (getIntent().getData() != null) {
                photoPath = FilePath.getPath(ImageOcrActivity.this, getIntent().getData());
            } else {
                CommonMethod.toDisplayToast(ImageOcrActivity.this, " No Image Found ");
                toExit();
            }

            if (ToCheckFileExists.isImage(photoPath)) {


                //Log.d("TAG_IMage   ", " int  " + photoPath);

                if (getSupportActionBar() != null) {
                    name = photoPath.substring(photoPath.lastIndexOf("/") + 1);
                    CommonMethod.toSetTitle(getSupportActionBar(), ImageOcrActivity.this, name);
                }

                mRl = findViewById(R.id.rlImageOcrActivity);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(photoPath.trim().replaceAll("%20", " "), options);

                mIvOcr = findViewById(R.id.imgOcr);
                mIvOcr.setImageBitmap(bitmap);
                new toGetImage().execute();
                fn_permission();

                PrefManager prefManager = new PrefManager(ImageOcrActivity.this);
                ArrayList<String> list = prefManager.toGetImageListRecent();
                if (list != null) {
                    if (!list.contains(photoPath)) {
                        list.add(photoPath.replaceAll("\\s", "%20"));
                        PrefManager.AddedRecentImage = true;
                        prefManager.toSetImageFileListRecent(list);
                    }
                } else {
                    list = new ArrayList<>();
                    list.add(photoPath.replaceAll("\\s", "%20"));
                    PrefManager.AddedRecentImage = true;
                    prefManager.toSetImageFileListRecent(list);
                }
            } else {
                CommonMethod.toDisplayToast(ImageOcrActivity.this, "Not Image");
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toExit() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        try {
//            tts = new TTS(ImageOcrActivity.this);
            new toGetImage().execute();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onRestart() {
        try {
            super.onRestart();
//            tts = new TTS(ImageOcrActivity.this);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onResume() {
        try {
            //Log.d("TAG", "Resume Image "  );
            super.onResume();
            tts = new TTS(ImageOcrActivity.this);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(ImageOcrActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(ImageOcrActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(ImageOcrActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(ImageOcrActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class toGetImage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                imageFrame = new Frame.Builder().setBitmap(bitmap).build();

//            textBlocks = textRecognizer.detect(imageFrame);
//            for (int i = 0; i < textBlocks.size(); i++) {
//                textBlock = textBlocks.get(textBlocks.keyAt(i));
//                imageText = textBlock.getValue();                   // return string
//                //Log.d("TAG", " Result : " + i + ":" + imageText);
//            }
//            //Log.d("TAG", " Result Final: " + imageText);

                stringBuilder = new StringBuilder();
                items = textRecognizer.detect(imageFrame);
                for (int i = 0; i < items.size(); i++) {
                    item = items.valueAt(i);
                    stringBuilder.append(item.getValue());
                    stringBuilder.append("\n");
                }
                //Log.d("TAG", " Final DATA " + stringBuilder);
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
            return null;
        }


        @SuppressLint("InflateParams")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (inflater != null) {
                    view = inflater.inflate(R.layout.layout_ocr_image, null, false);

                    tvImgOcr = view.findViewById(R.id.tvImgOcr);
                    ivSpeak = view.findViewById(R.id.ivSpeak);
                    ivReload = view.findViewById(R.id.ivReload);

                    tvImgOcr.setText(stringBuilder);

                    ivReload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {

                                tvImgOcr.setText("");
                                tts.toStop();

                                new toGetImage().execute();

                            } catch (Exception | Error e) {
                                e.printStackTrace();
                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e);
                                FirebaseCrash.report(e);
                            }
                        }
                    });

                    ivSpeak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
//                                Animation myAnim = AnimationUtils.loadAnimation(ImageOcrActivity.this, R.anim.bounce);
//
//                                // Use bounce interpolator with amplitude 0.2 and frequency 20
//                                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
//                                myAnim.setInterpolator(interpolator);
//
//                                ivSpeak.startAnimation(myAnim);

                                tts.SpeakLoud(stringBuilder.toString());//, "AUD_Image" + name.substring(0, name.lastIndexOf(".")) + System.currentTimeMillis());
//                                tts.SpeakLoud(stringBuilder.toString(), "AUD_Image" + name.substring(0, (name.length() - 4)) + System.currentTimeMillis());
//                                tts.toSaveAudioFile(stringBuilder.toString(), "AUD_Image"+name.substring(0, (name.length()-4) )+System.currentTimeMillis());
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
                            CommonMethod.dpToPx(10, ImageOcrActivity.this),
                            CommonMethod.dpToPx(10, ImageOcrActivity.this),
                            CommonMethod.dpToPx(10, ImageOcrActivity.this),
                            CommonMethod.dpToPx(10, ImageOcrActivity.this)
                    );

                    mRl.addView(view, layoutParams);
                }

            } catch (Exception | Error e) {
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            if (menu != null) {
                getMenuInflater().inflate(R.menu.image_menu, menu);
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item != null) {
                if (item.getItemId() == R.id.rotate_img) {
                    bitmap = CameraUtils.rotateImage(bitmap, 90);
                    mIvOcr.setImageBitmap(bitmap);
                    new toGetImage().execute();
                    fn_permission();
                }
                ToSetMore.MenuOptions(ImageOcrActivity.this, item);
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        try {
            //Log.d("TAG_BACK", " Image " + PrefManager.ActivityCount);
            if (PrefManager.ActivityCount <= 1) {
                if (PrefManager.CurrentPage != 4) {
                    startActivity(new Intent(ImageOcrActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                } else {
                    toExit();
                }
            } else {
                toExit();
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected void onPause() {
        try {
            if (tts != null) {
                tts.toStop();
            }
            if (mRl != null) {
                if (mRl.getChildCount() > 1) {
                    if (view != null) {
                        mRl.removeView(view);
                    }
                }
            }
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
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }
            if (mRl != null) {
                if (mRl.getChildCount() > 1) {
                    if (view != null) {
                        mRl.removeView(view);
                    }
                }
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        super.onDestroy();
    }
}