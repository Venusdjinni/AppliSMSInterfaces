package com.venus.app.Modele;

import com.orm.SugarRecord;
import org.json.JSONObject;

import java.lang.reflect.Field;

import static com.venus.app.applismsinterfaces.Utils.getMethodName;

public class GroupeContact extends SugarRecord {
    private Groupe groupe;
    private Contact contact;

    public GroupeContact() {}

    public GroupeContact(Groupe groupe, Contact contact) {
        this.groupe = groupe;
        this.contact = contact;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
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
