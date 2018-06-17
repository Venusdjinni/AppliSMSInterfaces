package com.venus.app.Modele;

import com.orm.SugarRecord;
import org.json.JSONObject;

import java.lang.reflect.Field;

import static com.venus.app.applismsinterfaces.Utils.getMethodName;

public class Contact extends SugarRecord {
    private String nom;
    private String numero;
    private String operateur;

    public Contact() {}

    public Contact(String nom, String numero, String operateur) {
        this.nom = nom;
        this.numero = numero;
        this.operateur = operateur;
    }

    public Contact(String numero) {
        this.numero = numero;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNumero() {
        return numero;
    }

    public String getOperateur() {
        return operateur;
    }

    public void setOperateur(String operateur) {
        this.operateur = operateur;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        for (Field f : this.getClass().getDeclaredFields()) {
            try {
                json.put(f.getName(), this.getClass().getDeclaredMethod(getMethodName(f), (Class<?>) null));
            } catch (Exception ignored) {}
        }

        return json.toString();
    }
}
