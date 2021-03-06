package com.alchemistdigital.buxa.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alchemistdigital.buxa.DBHelper.DatabaseClass;
import com.alchemistdigital.buxa.R;
import com.alchemistdigital.buxa.asynctask.ForgotPwdAsynTask;
import com.alchemistdigital.buxa.asynctask.InsertInternationalDestinationPort;
import com.alchemistdigital.buxa.sharedprefrencehelper.GetSharedPreference;
import com.alchemistdigital.buxa.sharedprefrencehelper.SetSharedPreference;
import com.alchemistdigital.buxa.utilities.CommonVariables;
import com.alchemistdigital.buxa.utilities.RestClient;
import com.alchemistdigital.buxa.utilities.Validations;
import com.alchemistdigital.buxa.utilities.WakeLocker;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.R.id.input;
import static com.alchemistdigital.buxa.utilities.CommonUtilities.isConnectingToInternet;
import static com.alchemistdigital.buxa.utilities.Validations.emailValidate;
import static com.alchemistdigital.buxa.utilities.Validations.isEmptyString;


public class Login extends Fragment implements View.OnClickListener {
    LinearLayout layout_noConnection;
    RelativeLayout relativeLayout_loginPanel;
    EditText txtLogin, txtPassword;
    TextInputLayout loginEmail_InputLayout, loginPwd_InputLayout;
    ProgressDialog prgDialog;
    Button btnLogin, btnGoToRegister;
    TextView errorMessage, tv_forgot;
    GetSharedPreference getSharedPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        loginMethod(rootView);

