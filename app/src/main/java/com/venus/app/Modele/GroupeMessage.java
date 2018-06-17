package com.venus.app.Modele;

import com.orm.SugarRecord;
import org.json.JSONObject;

import java.lang.reflect.Field;

import static com.venus.app.applismsinterfaces.Utils.getMethodName;

public class GroupeMessage extends SugarRecord {
    private Groupe groupe;
    private Message message;

    public GroupeMessage() {}

    public GroupeMessage(Groupe groupe, Message message) {
        this.groupe = groupe;
        this.message = message;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
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
