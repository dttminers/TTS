package in.tts.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import in.tts.R;
import in.tts.utils.CommonMethod;

public class Contact_us extends AppCompatActivity {

    private Button mBtnSave;
    private EditText mEditEmailId;
    private EditText mEditMessage;
    private EditText mEditMobileNo;
    private EditText mEditName;

    private TextView mTxtEmailId;
    private TextView mTxtEmailIdError;

    private TextView mTxtMessage;
    private TextView mTxtMessageError;

    private TextView mTxtMobileNo;
    private TextView mTxtMobileNoError;

    private TextView mTxtName;
    private TextView mTxtNameError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        try {
            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), Contact_us.this, getString(R.string.app_name));
            }

            mTxtName = findViewById(R.id.txtCuUserName);
            mTxtNameError = findViewById(R.id.txtCuUserNameError);

            mTxtMobileNo = findViewById(R.id.txtCuUserMobileNumber);
            mTxtMobileNoError = findViewById(R.id.txtCuUserMobileNumberError);

            mTxtEmailId = findViewById(R.id.txtCuUserEmail);
            mTxtEmailIdError = findViewById(R.id.txtCuUserEmailError);

            mTxtMessage = findViewById(R.id.txtCuUserMessage);
            mTxtMessageError = findViewById(R.id.txtCuUserMessageError);

            mEditName = findViewById(R.id.edtCuUserName);
            mEditMobileNo = findViewById(R.id.edtCuUserMobileNumber);

            mEditEmailId = findViewById(R.id.edtCuUserEmail);
            mEditMessage = findViewById(R.id.edtCuUserMessage);

            mBtnSave = findViewById(R.id.btnCuSave);

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        toReset();
    }

    private void toReset() {
        try {
            if (mEditName != null) {
                mEditName.setText("");
            }

            if (mEditEmailId != null) {
                mEditEmailId.setText("");
            }

            if (mEditMobileNo != null) {
                mEditMobileNo.setText("");
            }

            if (mEditMessage != null) {
                mEditMessage.setText("");
            }


            if (mTxtNameError != null) {
                mTxtNameError.setVisibility(View.GONE);
            }

            if (mTxtEmailIdError != null) {
                mTxtEmailIdError.setVisibility(View.GONE);
            }

            if (mTxtMobileNoError != null) {
                mTxtMobileNoError.setVisibility(View.GONE);
            }
            if (mTxtMessageError != null) {
                mTxtMessageError.setVisibility(View.GONE);
            }

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }
}