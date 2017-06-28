package com.looper.andremachado.cleanwater.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
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
import com.looper.andremachado.cleanwater.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;
import static com.looper.andremachado.cleanwater.utils.AppUtils.baseUrl;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView1;
    private EditText mPasswordView2;
    private View mProgressView;
    private View mRegisterFormView;

    private String pk, username, first_name, last_name, email, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.register_username);

        mPasswordView1 = (EditText) findViewById(R.id.register_password1);
        mPasswordView2 = (EditText) findViewById(R.id.register_password2);
        mPasswordView1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailRegisterButton = (Button) findViewById(R.id.username_email_register_button);
        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mUsernameView.setError(null);
        mPasswordView1.setError(null);
        mPasswordView2.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password1 = mPasswordView1.getText().toString();
        String password2 = mPasswordView2.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2) && !isPasswordValid(password1)) {
            mPasswordView1.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView1;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(email)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }


        if (TextUtils.isEmpty(password1)) {
            mPasswordView1.setError(getString(R.string.error_field_required));
            focusView = mPasswordView1;
            cancel = true;
        } else if (!password1.equals(password2)) {
            mPasswordView2.setError(getString(R.string.error_different_password));
            focusView = mPasswordView2;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(username, email, password1, password2);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 4;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mUsername;
        private final String mPassword1;
        private final String mPassword2;

        UserRegisterTask(String username, String email, String password1, String password2) {
            mUsername = username;
            mEmail = email;
            mPassword1 = password1;
            mPassword2 = password2;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String url = AppUtils.baseUrl + "rest-auth/registration/";
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", mUsername);
                jsonBody.put("email", mEmail);
                jsonBody.put("password1", mPassword1);
                jsonBody.put("password2", mPassword1);
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //TODO Handle Response 200 and etc
                            JSONObject json = new JSONObject(response);

                            String key = json.getString("key");

                            Log.d("TAG2", "Token: " + key);

                            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.user_token), MODE_PRIVATE).edit();

                            editor.putString(getString(R.string.user_token), key);
                            editor.apply();

                            token = key;


                            //========================================
                            // Instantiate the RequestQueue.
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            String url = baseUrl + "rest-auth/user/";

                            // Request a string response from the provided URL.
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject json = new JSONObject(response);
                                                pk = json.getString("pk");
                                                username = json.getString("username");
                                                first_name = json.getString("first_name");
                                                last_name = json.getString("last_name");
                                                email = json.getString("email");

                                                Log.d("TAG", "test: " + pk+ username+ first_name+ last_name+ email);



                                                Intent mainIntent = new Intent(getApplicationContext(), CameraActivity.class);

                                                mainIntent.putExtra("pk",pk);
                                                mainIntent.putExtra("first_name",first_name);
                                                mainIntent.putExtra("last_name",last_name);
                                                mainIntent.putExtra("username",username);
                                                mainIntent.putExtra("email",email);

                                                getApplicationContext().startActivity(mainIntent);
                                                RegisterActivity.this.finish();


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    NetworkResponse networkResponse = error.networkResponse;

                                    if(networkResponse == null){
                                        //networkResponseDialog();
                                    }

                                    if (networkResponse != null && networkResponse.statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
                                        // HTTP Status Code: 401 Unauthorized
                                        Log.e("TAG", "ErrSplash: 403");
                                    }
                                }
                            }
                            ) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String>  params = new HashMap<String, String>();
                                    params.put("Content-Type", "application/json");
                                    params.put("Authorization", "Token " + token);

                                    return params;
                                }
                            };
                            // Add the request to the RequestQueue.
                            Log.d("TAG", "Iniciei o pedido");
                            queue.add(stringRequest);

                            //==========================================================



                        } catch (JSONException e) {
                            //TODO Handle 400 and etc errors
                        }
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
                };

                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent mainIntent = new Intent(getApplicationContext(), CameraActivity.class);

                mainIntent.putExtra("pk",pk);
                mainIntent.putExtra("first_name",first_name);
                mainIntent.putExtra("last_name",last_name);
                mainIntent.putExtra("username",username);
                mainIntent.putExtra("email",email);

                getApplicationContext().startActivity(mainIntent);
                RegisterActivity.this.finish();
            } else {
                mPasswordView1.setError(getString(R.string.error_incorrect_password));
                mPasswordView1.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

