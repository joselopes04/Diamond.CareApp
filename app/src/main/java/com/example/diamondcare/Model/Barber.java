package com.example.diamondcare.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Barber implements Parcelable {
    private String name, barberId;

    public Barber() {
    }

    protected Barber(Parcel in) {
        name = in.readString();
        barberId = in.readString();
    }

    public static final Creator<Barber> CREATOR = new Creator<Barber>() {
        @Override
        public Barber createFromParcel(Parcel in) {
            return new Barber(in);
        }

        @Override
        public Barber[] newArray(int size) {
            return new Barber[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(barberId);
    }
}
