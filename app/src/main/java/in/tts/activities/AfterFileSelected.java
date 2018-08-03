package in.tts.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import in.tts.R;
import in.tts.classes.ToSetMore;
import in.tts.utils.CommonMethod;

public class AfterFileSelected extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    private static final String TAG = "OOOOOOO";
    public static String SAMPLE_FILE = "kkkk.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    PdfReader reader;
    int noOfUtterance = 0;
    int prevVal = 0;


    TextToSpeech t1;
    LinkedHashMap<String, String> inCaseOfPause = new LinkedHashMap<>();
    List<String> inCaseOfForward = new LinkedList<>();

    List<String> textCollection = new ArrayList<String>();

    String parsedText = "";
    String path = null;
    //Button lklklkl,lklklkl222,backword,forward;
//    Button playMale,playFemale,pause,stop,forward,backward;//,backword,forward;
    Button pause, stop, forward, backward;//,backword,forward;
    boolean isPause = false;
    boolean isFrdOrBackwrd = false;

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(AfterFileSelected.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(AfterFileSelected.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(AfterFileSelected.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(AfterFileSelected.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(AfterFileSelected.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(AfterFileSelected.this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_act);
        fn_permission();
        try {
            if (getIntent().getStringExtra("file") != null) {

                path = getIntent().getStringExtra("file").trim();

                File file = new File(path);

                if (getSupportActionBar() != null) {
                    CommonMethod.toSetTitle(getSupportActionBar(), AfterFileSelected.this, file.getName());
                }

            }
//            path = getIntent().getExtras().getString("data");
        } catch (Exception e) {
            e.printStackTrace();
        }


//        Log.d(TAG, "##################====" +path);

        //lklklkl = (Button) findViewById(R.id.lklklkl);
//        playMale = (Button) findViewById(R.id.playMale);
//        playFemale = (Button) findViewById(R.id.playFemale);
        pause = (Button) findViewById(R.id.playPause);
        stop = (Button) findViewById(R.id.close);
        forward = (Button) findViewById(R.id.forward);
        backward = (Button) findViewById(R.id.backward);
        //lklklkl222 = (Button) findViewById(R.id.lklklkl222);
        //forward = (Button) findViewById(R.id.forward);
        //lklklkl222.setVisibility(GONE);
//        SAMPLE_FILE = path;

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t1.stop();
                isFrdOrBackwrd = false;
                isPause = false;

                try {
                    inCaseOfPause.clear();
                    inCaseOfForward.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                inCaseOfPause = new LinkedHashMap<>();
                inCaseOfForward = new LinkedList<String>();
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isFrdOrBackwrd = true;
                isPause = true;
                t1.stop();
                Toast.makeText(getApplicationContext(), "prevL = " + prevVal + " &&  currL = " + noOfUtterance, Toast.LENGTH_SHORT).show();

                if (isPause) {


                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String lineByLineAfterPause = "";

                            for (int i = prevVal + 2; i < inCaseOfForward.size(); i++) {
                                noOfUtterance = 0;

                                String val = inCaseOfForward.get(i) + ".";
                                Log.d("checkWhat", "FORWAREDDDD = " + val);
                                lineByLineAfterPause = lineByLineAfterPause + "" + val;
                                Log.d("checkWhat", "------------------>>>>>>>>>>>>>>>> " + lineByLineAfterPause);

                            }
                            try {
                                inCaseOfPause.clear();
                                inCaseOfForward.clear();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            inCaseOfPause = new LinkedHashMap<>();
                            inCaseOfForward = new LinkedList<String>();
                            isPause = false;

                            speakSpeech("" + lineByLineAfterPause);
                        }
                    }, 2000);
                } else {
                    Toast.makeText(getApplicationContext(), "Play it first!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isFrdOrBackwrd = true;
                isPause = true;
                t1.stop();
                Toast.makeText(getApplicationContext(), "prevL = " + prevVal + " &&  currL = " + noOfUtterance, Toast.LENGTH_SHORT).show();

                if (isPause) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            String lineByLineAfterPause = "";
                            try {
                                for (int i = prevVal; i < inCaseOfForward.size(); i++) {
                                    noOfUtterance = 0;

                                    String val = inCaseOfForward.get(i) + ".";
                                    Log.d("checkWhat", "FORWAREDDDD = " + val);
                                    lineByLineAfterPause = lineByLineAfterPause + "" + val;
                                    Log.d("checkWhat", "<<<<<<<<<<<<<<<<<<<<<<<<------------------ " + lineByLineAfterPause);
                                    //speakSpeechAfterPause(inCaseOfPause);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                inCaseOfPause.clear();
                                inCaseOfForward.clear();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            inCaseOfPause = new LinkedHashMap<>();
                            inCaseOfForward = new LinkedList<String>();
                            isPause = false;

                            speakSpeech("" + lineByLineAfterPause);
                        }
                    }, 2000);

                } else {
                    Toast.makeText(getApplicationContext(), "Play it first!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("checkWhat", "################OnPause remaining texts ******************************* ");

                for (Map.Entry<String, String> entry : inCaseOfPause.entrySet()) {
                    String key = entry.getKey();
                    String val = entry.getValue();
                    //afterPauseUse.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,""+val);
                    Log.d("checkWhat", "key = " + key + ",,,val = " + val);
                }
                t1.stop();
                isPause = true;

                isFrdOrBackwrd = false;
            }
        });

//        playFemale.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isFrdOrBackwrd = false;
//                if(t1.isSpeaking())
//                {
//                    Toast.makeText(getApplicationContext(),"Already playing!",Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//                        Set<Voice> voiceList = t1.getVoices();
//                        for (Voice voice : voiceList) {
//                            Log.d("rarrarar", "Voice: " + voice.getName());
//                            if (voice.getName().toLowerCase().contains("en-us") && voice.getName().toLowerCase().contains("female"))
//                            {
//                                Log.d("rarrarar", "Voice available: " + voice.getName());
//                                t1.setVoice(voice);
//                                break;
//                            }
//                        }
//                    }
//
//
//                    if(isPause)
//                    {
//                    /*t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                        @Override
//                        public void onInit(int status) {
//                            if(status != TextToSpeech.ERROR) {
//                                t1.setLanguage(Locale.UK);
//                            }
//                        }
//                    });*/
//                        noOfUtterance = 0;
//                        String lineByLineAfterPause = "";
//
//                        for ( Map.Entry<String, String> entry : inCaseOfPause.entrySet()) {
//                            String key = entry.getKey();
//                            String val = entry.getValue()+".";
//                            Log.d("checkWhat","key = "+key+",,,val = "+val);
//                            lineByLineAfterPause = lineByLineAfterPause+""+val;
//                        }
//                        Log.d("checkWhat","*************************&&&&&&&&&&\n "+lineByLineAfterPause);
//
//                        try {
//                            inCaseOfPause.clear();
//                            inCaseOfForward.clear();
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                        inCaseOfPause = new LinkedHashMap<>();
//                        inCaseOfForward = new LinkedList<String>();
//                        isPause = false;
//
//                        speakSpeech(""+lineByLineAfterPause);
//                        //speakSpeechAfterPause(inCaseOfPause);
//                    }
//                    else
//                    {
//                        try {
//
//                            PdfReader reader = new PdfReader("file:///storage/emulated/0/Download/easymock_tutorial.pdf");
//                            Log.d("TAG", " TG1" + reader.getFileLength());
//                            int n = reader.getNumberOfPages();
//                            try
//                            {
//                                t1.stop();
//                            }
//                            catch (Exception e)
//                            {
//                                e.printStackTrace();
//
//                            }
//
//                            int pg = pageNumber;
//
//                            setTitle(String.format("%s %s / %s", pdfFileName, pg + 1, n));
//
//                            Log.d("pggnooooSpeech","pg = "+pg+" && "+(pg+1));
//                            try {
//                                String lineByLine = PdfTextExtractor.getTextFromPage(reader, pg+1).trim()+"\n";
//                                Log.d("pggnoooo","LINE---> "+lineByLine);
//                                String otherTxt = lineByLine;
//
//                                speakSpeech(""+lineByLine);
//
//                                //Code to generate aud file
//                                File dir = Environment.getExternalStorageDirectory();
//                                String filep = dir.getAbsolutePath()+"/MyPdfAud/file.wav";
//
//                                HashMap<String, String> myHashRender = new HashMap();
//                                myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, otherTxt);
//                                t1.synthesizeToFile(otherTxt, myHashRender, filep);
//
//                                /*File destinationFile = new File(filep, "file" + ".wav");
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                    t1.synthesizeToFile(otherTxt, null, destinationFile, "file");
//                                }*/
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                }
//
//
//            }
//        });
//        playMale.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isFrdOrBackwrd = false;
//                if(t1.isSpeaking())
//                {
//                    Toast.makeText(getApplicationContext(),"Already playing!",Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//                        Set<Voice> voiceList = t1.getVoices();
//                        for (Voice voice : voiceList) {
//                            Log.d("rarrarar", "Voice: " + voice.getName());
//                            if (voice.getName().toLowerCase().contains("en-us") && voice.getName().toLowerCase().contains("#male"))
//                            {
//                                Log.d("rarrarar", "Voice available: " + voice.getName());
//                                t1.setVoice(voice);
//                                break;
//                            }
//                        }
//                    }
//
//
//                    if(isPause)
//                    {
//                    /*t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                        @Override
//                        public void onInit(int status) {
//                            if(status != TextToSpeech.ERROR) {
//                                t1.setLanguage(Locale.UK);
//                            }
//                        }
//                    });*/
//                        noOfUtterance = 0;
//                        String lineByLineAfterPause = "";
//
//                        for ( Map.Entry<String, String> entry : inCaseOfPause.entrySet()) {
//                            String key = entry.getKey();
//                            String val = entry.getValue()+".";
//                            Log.d("checkWhat","key = "+key+",,,val = "+val);
//                            lineByLineAfterPause = lineByLineAfterPause+""+val;
//                        }
//                        Log.d("checkWhat","*************************&&&&&&&&&&\n "+lineByLineAfterPause);
//
//                        try {
//                            inCaseOfPause.clear();
//                            inCaseOfForward.clear();
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                        inCaseOfPause = new LinkedHashMap<>();
//                        inCaseOfForward = new LinkedList<String>();
//                        isPause = false;
//
//                        speakSpeech(""+lineByLineAfterPause);
//                        //speakSpeechAfterPause(inCaseOfPause);
//                    }
//                    else
//                    {
//                        try {
//
//                            PdfReader reader = new PdfReader("file:///storage/emulated/0/Download/easymock_tutorial.pdf");
//                            Log.d("TAG", " TG2" + reader.getFileLength());
//                            int n = reader.getNumberOfPages();
//                            try
//                            {
//                                t1.stop();
//                            }
//                            catch (Exception e)
//                            {
//                                e.printStackTrace();
//
//                            }
//
//                            int pg = pageNumber;
//
//                            setTitle(String.format("%s %s / %s", pdfFileName, pg + 1, n));
//
//                            Log.d("pggnooooSpeech","pg = "+pg+" && "+(pg+1));
//                            try {
//                                String lineByLine = PdfTextExtractor.getTextFromPage(reader, pg+1).trim()+"\n";
//                                Log.d("pggnoooo","LINE---> "+lineByLine);
//
//                                String otherTxt = lineByLine;
//                                speakSpeech(""+lineByLine);
//
//                                //Code to generate aud file
//                                File dir = Environment.getExternalStorageDirectory();
//                                String filep = dir.getAbsolutePath()+"/MyPdfAud/fileMale.wav";
//
//                                HashMap<String, String> myHashRender = new HashMap();
//                                myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, otherTxt);
//                                t1.synthesizeToFile(otherTxt, myHashRender, filep);
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                }
//
//
//            }
//        });

       /* forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //Log.d("rarrarar",""+t1.getVoices().toString());

                    //tts.speak("வரவேற்பு rubin", TextToSpeech.QUEUE_ADD, null);

                    Set<Voice> voiceList = t1.getVoices();
                    for (Voice voice : voiceList) {
                        Log.d("rarrarar", "Voice: " + voice.getName());
                        if (voice.getName().contains("en-us") && voice.getName().contains("#male"))
                        {
                            Log.d("rarrarar", "Voice available: " + voice.getName());
                            t1.setVoice(voice);
                        }
                    }
                }
            }
        });*/

        pdfView = (PDFView) findViewById(R.id.pdfView);
        displayFromAsset(path);
//        displayFromAsset("file:///storage/emulated/0/Download/easymock_tutorial.pdf");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pdfView.setOnContextClickListener(new View.OnContextClickListener() {
                @Override
                public boolean onContextClick(View view) {

                    Log.d("pggnoooo", "TRUE.....");
                    return false;
                }
            });
        }

        pdfView.setSelected(true);

        try {
            reader = new PdfReader(path);
            Log.d("TAG", " TG3" + reader.getFileLength());
        } catch (IOException e) {
            e.printStackTrace();
        }

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        t1.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
            @Override
            public void onUtteranceCompleted(String s) {
                Log.d("checkWhat", "---->" + " wait wt = " + noOfUtterance + " isPause = " + isPause + " == arrSize = " + inCaseOfPause.size());

                if (!isPause) {
                    prevVal = noOfUtterance;
                    inCaseOfPause.remove("" + noOfUtterance);
                    noOfUtterance = noOfUtterance + 1;
                }


                //completedText.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,s.trim());
                /*for ( Map.Entry<String, String> entry : inCaseOfPause.entrySet()) {
                    String key = entry.getKey();
                    String val = entry.getValue();
                    if(val.trim().equals(""+s.trim()))
                    {
                        Log.d("checkWhat","key = "+key+",,,val = "+val);
                    }
                }*/
            }
        });


        /*lklklkl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //speakOut();
                //t1.speak("SwaRaj Kriya is a Meditation technique. SwaRaj Kriya app gives simpler way to perform meditation\\n\" +\n", TextToSpeech.QUEUE_FLUSH, null);


                try {

                    PdfReader reader = new PdfReader(path);
                    int n = reader.getNumberOfPages();

                    Log.d("pppapapap","number of pages n = \n\n"+n);

                    for (int i = 0; i <n ; i++) {

                        String lineByLine = PdfTextExtractor.getTextFromPage(reader, i+1).trim()+"\n";

                        textCollection.add(lineByLine);

                        Log.d("pppapapap","NEW---------------"+lineByLine);
                        parsedText   = parsedText+lineByLine ;
                    }
                    //Log.d("pppapapap","====\n\n"+parsedText);
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for(int i=0;i<textCollection.size();i++)
                {
                    Log.d("pppapapap","###################################################################################################"+i+"\n"+textCollection.get(i));
                    while(t1.isSpeaking())
                    {

                    }

                    Toast.makeText(AfterFileSelected.this, ""+i, Toast.LENGTH_LONG).show();

                    //new SpeakTheText().execute(textCollection.get(i));

                    t1.speak(textCollection.get(i), TextToSpeech.QUEUE_ADD, null);

                }

            }
        });*/


    }

    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromFile(new File("/storage/emulated/0/Download/easymock_tutorial.pdf"))
                .defaultPage(pageNumber)
                .enableSwipe(true)
