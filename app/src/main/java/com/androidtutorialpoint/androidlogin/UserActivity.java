package com.androidtutorialpoint.androidlogin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.androidtutorialpoint.androidlogin.AppSingleton.JSON;


public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";
    private static final String URL_FOR_USERNAME = "http://192.168.1.2:8081/api/username";

    private TextView greetingTextView;
    private Button btnLogOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        greetingTextView = (TextView) findViewById(R.id.greeting_text_view);
        btnLogOut = (Button) findViewById(R.id.logout_button);
        Bundle bundle = getIntent().getExtras();
        String securityToken =  AppSingleton.getInstance(getApplicationContext()).getToken();

        String token = bundle.getString("jsonToken");

        OkHttpClient client = AppSingleton.getInstance(this).getHttpClient();
        Request request = new Request.Builder()
                .url(URL_FOR_USERNAME)
                .addHeader("cookie", token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "User Error: " + ex.getMessage());
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
                        String result = null;
                        try {
                            //JSONObject resultJson = new JSONObject(response.body().string());
                            System.out.println("header: " + response.headers());
                            System.out.println("code: " + response.code());
//                            isOk = resultJson.getBoolean("ok");
//                            result = resultJson.getString("result");
                        } catch (Exception ex) {
                            Log.e(TAG, "User Error: " + ex.getMessage());
                            throw new IllegalStateException();
                        }
                        if (response.isSuccessful() && isOk) {
                            System.out.println("SUCCESS");
                            System.out.println("result : " + result);
                            greetingTextView.setText("Hello " + result) ;

                        } else {
                            System.out.println("NOT SUCCESS");
                            System.out.println("result : " + result);
                            Toast.makeText(getApplicationContext(),
                                    result, Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });

        // Progress dialog
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }
}


