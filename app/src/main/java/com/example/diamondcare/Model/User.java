package com.example.diamondcare.Model;

public class User {

    public String name, email, phone, password, birth;
    public int points;


    public User(){

    }

    //Metodo construtor do user
    public User (String name, String email, String phone, String password, String birth, int points){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.birth = birth;
        this.points = points;
    }
}
