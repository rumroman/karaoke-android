package com.androidtutorialpoint.androidlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.androidtutorialpoint.androidlogin.AppSingleton.JSON;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String URL_FOR_LOGIN = "http://192.168.1.2:8081/api/login";
    ProgressDialog progressDialog;
    private EditText loginInputEmail, loginInputPassword;
    private Button btnlogin;
    private Button btnLinkSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginInputEmail = findViewById(R.id.login_input_email);
        loginInputPassword = findViewById(R.id.login_input_password);
        btnlogin = findViewById(R.id.btn_login);
        btnLinkSignup = findViewById(R.id.btn_link_signup);
        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(loginInputEmail.getText().toString(),
                        loginInputPassword.getText().toString());
            }
        });

        btnLinkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginUser( final String email, final String password) {
        // Tag used to cancel the request
        String cancel_req_tag = "login";
        progressDialog.setMessage("Logging you in...");
        showDialog();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", password);
            jsonObject.put("username", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = AppSingleton.getInstance(this).getHttpClient();

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(URL_FOR_LOGIN)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();
                        Log.e(TAG, "Login Error: " + ex.getMessage());
                        Toast.makeText(getApplicationContext(),
                                ex.getMessage(), Toast.LENGTH_LONG).show();
                        call.cancel();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isOk = false;
                        String result;
                        try {
                            JSONObject resultJson = new JSONObject(response.body().string());
                            //String tokenCookie = response.header("Set-Cookie");
                            isOk = resultJson.getBoolean("ok");
                            result = resultJson.getString("result");
                        } catch (IOException | JSONException ex) {
                            Log.e(TAG, "Login Error: " + ex.getMessage());
                            throw new IllegalStateException();
                        }
                        if (response.isSuccessful() && isOk) {
                            System.out.println("SUCCESS");
                            hideDialog();
                            System.out.println("result : " + result);
                            System.out.println("headers : " + response.headers().toString());
                            AppSingleton.getInstance(getApplicationContext()).setToken(result);
//                            Intent intent = new Intent(
//                                    LoginActivity.this,
//                                    UserActivity.class);
//                            intent.putExtra("jsonToken", result);
//                            startActivity(intent);
//                            finish();
                        } else {
                            System.out.println("NOT SUCCESS");
                            System.out.println("result : " + result);
                            hideDialog();
                            Toast.makeText(getApplicationContext(),
                                    result, Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });

    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}


