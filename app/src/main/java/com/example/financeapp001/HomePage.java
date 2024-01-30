package com.example.financeapp001;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
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
