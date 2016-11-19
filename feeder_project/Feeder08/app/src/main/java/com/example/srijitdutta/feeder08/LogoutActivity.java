package com.example.srijitdutta.feeder08;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogoutActivity extends AppCompatActivity implements OnClickListener{

    Button button_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        if(SaveSharedPreference.getUserName(LogoutActivity.this).length()==0) {
            Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mIntent);
        }

        button_logout = (Button) findViewById(R.id.button_logout);
        button_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if(v==button_logout)
        {
            SaveSharedPreference.clearUserName(LogoutActivity.this);
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(myIntent);
        }
    }
}
