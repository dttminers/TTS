package in.tts.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import in.tts.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle("Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_backward));
        } else {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        findViewById(R.id.rlLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doExit();
            }
        });
    }

    private void doExit() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setPositiveButton(getResources().getString(R.string.lbl_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            alertDialog.setNegativeButton(getResources().getString(R.string.lbl_no), null);
            alertDialog.setMessage(getResources().getString(R.string.lbl_str_exit_from_app));
            alertDialog.setTitle(getResources().getString(R.string.app_name));
            alertDialog.show();
        } catch (Exception | Error e) {
            e.printStackTrace();
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
