package com.areebahackathon.challenge2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.areebahackathon.challenge2.Adapters.DatabaseHelper;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TopUp extends AppCompatActivity {
    Button btn_save;
    EditText txt_amount;
    ImageView back_image;
    SharedPreferences sharedPreferences;
    KProgressHUD kProgressHUD;

    DatabaseHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        helper = new DatabaseHelper(TopUp.this);

        sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);

        btn_save = findViewById(R.id.btn_save_top_up);
        txt_amount = findViewById(R.id.txt_amount);
        back_image = findViewById(R.id.img_request_card_back);

        // back
        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        prepareLoading(); // prepare loading dialog
        // save amount
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if amount is valid number
                if (txt_amount.getText().toString().matches("\\d+(?:\\.\\d+)?") && txt_amount.getText().toString().trim() != ""){
                    kProgressHUD.show(); // start loading dialog
                    sendPost();
                }else {
                    Toast.makeText(TopUp.this, "Please enter valid number!", Toast.LENGTH_SHORT).show();
                    txt_amount.requestFocus();
                    txt_amount.beginBatchEdit();
                }

            }
        });
    }
    // send request with parameters and get response
    private void sendPost() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.areeba.com/cms1/payments/topup?action=deposit");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/vnd.areeba.cms+json; version=1.0");
                    conn.addRequestProperty("Authorization", sharedPreferences.getString("token",""));

                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("cardId", sharedPreferences.getString("cardId",""));
                    jsonParam.put("amount", txt_amount.getText().toString());
                    jsonParam.put("currency", "USD");

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    if (String.valueOf(conn.getResponseCode()).equals("500")){
                        postToastMessage("Usage Limit DAILY_TOP_UP Number Exceeded");
                        conn.disconnect();
                    }
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject jsonObject = new JSONObject(response.toString());

                    addToHistory(jsonObject.getString("authCode"));

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    // to show error message
    private void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                kProgressHUD.dismiss();
                Toast.makeText(TopUp.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
    // add to transactions history
    private void addToHistory(final String authCode) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                String currentDateandTime = sdf.format(new Date());

                helper.insertIntoTable(currentDateandTime,"Deposit using mobile app",txt_amount.getText().toString(),authCode);
                kProgressHUD.dismiss();
                Toast.makeText(TopUp.this, "Saved!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    // prepare loading dialog while save
    private void prepareLoading(){
        kProgressHUD = KProgressHUD.create(TopUp.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Savinig")
	            .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }
}