package com.areebahackathon.challenge2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.areebahackathon.challenge2.Model.TransactionsModel;
import com.areebahackathon.challenge2.R;

import java.util.ArrayList;
import java.util.List;

public class TransactionsListAdapter extends ArrayAdapter<TransactionsModel> {
    Context context;
    ArrayList<TransactionsModel> transactions;
    int resource;

    public TransactionsListAdapter(Context context,List<TransactionsModel> objects, int resource ) {
        super(context, R.layout.transaction_list_layout , objects);
        this.context = context;
        this.resource = resource;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.transaction_list_layout, null, true);

            TextView txt_action = convertView.findViewById(R.id.txt_transaction_layout_action);
            TextView txt_date = convertView.findViewById(R.id.txt_transaction_layout_date);
            TextView txt_amount = convertView.findViewById(R.id.txt_transaction_layout_amount);
            TextView txt_authCode = convertView.findViewById(R.id.txt_transaction_layout_authCode);

            final TransactionsModel transactionsModel = getItem(position);

            txt_action.setText(transactionsModel.action);
            txt_date.setText(transactionsModel.date);
            txt_amount.setText(transactionsModel.amount);
            txt_authCode.setText(transactionsModel.authCode);
        }

        return convertView;
    }
}
