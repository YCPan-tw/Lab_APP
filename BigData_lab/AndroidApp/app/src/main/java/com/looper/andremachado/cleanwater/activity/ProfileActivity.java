package com.looper.andremachado.cleanwater.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.looper.andremachado.cleanwater.R;
import com.looper.andremachado.cleanwater.SplashActivity;
import com.looper.andremachado.cleanwater.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserUpdateTask mUpdateTask = null;
    private UserLogoutTask mLogoutTask = null;


    private String pk, username, first_name, last_name, email;
    private String token;


    // UI references.
    private TextView mUsernameTextView;
    private TextView mMailTextView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private View mUpdateFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();

        pk = intent.getStringExtra("pk");
        first_name = intent.getStringExtra("first_name");
        last_name = intent.getStringExtra("last_name");
        email = intent.getStringExtra("email");
        username = intent.getStringExtra("username");

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.user_token), Context.MODE_PRIVATE);
        token = sharedPref.getString(getString(R.string.user_token), null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("Title");


        toolbar.setNavigationIcon(R.drawable.common_google_signin_btn_icon_dark);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);

                intent.putExtra("pk",pk);
                intent.putExtra("first_name",first_name);
                intent.putExtra("last_name",last_name);
                intent.putExtra("username",username);
                intent.putExtra("email",email);

                startActivity(intent);
            }
        });


        // Set up the login form.
        mUsernameTextView = (TextView) findViewById(R.id.profile_username_textview);
        mUsernameTextView.setText(username);
        mMailTextView = (TextView) findViewById(R.id.profile_email_textview);
        mMailTextView.setText(email);

        mFirstNameView = (EditText) findViewById(R.id.update_first_name);
        mFirstNameView.setText(first_name);
        mLastNameView = (EditText) findViewById(R.id.update_last_name);
        mLastNameView.setText(last_name);


        Button mUsernameUpdateButton = (Button) findViewById(R.id.update_user_button);
        mUsernameUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptUpdate();
            }
        });

        Button mLogoutButton = (Button) findViewById(R.id.logout_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogout();
            }
        });


        mUpdateFormView = findViewById(R.id.update_form);

    }

    private void attemptUpdate() {
        if (mUpdateTask != null) {
            return;
        }

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);

        // Store values at the time of the login attempt.
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid first name.
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        } else if (!isValid(firstName)) {
            mFirstNameView.setError(getString(R.string.error_invalid_name));
            focusView = mFirstNameView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        } else if (!isValid(lastName)) {
            mLastNameView.setError(getString(R.string.error_invalid_name));
            focusView = mLastNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            mUpdateTask = new UserUpdateTask(firstName, lastName);
            mUpdateTask.execute((Void) null);
        }
    }

    private void attemptLogout() {
        if (mLogoutTask != null) {
            return;
        }
        mLogoutTask = new UserLogoutTask();
        mLogoutTask.execute((Void) null);

    }

    public class UserUpdateTask extends AsyncTask<Void, Void, Boolean> {

        private final String mFirstName;
        private final String mLastName;

        UserUpdateTask(String firstName, String lastName) {
            mFirstName = firstName;
            mLastName = lastName;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = AppUtils.baseUrl + "rest-auth/user/";
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("first_name", mFirstName);
                jsonBody.put("last_name", mLastName);
                jsonBody.put("username", username);
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //TODO Handle Response 200 and etc
                            JSONObject json = new JSONObject(response);
                            first_name = json.getString("first_name");
                            last_name = json.getString("last_name");

                            Intent mainIntent = new Intent(getApplicationContext(), CameraActivity.class);

                            mainIntent.putExtra("pk",pk);
                            mainIntent.putExtra("first_name",first_name);
                            mainIntent.putExtra("last_name",last_name);
                            mainIntent.putExtra("username",username);
                            mainIntent.putExtra("email",email);

                            getApplicationContext().startActivity(mainIntent);
                            ProfileActivity.this.finish();

                        } catch (JSONException e) {
                            //TODO Handle 400 and etc errors
                        }
                        Log.i("VOLLEY2", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        try {
                            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                            return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
                        } catch (Exception e) {
                            return Response.error(new ParseError(e));
                        }
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Token " + token);

                        return params;
                    }
                };

                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();

            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUpdateTask = null;

            if (success) {

            } else {
                mFirstNameView.setError(getString(R.string.error_invalid_name));
                mFirstNameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mUpdateTask = null;
        }
    }

    public class UserLogoutTask extends AsyncTask<Void, Void, Boolean> {


        UserLogoutTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            String url = AppUtils.baseUrl + "rest-auth/logout/";

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject jsonBody = new JSONObject();
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.user_token), MODE_PRIVATE).edit();
                        editor.putString(getString(R.string.user_token), "");
                        editor.apply();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8; ";
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Token " + token);

                        return params;
                    }


                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        try {
                            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

                            return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
                        } catch (Exception e) {
                            return Response.error(new ParseError(e));
                        }
                    }
                };

                requestQueue.add(stringRequest);


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUpdateTask = null;

            if (success) {
                Intent mainIntent = new Intent(getApplicationContext(), SplashActivity.class);
                getApplicationContext().startActivity(mainIntent);
                ProfileActivity.this.finish();

            } else {
            }
        }

        @Override
        protected void onCancelled() {
            mUpdateTask = null;
        }
    }

    private boolean isValid(String str){
        return true;
    }


}
