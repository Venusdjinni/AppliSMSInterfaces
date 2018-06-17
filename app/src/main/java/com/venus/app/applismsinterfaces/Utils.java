package com.venus.app.applismsinterfaces;

import java.lang.reflect.Field;
import java.util.Calendar;

public abstract class Utils {
    public static enum DateHeureType {
        DATABASE,
        READABLE
    }

    public static boolean isGoodStringValue(String s) {
        if (s == null) return false;
        while (s.startsWith(" ") || s.startsWith("\n"))
            s = s.substring(1);
        return !s.isEmpty();
    }

    public static String toDateHeure(Calendar c, DateHeureType d) {
        switch (d) {
            // todo: ajouter les 0 avant les chiffres
            case DATABASE:
                return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " +
                        c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
            case READABLE: return "";
        }
        return "";
    }

    public static String getMethodName(Field f) {
        String cName = f.getName();
        cName = cName.substring(0, 1).toUpperCase() + cName.substring(1);
        return (Boolean.class.isAssignableFrom(f.getType()) ? "is" : "get") + cName;
    }
}
