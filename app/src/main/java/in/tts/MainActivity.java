package in.tts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import in.tts.R;

public class MainActivity extends Activity{
    private static final String TAG = "OOOOOOO";
    TextToSpeech t1;
    Button lklklkl,lklklkl222;
    String speakTextTxt="Yuhhu worked. Empty DATA";
    String tempDestFile = "";
    HashMap<String, String> myHashRender = new HashMap<String, String>();
    EditText editT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                //File write logic here
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 155);

            }
        }

        lklklkl = (Button) findViewById(R.id.lklklkl);
        lklklkl.setVisibility(View.GONE);
        lklklkl222 = (Button) findViewById(R.id.lklklkl222);
        editT = (EditText) findViewById(R.id.editT);



        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });





        lklklkl222.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speakTextTxt = ""+editT.getText();
                if(speakTextTxt.equals(""))
                {
                    speakTextTxt = "Yuhhu worked. Empty DATA";
                }
                t1.speak(speakTextTxt+"", TextToSpeech.QUEUE_FLUSH, null);

                //speakTextTxt = "HEYY HEY Toast.makeText(FirstPageActivity.this,i, Toast.LENGTH_LONG).show()";
                myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, speakTextTxt);

                String exStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                Log.d("MainActivity", "exStoragePath : "+exStoragePath);
                File appTmpPath = new File(exStoragePath + "/sounds/");
                boolean isDirectoryCreated = appTmpPath.mkdirs();
                Log.d("MainActivity", "directory "+appTmpPath+" is created : "+isDirectoryCreated);
                long pp = System.currentTimeMillis();

                String tempFilename = pp+".wav";
                tempDestFile = appTmpPath.getAbsolutePath() + File.separator + tempFilename;
                Log.d("MainActivity", "tempDestFile : "+tempDestFile);
                new CreateMediaFile(speakTextTxt);

            }
        });
    }

    class CreateMediaFile implements TextToSpeech.OnInitListener
    {

        String tts;
        TextToSpeech mTts;

        public CreateMediaFile(String tts)
        {
            this.tts = tts;
            mTts = new TextToSpeech(MainActivity.this, this);
        }

        @Override
        public void onInit(int status)
        {
            Log.v("log", "initi");
            int i = mTts.synthesizeToFile(speakTextTxt, myHashRender, tempDestFile);
            if(i == TextToSpeech.SUCCESS)
            {
                Toast toast = Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private static final int FILE_SELECT_CODE = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try
        {
            t1.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
    }
}