        return rootView;
    }

    private void loginMethod(View rootView) {
        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(getActivity());
        // Set Progress Dialog Text
        prgDialog.setMessage("Logging ...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        layout_noConnection = (LinearLayout) rootView.findViewById(R.id.id_noInternet_login);
        errorMessage = (TextView) rootView.findViewById(R.id.login_error_msg);
        tv_forgot = (TextView) rootView.findViewById(R.id.id_btn_forgot);
        relativeLayout_loginPanel = (RelativeLayout) rootView.findViewById(R.id.layout_loginPanel);
        txtLogin = (EditText) rootView.findViewById(R.id.login_email);
        txtPassword = (EditText) rootView.findViewById(R.id.login_password);
        loginEmail_InputLayout = (TextInputLayout) rootView.findViewById(R.id.input_layout_login_email);
        loginPwd_InputLayout = (TextInputLayout) rootView.findViewById(R.id.input_layout_login_password);
        btnLogin = (Button) rootView.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnGoToRegister  = (Button) rootView.findViewById(R.id.btn_Register);
        btnGoToRegister.setOnClickListener(this);

        getSharedPreference = new GetSharedPreference(getActivity());

        // take values for validation purpose.
        // it validates entered email id and pwd when Login button pressed
        String loginSharedPrefEmail = getSharedPreference.getLoginEmail(getResources().getString(R.string.loginEmail));
        txtLogin.setText(loginSharedPrefEmail);

        Animation translateAnim = AnimationUtils.loadAnimation(getActivity(),
                R.anim.anim_logo_login);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.id_buxaLogo_splashscreen);
        imageView.startAnimation(translateAnim);
        translateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                relativeLayout_loginPanel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tv_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if Internet present
                if (!isConnectingToInternet(getActivity())) {
                    layout_noConnection.setVisibility(View.VISIBLE);
                    errorMessage.setText(getResources().getString(R.string.strNoConnection));
                    // stop executing code by return
                    return;
                } else {
                    layout_noConnection.setVisibility(View.GONE);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View inflate = getActivity().getLayoutInflater().inflate(R.layout.forgotpwd_layout, null);
                    builder.setView(inflate);

                    final TextInputLayout emailLayout = (TextInputLayout) inflate.findViewById(R.id.input_layout_forgotpassword_email);
                    final EditText txtEmail = (EditText) inflate.findViewById(R.id.forgotpassword_email);

                    builder.setMessage("Enter Your Registered Email-Id");
                    final TextView btnCancel = (TextView) inflate.findViewById(R.id.forgotdialog_btnCancel);
                    final TextView btnRequest = (TextView) inflate.findViewById(R.id.forgotdialog_btnRequest);


                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });

                    btnRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String emailId = txtEmail.getText().toString();
                            Boolean boolEmail = Validations.emailValidate(emailId);

                            if(boolEmail) {
                                emailLayout.setErrorEnabled(false);
                                dialog.cancel();

                                String apiKeyHeader = getSharedPreference.getApiKey(getResources().getString(R.string.apikey));

                                new ForgotPwdAsynTask(getActivity(), apiKeyHeader, emailId).sendForgotPwdRequest();
                            }
                            else {
                                emailLayout.setErrorEnabled(true);
                                emailLayout.setError("Email field is wrong.");
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Boolean boolEmail = emailValidate(txtLogin.getText().toString());
                Boolean boolPwd = isEmptyString(txtPassword.getText().toString());

                if (boolEmail) {
                    loginEmail_InputLayout.setErrorEnabled(false);
                } else {
                    loginEmail_InputLayout.setErrorEnabled(true);
                    loginEmail_InputLayout.setError("Email field is wrong.");
                }

                if (boolPwd) {
                    loginPwd_InputLayout.setErrorEnabled(false);
                } else {
                    loginPwd_InputLayout.setErrorEnabled(true);
                    loginPwd_InputLayout.setError("Password field is empty.");
                }

                if ( boolEmail && boolPwd ) {
                    // Check if Internet present
                    if (!isConnectingToInternet(getActivity())) {
                        layout_noConnection.setVisibility(View.VISIBLE);
                        errorMessage.setText(getResources().getString(R.string.strNoConnection));
                        // stop executing code by return
                        return;
                    } else {
                        layout_noConnection.setVisibility(View.GONE);
                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        LoginCompanyForBuxa();
                    }
                }
                break;

            case R.id.btn_Register:
                startActivity(new Intent(getActivity(), RegisterActivity.class));
                getActivity().finish();
                break;
        }
    }

    private void LoginCompanyForBuxa() {
        String fcmId = getSharedPreference.getFCMRegId(getResources().getString(R.string.FCM_RegId));
//        System.out.println(fcmId);
        RequestParams params;
        params = new RequestParams();

        params.put("email", txtLogin.getText().toString());
        params.put("password", txtPassword.getText().toString());
        params.put("fcmId",fcmId);

        invokeWS(params);
    }

    private void invokeWS(RequestParams params) {
        // Show Progress Dialog
        prgDialog.show();

        // Make RESTful webservice call using AsyncHttpClient object
        RestClient.post(CommonVariables.COMPANY_LOGIN_SERVER_URL, params, new JsonHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                prgDialog.cancel();
                try {
//                    JSONObject json = new JSONObject(response);

                    Boolean error = response.getBoolean(CommonVariables.TAG_ERROR);
                    if (error) {
                        layout_noConnection.setVisibility(View.VISIBLE);
                        errorMessage.setText(response.getString(CommonVariables.TAG_MESSAGE));
                    } else {
                        layout_noConnection.setVisibility(View.GONE);
                        SetSharedPreference setSharedPreference = new SetSharedPreference(getActivity());

                        setSharedPreference.setLoginId(getResources().getString(R.string.loginId), response.getInt("id"));
                        setSharedPreference.setApiKey(getResources().getString(R.string.apikey), response.getString("api_key"));
                        setSharedPreference.setLoginEmail(getResources().getString(R.string.loginEmail), response.getString("email"));
                        setSharedPreference.setLoginName(getResources().getString(R.string.loginName), response.getString("loginName"));
                        setSharedPreference.setCompanyName(getResources().getString(R.string.companyName), response.getString("companyName"));

                        DatabaseClass dbHelper = new DatabaseClass(getActivity());
                        if (dbHelper.numberOfComodityRows() <= 0 ) {
                            getActivity().setContentView(R.layout.activity_splash_screen);

                            new InsertInternationalDestinationPort(getActivity()).execute();

                        }
                        else {

                            // sign in from app.
                            setSharedPreference.setBooleanLogin(getString(R.string.boolean_login_sharedPref), "true");

                            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getActivity().finish();
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                prgDialog.cancel();
                System.out.println("status code: "+statusCode);
                System.out.println("responseString: "+responseString);
                Toast.makeText(getActivity(), "Error "+statusCode+" : "+responseString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.cancel();
                // When Http response code is '404'
                if (statusCode == 404) {
                    System.out.println("Requested resource not found");
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    System.out.println("Something went wrong at server end");
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    try {
                        if( errorResponse.getBoolean("error") ) {
                            System.out.println(errorResponse.getString("message"));
                            Toast.makeText(getActivity(), errorResponse.getString("message"),Toast.LENGTH_LONG).show();
                        }
                        else {
                            System.out.println("Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]");
                            Toast.makeText(getActivity(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(CommonVariables.EXTRA_MESSAGE);

            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getActivity());

            if(newMessage.equals("success")) {

            }

            // Releasing wake lock
            WakeLocker.release();
        }
    };
}
