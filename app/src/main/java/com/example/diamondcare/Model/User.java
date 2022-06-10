package com.example.diamondcare.Model;

import java.util.ArrayList;

public class User {

    public String name, email, phone, password, birth;
    public ArrayList<String> appointments;


    public User(){

    }

    public User (String name, String email, String phone, String password, String birth, ArrayList<String> appointments){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.birth = birth;
        this.appointments = appointments;
    }
}
