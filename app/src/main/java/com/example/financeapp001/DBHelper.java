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
    public static final String STUD_DATE_OF_DOWNLOAD="download";
    public static final String STUD_DATE_OF_SALARY="salary";



    public static final String TABLE_NAME2 = "financial_actions";
    public static final String AC_DATE="date";
    public  static final String AC_SUM="sum";
    public static final String AC_REASON="reason";
    public static final String AC_USER="user";
    public static final String AC_KIND_OF_ACTION="kind";


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
        s+=STUD_GENDER+" TEXT, ";
        s+=STUD_DATE_OF_SALARY+" TEXT);";
        db.execSQL(s);

        //AC->action
       s="CREATE TABLE "+TABLE_NAME2+" (";
        s+=AC_DATE+" TEXT, ";
        s+=AC_SUM+" TEXT, ";
        s+=AC_REASON+" TEXT, ";
        s+=AC_USER+" TEXT, ";
        s+=AC_KIND_OF_ACTION+" TEXT);";
        db.execSQL(s);
    }


    //פעולה לעדכון גירסה
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String st = "DROP TABLE IF EXISTS "+ TABLE_NAME;
        db.execSQL(st);
        onCreate(db);

        String ac= "DROP TABLE IF EXISTS "+ TABLE_NAME2;
        db.execSQL(ac);
        onCreate(db);
    }
}
