package com.example.diamondcare.Model;

import java.io.Serializable;

//Guardar appointments
public class Appointment implements Serializable {

     String date, hour, service;

    public Appointment(String hour, String date, String service) {
        this.date = date;
        this.hour = hour;
        this.service = service;
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

    public String getService() {
        return service;
    }
    public void setService(String service) {
        this.service = service;
    }
}
