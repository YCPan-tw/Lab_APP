package com.looper.andremachado.cleanwater;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.looper.andremachado.cleanwater.activity.CameraActivity;
import com.looper.andremachado.cleanwater.activity.ConfirmationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.looper.andremachado.cleanwater.utils.AppUtils.baseUrl;
import com.looper.andremachado.cleanwater.R;

/**
 * Created by andremachado on 11/06/2017.
 */

public class QRCodeReader {

    //SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    private Context context;

    private ArrayList<GetResult> list = new ArrayList<GetResult>();


    private SendDataTask mSendDataTask = null;
    private String token;
    private static ListView listview;


    public QRCodeReader(final Context context) {

        this.context = context;

        barcodeDetector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource
                .Builder(context, barcodeDetector)
                .build();
    }

    public void startCamera(SurfaceView cameraPreview){
        try {
            cameraSource.start(cameraPreview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stopCamera(){
        cameraSource.stop();
    }

    public void setProcessor(final TextView txtResult, String token, final ListView listView){

        this.token =token;
        final boolean[] detected = {false};
        this.listview = listView;

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0)
                {
                    Log.d("BREAK","Updating txtResult");
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {
                            txtResult.setText(qrcodes.valueAt(0).displayValue);
                        }
                    });

                    Log.d("BREAK","Attempting");

                    if(!detected[0]){
                        attemptSendData(qrcodes.valueAt(0).displayValue);
                        detected[0] = true;
                    }
                    else Log.d("BREAK","Didnt start");

                }
            }
        });
    }




    //=========================================================


    public class SendDataTask extends AsyncTask<Void, Void, Boolean> {

        private final String mQrCodeId;

        SendDataTask(String qrCodeId) {
            mQrCodeId = qrCodeId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("BREAK","Starting Background");

            String url = baseUrl + "retrieve/" + mQrCodeId;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest jsObjRequest = new StringRequest
                    (Request.Method.GET, url,  new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            JSONObject obj;
                            try {

                                obj = new JSONObject(response);

                                Log.d("BREAK","Response received - " + response);
                                JSONArray jsonArray = null;
                                try {

                                    Log.d("BREAK","Parsing data");
                                    jsonArray = obj.getJSONArray("results");

                                    for (int i = 0; i < jsonArray.length();i++){
                                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                        String username = jsonObject.getString("username");
                                        String first_name = jsonObject.getString("first_name");
                                        String last_name = jsonObject.getString("last_name");
                                        String longitude = jsonObject.getString("longitude");
                                        String latitude = jsonObject.getString("latitude");
                                        String qrCodeId = jsonObject.getString("qrCodeId");
                                        String time  = jsonObject.getString("time");
                                        String email = jsonObject.getString("email");

                                        Log.d("TAG3",jsonObject.getString("email"));
                                        GetResult val = new GetResult(username,first_name,last_name,longitude,latitude,qrCodeId,time,email);

                                        if(val == null) Log.d("TAG5","Null val");
                                        else Log.d("TAG5","Inserting val " + val.getEmail());
                                        list.add(val);

                                        Log.d("JSON",jsonObject.getString("username"));
                                    }


                                    Log.d("TAG5",""+list.size());
                                    String [] data = new String[list.size()];
                                    //Log.d("TAG5",""+list.size());

                                    for (int i = 0; i < list.size();i++){
                                        Log.d("BREAK","BRK3");
                                        if(list.get(i).getFirst_name().equals("") || list.get(i).getLast_name().equals(""))
                                            data[i] = list.get(i).getUsername() + "\nLat: " + list.get(i).getLatitude() + "   Lon: " + list.get(i).getLongitude() + "\n" + list.get(i).getTime();
                                        else
                                            data[i] = list.get(i).getUsername() + "  (" + list.get(i).getFirst_name() + " " + list.get(i).getLast_name() +  ")\nLat: " + list.get(i).getLatitude() + "   Lon: " + list.get(i).getLongitude() + "\n" + list.get(i).getTime();

                                    }
                                    Log.d("BREAK","BRK1");
                                    ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, data);

                                    if(listview == null){
                                        Log.d("BREAK","ListView Null");
                                    }else
                                        listview.setAdapter(adapter);




                                } catch (JSONException e) {

                                    Log.d("BREAK","Error Parsing");
                                    e.printStackTrace();
                                }
                            } catch (Throwable t) {
                                Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                            }


                            Log.d("BREAK","Response Success");
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("BREAK","Response Error  " + error + "  -  " + error.networkResponse.statusCode);
                            // TODO Auto-generated method stub

                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    //params.put("Authorization", "Token " + token);

                    return params;
                }
            };

            requestQueue.add(jsObjRequest);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if(success){


            }

            Log.d("BREAK","OnPostExecute");
            mSendDataTask = null;

        }

        @Override
        protected void onCancelled() {
            mSendDataTask = null;
        }
    }



    private void attemptSendData(String mQrCode) {
        if (mSendDataTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        //String qrCodeId = txtResult.getText().toString();
        //String qrCodeId = "Hardcoded test";

        //qrcode verifications
        if (true) {
            Log.d("TAG2","Error2");
            //txtResult.setError(getString(R.string.error_invalid_password));
            //focusView = txtResult;
            //cancel = true;
        }

        // Check for a valid password, if the user entered one.


        if (cancel) {

        } else {
            Log.d("BREAK","Starting Async Task");
            mSendDataTask = new SendDataTask(mQrCode);
            mSendDataTask.execute((Void) null);
        }
    }

}
