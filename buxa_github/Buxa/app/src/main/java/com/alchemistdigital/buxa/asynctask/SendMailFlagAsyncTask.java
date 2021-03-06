package com.alchemistdigital.buxa.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.alchemistdigital.buxa.R;
import com.alchemistdigital.buxa.activities.QuotationActivity;
import com.alchemistdigital.buxa.sharedprefrencehelper.GetSharedPreference;
import com.alchemistdigital.buxa.utilities.CommonVariables;
import com.alchemistdigital.buxa.utilities.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.alchemistdigital.buxa.activities.FreightForwardingActivity.isCCService;

/**
 * Created by Pimpu on 11/16/2016.
 */
public class SendMailFlagAsyncTask {
    private static ProgressDialog prgDialog;

    public static void postSendMail(Context context, ArrayList<String> availedService, ArrayList<String> service, String bookId) {

        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(context);
        // Set Progress Dialog Text
        prgDialog.setMessage("wait ...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        RequestParams params;
        params = new RequestParams();
        params.put("availedService", availedService.toString());
        params.put("service", service.toString());
        params.put("bookingId", bookId);

        invokeWS(context, params);
    }

    private static void invokeWS(final Context context, RequestParams params) {
        // Show Progress Dialog
        prgDialog.show();

        GetSharedPreference getSharedPreference = new GetSharedPreference(context);
        String apiKeyHeader = getSharedPreference.getApiKey(context.getResources().getString(R.string.apikey));

        // Make RESTful webservice call using AsyncHttpClient object
        RestClient.postWithHeader(CommonVariables.SEND_EMAIL_FLAG_SERVER_URL, apiKeyHeader, params, new JsonHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                prgDialog.cancel();

                try{
                    System.out.println(json);

                    Boolean error = json.getBoolean(CommonVariables.TAG_ERROR);

                    if (error) {
                        Toast.makeText(context, json.getString(CommonVariables.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        System.out.println(json.getString(CommonVariables.TAG_MESSAGE));
                    } else {
                        System.out.println("Mail sent");
                        ((Activity)context).finish();

                        // below keyword is define static
                        // user select CC and FF service and save, program flow is ok
                        // but after that user select transport and FF service, error come
                        // because isCCService is remains true value which are set in previous enquiry.
                        // so it become false here
                        isCCService = false;
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
                Toast.makeText(context, "Error "+statusCode+" : "+responseString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                prgDialog.cancel();
                // When Http response code is '404'
                if (statusCode == 404) {
                    System.out.println("Requested resource not found");
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    System.out.println("Something went wrong at server end");
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    try {
                        if( errorResponse.getBoolean("error") ) {
                            System.out.println(errorResponse.getString("message"));
                            Toast.makeText(context, errorResponse.getString("message"),Toast.LENGTH_LONG).show();
                        }
                        else {
                            System.out.println("Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]");
                            Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
