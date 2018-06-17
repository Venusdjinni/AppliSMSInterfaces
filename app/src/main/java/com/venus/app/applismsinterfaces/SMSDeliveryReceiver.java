package com.venus.app.applismsinterfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.venus.app.Modele.Message;

import static android.app.Activity.RESULT_OK;

public class SMSDeliveryReceiver extends BroadcastReceiver {
    public static final String SMS_SENT_ACTION = "SMSSent";
    public static final String SMS_DELIVERED_ACTION = "SMSDelivered";
    public static final String SMS_ARG_CONTACT_ID = "SMS Contact Id";
    public static final String SMS_ARG_GROUPE_ID = "SMS Groupe Id";
    public static final String SMS_ARG_MSG_ID = "SMS Msg Id";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println("Receiver = " + action);

        if (SMS_SENT_ACTION.equals(action)) {
            // envoi effectué, échoué ou pas. On vérifie mtnt si c'est bon
            Message m = Message.findById(Message.class, (long) intent.getIntExtra(SMS_ARG_MSG_ID, 0));
            if (getResultCode() == RESULT_OK)
                m.setSuccess(m.getSuccess() + 1);
            else m.setFailed(m.getFailed() + 1);
            m.save();
        } else if (SMS_DELIVERED_ACTION.equals(action)) {
            //
        }
    }
}
