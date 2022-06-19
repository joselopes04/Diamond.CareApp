package com.example.diamondcare.Model;

public class User {

    public String name, email, phone, password, birth;


    public User(){

    }

    public User (String name, String email, String phone, String password, String birth){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.birth = birth;
    }
}
