package com.example.financeapp001;

import android.content.Intent;
import android.os.Bundle;

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


public class LogInFragment extends Fragment {
    EditText userName,password;
    Button btnFinishL;

    LinearLayout layout;
    FrameLayout frameLayout;

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

}