package com.venus.app.Modele;

import com.orm.SugarRecord;

public class Sim extends SugarRecord {
    private String operateur;
    private String numero;

    public Sim() {}

    public Sim(String operateur, String numero) {
        this.operateur = operateur;
        this.numero = numero;
    }

    public String getOperateur() {
        return operateur;
    }

    public void setOperateur(String operateur) {
        this.operateur = operateur;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
