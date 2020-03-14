package com.areebahackathon.challenge2;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.areebahackathon.challenge2.Adapters.DatabaseHelper;
import com.areebahackathon.challenge2.Adapters.TransactionsListAdapter;
import com.areebahackathon.challenge2.Model.TransactionsModel;
import com.areebahackathon.challenge2.R;

import java.util.ArrayList;
import java.util.List;

public class Transactions extends AppCompatActivity {
    ImageView img_transaction_back;
    ListView listView;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        databaseHelper = new DatabaseHelper(Transactions.this);

        img_transaction_back = findViewById(R.id.img_transaction_back);
        listView = findViewById(R.id.transactions_list);

        img_transaction_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // make transactions list
        ArrayList<TransactionsModel> arrayList = new ArrayList<>();

        Cursor res = databaseHelper.getResult();

        while (res.moveToNext()) {
            arrayList.add(new TransactionsModel(
                    res.getString(1), // column date
                    res.getString(2), // column action
                    res.getString(3) + "USD", // column amount
                    res.getString(4) // column authCode
            ));
        }

        TransactionsListAdapter adapter = new TransactionsListAdapter(Transactions.this,arrayList,R.layout.transaction_list_layout );

        listView.setAdapter(adapter);
    }
}