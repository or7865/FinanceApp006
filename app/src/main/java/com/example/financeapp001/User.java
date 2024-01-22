package com.example.financeapp001;

import androidx.annotation.NonNull;

public class User {
    private String  name,LastName,user,pass,gender;


    //בנאי
    public User(String  name,String lastName,String user,String pass,String gender){
        this.name=name;
        this.LastName=lastName;
        this.user=user;
        this.pass=pass;
        this.gender=gender;
    }

    public String getName(){
        return this.name;
    }

    public String getLastName(){
        return this.LastName;
    }

    public String getUser(){
        return this.user;
    }

    public String getPass(){
        return this.pass;
    }

    public String getGender(){
        return this.gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", LastName='" + LastName + '\'' +
                ", user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
