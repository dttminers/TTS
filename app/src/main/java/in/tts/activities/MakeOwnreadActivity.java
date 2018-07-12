package in.tts.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import in.tts.R;

public class MakeOwnreadActivity extends AppCompatActivity {

    private EditText editText;
    private TextToSpeech t1;
    private ImageView ivCopy, ivPaste, ivShare, ivSpeak;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_ownread);

        editText = findViewById(R.id.edMakeRead);
        ivCopy = findViewById(R.id.ivCopy);
        ivPaste = findViewById(R.id.ivPaste);
        ivShare = findViewById(R.id.ivShare);
        ivSpeak = findViewById(R.id.ivSpeak);


    }
}
