package com.areebahackathon.challenge2.Model;

import com.areebahackathon.challenge2.Transactions;

import java.security.PublicKey;

public class TransactionsModel {
    public String date;
    public String action;
    public String amount;
    public String authCode;

    public TransactionsModel(String _date,String _action,String _amount,String _authCode){
        this.date = _date;
        this.action = _action;
        this.amount = _amount;
        this.authCode = _authCode;
    }
}
