package com.example.financeapp001;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    //שם הטבלה
    public static final String TABLE_NAME = "users";

    //התכונות/השדות שיהיו בטבלה
    public static final String STUD_FNAME = "fName";
    public static final String STUD_LNAME = "lName";
    public static final String STUD_USERNAME="username";
    public static final String STUD_PASS = "password";
    public  static final String STUD_GENDER="gender";


    public DBHelper(@Nullable Context context) {
        super(context, "users.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //יוצר טבלה
        String s="CREATE TABLE "+TABLE_NAME+" (";
        s+=STUD_FNAME+" TEXT, ";
        s+=STUD_LNAME+" TEXT, ";
        s+=STUD_USERNAME+" TEXT, ";
        s+=STUD_PASS+" TEXT, ";
        s+= STUD_GENDER+ " TEXT);";
        db.execSQL(s);
    }


    //פעולה לעדכון גירסה
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String st = "DROP TABLE IF EXISTS "+ TABLE_NAME;
        db.execSQL(st);
        onCreate(db);
    }
}
