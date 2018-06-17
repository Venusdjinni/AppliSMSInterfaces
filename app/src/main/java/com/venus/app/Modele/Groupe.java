package com.venus.app.Modele;

import android.os.Parcel;
import android.os.Parcelable;
import com.orm.SugarRecord;

public class Groupe extends SugarRecord implements Parcelable {
    private String nom;

    public Groupe() {}

    public Groupe(String nom) {
        this.nom = nom;
    }

    protected Groupe(Parcel in) {
        nom = in.readString();
    }

    public static final Creator<Groupe> CREATOR = new Creator<Groupe>() {
        @Override
        public Groupe createFromParcel(Parcel in) {
            return new Groupe(in);
        }

        @Override
        public Groupe[] newArray(int size) {
            return new Groupe[size];
        }
    };

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nom);
    }

    @Override
    public String toString() {
        return nom;
    }
}
