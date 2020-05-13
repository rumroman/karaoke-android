package com.androidtutorialpoint.androidlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterActivity2 extends AppCompatActivity {

    private TextView textViewBrand;
    private TextView textViewConfirmation;
    private Button btnDone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        textViewConfirmation = (TextView) findViewById(R.id.tv_confirmation);
        textViewBrand = (TextView) findViewById(R.id.tv_brand);

        btnDone = (Button) findViewById(R.id.btn_done);

        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();;
            }
        });

    }
}
