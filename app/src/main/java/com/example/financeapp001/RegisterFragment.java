package com.example.financeapp001;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import android.widget.RadioButton;
import android.widget.Toast;


public class RegisterFragment extends Fragment {
    EditText name,LastName,user,pass;
    RadioButton male,female;
    Button btnFinishR;
    LinearLayout layout;
    FrameLayout frameLayout;
    DBHelper dbHelper;
    SQLiteDatabase db;
    User u;

    public RegisterFragment( LinearLayout layout,FrameLayout frameLayout){
        this.layout=layout;
        this.frameLayout=frameLayout;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_register, container, false);
        name=v.findViewById(R.id.etName);
        LastName=v.findViewById(R.id.etLastName);
        user=v.findViewById(R.id.etUser);
        pass=v.findViewById(R.id.etPassword);
        male=v.findViewById(R.id.rBmale);
        female=v.findViewById(R.id.rBfemale);
        btnFinishR=v.findViewById(R.id.btnFinishR);
        setHasOptionsMenu(true);
        btnFinishR.setOnClickListener(new View.OnClickListener() {
            String s; //לשמירת המגדר

            @Override
            public void onClick(View view) {
                if (foundUser()) {
                    Toast.makeText(getActivity(), "user already exist", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (female.isChecked())
                        s = "female";
                    else
                        s = "male";

                    u = new User(name.getText().toString(), LastName.getText().toString(), user.getText().toString(), pass.getText().toString(), s);
                    ContentValues cv = new ContentValues(); //עצם לכתיבה בטבלה
                    dbHelper = new DBHelper(getActivity());
                   // Toast.makeText(getActivity(), u + "", Toast.LENGTH_LONG).show();
                    cv.put(DBHelper.STUD_FNAME,u.getName());
                    cv.put(DBHelper.STUD_LNAME,u.getLastName());
                    cv.put(DBHelper.STUD_USERNAME,u.getUser());
                    cv.put(DBHelper.STUD_PASS,u.getPass());
                    cv.put(DBHelper.STUD_GENDER,u.getGender());

                    db = dbHelper.getWritableDatabase(); //גישה לכתיבה בטבלה
                    db.insert(DBHelper.TABLE_NAME, null, cv);
                    db.close(); //סגירת הגישה
                    //לרוקן הכל למשתמש הבא
                    name.setText("");
                    LastName.setText("");
                    user.setText("");
                    pass.setText("");
                    female.setChecked(false);
                    male.setChecked(true);

                    Intent go = new Intent(getActivity(), HomePage.class);
                    startActivity(go);
                }
            }
        });
        return v;
    }


    //שתי הפעולות שעוזרות לנו עם התפריט המבורגר אחת מייצרת את התפריט שיצרנו והשנייה את האופציות שיש בתוך התפריט
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
        Cursor c = db.query(dbHelper.TABLE_NAME, null, null, null, null, null, null, null);
        c.moveToFirst();
        int x = c.getColumnIndex(dbHelper.STUD_USERNAME);
        while (!c.isAfterLast()) { //בודק את כל השורות בטבלה
            if (c.getString(x).equals(user.getText())) {
                return true; //המשתמש נמצא
            }
            c.moveToNext(); //עובר לשורה הבאה
        }
        c.close(); //לסגור גישה
        db.close(); //לסגור גישה
        return false; //המשתמש לא נמצא
    }

}