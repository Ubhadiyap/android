package com.alchemistdigital.buxa.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.alchemistdigital.buxa.DBHelper.DatabaseClass;
import com.alchemistdigital.buxa.R;
import com.alchemistdigital.buxa.activities.Login;
import com.alchemistdigital.buxa.activities.SelectServiceActivity;
import com.alchemistdigital.buxa.model.TransportServiceModel;
import com.alchemistdigital.buxa.model.TransportTypeModel;
import com.alchemistdigital.buxa.sharedprefrencehelper.SetSharedPreference;
import com.alchemistdigital.buxa.utilities.CommonUtilities;
import com.alchemistdigital.buxa.utilities.CommonVariables;
import com.alchemistdigital.buxa.utilities.RestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Pimpu on 8/30/2016.
 */
public class GetAllTransportService {

    public static void getTransportService(final Context context, String url) {

        RestClient.get(url, null, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'

            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);

                    Boolean error = json.getBoolean(CommonVariables.TAG_ERROR);
                    if (error) {
                        Toast.makeText(context,json.getString(CommonVariables.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    } else {

                        DatabaseClass databaseClass = new DatabaseClass(context);

                        JSONArray arrayTS= json.getJSONArray("trasnportService");

                        for (int i = 0 ; i < arrayTS.length(); i++ ) {
                            int tsServerId = arrayTS.getJSONObject(i).getInt("id");
                            String tsName = arrayTS.getJSONObject(i).getString("name");
                            int tsStatus = arrayTS.getJSONObject(i).getInt("status");

                            long l = databaseClass.insertTransportService(new TransportServiceModel(tsServerId, tsName, tsStatus));
                        }

                        // close database in synchronized condition
                        databaseClass.closeDB();

                        // get all custom clearance from server.
                        GetAllCustomClearanceCategory.getCCC(context, CommonVariables.QUERY_CUSTOM_CLEARANCE_CATEGORY_SERVER_URL);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
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
                    System.out.println("Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]");
                    Toast.makeText(context, "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
