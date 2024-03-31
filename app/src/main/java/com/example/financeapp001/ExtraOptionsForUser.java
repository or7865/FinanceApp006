package com.example.financeapp001;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtraOptionsForUser extends AppCompatActivity {
    Button salary, fSaving,conAndSave;
    SharedPreferences pref;
    DBHelper dbHelper;
    SQLiteDatabase db;
    public  String saveDateOfSalary,salaryValue;


    //add to database
    ContentValues cv;

    //check in database
    Cursor c;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_options_for_user);
        salary=findViewById(R.id.btnSalary);
        fSaving=findViewById(R.id.btnFutureSaving);
        conAndSave=findViewById(R.id.btnConfirmAndSave);

        salary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDateForSalary();
            }
        });

    }

    //פעולה שפותחת אלרט דיאלוג למשתמש ושם הוא יכול להוסיף את המשכורת היום בחודש שבו היא נכנסת
    public void pickDateForSalary(){
        pref= this.getSharedPreferences(RegisterFragment.USER_PREF, Context.MODE_PRIVATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = builder.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        builder.setTitle("Attention!");
        builder.setMessage(pref.getString("name",null)+" "+"if you receive a monthly salary,you can pick here the day of your entry :");
        Spinner op=new Spinner(context);
        String [] dates={"1","10","15","25"};
        //אדפטר ששומר בתוכו את התצוגה של הספינר הדיפולטית ואת המערך שצריך להציג על פניה שזה התאריכים שבהן יכולה המשכורת להיכנס
        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EditText salaryVal=new EditText(context);
        salaryVal.setHint("enter the amount of your salary");
        //נציג על הספינר את המערך באמצעות האדפטר
        op.setAdapter(ad);
        layout.addView(op);
        layout.addView(salaryVal);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveDateOfSalary=op.getSelectedItem().toString();
                dbHelper = new DBHelper(ExtraOptionsForUser.this); //יצירת עצם חדש
                db = dbHelper.getReadableDatabase();
                db=dbHelper.getWritableDatabase();
                cv = new ContentValues(); //עצם לכתיבה בטבלה
                //עצם לחיפוש בטבלה
                c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null, null);
                c.moveToFirst();
                int x1 = c.getColumnIndex(DBHelper.STUD_USERNAME);
                //לוקח את התאריך של היום הנוכחי ומשאיר את היום בתוך משתנה מחרוזתי
                //שם בשדה המסוים את הערך של המשכורת של הבנאדם
                salaryValue=salaryVal.getText().toString();
                // todo להוסיף בדיקות תקינות אם יש ערך חיובי במשכורת
                cv.put(DBHelper.AC_SALARY_ACCOUNT_DATE,saveDateOfSalary);
                cv.put(DBHelper.AC_SUM, salaryVal.getText().toString());
                cv.put(DBHelper.AC_INSERT_TIME, DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDateTime.now()));
                cv.put(DBHelper.AC_SALARY_ADDED_COUNTER, 0);
                cv.put(DBHelper.AC_KIND_OF_ACTION, "salary");
                cv.put(DBHelper.AC_USER,pref.getString("userName",null));
                db.insert(DBHelper.TABLE_NAME2,null,cv);
                db.close(); //סגירת הגישה
                c.close();//סגירת גישה גם לסמן
               // Toast.makeText(context, salaryValue+"", Toast.LENGTH_LONG).show();

            }
        });
        builder.setView(layout);
        builder.setCancelable(true);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //אם מה שקיבלנו תואם לכפתור של הבק למטה בטלפון ולחצו עליו- המחלקה של ה KeyEvent יודעת לזהות את זה
        if(keyCode == KeyEvent.KEYCODE_BACK){
            //מחזיר אותו למסך הראשי
            startActivity(new Intent(this,Start.class));
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
