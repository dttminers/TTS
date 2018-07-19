package in.tts.activities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import in.tts.*;
import in.tts.classes.TTS;
import in.tts.classes.ToSetMore;
import in.tts.utils.CommonMethod;

public class ImageOcrActivity extends AppCompatActivity {

    private String photoPath;
    private Bitmap bitmap;
    private TextRecognizer textRecognizer;
    private Frame imageFrame;
    private SparseArray<TextBlock> items;
    private TextBlock item;
    private StringBuilder stringBuilder;
    private View view;

    private RelativeLayout mRl;
    private TTS tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_image_ocr);

            photoPath = getIntent().getStringExtra("PATH");
            tts = new TTS(ImageOcrActivity.this);

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), ImageOcrActivity.this, photoPath.substring(photoPath.lastIndexOf("/") + 1));
            }

            mRl = findViewById(R.id.rlImageOcrActivity);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(photoPath.trim(), options);

            ImageView mIvOcr = findViewById(R.id.imgOcr);
            mIvOcr.setImageBitmap(bitmap);
            new toGetImage().execute();
            fn_permission();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
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
//            if ((ContextCompat.checkSelfPermission(ImageOcrActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
//                ActivityCompat.requestPermissions(ImageOcrActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
//            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }


    private class toGetImage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            imageFrame = new Frame.Builder().setBitmap(bitmap).build();

//            textBlocks = textRecognizer.detect(imageFrame);
//            for (int i = 0; i < textBlocks.size(); i++) {
//                textBlock = textBlocks.get(textBlocks.keyAt(i));
//                imageText = textBlock.getValue();                   // return string
//                Log.d("TAG", " Result : " + i + ":" + imageText);
//            }
//            Log.d("TAG", " Result Final: " + imageText);

            stringBuilder = new StringBuilder();
            items = textRecognizer.detect(imageFrame);
            for (int i = 0; i < items.size(); i++) {
                item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
            Log.d("TAG", " Final DATA " + stringBuilder);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
                            tvImgOcr.setText("");
                            tts.toStop();
                            new toGetImage().execute();
                        }
                    });

                    ivSpeak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                tts.SpeakLoud(stringBuilder.toString());
                            } catch (Exception | Error e) {
                                e.printStackTrace();
                                Crashlytics.logException(e);
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
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ToSetMore.MenuOptions(ImageOcrActivity.this, item);
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            tts.toStop();
            if (mRl != null) {
                if (mRl.getChildCount() > 1) {
                    if (view != null) {
                        mRl.removeView(view);
                    }
                }
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            tts.toShutDown();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }
}