package com.example.appdominales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appdominales.Controller.GestorDB;
import com.example.appdominales.Model.DBResultCallBack;

import java.util.ArrayList;

public class Register extends AppCompatActivity {

    private EditText user_name, user_email, user_date, user_pass, user_pass2;
    private EditText[] editTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);
        user_date = findViewById(R.id.user_date);
        user_pass = findViewById(R.id.user_pass);
        user_pass2 = findViewById(R.id.user_pass2);

        editTexts = new EditText[]{user_name,user_email,user_date,user_pass, user_pass2};

    }

    public void signUp(View v){
        if(!checkFields()){
            Toast.makeText(this, "Valores mal introducidos", Toast.LENGTH_SHORT).show();
            return;
        }

        GestorDB.getGesorDB().registerUser(this, user_name.getText().toString(), user_email.getText().toString(), user_pass.getText().toString(), new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                Log.i("DB", result);
                if(result.equalsIgnoreCase("DUPLICATE")){
                    setErrorEditText(user_name);
                    Toast.makeText(Register.this, "Email ya registrado", Toast.LENGTH_SHORT).show();
                }else if (result.equalsIgnoreCase("OK")){
                    Intent i = new Intent(Register.this, Login.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private boolean checkFields(){
        boolean result = true;

        for(int i=0;i<editTexts.length;i++){
            EditText editText = editTexts[i];
            if(editText.getText().toString().isEmpty()){
                setErrorEditText(editText);
                result = false;
            }
        }

        if(!user_pass.getText().toString().equals(user_pass2.getText().toString())){
            result = false;
            setErrorEditText(user_pass);
            setErrorEditText(user_pass2);
        }

        return result;
    }

    private void setErrorEditText(EditText e){
        e.setText("");
        e.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error, 0);
        e.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText edt = (EditText) v;
                if(hasFocus){
                    edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });
    }


}