package com.example.financeapp001;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class HomePage extends AppCompatActivity {
    TextView curSum,moreOp,tip,cdtTextView;
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
    CountDownTimer cdt;
    long miliLeft;




    @SuppressLint("MissingInflatedId")
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
        cdtTextView=findViewById(R.id.tvCdt);

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

        countDownTimerSalaryUpdate();

    }



    //מכניסה את המשכורת כל חודש באופן אוטומטי
    public void handleSalary(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String today = dateTimeFormatter.format(LocalDateTime.now());

        dbHelper = new DBHelper(HomePage.this); //יצירת עצם חדש
        db = dbHelper.getWritableDatabase(); //לקרוא מהטבלה
        Cursor c = db.query(DBHelper.TABLE_NAME2, null, null, null, null, null, null, null);
        c.moveToFirst();
        int x1 = c.getColumnIndex(DBHelper.AC_USER);
        int x2= c.getColumnIndex(DBHelper.AC_KIND_OF_ACTION);
        int x3= c.getColumnIndex(DBHelper.AC_SALARY_ADDED_COUNTER);
        int x4= c.getColumnIndex(DBHelper.AC_INSERT_TIME);
        int x5= c.getColumnIndex(DBHelper.AC_SALARY_ACCOUNT_DATE);
        int x6= c.getColumnIndex(DBHelper.AC_SUM);

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

                int accountDay = c.getInt(x5);

                while(!dateTimeFormatter.format(temp_date).equals(today)){
                    // להוסיף שני בדיקות, הראשונה אם עברנו את התאריך של היום, והשנייה אם הגענו ליום של המשכורת ולטפל בהתאם
                    if(temp_date.getDayOfMonth() == accountDay)
                        should_have_been_added_x_times++;

                    temp_date.plusDays(1);
                }

                if(should_have_been_added_x_times != Integer.parseInt(c.getString(x3))){
                    int moneyToAdd = (should_have_been_added_x_times - Integer.parseInt(c.getString(x3)) * Integer.parseInt(c.getString(x6)));
                    DBHelper users = new DBHelper(HomePage.this);
                    SQLiteDatabase userDB = users.getWritableDatabase();

                    //צריך לשנות את הסכום הכולל בעמודה של קארנט סאם אצל היוזר הנוכחי וצריך לשנות את הקאונטר
                    if (c.getString(x1).equals(pref.getString("userName",null))){
                        int upDateCurSum=Integer.parseInt(curSum.getText().toString())+Integer.parseInt(c.getString(x6));
                        curSum.setText(upDateCurSum);
                        db.update(DBHelper.TABLE_NAME,cv,DBHelper.STUD_CURRENT_SUM+"=?",new String[]{String.valueOf(upDateCurSum)});
                    }

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


    //פעולה שכאשר למשתנה יש תאריך של משכורת היא מציגה את הקאונט דאון טיימר שאומר כמה זמן נותר עד שהיא תיכנס לחשבון בימים דקות ושניות
   //הפעולה מתייחסת רק למשכורת האחרונה שהמשתמש הכניס!!
    public void countDownTimerSalaryUpdate(){
        //כי כל פעם שלוחצים על הכפתור של הלהתחיל מההתחלה זה יוצר עצם חדש!! ולכן צריך לגרוס אותו וליצור חדש אחר כךך והוא כבר יתחיל לספור מההתחלה
        if ((cdt != null)) {
            cdt.cancel();
        }

        dbHelper = new DBHelper(HomePage.this);
        db=dbHelper.getReadableDatabase();
        String[] arr={DBHelper.AC_USER,DBHelper.AC_SALARY_ACCOUNT_DATE,DBHelper.AC_KIND_OF_ACTION};
        Cursor c = db.query(dbHelper.TABLE_NAME2, null, null,null,null,null,null );
        c.moveToFirst();
        int u = c.getColumnIndex(dbHelper.AC_USER);
        int d = c.getColumnIndex(dbHelper.AC_SALARY_ACCOUNT_DATE);
        int k = c.getColumnIndex(dbHelper.AC_KIND_OF_ACTION);
        String useDayOfSal="";
        String s=pref.getString("userName",null);

        while (!c.isAfterLast()) { //בודק את כל השורות בטבלה
            if(c.getString(k).equals("salary") && c.getString(u).equals(s)) {
                useDayOfSal=c.getString(d).toString();
                //Toast.makeText(this, "user is:"+u+"\n day is:"+ useDayOfSal, Toast.LENGTH_LONG).show();

            }
            c.moveToNext(); //עובר לשורה הבאה
        }


        //הדיי של היום והדיי של המשכורת ואם היום של המשכורת לא עבר נראה כמה ימים נותרו עד למשכורת ונמיר את זה לשניות
        String dateToday = DateTimeFormatter.ofPattern("dd").format(LocalDateTime.now()).toString();
        int whatIsTheDayToday=Integer.parseInt(dateToday);
        int udos=Integer.parseInt(useDayOfSal);

        //כדי להפוך למילישניות
        int iii=(udos-whatIsTheDayToday)*24*60*60*1000;
        cdt = new CountDownTimer(iii, 60000){

            @Override
            public void onTick(long millisUntilFinished) {
                if(udos > whatIsTheDayToday){
                    //מחלקה שנותנת אפשרות ליצור של תבנית לפי סוג ומספר המספרים שאנו רוצים
                    NumberFormat f = new DecimalFormat("00");
                    miliLeft = millisUntilFinished;
                    //ה long  של הפעולה ממנו נחשב שניות דקות ושעות שיופיעו על גבי הטקסט וויו
                    long day = (millisUntilFinished /(24*60*60*1000));
                    long hour = (millisUntilFinished / 3600000)%24;
                    long min = (millisUntilFinished / 60000) % 60;
                    //מפעילים את הפורמט על כל השניות דקות ושניות
                    cdtTextView.setText(f.format(day) + ":" + f.format(hour) + ":" + f.format(min));
                }
            }
            @Override
            public void onFinish() {
                cdtTextView.setText("");
            }
        }.start();


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
