package com.example.financeapp001;

import android.content.Intent;
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


public class RegisterFragment extends Fragment {
    EditText name,LastName,user,pass;
    RadioButton male,female;
    Button btnFinishR;
    LinearLayout layout;
    FrameLayout frameLayout;

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

}