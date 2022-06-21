package com.example.diamondcare.Model;

import java.io.Serializable;

//Guardar appointments
public class Appointment implements Serializable {

     String date, hour;

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
