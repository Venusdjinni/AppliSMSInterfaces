package com.venus.app.Utils;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public abstract class Utils {
    public static Calendar parseCalendar(String s) {
        Calendar c = new GregorianCalendar(Calendar.getInstance().getTimeZone());
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(8, 10)));
        c.set(Calendar.MONTH, Integer.parseInt(s.substring(5, 7)) - 1);
        c.set(Calendar.YEAR, Integer.parseInt(s.substring(0, 4)));

        return c;
    }

    public static String invertDate(String s) {
        if (s.charAt(2) == '-') // format DD-MM-YYYY
            return s.substring(6, 10) + "-" + s.substring(3, 5) + "-" + s.substring(0, 2);
        else // format YYYY-MM-DD
            return s.substring(8, 10) + "-" + s.substring(5, 7) + "-" + s.substring(0, 4);
    }

    public static int daysBetween(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return (int) TimeUnit.MILLISECONDS.toDays(start - end);
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isGoodStringValue(String s) {
        if (s == null) return false;
        while (s.startsWith(" ") || s.startsWith("\n"))
            s = s.substring(1);
        return !s.isEmpty();
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    public static ProgressDialog newLoadingDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminate(true);
        dialog.setMessage("Chargement...");
        return dialog;
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    public static boolean hasConnection(Context context) {
        System.out.println("connect action:" + "CONNECTIVITY_ACTION");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}
