package com.example.appdominales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.appdominales.Controller.GestorDB;
import com.example.appdominales.Model.DBResultCallBack;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GestorDB.getGesorDB().login(this, "admin", "admin", new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                Log.i("TOKEM", result);
            }
        });
    }

    public void openActivity(View v){
        Intent i = new Intent(this, Planificador.class);
        startActivity(i);
    }
}