package com.areebahackathon.challenge2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import com.areebahackathon.challenge2.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    ImageView img_menu;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // icon menu to open Drawer
        img_menu = findViewById(R.id.ic_home_menu);

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(Gravity.LEFT)){
                    drawer.openDrawer(Gravity.LEFT);
                }
            }
        });

        if (checkFirstLaunchingStatus()){
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("first_launch",false);
            editor.putString("client_id","115870");
            editor.putString("bank_id","157856685855");
            editor.putString("contractProductType","MCPRPVIRTUSD");
            editor.putString("cardProductType","MCPRPVIRT");
            editor.apply();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("login_status",false);
        editor.apply();

        // get access token
        String url = "https://api.areeba.com/oauth2/token&grant_type=client_credentials";
        new GetAccessToken().execute(url);
    }


    // check if it's the first launching
    public boolean checkFirstLaunchingStatus(){
        sharedPreferences = getApplicationContext().getSharedPreferences("app_data", MODE_PRIVATE);
        return sharedPreferences.getBoolean("first_launch", true);
    }

    // get access token
    public class GetAccessToken extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
        }

        protected String doInBackground(String... params) {

            try {
                String urlParameters  = "grant_type=client_credentials";

                byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
                int postDataLength = postData.length;

                DataOutputStream printout;
                String data;
                String token_basic = BuildConfig.BASIC_ACCESS_TOKEN; // Protected Authorization Basic Token
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");
                urlConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.addRequestProperty("Authorization", token_basic);

                // Send POST output.
                printout = new DataOutputStream(urlConnection.getOutputStream());
                printout.write(postData);
                printout.flush();
                printout.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                data = Stream2String(in);
                in.close();
                publishProgress(data);
            } catch (Exception ignored) {
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            try {


                SharedPreferences.Editor editor = sharedPreferences.edit();

                JSONObject obj = new JSONObject(progress[0]);
                String token = "Bearer " + obj.getString("access_token");
                editor.putString("token", token);
                editor.apply();

            } catch (JSONException e) {
                //   Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        protected void onPostExecute(String result2) {
        }
    }

    // Convert Stream To String
    public String Stream2String(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder Text = new StringBuilder();
        while (true) {
            try {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                Text.append(line);
            } catch (Exception ignored) {
            }
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Text.toString();
    }
}