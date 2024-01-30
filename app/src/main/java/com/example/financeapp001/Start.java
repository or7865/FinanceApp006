package com.example.financeapp001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Start extends AppCompatActivity implements View.OnClickListener  {
    Button loginBT; //כפתור של כניסה למערכת
    Button registerBT; //כפתור של הרשמה למערכת
    FrameLayout flFragment; //רכיב שעוזר לנו להציג את הפרגמנט על גבי המסך

    RegisterFragment registerFragment; //הפרגמנט של ההרשמה

    LogInFragment logInFragment; //הפרגמנט של הכניסה
    LinearLayout layout;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        layout=findViewById(R.id.lay);

        flFragment=findViewById(R.id.flFragment);
        loginBT=findViewById(R.id.loginBT);
        registerBT=findViewById(R.id.registerBT);
        registerBT.setOnClickListener(this);
        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flFragment.setVisibility(View.VISIBLE);
                logInFragment=new LogInFragment(layout,flFragment);
                FragmentTransaction ft= getSupportFragmentManager().beginTransaction(); //העברת הנתונים
                ft.replace(R.id.flFragment,logInFragment); //להחליף את מה שיש עכשיו במסך של הפרגמנט באחד הרצוי שעיצבנו
                ft.commit(); //ביצוע כל הפעולות

            }
        });

    }



    @Override
    public void onClick(View view) {
        if(view==registerBT){
            flFragment.setVisibility(View.VISIBLE);
            registerFragment=new RegisterFragment(layout,flFragment);
            FragmentTransaction ft= getSupportFragmentManager().beginTransaction(); //העברת הנתונים
            ft.replace(R.id.flFragment,registerFragment); //להחליף את מה שיש עכשיו במסך של הפרגמנט באחד הרצוי שעיצבנו
            ft.commit(); //ביצוע כל הפעולות
        }

    }

    // Override the keys default action to what we want it to do and then chose if the key will also do what it intended...
   //זו פעולה שאחראית לכל המקשים של המקלדת ומה יקרה אם נלחץ עליהם
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       //אם מה שקיבלנו תואם לכפתור של הבק למטה בטלפון ולחצו עליו- המחלקה של ה KeyEvent יודעת לזהות את זה
        if(keyCode == KeyEvent.KEYCODE_BACK){
            //זה יצא מהאפליקציה כי זה המסך ההתחלתי
            finishAffinity();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}