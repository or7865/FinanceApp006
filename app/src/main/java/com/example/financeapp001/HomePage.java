package com.example.financeapp001;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class HomePage extends AppCompatActivity {
    TextView curSum,moreOp,tip;
    Button input,remove,info;
    String sIncome="0";
    String sOutcome="0";
    SharedPreferences pref;
    DBHelper dbHelper;
    SQLiteDatabase db;
    EditText out,outReason;
    TextView pickedDate;
    ContentValues cv;
    LocalDateTime tmp;
    String targetDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        curSum=findViewById(R.id.tvCurrentSum);
        moreOp=findViewById(R.id.tvMoreOptions);
        tip=findViewById(R.id.tvDailyTip);

        input=findViewById(R.id.btnInput);
        remove=findViewById(R.id.btnRemove);
        info=findViewById(R.id.btnInfo);

        pref = getSharedPreferences(RegisterFragment.USER_PREF, Context.MODE_PRIVATE);

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                income();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                immediateOutcome();
            }
        });

        //מעדכן את הערך בחשבון לערך העדכני של כמות הכסף שיש למשתמש בחשבון מה shared preferences וערך דיפולטי 0
        curSum.setText(pref.getString("sum","0"));
        if(Double.parseDouble(curSum.getText().toString())==0)
            curSum.setTextColor(getColor(R.color.white));
        else{
            if(Double.parseDouble(curSum.getText().toString())<0){
                curSum.setTextColor(getColor(R.color.red));
            }
            if(Double.parseDouble(curSum.getText().toString())>0){
                curSum.setTextColor(getColor(R.color.green));
            }
        }

        moreOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(HomePage.this,ExtraOptionsForUser.class);
                startActivity(i);
            }
        });

    }

    //מכניסה את המשכורת כל חודש באופן אוטומטי
    public void handleSalary(){
        String today = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDateTime.now());

        dbHelper = new DBHelper(HomePage.this); //יצירת עצם חדש
        db = dbHelper.getWritableDatabase(); //לקרוא מהטבלה
        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null, null);
        c.moveToFirst();
        int x1 = c.getColumnIndex(DBHelper.AC_USER);
        int x2= c.getColumnIndex(DBHelper.AC_KIND_OF_ACTION);
        int x3= c.getColumnIndex(DBHelper.AC_SALARY_ADDED_COUNTER);
        int x4= c.getColumnIndex(DBHelper.AC_INSERT_TIME);

        while (!c.isAfterLast()) { //בודק את כל השורות בטבלה
            //שהשם משתמש והסיסמה תואמים למה שהמשתמש הכניס
            if (c.getString(x1).equals(pref.getString("userName",null)) && c.getString(x2).equals("salary")) {
                SharedPreferences pref= getSharedPreferences(RegisterFragment.USER_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= pref.edit();
//                editor.putString();
                editor.apply();

                int should_have_been_added_x_times = 0;
                //split מפצל את המחרוזת למערך לפי תו ולפי הצורך
                String[] inserted_at = c.getString(x4).split("/");
                int day = Integer.parseInt(inserted_at[0]);
                int month = Integer.parseInt(inserted_at[1]);
                int year = Integer.parseInt(inserted_at[2]);
                LocalDateTime temp_date = LocalDateTime.of(year, month, day, 0, 0);
                while(true){
                    // todo להוסיף שני בדיקות, הראשונה אם עברנו את התאריך של היום, והשנייה אם הגענו ליום של המשכורת ולטפל בהתאם
                    if()
                    temp_date.plusDays(1);
                }
            }

            c.moveToNext(); //עובר לשורה הבאה
        }
        c.close(); //לסגור גישה
        db.close(); //לסגור גישה

    }




    //בדיקה שכל התווים בהכנסה אכן מספרים והכנסתם לחשבון
    public void income(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = builder.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        builder.setTitle("Insert your income : ");
        EditText in=new EditText(context);
        in.setHint("insert sum");
        EditText inReason=new EditText(context);
        inReason.setHint("Please insert the reason for the income ");
        //ברגע שהמשתמש יכניס תאריך זה יודיע לו בטקסט וויו
        pickedDate= new TextView(context);
        pickedDate.setVisibility(View.INVISIBLE);
        Button editDate=new Button(context);
        editDate.setText("edit date");
        editDate.setBackgroundColor(getColor(android.R.color.white));
        editDate.setTextColor(getColor(android.R.color.black));
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //עצם\רכיב שיודע לייבא את המידע על השנים ,חודשים,ימים..
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                // Create a DatePickerDialog with the current date as the default
                DatePickerDialog picker = new DatePickerDialog(HomePage.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                                                 //זה כבר שולף מתוך הלוח שנה את השנה שנבחרה החודש והיום
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //מעצב תבנית שלפיה התאריך יעוצב ולפי מה שהמשתמש בחר מעביר אותו לפורמט הזה ושומר בתור מחרוזת
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy");
                                                                          //כי הוא סופר את החודשים מ 0 עד 11
                                tmp = LocalDateTime.of(year, (month + 1), dayOfMonth, 0, 0);
                                targetDate = tmp.format(dtf);
                                pickedDate.setVisibility(View.VISIBLE);
                                pickedDate.setText("*"+"The date you picked:"+" "+ targetDate);

                            }
                        }, year, month, dayOfMonth);

                // Set the maximum date to today
                picker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                // Show the dialog
                picker.show();
            }
        });

        layout.addView(in);
        layout.addView(inReason);
        layout.addView(editDate);
        layout.addView(pickedDate);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!(in.getText().toString().equals(""))||!(in.getText().toString().equals(null))){
                    boolean ans=true;
                    for(int z=0; z<in.getText().toString().length();z++){
                        //אם היוניקוד שלהם זה כן ספרה
                        if(((int)(in.getText().toString().charAt(z)))>=48 && ((int)(in.getText().toString().charAt(z))<=57)){
                            //ימשיך לבדוק
                        }
                        else{
                            ans=false;
                           MessageForWrongValue();
                        }
                    }
                    if(ans){
                        //מעדכן את המשתמש על איזה תאריך הוא בחר ומציג לו אותו ומכניס את כל מה שהוא רשם לתוך הדטא בייס
                        cv = new ContentValues(); //עצם לכתיבה בטבלה
                        dbHelper = new DBHelper(HomePage.this);
                        db = dbHelper.getWritableDatabase(); //גישה לכתיבה בטבלה
                        cv.put(DBHelper.AC_DATE,targetDate);
                        cv.put(DBHelper.AC_SUM,in.getText().toString());
                        cv.put(DBHelper.AC_REASON,inReason.getText().toString());
                        pref= HomePage.this.getSharedPreferences(RegisterFragment.USER_PREF, Context.MODE_PRIVATE);
                        cv.put(DBHelper.AC_USER,pref.getString("userName",null));
                        cv.put(DBHelper.AC_KIND_OF_ACTION,"income");
                        db.insert(DBHelper.TABLE_NAME2, null, cv);
                       // Toast.makeText(context, cv+"", Toast.LENGTH_LONG).show();
                        db.close(); //סגירת הגישה

                        sIncome= in.getText().toString();
                        //המרה לטיפוס דאבל-מספרי כדי לבצע חיבור ולהחזיר לתוך הטקסט באדיטקסט
                        Double d1= Double.parseDouble(curSum.getText().toString());
                        Double d2=Double.parseDouble(sIncome);
                        curSum.setText(d1+d2+"");
                        //מעדכן את הערך העדכני גם בטבלה
                        dbHelper = new DBHelper(HomePage.this);
                        db=dbHelper.getWritableDatabase();
                        cv = new ContentValues();
                        cv.put(DBHelper.STUD_CURRENT_SUM,curSum.getText().toString());
                        db.update(DBHelper.TABLE_NAME,cv,DBHelper.STUD_USERNAME+"=?", new String[]{pref.getString("userName", null)});
                        db.close();

                        if(d1+d2==0)
                            curSum.setTextColor(getColor(R.color.white));
                        else{
                           if(d1+d2<0){
                               curSum.setTextColor(getColor(R.color.red));
                           }
                            if(d1+d2>0){
                                curSum.setTextColor(getColor(R.color.green));
                            }
                        }
                        //מכניס כל פעם את הערך העדכני והסכום שיש למשתמש כדי שכשהוא פותח את האפליקציה זה יראה לו אותו
                        SharedPreferences.Editor editor= pref.edit();
                        //המחסן פתוח לעריכה
                        editor.putString("sum",curSum.getText().toString());
                        editor.apply();
                        sIncome="0";

                    }
                }
            }
        });

        builder.setView(layout);
        builder.setCancelable(true);
        AlertDialog ad=builder.create();
        ad.show();


    }

    //אם המשתמש הזין ערך שגוי בהכנסה/הוצאה המערכת תודיע לו על כך בעזרת אלרט דיאלוג
    public void MessageForWrongValue(){
        pref= this.getSharedPreferences(RegisterFragment.USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= pref.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = builder.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        // \n
        builder.setTitle("Attention!");
        builder.setMessage(pref.getString("name",null)+" "+"the value you entered contains incorrect values, please try again");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setView(layout);
        builder.setCancelable(false);
        AlertDialog ad=builder.create();
        ad.show();
    }

    public void immediateOutcome(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = builder.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        builder.setTitle("Insert your outcome : ");
        out=new EditText(context);
        out.setHint("insert sum");
        outReason=new EditText(context);
        outReason.setHint("Please insert the reason for the outcome ");

        //ברגע שהמשתמש יכניס תאריך זה יודיע לו בטקסט וויו
        pickedDate= new TextView(context);
        pickedDate.setVisibility(View.INVISIBLE);
        Button editDate=new Button(context);
        editDate.setText("edit date");
        editDate.setBackgroundColor(getColor(android.R.color.white));
        editDate.setTextColor(getColor(android.R.color.black));
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //עצם\רכיב שיודע לייבא את המידע על השנים ,חודשים,ימים..
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                // Create a DatePickerDialog with the current date as the default
                DatePickerDialog picker = new DatePickerDialog(HomePage.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            //זה כבר שולף מתוך הלוח שנה את השנה שנבחרה החודש והיום
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                //מעצב תבנית שלפיה התאריך יעוצב ולפי מה שהמשתמש בחר מעביר אותו לפורמט הזה ושומר בתור מחרוזת
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy");
                                //כי הוא סופר את החודשים מ 0 עד 11
                                tmp = LocalDateTime.of(year, (month + 1), dayOfMonth, 0, 0);
                                targetDate = tmp.format(dtf);
                                //מעדכן את המשתמש על איזה תאריך הוא בחר ומציג לו אותו ומכניס את כל מה שהוא רשם לתוך הדטא בייס
                                pickedDate.setVisibility(View.VISIBLE);
                                pickedDate.setText("*"+"The date you picked:"+" "+ targetDate);
                            }
                        }, year, month, dayOfMonth);

                // Set the maximum date to today
                picker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                // Show the dialog
                picker.show();
            }
        });

       //הוספת האדיטקסטים לליאאוט
        layout.addView(out);
        layout.addView(outReason);
        layout.addView(editDate);
        layout.addView(pickedDate);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sOutcome=out.getText().toString();
                //isEmpty
                if(!(sOutcome.equals("null") || sOutcome.equals(""))){
                    boolean ans=true;
                    for(int z=0; z<sOutcome.length();z++){
                        //אם היוניקוד שלהם זה לא ספרה
                        if(sOutcome.charAt(z) < '0' || sOutcome.charAt(z) > '9'){
                            ans=false;
                            MessageForWrongValue();
                            break;
                        }
                    }

                    if (ans && Double.parseDouble(curSum.getText().toString())+(Double.parseDouble(sOutcome)*(-1))<0){
                        ans=false;
                        minus();
                    }

                    if(ans &&Double.parseDouble(curSum.getText().toString())+(Double.parseDouble(sOutcome)*(-1))>=0){
                        //המרה לטיפוס דאבל-מספרי כדי לבצע חיבור ולהחזיר לתוך הטקסט באדיטקסט
                        Double d1= Double.parseDouble(curSum.getText().toString());
                        Double d2=Double.parseDouble(sOutcome)*(-1);
                        //מעדכן את הסכום אחרי ההוספה
                        curSum.setText(d1+d2+"");
                        //מעדכן את הדטא בייס
                        dbHelper = new DBHelper(HomePage.this);
                        db=dbHelper.getWritableDatabase();
                        cv = new ContentValues();
                        cv.put(DBHelper.STUD_CURRENT_SUM,curSum.getText().toString());
                        db.update(DBHelper.TABLE_NAME,cv,DBHelper.STUD_USERNAME+"=?", new String[]{pref.getString("userName", null)});
                        db.close();

                        //מעדכן את הצבע של הסכום לפייתרה או מינוס גם מצב ביניים-0
                        if(d1+d2==0)
                            curSum.setTextColor(getColor(R.color.white));
                        else{
                            if(d1+d2<0){
                                curSum.setTextColor(getColor(R.color.red));
                            }
                            if(d1+d2>0){
                                curSum.setTextColor(getColor(R.color.green));
                            }
                        }

                        //מכניס כל פעם את הערך העדכני והסכום שיש למשתמש כדי שכשהוא פותח את האפליקציה זה יראה לו אותו
                        SharedPreferences.Editor editor= pref.edit();
                        //המחסן פתוח לעריכה
                        editor.putString("sum",curSum.getText().toString());
                        editor.apply();
                        sOutcome="0";

                        cv = new ContentValues(); //עצם לכתיבה בטבלה
                        dbHelper = new DBHelper(HomePage.this);
                        cv.put(DBHelper.AC_DATE,targetDate);
                        cv.put(DBHelper.AC_SUM,out.getText().toString());
                        cv.put(DBHelper.AC_REASON,outReason.getText().toString());
                        pref= HomePage.this.getSharedPreferences(RegisterFragment.USER_PREF, Context.MODE_PRIVATE);
                        cv.put(DBHelper.AC_USER,pref.getString("userName",null));
                        cv.put(DBHelper.AC_KIND_OF_ACTION,"outcome");
                        db = dbHelper.getWritableDatabase(); //גישה לכתיבה בטבלה
                        db.insert(DBHelper.TABLE_NAME2, null, cv);

                    }
                }
            }
        });

        builder.setView(layout);
        builder.setCancelable(true);
        AlertDialog ad=builder.create();
        ad.show();
    }


    // מצב של מינוס בחשבון מתריע שאין אפשרות לבצע הורדה או מאפשר להיכנס למינוס
    public void minus(){
        AlertDialog.Builder build=new AlertDialog.Builder(this);
        //הקשר איפה נמצאים בתוך האפליקציה
        Context context = build.getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        build.setTitle("Attention!");
        build.setMessage("You don't have enough money in your account to perform this action.\n"+ "If you proceed your new balance will be: "+(Double.parseDouble(curSum.getText().toString())+Double.parseDouble(sOutcome)*(-1)));
        build.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        build.setNegativeButton("Do it anyway", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                curSum.setText(Double.parseDouble(curSum.getText().toString())+Double.parseDouble(sOutcome)*(-1)+"");
                //מעדכן את הערך בטבלה
                dbHelper = new DBHelper(HomePage.this);
                db=dbHelper.getWritableDatabase();
                cv = new ContentValues();
                cv.put(DBHelper.STUD_CURRENT_SUM,curSum.getText().toString());
                db.update(DBHelper.TABLE_NAME,cv,DBHelper.STUD_USERNAME+"=?", new String[]{pref.getString("userName", null)});
                db.close();

                curSum.setTextColor(getColor(R.color.red));
                //מכניס כל פעם את הערך העדכני והסכום שיש למשתמש כדי שכשהוא פותח את האפליקציה זה יראה לו אותו
                SharedPreferences.Editor editor= pref.edit();
                //המחסן פתוח לעריכה
                editor.putString("sum",curSum.getText().toString());
                editor.apply();
                sOutcome="0";

                cv = new ContentValues(); //עצם לכתיבה בטבלה
                dbHelper = new DBHelper(HomePage.this);
                cv.put(DBHelper.AC_DATE,targetDate);
                cv.put(DBHelper.AC_SUM,out.getText().toString());
                cv.put(DBHelper.AC_REASON,outReason.getText().toString());
                pref= HomePage.this.getSharedPreferences(RegisterFragment.USER_PREF, Context.MODE_PRIVATE);
                cv.put(DBHelper.AC_USER,pref.getString("userName",null));
                cv.put(DBHelper.AC_KIND_OF_ACTION,"outcome");
                db = dbHelper.getWritableDatabase(); //גישה לכתיבה בטבלה
                db.insert(DBHelper.TABLE_NAME2, null, cv);

            }
        });
        //עיצוב הכפתור
        //except.setBackground(getResources().getDrawable(R.drawable.circle));

        build.setView(layout);
        build.setCancelable(false);
        AlertDialog ad=build.create();
        ad.show();
    }

    //הלחצן של החזור למטה בטלפון -התכנות שלו ושל עוד מקשים אחרים
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
