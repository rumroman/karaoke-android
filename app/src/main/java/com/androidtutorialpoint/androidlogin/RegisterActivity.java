package com.androidtutorialpoint.androidlogin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.androidtutorialpoint.androidlogin.AppSingleton.JSON;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final String URL_FOR_REGISTRATION = "http://192.168.1.2:8081/api/register";
    ProgressDialog progressDialog;

    private EditText signupInputName, signupInputEmail, signupInputPassword;
    private Button btnSignUp;
    private Button btnLinkLogin;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        signupInputName = (EditText) findViewById(R.id.signup_input_name);
        signupInputEmail = (EditText) findViewById(R.id.signup_input_email);
        signupInputPassword = (EditText) findViewById(R.id.signup_input_password);

        btnSignUp = (Button) findViewById(R.id.btn_signup);
        btnLinkLogin = (Button) findViewById(R.id.btn_link_login);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        btnLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void submitForm() {

        registerUser(signupInputName.getText().toString(),
                     signupInputEmail.getText().toString(),
                     signupInputPassword.getText().toString());
    }

    private void registerUser(final String name,  final String email, final String password)  {

        if (name.trim().isEmpty() || name.trim().length() <= 2) {
            this.showAlertDialog("Имя должно состоять минимум из 2-х символов");
        } else if (!this.isValidEmail(email)) {
            this.showAlertDialog("Неверный формат электронной почты");
        } else if (password.length() <= 3) {
            this.showAlertDialog("Пароль должен состоять минимум из 4-х символов");
        } else {
            // Tag used to cancel the request
            String cancel_req_tag = "register";

            progressDialog.setMessage("Обработка данных ...");
            showDialog();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email);
                jsonObject.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = AppSingleton.getInstance(this).getHttpClient();

            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
            Request request = new Request.Builder()
                    .url(URL_FOR_REGISTRATION)
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                System.out.println("ResponseResult: " + response.body().toString());
                hideDialog();
                if (!response.header("error_code").isEmpty() && response.code() == 409) {
                    showAlertDialog(response.header("description"));
                } else {
                    Intent intent = new Intent(
                            RegisterActivity.this,
                            RegisterActivity2.class);
                    startActivity(intent);
                    finish();
                }
            } catch(IOException ex) {
                Log.e(TAG, ex.toString());
                Log.e(TAG, "Registration Error: " + ex.getMessage());
                String errorMessage = ex.getMessage();
                if (errorMessage == null || errorMessage.trim().length() <= 0) {
                    errorMessage = "Неизвестная ошибка передачи данных на сервер";
                }
                Toast.makeText(getApplicationContext(),
                        errorMessage, Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void showAlertDialog(String message) {
        if (alertDialog != null) {
            alertDialog.setMessage(message);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage(message)
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog = builder.create();
        }

        alertDialog.show();
    }

    private boolean isValidEmail(String email) {
        email = email.trim();
        if (email.isEmpty()) {
            return false;
        }
        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            return false;
        }

        int i = email.indexOf("@");
        email = email.substring(i+1);

        if (email.contains("@")){
            return false;
        }

        return true;
    }

}