// allows to draw something on the current page, usually visible in the middle of the screen
                .onDraw(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                        //Log.d("pggnoooo","DRAWWWWWW "+displayedPage+" ,"+pageWidth+","+pageHeight);
                        /*canvas.drawColor(Color.parseColor("#ff0000"));
                        Paint myPaint = new Paint();
                        myPaint.setStyle(Paint.Style.STROKE);
                        myPaint.setColor(Color.rgb(0, 0, 0));
                        myPaint.setStrokeWidth(10);
                        canvas.drawRect(100, 100, 200, 200, myPaint);*/

                    }
                })
                .enableAnnotationRendering(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }


    @Override
    public void onPageChanged(int page, int pageCount) {

        try {
            t1.stop();
        } catch (Exception e) {
            e.printStackTrace();

        }

        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));

        /*Log.d("pggnoooo","pageNumber = "+pageNumber+" && "+(page+1)+"---"+pageCount);
        try {

            //int n = reader.getNumberOfPages();


            //for (int i = 0; i <n ; i++) {

            String lineByLine = PdfTextExtractor.getTextFromPage(reader, page+1).trim()+"\n";


            Log.d("pggnoooo","LINE---> "+lineByLine);

            t1.speak(""+lineByLine, TextToSpeech.QUEUE_FLUSH, null);
            //parsedText   = parsedText+lineByLine ;
            //}
            //Log.d("pppapapap","====\n\n"+parsedText);
            //reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

        Log.d("pggnoooo", "meta---> " + meta.getKeywords());

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            t1.stop();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void speakSpeechAfterPause(LinkedHashMap<String, String> myHash) {

        LinkedHashMap<String, String> myHashUse = myHash;

        try {
            inCaseOfPause.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        inCaseOfPause = new LinkedHashMap<>();
        LinkedHashMap<String, String> myHashAfterP = new LinkedHashMap<String, String>();

        isPause = false;

        Log.d("checkWhat", "4$$$$$$$$$$$$ BEFORE LOOPING = " + myHashUse.size());

        int i = 0;
        for (Map.Entry<String, String> entry : myHashUse.entrySet()) {
            //String key = entry.getKey();
            String val = entry.getValue();

            Log.d("checkWhat", "#### AFTER PAUSE = " + val);

            myHashAfterP.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "" + val);
            inCaseOfPause.put("" + i, "" + val);

            if (i == 0) { // Use for the first splited text to flush on audio stream

                t1.speak(val.toString().trim(), TextToSpeech.QUEUE_FLUSH, myHashAfterP);

            } else { // add the new test on previous then play the TTS

                t1.speak(val.toString().trim(), TextToSpeech.QUEUE_ADD, myHashAfterP);
            }

            t1.playSilence(10, TextToSpeech.QUEUE_ADD, null);

            i++;

        }

        Log.d("checkWhat", "inCaseOfPause SIZE AFTER PAUSE = " + inCaseOfPause.size());

    }

    public void speakSpeech(String speech) {

        LinkedHashMap<String, String> myHash = new LinkedHashMap<String, String>();

        String[] splitspeech = speech.split("\\.");

        Log.d("checkWhat", "llllleenghttttthhhhhhhhhhhhhhhhhhhh = " + splitspeech.length);

        for (int i = 0; i < splitspeech.length; i++) {

            Log.d("checkWhat", "splitspeech = " + splitspeech[i]);

            myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "" + splitspeech[i]);
            inCaseOfPause.put("" + i, "" + splitspeech[i]);
            inCaseOfForward.add("" + splitspeech[i]);

            if (i == 0) { // Use for the first splited text to flush on audio stream

                t1.speak(splitspeech[i].toString().trim(), TextToSpeech.QUEUE_FLUSH, myHash);

            } else { // add the new test on previous then play the TTS

                t1.speak(splitspeech[i].toString().trim(), TextToSpeech.QUEUE_ADD, myHash);
            }

            t1.playSilence(10, TextToSpeech.QUEUE_ADD, null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d("checkWhat", "speech length = " + t1.getMaxSpeechInputLength() + "---");
        }

        Log.d("checkWhat", "inCaseOfPause SIZE = " + inCaseOfPause.size());


        /*for ( Map.Entry<String, String> entry : myHash.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            Log.d("checkWhat","key = "+key+",,,val = "+val);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.speak_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            ToSetMore.MenuOptions(AfterFileSelected.this, item);
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                case R.id.menuSpeak:
                    try {
//                        progressBarSpeak.setVisibility(View.VISIBLE);
//                        toGetData((firstVisibleItem + 1));
                        toSpeak();
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                    }
                    break;
                default:
                    return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
        }
        return super.onOptionsItemSelected(item);
    }

    private void toSpeak() {

        isFrdOrBackwrd = false;
        if (t1.isSpeaking()) {
            Toast.makeText(getApplicationContext(), "Already playing!", Toast.LENGTH_LONG).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Set<Voice> voiceList = t1.getVoices();
                for (Voice voice : voiceList) {
                    Log.d("rarrarar", "Voice: " + voice.getName());
                    if (voice.getName().toLowerCase().contains("en-us") && voice.getName().toLowerCase().contains("#male")) {
                        Log.d("rarrarar", "Voice available: " + voice.getName());
                        t1.setVoice(voice);
                        break;
                    }
                }
            }


            if (isPause) {
                    /*t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR) {
                                t1.setLanguage(Locale.UK);
                            }
                        }
                    });*/
                noOfUtterance = 0;
                String lineByLineAfterPause = "";

                for (Map.Entry<String, String> entry : inCaseOfPause.entrySet()) {
                    String key = entry.getKey();
                    String val = entry.getValue() + ".";
                    Log.d("checkWhat", "key = " + key + ",,,val = " + val);
                    lineByLineAfterPause = lineByLineAfterPause + "" + val;
                }
                Log.d("checkWhat", "*************************&&&&&&&&&&\n " + lineByLineAfterPause);

                try {
                    inCaseOfPause.clear();
                    inCaseOfForward.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                inCaseOfPause = new LinkedHashMap<>();
                inCaseOfForward = new LinkedList<String>();
                isPause = false;

                speakSpeech("" + lineByLineAfterPause);
                //speakSpeechAfterPause(inCaseOfPause);
            } else {
                try {

                    PdfReader reader = new PdfReader("file:///storage/emulated/0/Download/easymock_tutorial.pdf");
                    Log.d("TAG", " TG2" + reader.getFileLength());
                    int n = reader.getNumberOfPages();
                    try {
                        t1.stop();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    int pg = pageNumber;

                    setTitle(String.format("%s %s / %s", pdfFileName, pg + 1, n));

                    Log.d("pggnooooSpeech", "pg = " + pg + " && " + (pg + 1));
                    try {
                        String lineByLine = PdfTextExtractor.getTextFromPage(reader, pg + 1).trim() + "\n";
                        Log.d("pggnoooo", "LINE---> " + lineByLine);

                        String otherTxt = lineByLine;
                        speakSpeech("" + lineByLine);

                        //Code to generate aud file
                        File dir = Environment.getExternalStorageDirectory();
                        String filep = dir.getAbsolutePath() + "/MyPdfAud/fileMale.wav";

                        HashMap<String, String> myHashRender = new HashMap();
                        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, otherTxt);
                        t1.synthesizeToFile(otherTxt, myHashRender, filep);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }


    }

}



