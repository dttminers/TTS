package in.tts.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Printer;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import in.tts.*;
import in.tts.classes.TTS;

public class ImageOcrActivity extends AppCompatActivity {

    private String photoPath;
    private Bitmap bitmap;
    private TextRecognizer textRecognizer;
    private Frame imageFrame;
    private String imageText = "";
    private SparseArray<TextBlock> textBlocks, items;
    private TextBlock textBlock, item;
    private StringBuilder stringBuilder;

    private RelativeLayout mRl;
    private TTS tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_image_ocr);
            tts = new TTS(ImageOcrActivity.this);
            Log.d("TAG", " path : " + getIntent().getStringExtra("PATH"));
            photoPath = getIntent().getStringExtra("PATH");

            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
                if (photoPath != null) {
                    getSupportActionBar().setTitle(photoPath);
                } else {
                    getSupportActionBar().setTitle(getString(R.string.app_name));
                }
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_left_white_24dp));
            }

            mRl = findViewById(R.id.rlImageOcrActivity);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(photoPath, options);

            ImageView mIvOcr = findViewById(R.id.imgOcr);
            mIvOcr.setImageBitmap(bitmap);

//            toGetImage(bitmap);
            new toGetImage().execute();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void toGetImage(Bitmap bitmap) {
        try {
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            Frame imageFrame = new Frame.Builder()
                    .setBitmap(bitmap)                 // your image bitmap
                    .build();
            String imageText = "";
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
            for (int i = 0; i < textBlocks.size(); i++) {
                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                imageText = textBlock.getValue();                   // return string
                Log.d("TAG", " Result : " + i + ":" + imageText);
            }
            Log.d("TAG", " Result Final: " + imageText);

            StringBuilder stringBuilder = new StringBuilder();
            final SparseArray<TextBlock> items = textRecognizer.detect(imageFrame);
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
                Log.d("TAG", "DATA : " + i + stringBuilder);
            }
            Log.d("TAG", " Final DATA " + stringBuilder);

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
                    View view = inflater.inflate(R.layout.layout_ocr_image, null, false);

                    TextView tvImgOcr = view.findViewById(R.id.tvImgOcr);
                    ImageView ivSpeak = view.findViewById(R.id.ivSpeak);
                    ImageView ivReload = view.findViewById(R.id.ivReload);

                    tvImgOcr.setText(stringBuilder);

                    ivReload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new toGetImage().execute();
                        }
                    });

                    ivSpeak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tts.SpeakLoud(stringBuilder.toString());
                        }
                    });
                    mRl.addView(view);
                }

            } catch (Exception | Error e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
    }
}