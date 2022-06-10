package com.example.diamondcare.Model;

import java.io.Serializable;


/*Mostrar a hora nas marcações e o estado: se está ocupado ou não de cada intervalo de tempo
* Usado no ecrã sessions Date*/
public class TimeSlotData implements Serializable {
    private String hour;
    private String state;
    private boolean used;

    public TimeSlotData(String hour, String state, boolean used) {
        this.hour = hour;
        this.state = state;
        this.used = used;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getState() {
       return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
