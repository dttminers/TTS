package in.tts.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import in.tts.*;

public class ImageOcrActivity extends AppCompatActivity {
    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_image_ocr);

            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
                getSupportActionBar().setTitle("Audio Settings");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_left_white_24dp));
            } else {
                getSupportActionBar().setTitle(R.string.app_name);
            }

            layoutBottomSheet = findViewById(R.id.bottom_sheet1);
//            sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

            Log.d("TAG", " path : " + getIntent().getStringExtra("PATH"));
            String photoPath = getIntent().getStringExtra("PATH");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);

            ImageView mIvOcr = findViewById(R.id.imgOcr);
            mIvOcr.setImageBitmap(bitmap);

            toGetImage(bitmap);
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

            TextView tv = findViewById(R.id.textOCR1);
            tv.setText(stringBuilder);
            layoutBottomSheet.setVisibility(View.VISIBLE);

//            toggleBottomSheet();
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
    public void toggleBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            btnBottomSheet.setText("Expand sheet");
        }
    }

}
