package com.example.financeapp001;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


public class LogInFragment extends Fragment {
    EditText userName,password;
    Button btnFinishL;

    LinearLayout layout;
    FrameLayout frameLayout;
    DBHelper dbHelper;
    SQLiteDatabase db;

    public LogInFragment( LinearLayout layout,FrameLayout frameLayout){
        this.layout=layout;
        this.frameLayout=frameLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_log_in, container, false);
        userName=v.findViewById(R.id.etLogUser);
        password=v.findViewById(R.id.etLogPass);
        btnFinishL=v.findViewById(R.id.btnFinishL);

        setHasOptionsMenu(true);

        btnFinishL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (foundUser()){
                    userName.setText("");
                    password.setText("");
                    Intent go=new Intent(getActivity(), HomePage.class);
                    startActivity(go);
                }
                else
                    Toast.makeText(getActivity(), "user or password incorrect", Toast.LENGTH_LONG).show();
            }
        });
        // Inflate the layout for this fragment
        return v;

    }

    //שתי הפעולות שעוזרות לנו עם התפריט המבורגר אחת מייצרת את התפריט שיצרנו והשנייה את האופציות שיש בתוך התפריט
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        inflater.inflate(R.menu.menu1,menu);
        super.onCreateOptionsMenu(menu,inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemID=item.getItemId();
        if(itemID==R.id.back){
            layout.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
            getFragmentManager().beginTransaction().remove(this).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean foundUser() {
        dbHelper = new DBHelper(getActivity()); //יצירת עצם חדש
        db = dbHelper.getReadableDatabase(); //לקרוא מהטבלה
        Cursor c = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null, null);
        c.moveToFirst();
        int x1 = c.getColumnIndex(DBHelper.STUD_USERNAME);
        int x2= c.getColumnIndex(DBHelper.STUD_PASS);
        int x3 = c.getColumnIndex(DBHelper.STUD_FNAME);


        while (!c.isAfterLast()) { //בודק את כל השורות בטבלה
               //שהשם משתמש והסיסמה תואמים למה שהמשתמש הכניס
            if (c.getString(x1).equals(userName.getText().toString()) && c.getString(x2).equals(password.getText().toString())) {
                SharedPreferences pref= getActivity().getSharedPreferences(RegisterFragment.USER_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= pref.edit();
                editor.putString("name",c.getString(x3));
                editor.putString("userName",c.getString(x1));
                editor.putString("password",c.getString(x2));
                editor.apply();
                return true; //המשתמש נמצא
            }

            c.moveToNext(); //עובר לשורה הבאה
        }
        c.close(); //לסגור גישה
        db.close(); //לסגור גישה
        return false; //המשתמש לא נמצא
    }

}