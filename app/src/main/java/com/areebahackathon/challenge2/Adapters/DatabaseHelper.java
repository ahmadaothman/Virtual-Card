package com.areebahackathon.challenge2.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
// user sqlite db to save transaction
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "appdb1.db";
    public static final String TABLE_NAME = "history";

    public static final String COL_1 = "date_added";
    public static final String COL_2 = "action_name";
    public static final String COL_3 = "amoun";
    public static final String COL_4 = "authCode";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        // SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE history(" +
                "ID INTEGER  PRIMARY KEY AUTOINCREMENT," +
                "date_added," +
                "action_name," +
                "amount);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public boolean insertIntoTable(String date,String action,String amount,String authCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,date);
        contentValues.put(COL_2,action);
        contentValues.put(COL_3,amount);
        contentValues.put(COL_4,authCode);

        long result = db.insert(TABLE_NAME,null,contentValues);
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }



    public Cursor getResult(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT DISTINCT * FROM " + TABLE_NAME + " ORDER BY ID DESC LIMIT 10" ,null);
        return res;
    }

}