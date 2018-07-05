package in.tts.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import in.tts.*;

public class ImageOcrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_image_ocr);
            Log.d("TAG", " path : " + getIntent().getStringExtra("PATH"));
            String photoPath = getIntent().getStringExtra("PATH");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
//            selected_photo.setImageBitmap(bitmap);
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
                Log.d("TAG", " Result : "+i+":" + imageText);
            }
            Log.d("TAG", " Result Final: " + imageText);

            StringBuilder stringBuilder = new StringBuilder();
            final SparseArray<TextBlock> items = textRecognizer.detect(imageFrame);
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
                Log.d("TAG","DATA : " + i + stringBuilder);
            }
            Log.d("TAG", " Final DATA " + stringBuilder);
        } catch (Exception | Error e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
