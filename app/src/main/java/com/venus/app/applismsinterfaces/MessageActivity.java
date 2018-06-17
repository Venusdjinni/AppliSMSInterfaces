package com.venus.app.applismsinterfaces;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.venus.app.Adapters.GroupesAdapter;
import com.venus.app.Modele.*;

import java.util.*;

public class MessageActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 89;
    private AppCompatTextView dateHeure;
    private AppCompatTextView texte;
    private RecyclerView rv;
    private AppCompatTextView total;
    private AppCompatTextView envoyes;
    private AppCompatTextView echecs;
    private Message message;
    private Long id;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("message")) {
            message = getIntent().getParcelableExtra("message");
            id = getIntent().getLongExtra("id", 0);
        }
        dateHeure = findViewById(R.id.msg_dateHeure);
        texte = findViewById(R.id.msg_message);
        rv = findViewById(R.id.msg_groupes);
        total = findViewById(R.id.msg_total);
        total.setText(getString(R.string.msg_total, message.getTotal()));
        envoyes = findViewById(R.id.msg_envoyes);
        envoyes.setText(getString(R.string.msg_envoyes, message.getSuccess()));
        echecs = findViewById(R.id.msg_echecs);
        echecs.setText(getString(R.string.msg_echecs, message.getFailed()));

        dateHeure.setText(message.getDateHeure());
        texte.setText(message.getTexte());
        List<GroupeMessage> liste = GroupeMessage.find(GroupeMessage.class, "MESSAGE = ?", String.valueOf(id));
        List<Groupe> groupes = new ArrayList<>();
        for (GroupeMessage gm : liste)
            groupes.add(gm.getGroupe());
        rv.setAdapter(new GroupesAdapter(groupes, null));
        progress = new ProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!message.isSent())
            getMenuInflater().inflate(R.menu.activity_message, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: finish(); break;
            case R.id.ab_resend: envoyer(message.getTexte()); break;
        }

        return super.onOptionsItemSelected(item);
    }

    private String toLongString(Calendar c) {
        Locale l = Locale.getDefault();
        String day = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, l);
        String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, l);
        return day.substring(0, 1).toUpperCase() + day.substring(1) + ", " +
                c.get(Calendar.DAY_OF_MONTH) + " " +
                month.substring(0, 1).toUpperCase() + month.substring(1) + " " +
                c.get(Calendar.YEAR);
    }

    private void envoyer(String message) {
        // TODO: Ajouter les demandes de permission
        if (ContextCompat.checkSelfPermission(MessageActivity.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MessageActivity.this,
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
                for (Groupe g : ((GroupesAdapter) MessageActivity.this.rv.getAdapter()).getGroupes()) {
                    List<GroupeContact> liste = GroupeContact.find(GroupeContact.class, "GROUPE = ?", String.valueOf(g.getId()));
                    List<Contact> cs = new ArrayList<>();
                    for (GroupeContact gc : liste)
                        cs.add(gc.getContact());
                    contacts.addAll(cs);
                }
                MessageActivity.this.message.setTotal(contacts.size());
                MessageActivity.this.message.save();
                for (Contact c : contacts)
                    for (Sim s : sims) {
                        if (c.getOperateur().equals(s.getOperateur())) {
                            // envoi
                            Intent iSent = new Intent(SMSDeliveryReceiver.SMS_SENT_ACTION);
                            Intent iDeliv = new Intent(SMSDeliveryReceiver.SMS_DELIVERED_ACTION);
                            iSent.putExtra(SMSDeliveryReceiver.SMS_ARG_MSG_ID, MessageActivity.this.message.getId());
                            iSent.putExtra(SMSDeliveryReceiver.SMS_ARG_CONTACT_ID, c.getId());
                            SmsManager.getDefault().sendTextMessage(c.getNumero(), null, message,
                                    PendingIntent.getBroadcast(context, 0, iSent, PendingIntent.FLAG_ONE_SHOT),
                                    PendingIntent.getBroadcast(context, 1, iDeliv, PendingIntent.FLAG_ONE_SHOT));
                        } else {
                            // envoi au premier s
                            Intent iSent = new Intent(SMSDeliveryReceiver.SMS_SENT_ACTION);
                            Intent iDeliv = new Intent(SMSDeliveryReceiver.SMS_DELIVERED_ACTION);
                            iDeliv.putExtra(SMSDeliveryReceiver.SMS_ARG_MSG_ID, MessageActivity.this.message.getId());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    envoyerMemeMeme(message.getTexte());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
