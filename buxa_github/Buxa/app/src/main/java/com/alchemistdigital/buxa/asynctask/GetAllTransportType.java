package com.alchemistdigital.buxa.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.alchemistdigital.buxa.DBHelper.DatabaseClass;
import com.alchemistdigital.buxa.model.ShipmentTermModel;
import com.alchemistdigital.buxa.model.TransportTypeModel;
import com.alchemistdigital.buxa.utilities.CommonVariables;
import com.alchemistdigital.buxa.utilities.RestClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Pimpu on 8/30/2016.
 */
public class GetAllTransportType {
    private static ProgressDialog prgDialog;

    public static void getTransportType(final Context context, String url) {
        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(context);
        // Set Progress Dialog Text
        prgDialog.setMessage("Logging ...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        // Show Progress Dialog
        prgDialog.show();

        RestClient.get(url, null, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'

            @Override
            public void onSuccess(String response) {
                prgDialog.cancel();
                try {
                    JSONObject json = new JSONObject(response);

                    Boolean error = json.getBoolean(CommonVariables.TAG_ERROR);
                    if (error) {
                        Toast.makeText(context,json.getString(CommonVariables.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    } else {

                        DatabaseClass databaseClass = new DatabaseClass(context);

                        JSONArray arrayTT= json.getJSONArray("trasnportType");

                        for (int i = 0 ; i < arrayTT.length(); i++ ) {
                            int ttServerId = arrayTT.getJSONObject(i).getInt("id");
                            String ttName = arrayTT.getJSONObject(i).getString("name");
                            int ttStatus = arrayTT.getJSONObject(i).getInt("status");

                            long l = databaseClass.insertTransportType(new TransportTypeModel(ttServerId, ttName, ttStatus));
                            System.out.println("Transport Type id: "+l);
                        }

                        // close database in synchronized condition
                        databaseClass.closeDB();

                        // get all transport service from server.
                        GetAllTransportService.getTransportService(context, CommonVariables.QUERY_TRANSPORT_SERVICE_SERVER_URL);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // Hide Progress Dialog
                prgDialog.hide();
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