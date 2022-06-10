package com.example.diamondcare.Model;

import java.io.Serializable;

//Lista Importante não apagar
public class Appointment implements Serializable {

    private String date;
    private String hour;


    public Appointment(String hour, String date) {
        this.date = date;
        this.hour = hour;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String state) {
        this.date = state;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

}
