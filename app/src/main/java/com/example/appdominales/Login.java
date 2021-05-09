package com.example.appdominales;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.appdominales.Controller.GestorDB;
import com.example.appdominales.Controller.GestorUsuarios;
import com.example.appdominales.Model.DBResultCallBack;
import com.example.appdominales.Model.OdooResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {


    private TextInputLayout emailTextInput;
    private TextInputLayout passTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTextInput = findViewById(R.id.emailTextInput);
        passTextInput = findViewById(R.id.passTextInput);

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = getSharedPreferences("appdominales", Context.MODE_PRIVATE);
        String session_id = preferences.getString("session_id",null);
        long current_id = preferences.getLong("user_id",-1);
        if(session_id != null && !session_id.isEmpty() && current_id != -1){

            Log.i("DB","Coger datos del usuario");
            GestorDB.getGesorDB().getCurrentUserData(this, new DBResultCallBack() {
                @Override
                public void onGetResult(String result) {
                    if(result.equals("OK")){
                        Intent i = new Intent(Login.this, Planificador.class);
                        startActivity(i);
                        finish();
                    }
                }
            });


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                login(account.getDisplayName(), account.getEmail(),account.getIdToken(), true);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void openActivity(View v){
        Toast.makeText(this, "No disponible aún", Toast.LENGTH_SHORT).show();
    }

    public void loginGoogle(View v){
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient cliente = GoogleSignIn.getClient(this,gso);
        cliente.signOut();
        startActivityForResult(cliente.getSignInIntent(), 100);
    }

    public void signUp(View v){
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }


    public void loginButton(View v){
        String email = emailTextInput.getEditText().getText().toString();
        String pass = passTextInput.getEditText().getText().toString();

        login(null, email,pass, false);

    }

    private void login(String name, String email, String pass, boolean provider){
        DBResultCallBack dbResultCallBack = new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                if(result.equals(OdooResult.ACCESS_DENIED.toString())){
                    Toast.makeText(Login.this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(result.equalsIgnoreCase("OK")){
                    Intent i = new Intent(Login.this, Planificador.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        if (provider) {
            odooAuthWithGoogle(name, email, pass, dbResultCallBack);
        } else {
            odooLogin(email, pass, dbResultCallBack);
        }
    }

    private void odooLogin(String email, String pass, DBResultCallBack dbResultCallBack){
        GestorDB.getGesorDB().loginWithEmail(this, email, pass, dbResultCallBack);
    }

    private void odooAuthWithGoogle(String name, String email, String pass, DBResultCallBack dbResultCallBack){
        GestorDB.getGesorDB().loginWithProvider(this, name, email, pass, dbResultCallBack);
    }


}