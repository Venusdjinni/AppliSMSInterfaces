package com.venus.app.Modele;

import android.os.Parcel;
import android.os.Parcelable;
import com.orm.SugarRecord;
import org.json.JSONObject;

import java.lang.reflect.Field;

import static com.venus.app.applismsinterfaces.Utils.getMethodName;

public class Message extends SugarRecord implements Parcelable {
    private String texte;
    private String dateHeure;
    private boolean sent = false;
    private int total;
    private int success;
    private int failed;

    public Message() {}

    public Message(String texte, String dateHeure, boolean sent) {
        this.texte = texte;
        this.dateHeure = dateHeure;
        this.sent = sent;
    }

    protected Message(Parcel in) {
        texte = in.readString();
        dateHeure = in.readString();
        sent = in.readByte() != 0;
        total = in.readInt();
        success = in.readInt();
        failed = in.readInt();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public String getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(String dateHeure) {
        this.dateHeure = dateHeure;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(texte);
        dest.writeString(dateHeure);
        dest.writeByte((byte) (sent ? 1 : 0));
        dest.writeInt(total);
        dest.writeInt(success);
        dest.writeInt(failed);
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
