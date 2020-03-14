package com.areebahackathon.challenge2.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.areebahackathon.challenge2.Transactions;
import com.areebahackathon.challenge2.Login;
import com.areebahackathon.challenge2.R;
import com.areebahackathon.challenge2.RequestCard;
import com.areebahackathon.challenge2.TopUp;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.areebahackathon.challenge2.R.drawable.card_bg;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    TextView txt_card_number;
    TextView txt_card_date;
    TextView txt_card_name;
    TextView txt_card_title;
    LinearLayout home_card_layout;
    Button btn_request_card;
    Button btn_top_up;
    ImageView img_logout;
    LinearLayout btn_menu_login_logout;
    SharedPreferences sharedPreferences;
    TextView txt_menu_login_logout;
    LinearLayout btn_transaction;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        txt_card_number = view.findViewById(R.id.text_card_number);
        txt_card_date = view.findViewById(R.id.text_card_date);
        txt_card_name = view.findViewById(R.id.text_card_name);
        txt_card_title = view.findViewById(R.id.text_card_title);
        home_card_layout = view.findViewById(R.id.home_card_layout);
        btn_request_card = view.findViewById(R.id.btn_request_card);
        btn_top_up = view.findViewById(R.id.btn_top_up);
        img_logout = Objects.requireNonNull(getActivity()).findViewById(R.id.img_logout);
        btn_menu_login_logout = getActivity().findViewById(R.id.btn_menu_login_logout);
        txt_menu_login_logout = getActivity().findViewById(R.id.txt_menu_login_logout);
        btn_transaction = getActivity().findViewById(R.id.btn_transaction);

        btn_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLoginStatus()){
                    startActivity(new Intent(getContext(), Transactions.class));
                }else {
                    startActivity(new Intent(getContext(), Login.class));
                }
            }
        });

        // logout
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("app_data", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("login_status",false);
                editor.apply();
                disabledCardStyle();
                img_logout.setVisibility(View.GONE);
            }
        });


        // go send request page
        btn_request_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request card if user logged in
               if (checkLoginStatus()){
                   startActivity(new Intent(getContext(), RequestCard.class));
               }else {
                   startActivity(new Intent(getContext(), Login.class));
               }
            }
        });

        btn_top_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TopUp.class));
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();

        if (!checkFirstLaunchingStatus() && checkUserCard() && checkLoginStatus()){
            enableCardStyle();
        }else {
            disabledCardStyle();
        }

        if (checkLoginStatus()){
            img_logout.setVisibility(View.VISIBLE);
            txt_menu_login_logout.setText("Logout");

            btn_menu_login_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("app_data", MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean("login_status",false);
                    editor.apply();
                    disabledCardStyle();
                    img_logout.setVisibility(View.GONE);
                    txt_menu_login_logout.setText("Login");
                }
            });
        }else {
            img_logout.setVisibility(View.GONE);
            txt_menu_login_logout.setText("Login");
            btn_menu_login_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(),Login.class));
                }
            });
            disabledCardStyle();
        }
    }

    // check if it's the first launching
    public boolean checkFirstLaunchingStatus(){
        sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("app_data", MODE_PRIVATE);
        return sharedPreferences.getBoolean("first_launch", true);
    }

    // check if user has card
    public boolean checkUserCard(){
        sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("app_data", MODE_PRIVATE);
        return sharedPreferences.getBoolean("has_card", false);
    }

    // check if user logged in
    public boolean checkLoginStatus(){
        sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("app_data", MODE_PRIVATE);
        return sharedPreferences.getBoolean("login_status", false);
    }

    @SuppressLint("ResourceAsColor")
    private void disabledCardStyle(){
        home_card_layout.setBackgroundColor(Color.parseColor("#D3D3D3"));
        txt_card_name.setTextColor(Color.GRAY);
        txt_card_number.setTextColor(Color.GRAY);
        txt_card_date.setTextColor(Color.GRAY);
        txt_card_title.setTextColor(Color.GRAY);

        txt_card_number.setText(formatCard("xxxxxxxxxxxxxxxx"));
        txt_card_date.setText("00/00");
        txt_card_name.setText("Your Name");

        btn_request_card.setVisibility(View.VISIBLE);
        btn_top_up.setVisibility(View.GONE);


    }
    private void enableCardStyle(){
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            home_card_layout.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), card_bg) );
        } else {
            home_card_layout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.card_bg));
        }
        txt_card_name.setTextColor(Color.WHITE);
        txt_card_number.setTextColor(Color.WHITE);
        txt_card_date.setTextColor(Color.WHITE);
        txt_card_title.setTextColor(Color.WHITE);

        // expire date format
        String expire_date = sharedPreferences.getString("expiryDate","");
        String last_two_digits = expire_date.substring(expire_date.length() - 2);
        String first_two_digits = expire_date.substring(0,2);
        // credit card format
        String card_number = sharedPreferences.getString("cardNumber","");
        String formatted_card_number = formatCard(card_number);

        String expire_date_formatted = last_two_digits + "/" + first_two_digits;
        txt_card_number.setText(formatted_card_number);
        txt_card_date.setText(expire_date_formatted);
        txt_card_name.setText(sharedPreferences.getString("cardholderName",""));

        btn_request_card.setVisibility(View.GONE);
        btn_top_up.setVisibility(View.VISIBLE);

    }
    // credit card format
    private static String formatCard(String cardNumber) {
        if (cardNumber == null) return null;
        char delimiter = ' ';
        String number = cardNumber.replaceAll(".{4}(?!$)", "$0" + delimiter);

        return number.substring(0,4) + " xxxx xxxx xxxx";
    }


}