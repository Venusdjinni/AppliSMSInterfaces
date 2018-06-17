package com.venus.app.applismsinterfaces;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.venus.app.Adapters.GroupesAdapter;
import com.venus.app.Modele.*;
import com.venus.app.Utils.SimpleMessageDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class NewMessageActivity extends AppCompatActivity {
    private static final int ARG_SEND_SMS = 2;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 89;
    private AppCompatEditText add;
    private RecyclerView rv;
    private GroupesAdapter gAdapter;
    private AppCompatEditText text;
    private FloatingActionButton send;
    private ListPopupWindow listPopupWindow;
    private String message;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add = findViewById(R.id.newm_add);
        add.addTextChangedListener(addlistener);
        rv = findViewById(R.id.newm_recyclerview);
        rv.setNestedScrollingEnabled(false);
        gAdapter = new GroupesAdapter();
        rv.setAdapter(gAdapter);
        text = findViewById(R.id.newm_text);
        text.addTextChangedListener(textlistener);
        send = findViewById(R.id.newm_send);

        // on charge le popupWindow
        listPopupWindow = new ListPopupWindow(getApplicationContext());
        //listPopupWindow.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, Groupe.listAll(Groupe.class)));
        listPopupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setAnchorView(add);
        listPopupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.spinner_dropdown_background));
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!gAdapter.getGroupes().contains((Groupe) adapterView.getItemAtPosition(i)))
                    gAdapter.addGroupe((Groupe) adapterView.getItemAtPosition(i));
                listPopupWindow.dismiss();
                add.setText("");
            }
        });

        progress = new ProgressDialog(this);
    }

    private TextWatcher addlistener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().isEmpty()) {
                listPopupWindow.setAdapter(new ArrayAdapter<>(NewMessageActivity.this, android.R.layout.simple_list_item_activated_1,
                        Groupe.find(Groupe.class, "NOM like ?", "%" + s + "%")));
                listPopupWindow.show();
            } else listPopupWindow.dismiss();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher textlistener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (Utils.isGoodStringValue(s.toString())) {
                if (gAdapter.getItemCount() > 0)
                    send.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                send.setOnClickListener(sendlistener);
            } else {
                send.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDarkGrey)));
                send.setOnClickListener(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener sendlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (gAdapter.getItemCount() > 0) {
                // envoi du message
                new SendMessageDialog().show(getSupportFragmentManager(), "send");
            } else SimpleMessageDialog.newInstance("Veuillez selectionner au moins un groupe").show();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default: finish(); break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void envoyer(String message) {
        // TODO: Ajouter les demandes de permission
        if (ContextCompat.checkSelfPermission(NewMessageActivity.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(NewMessageActivity.this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            // Permission has already been granted
            envoyerMemeMeme(message);
        }
    }

    private void envoyerMemeMeme(final String message) {
        progress.show();
        // On sauveagarde d'abord tout ça
        this.message = message;
        final int msgId = sauvegarder(true);
        final Message m = Message.findById(Message.class, (long) msgId);
        // puis on envoie le message
        //
        // On recupere la liste des numeros auxquels envoyer le message
        new Handler().post(new Thread() {
            private Context context;
            Thread setContext(Context c) {
                context = c;
                return this;
            }

            @Override
            public void run() {
                List<Sim> sims =  Sim.listAll(Sim.class);
                HashSet<Contact> contacts = new HashSet<>();
                for (Groupe g : gAdapter.getGroupes()) {
                    List<GroupeContact> liste = GroupeContact.find(GroupeContact.class, "GROUPE = ?", String.valueOf(g.getId()));
                    List<Contact> cs = new ArrayList<>();
                    for (GroupeContact gc : liste)
                        cs.add(gc.getContact());
                    contacts.addAll(cs);
                }
                m.setTotal(contacts.size());
                m.save();
                for (Contact c : contacts)
                    for (Sim s : sims) {
                        if (c.getOperateur().equals(s.getOperateur())) {
                            // envoi
                            Intent iSent = new Intent(SMSDeliveryReceiver.SMS_SENT_ACTION);
                            Intent iDeliv = new Intent(SMSDeliveryReceiver.SMS_DELIVERED_ACTION);
                            iSent.putExtra(SMSDeliveryReceiver.SMS_ARG_MSG_ID, msgId);
                            iSent.putExtra(SMSDeliveryReceiver.SMS_ARG_CONTACT_ID, c.getId());
                            SmsManager.getDefault().sendTextMessage(c.getNumero(), null, message,
                                    PendingIntent.getBroadcast(context, 0, iSent, PendingIntent.FLAG_ONE_SHOT),
                                    PendingIntent.getBroadcast(context, 1, iDeliv, PendingIntent.FLAG_ONE_SHOT));
                        } else {
                            // envoi au premier s
                            Intent iSent = new Intent(SMSDeliveryReceiver.SMS_SENT_ACTION);
                            Intent iDeliv = new Intent(SMSDeliveryReceiver.SMS_DELIVERED_ACTION);
                            iDeliv.putExtra(SMSDeliveryReceiver.SMS_ARG_MSG_ID, msgId);
                            iDeliv.putExtra(SMSDeliveryReceiver.SMS_ARG_CONTACT_ID, c.getId());
                            SmsManager.getDefault().sendTextMessage(c.getNumero(), null, message,
                                    PendingIntent.getBroadcast(context, 0, iSent, PendingIntent.FLAG_ONE_SHOT),
                                    PendingIntent.getBroadcast(context, 1, iDeliv, PendingIntent.FLAG_ONE_SHOT));
                        }
                    }
                progress.dismiss();
                finish();
            }
        }.setContext(this));

        //
        Toast.makeText(this, "Messages envoyés", Toast.LENGTH_LONG).show();
    }

    private int sauvegarder(boolean sent) {
        Message m = new Message(text.getText().toString(), Utils.toDateHeure(Calendar.getInstance(), Utils.DateHeureType.DATABASE), sent);
        m.save();
        for (Groupe g : gAdapter.getGroupes())
            new GroupeMessage(g, m).save();
        if (!sent) {
            finish();
            return 0;
        } else return m.getId().intValue();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    envoyerMemeMeme(message);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onDestroy() {
        System.out.println("new message on destroy");
        MainActivity.fragmentToUpdate = 0;
        super.onDestroy();
    }

    public static class SendMessageDialog extends AppCompatDialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getContext())
                    .setMessage("Vous pouvez envoyer ce message maintenant ou le sauvegarder pour l'envoyer plus tard")
                    .setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((NewMessageActivity) getActivity()).envoyer(((NewMessageActivity) getActivity()).text.getText().toString());
                        }
                    })
                    .setNeutralButton("Sauvegarder", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((NewMessageActivity) getActivity()).sauvegarder(false);
                        }
                    })
                    .setNegativeButton("Annuler", null)
                    .create();
        }
    }

    /*private Dialog sendMessageDialog = new AlertDialog.Builder(getApplicationContext())
            .setMessage("Vous pouvez envoyer ce message maintenant ou le sauvegarder pour l'envoyer plus tard")
            .setPositiveButton("Envoyer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    envoyer();
                }
            })
            .setNeutralButton("Sauvegarder", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sauvegarder(false);
                }
            })
            .setNegativeButton("Annuler", null)
            .create();*/
}
