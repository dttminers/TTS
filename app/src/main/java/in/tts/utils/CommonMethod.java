package in.tts.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.MailTo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.Settings.Secure;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import in.tts.R;

public class CommonMethod {
    private static AlertDialog dialog;
    private static TextView tvMsg;

    public static int dpToPx(int dp, Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static boolean isValidEmail(String email) {
        return Pattern.compile("^[_A-Za-z0-9-\\s+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(email).matches();
    }

    public static boolean isValidMobileNo(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isValidPassword(String str) {
        //Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character:
        return Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}").matcher(str).matches();
    }

    public static String firstLetterCaps(String myString) {
        return myString.substring(0, 1).toUpperCase() + myString.substring(1);
    }

    public static void setAnalyticsData(Context context, String id, String name, String contentType) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        bundle.putString("DeviceId", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Map<String, String> articleParams = new HashMap<>();
        articleParams.put("PAGE", id);
        articleParams.put("SUB_PAGE", name);
        articleParams.put("DATA", contentType);
        articleParams.put("DeviceId", Secure.getString(context.getContentResolver(), Secure.ANDROID_ID));
        FlurryAgent.logEvent("Article_Read", articleParams, true);
    }

    public static void toCallLoader(Context context, String msg) {
        try {
            //Log.d("TAG", " LOADER stxt : " + msg);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (inflater != null) {
                View view = inflater.inflate(R.layout.custom_loader, null, false);

                alertDialogBuilder.setView(view);
                alertDialogBuilder.setTitle(null);
                alertDialogBuilder.setCancelable(false);

                dialog = alertDialogBuilder.create();
                dialog.setCancelable(false);
                dialog.show();

                tvMsg = view.findViewById(R.id.txtLoaderMsg);
                tvMsg.setText(msg);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public static void toCloseLoader() {
        try {
            if (dialog != null && dialog.isShowing()) {
                if (tvMsg != null) {
                    //Log.d("TAG", " LOADER ctxt : " + tvMsg.getText().toString());
                }
                dialog.dismiss();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public static void toReleaseMemory() {
        Runtime.getRuntime().runFinalization();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public static boolean isSignedIn(Context context) {
        try {
            //Log.d("TAG ", "signed Google : " + (GoogleSignIn.getLastSignedInAccount(context) != null));
            //Log.d("TAG ", "signed Facebook 1 :" + AccessToken.isCurrentAccessTokenActive());
            //Log.d("TAG ", "signed Facebook 2 :" + AccessToken.getCurrentAccessToken());
            //Log.d("TAG ", "signed Facebook 3 :" + Profile.getCurrentProfile());
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return false;
    }

    public static void toDisplayToast(Context context, String str) {
        try {
            //Log.d("TAG ", " toDisplayToast " + str);
            if (context != null) {
                if (str != null) {
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public static void toSetTitle(ActionBar supportActionBar, Context context, String title) {
        try {
            supportActionBar.show();
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(true);
            supportActionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(context, R.drawable.ic_left_white_24dp));

            if (title != null) {
                supportActionBar.setTitle(title);
            } else {
                supportActionBar.setTitle(context.getResources().getString(R.string.app_name));
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    // Online Connection checking Code.................
    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return !(connectivityManager == null || connectivityManager.getActiveNetworkInfo() == null || !connectivityManager.getActiveNetworkInfo().isConnected());
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return false;
        }
    }

    public static String getFileSize(File file) {
        try {
            DecimalFormat format = new DecimalFormat("#.##");
            long MiB = 1024 * 1024;
            long KiB = 1024;
            if (file.exists()) {
                if (!file.isFile()) {
                    throw new IllegalArgumentException("Expected a file");
                }
                final double length = file.length();

                if (length > MiB) {
                    return format.format(length / MiB) + " Mb";//" MiB";
                }
                if (length > KiB) {
                    return format.format(length / KiB) + " Kb";//" KiB";
                }
                return format.format(length) + " B";
            } else {
                return "0 B";
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return "0 B";
        }
    }

    public static Locale LocaleFromString(String locale) {
        try {
            String parts[] = locale.split("_", -1);
            if (parts.length == 1) return new Locale(parts[0]);
            else if (parts.length == 2
                    || (parts.length == 3 && parts[2].startsWith("#")))
                return new Locale(parts[0], parts[1]);
            else return new Locale(parts[0], parts[1], parts[2]);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return Locale.US;
        }
    }

    public static void bound(Context context, View view) {
        int windowWidth = getWindowWidth(context);
        int windowHeight = getWindowHeight(context);
        int statusBarHeight = getStatusBarHeight(context);
        int dimen56dp = context.getResources().getDimensionPixelOffset(R.dimen.layout_margin_56dp);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(windowHeight - statusBarHeight - dimen56dp, View.MeasureSpec.EXACTLY);

        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    public static Bitmap capture(View view, float width, float height, Bitmap.Config config) {
        if (!view.isDrawingCacheEnabled()) {
            view.setDrawingCacheEnabled(true);
        }

        view.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, config);
        bitmap.eraseColor(Color.WHITE);

        Canvas canvas = new Canvas(bitmap);
        int left = view.getLeft();
        int top = view.getTop();
        int status = canvas.save();
        canvas.translate(-left, -top);

        float scale = width / view.getWidth();
        canvas.scale(scale, scale, left, top);

        view.draw(canvas);
        canvas.restoreToCount(status);

        Paint alphaPaint = new Paint();
        alphaPaint.setColor(Color.TRANSPARENT);

        canvas.drawRect(0f, 0f, 1f, height, alphaPaint);
        canvas.drawRect(width - 1f, 0f, width, height, alphaPaint);
        canvas.drawRect(0f, 0f, width, 1f, alphaPaint);
        canvas.drawRect(0f, height - 1f, width, height, alphaPaint);
        canvas.setBitmap(null);

        return bitmap;
    }

    public static float dp2px(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static Drawable getDrawable(Context context, int id) {
        return context.getResources().getDrawable(id, null);
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }

        return 0;
    }

    public static int getWindowHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getWindowWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length), 50, 50, false);
        } catch (Exception e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return null;
        }
    }


    public static int sizeOf(Bitmap data) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                return data.getRowBytes() * data.getHeight();
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return data.getByteCount();
            } else {
                return data.getAllocationByteCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            return 0;
        }
    }

    public static Intent getEmailIntent(MailTo mailTo) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailTo.getTo()});
        intent.putExtra(Intent.EXTRA_TEXT, mailTo.getBody());
        intent.putExtra(Intent.EXTRA_SUBJECT, mailTo.getSubject());
        intent.putExtra(Intent.EXTRA_CC, mailTo.getCc());
        intent.setType("message/rfc822");

        return intent;
    }

    public static void share(Context context, String title, String url) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(sharingIntent, (context.getString(R.string.menu_share_link))));
    }


    // Methods

    public static void printPDF (WebView ninjaWebView, Activity activity) {

        try {
//            sp.edit().putBoolean("pdf_create", true).commit();

//            if (share) {
//                sp.edit().putBoolean("pdf_share", true).commit();
//            } else {
//                sp.edit().putBoolean("pdf_share", false).commit();
//            }

            String title = HelperUnit.fileName(ninjaWebView.getUrl());
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            File file = new File(dir, title + ".pdf");
//            sp.edit().putString("pdf_path", file.getPath()).apply();

            String pdfTitle = file.getName().replace(".pdf", "");

            PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter printAdapter = ninjaWebView.createPrintDocumentAdapter(title);
            Objects.requireNonNull(printManager).print(pdfTitle, printAdapter, new PrintAttributes.Builder().build());

        } catch (Exception e) {
//            sp.edit().putBoolean("pdf_create", false).commit();
            e.printStackTrace();
        }
    }
}

//    public static void readDocxFile() {
//
//        try {
//            File file = new File("C:\\test.docx");
//            FileInputStream fis = new FileInputStream(file.getAbsolutePath());
//            XWPFDocument document = new XWPFDocument(fis);
//            List<XWPFParagraph> paragraphs = document.getParagraphs();
//
//            for (XWPFParagraph para : paragraphs) {
//                System.out.println(para.getText());
//
//                fis.close();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String docxRead(String filePath) {
//        try {
//            WordDocument doc = new WordDocument(filePath);
//            String text = doc.toText();
//            return text;
//        }
//        catch (Exception e) {
////            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//        return "";
//    }
//}

/*
BigInteger currentParagraphNumberingID = currentPara_Line.getCTP().getPPr().getNumPr().getNumId().getVal();
BigInteger currentParagraphAbstractNumID2 = currentPara_Line.getDocument().getNumbering().getAbstractNumID(currentParagraphNumberingID);
XWPFAbstractNum currentParagraphAbstractNum = currentPara_Line.getDocument().getNumbering().getAbstractNum(currentParagraphAbstractNumID2);
CTAbstractNum currentParagraphAbstractNumFormatting = currentParagraphAbstractNum.getCTAbstractNum();



 */