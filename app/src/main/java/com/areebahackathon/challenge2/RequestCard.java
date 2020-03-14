package com.areebahackathon.challenge2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestCard extends AppCompatActivity {
    EditText txt_first_name;
    EditText txt_last_name;
    Button btn_complete;
    ImageView img_back;
    SharedPreferences sharedPreferences;
    KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_card);
        sharedPreferences = getSharedPreferences("app_data",MODE_PRIVATE);

        txt_first_name = findViewById(R.id.txt_request_card_first_name);
        txt_last_name = findViewById(R.id.txt_request_card_last_name);
        btn_complete =  findViewById(R.id.btn_complete_request_card);
        img_back = findViewById(R.id.img_request_card_back);

        // close activity
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        prepareLoading(); // prepare loading dialog
        // complete request
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_first_name.getText().toString().length() < 3 || txt_last_name.getText().toString().length() < 3){
                    Toast.makeText(RequestCard.this, "First name and Last name must bet more than 3 characters!", Toast.LENGTH_SHORT).show();
                }else {
                    kProgressHUD.show();
                    sendPost();
                }
            }
        });
    }

    // send request with parameters and get response

    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.areeba.com/cms1/cards/virtual");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/vnd.areeba.cms+json; version=1.0");
                    conn.addRequestProperty("Authorization", sharedPreferences.getString("token",""));

                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("firstName", txt_first_name.getText().toString());
                    jsonParam.put("lastName", txt_last_name.getText().toString());
                    jsonParam.put("clientId", "115870");
                    jsonParam.put("bankId", "157856685855");
                    jsonParam.put("contractProductType", "MCPRPVIRTUSD");
                    jsonParam.put("cardProductType", "MCPRPVIRT");

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());
                    os.flush();
                    os.close();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject object = new JSONObject(response.toString());
                    // save card information in app data
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("cardNumber",object.getString("cardNumber"));
                    editor.putString("expiryDate",object.getString("expiryDate"));
                    editor.putString("securityCode",object.getString("securityCode"));
                    editor.putString("cardholderName",object.getString("cardholderName"));
                    editor.putString("cardId",object.getString("cardId"));
                    editor.putString("contractId",object.getString("contractId"));
                    editor.putString("contractNumber",object.getString("contractNumber"));
                    editor.putBoolean("has_card", true);
                    editor.apply();

                    kProgressHUD.dismiss();
                    finish();

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    // prepare loading dialog while save
    private void prepareLoading(){
        kProgressHUD = KProgressHUD.create(RequestCard.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Savinig")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }
}