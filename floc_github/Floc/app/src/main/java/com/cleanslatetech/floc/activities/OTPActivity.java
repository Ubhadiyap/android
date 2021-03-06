package com.cleanslatetech.floc.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cleanslatetech.floc.R;
import com.cleanslatetech.floc.asynctask.OTPConformAsyncTask;
import com.cleanslatetech.floc.asynctask.RegisterUserAsyncTask;

import static com.cleanslatetech.floc.utilities.CommonUtilities.isConnectingToInternet;

public class OTPActivity extends AppCompatActivity {
    EditText txtFirstOTP, txtSecondOTP, txtThirdOTP, txtFourthOTP;
    TextView tvEmailText;
    String name, email, pwd, cnfrmPwd, OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otp);

        txtFirstOTP = (EditText) findViewById(R.id.firstOTP);
        txtSecondOTP = (EditText) findViewById(R.id.secondOTP);
        txtThirdOTP = (EditText) findViewById(R.id.thirdOTP);
        txtFourthOTP = (EditText) findViewById(R.id.fourthOTP);

        tvEmailText = (TextView) findViewById(R.id.id_email_cnfm);

        name = getIntent().getExtras().getString("name");
        email = getIntent().getExtras().getString("email");
        pwd = getIntent().getExtras().getString("pwd");
        cnfrmPwd = getIntent().getExtras().getString("cnfrmPwd");
        OTP = getIntent().getExtras().getString("OTP");

        tvEmailText.setText(email);

        txtFirstOTP.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start,int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(txtFirstOTP.getText().toString().length() == 1 ) {
                    txtSecondOTP.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

        });

        txtSecondOTP.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start,int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(txtSecondOTP.getText().toString().length()== 1 ) {
                    txtThirdOTP.requestFocus();
                }
                else if(txtSecondOTP.getText().toString().length()== 0 ) {
                    txtFirstOTP.requestFocus();
                }
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

        });

        txtThirdOTP.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start,int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(txtThirdOTP.getText().toString().length()== 1 ) {
                    txtFourthOTP.requestFocus();
                }
                else if(txtThirdOTP.getText().toString().length() == 0 ) {
                    txtSecondOTP.requestFocus();
                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

        });

        txtFourthOTP.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start,int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(txtFourthOTP.getText().toString().length() == 0 ) {
                    txtThirdOTP.requestFocus();
                }

            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void btnConformOTP(View view) {
        String fOtp = txtFirstOTP.getText().toString();
        String sOtp = txtSecondOTP.getText().toString();
        String tOtp = txtThirdOTP.getText().toString();
        String foOtp = txtFourthOTP.getText().toString();

        // OTP match
        if(OTP.equals(fOtp+sOtp+tOtp+foOtp)) {

            // Check if Internet present
            if (!isConnectingToInternet(this)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.strNoInternet),Toast.LENGTH_LONG).show();
                return;
            } else {
                View view1 = getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
                }

                new RegisterUserAsyncTask(OTPActivity.this, name, email, pwd, cnfrmPwd).postData();
            }

        }
        else {
            Toast.makeText(getApplicationContext(),"OTP did not match",Toast.LENGTH_LONG).show();
        }

    }
}
