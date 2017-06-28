package com.looper.andremachado.cleanwater.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.looper.andremachado.cleanwater.R;

public class ConfirmationActivity extends AppCompatActivity {

    private String pk, username, first_name, last_name, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        Intent intent = getIntent();

        pk = intent.getStringExtra("pk");
        first_name = intent.getStringExtra("first_name");
        last_name = intent.getStringExtra("last_name");
        email = intent.getStringExtra("email");
        username = intent.getStringExtra("username");


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

    }
}
