package in.tts.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.crash.FirebaseCrash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import in.tts.R;
import in.tts.classes.TTS;
import in.tts.classes.TTTS;
import in.tts.classes.ToSetMore;
import in.tts.model.AudioSetting;
import in.tts.utils.CommonMethod;

public class MyCameraActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private ImageView imageView;
    private String bitmapString;
    private Bitmap bitmap;

    private TextRecognizer textRecognizer;
    private Frame imageFrame;
    private SparseArray<TextBlock> items;
    private TextBlock item;
    private StringBuilder stringBuilder;
    private View view;

    private RelativeLayout mrl;
    private TTS tts;
    private Bitmap photo;
    private File file;
    private String photopath = "-";
    private Button photoButton, readout;

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (tts != null) {
                tts.toShutDown();
            }
            if (mrl != null) {
                if (mrl.getChildCount() > 1) {
                    if (view != null) {
                        mrl.removeView(view);
                    }
                }
            }
            fn_permission();
            tts = new TTS(MyCameraActivity.this);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            if (tts != null) {
                tts.toShutDown();
            }
            if (mrl != null) {
                if (mrl.getChildCount() > 1) {
                    if (view != null) {
                        mrl.removeView(view);
                    }
                }
            }
            fn_permission();
            tts = new TTS(MyCameraActivity.this);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.my_camera_activity);
            imageView = (ImageView) this.findViewById(R.id.imageView);
            photoButton = (Button) this.findViewById(R.id.capture);
            readout = (Button) this.findViewById(R.id.readout);

//            tts = new TTS(MyCameraActivity.this);
            //    tts = new TextToSpeech(this, this);
            mrl = findViewById(R.id.rlReadLayout);

            photoButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    photopath = "-";
                    imageView.setBackgroundResource(0);
                    fn_permission();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            });

            readout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!photopath.equals("-")) {
                        try {
                            // tts = new TTS(MyCameraActivity.this);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            bitmap = BitmapFactory.decodeFile(photopath.trim(), options);

                            Log.d("MYPATH", " PHOTOPATH " + photopath);
                            // mIvOcr = findViewById(R.id.imageView);
                            imageView.setImageBitmap(bitmap);
                            new toGetImage().execute();
                            fn_permission();
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                            Crashlytics.logException(e);
                            FirebaseCrash.report(e);
                        }
                    } else {
                        Toast.makeText(MyCameraActivity.this, "Please capture image first", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == MY_CAMERA_PERMISSION_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

      /*  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
            }
        }*/


    public void onActivityResult(int requestCode, int resultCode, Intent data1) {
        try {
            if (requestCode == CAMERA_REQUEST) {
                if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
                    photo = (Bitmap) data1.getExtras().get("data");
                    imageView.setImageBitmap(photo);
                    bitmapString = getStringImage1(photo);
                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    Uri tempUri = getImageUri(getApplicationContext(), photo);
                    if (tempUri != null) {
                        // CALL THIS METHOD TO GET THE ACTUAL PATH
                        file = new File(getRealPathFromURI(tempUri));
                        //photopath=file.getAbsolutePath();
                        photopath = file.getCanonicalPath();
                    }
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public String getStringImage1(Bitmap bmp) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);

            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(MyCameraActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MyCameraActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(MyCameraActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MyCameraActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(MyCameraActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(MyCameraActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }


    private class toGetImage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                imageFrame = new Frame.Builder().setBitmap(bitmap).build();

                stringBuilder = new StringBuilder();
                items = textRecognizer.detect(imageFrame);
                for (int i = 0; i < items.size(); i++) {
                    item = items.valueAt(i);
                    stringBuilder.append(item.getValue());
                    stringBuilder.append("\n");
                }
                // copystring=stringBuilder;
                Log.d("TAG", " Final DATA11 " + stringBuilder);
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
                    Log.d("OnPostExecute", "test1");

                    final TextView tvImgOcr = view.findViewById(R.id.tvImgOcr);
                    ImageView ivSpeak = view.findViewById(R.id.ivSpeak);
                    ImageView ivReload = view.findViewById(R.id.ivReload);

                    tvImgOcr.setText(stringBuilder);
                    Log.d("OnPostExecute", "value " + stringBuilder);
                    // tts=new TTS(MyCameraActivity.this);

                    ivReload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                tvImgOcr.setText("");
                                // TTS tts = new TTS(MyCameraActivity.this);
                                if (tts != null) {
                                    tts.toStop();
                                }
                                new MyCameraActivity.toGetImage().execute();
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
                                Log.d("OnPostExecuteEXP", "values  " + stringBuilder.toString());
                                //   TTS tts = new TTS(MyCameraActivity.this);
                                //tts.SpeakLoud("Hi MANASI");
                                if (tts != null) {
                                    tts.SpeakLoud(stringBuilder.toString());
                                    tts.toSaveAudioFile(stringBuilder.toString(), "AUD_Image" + getResources().getString(R.string.app_name).substring(0, (getResources().getString(R.string.app_name).length() - 4)) + System.currentTimeMillis());
                                }
                            } catch (Exception | Error e) {
                                Log.d("OnPostExecuteEXP", "exception  " + e);
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
                            CommonMethod.dpToPx(10, MyCameraActivity.this),
                            CommonMethod.dpToPx(10, MyCameraActivity.this),
                            CommonMethod.dpToPx(10, MyCameraActivity.this),
                            CommonMethod.dpToPx(50, MyCameraActivity.this)
                    );
                    mrl.addView(view, layoutParams);
                }
            } catch (Exception | Error e) {
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
            return Uri.parse(path);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    public String getRealPathFromURI(Uri uri) {
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                String path = cursor.getString(idx);
                cursor.close();
                return path;
            } else {
                return null;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (tts != null) {
                tts.toStop();
            }
            if (mrl != null) {
                if (mrl.getChildCount() > 1) {
                    if (view != null) {
                        mrl.removeView(view);
                    }
                }
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (tts != null) {
                tts.toShutDown();
            }
            if (mrl != null) {
                if (mrl.getChildCount() > 1) {
                    if (view != null) {
                        mrl.removeView(view);
                    }
                }
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
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
                ToSetMore.MenuOptions(MyCameraActivity.this, item);
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
        try {
            super.onBackPressed();
            finish();
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
    }
}