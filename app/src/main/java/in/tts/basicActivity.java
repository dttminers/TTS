package in.tts;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

public class basicActivity extends AppCompatActivity {

    private TextView mTextMessage;

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
//            return false;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);

        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        walkdir(Environment.getExternalStorageDirectory());
    }

/*public void textfrom pdf(File dir) {
PDDocument pd;
 BufferedWriter wr;
 try {
         File input = new File("C:\\Invoice.pdf");  // The PDF file from where you would like to extract
         File output = new File("C:\\SampleText.txt"); // The text file where you are going to store the extracted data
         pd = PDDocument.load(input);
         System.out.println(pd.getNumberOfPages());
         System.out.println(pd.isEncrypted());
         pd.save("CopyOfInvoice.pdf"); // Creates a copy called "CopyOfInvoice.pdf"
         PDFTextStripper stripper = new PDFTextStripper();
         stripper.setStartPage(3); //Start extracting from page 3
         stripper.setEndPage(5); //Extract till page 5
         wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
         stripper.writeText(pd, wr);
         if (pd != null) {
             pd.close();
         }
        // I use close() to flush the stream.
        wr.close();
 } catch (Exception e){
         e.printStackTrace();
        }
     }

*/
    public void walkdir(File dir) {
        String pdfPattern = ".pdf";

        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (File aListFile : listFile) {

                if (aListFile.isDirectory()) {
                    walkdir(aListFile);
                } else {
                    if (aListFile.getName().endsWith(pdfPattern)) {
                        //Do what ever u want
                        Log.d("TAG", "IS PDF  " + aListFile.getName() + ":" + aListFile.canWrite());
                    } else {
                        Log.d("TAG", "NO PDF  " + aListFile.getName() + ":" + aListFile.canWrite());
                    }
                }
            }
        }
    }
}
