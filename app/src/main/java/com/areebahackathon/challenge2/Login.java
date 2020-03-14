package com.areebahackathon.challenge2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    EditText txt_email;
    EditText txt_password;
    Button btn_login;
    ImageView img_login_back;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("app_data",MODE_PRIVATE);

        txt_email = findViewById(R.id.txt_login_email);
        txt_password = findViewById(R.id.txt_login_password);
        btn_login = findViewById(R.id.btn_login);
        img_login_back = findViewById(R.id.img_login_back);

        img_login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*It is static login method, we can user other login method (local database,api,web services...)*/
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txt_email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")){ // email validation
                    Toast.makeText(Login.this, "Please enter valid email address!", Toast.LENGTH_SHORT).show();
                    txt_email.requestFocus();
                    return;
                }

                if (txt_email.getText().toString().equals("ahmadaothman96@gmail.com") && txt_password.getText().toString().equals("ahmad1234")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // save login status and user information
                    editor.putBoolean("login_status",true);
                    editor.putString("logged_in_user_name","Ahmad Ossman");
                    editor.apply();

                    finish();
                }else {
                    Toast.makeText(Login.this, "Email or Password incorrect!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}