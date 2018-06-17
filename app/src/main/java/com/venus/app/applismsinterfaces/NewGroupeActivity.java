package com.venus.app.applismsinterfaces;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import com.venus.app.Adapters.ContactsAdapter;
import com.venus.app.Modele.Contact;
import com.venus.app.Modele.Groupe;
import com.venus.app.Modele.GroupeContact;
import com.venus.app.Utils.SimpleMessageDialog;
import com.venus.app.Utils.Terminating;

public class NewGroupeActivity extends AppCompatActivity implements Terminating {
    private static final int ARG_PICK_CONTACT = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 99;
    private AutoCompleteTextView nom;
    private AppCompatTextView count;
    private AppCompatImageView add;
    private RecyclerView rv;
    private ContactsAdapter adapter;
    private String argNom = "", argNumero = "";
    private boolean shouldShowDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_groupe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nom = findViewById(R.id.newg_nom);
        count = findViewById(R.id.newg_count);
        add = findViewById(R.id.newg_add);
        add.setOnClickListener(add_listener);
        rv = findViewById(R.id.newg_recyclerview);
        adapter = new ContactsAdapter();
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_groupe, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ab_enregistrer: enregistrer(); break;
            default: finish(); break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enregistrer() {
        boolean ok = true;
        if (!Utils.isGoodStringValue(nom.getText().toString())) {
            nom.setError("Champ requis");
            ok = false;
        }
        if (adapter.getContacts().size() == 0) {
            ok = false;
            SimpleMessageDialog.newInstance("Ajoutez au moins un contact").show(getSupportFragmentManager(), "simple");
        }
        if (ok) {
            Groupe g = new Groupe(nom.getText().toString());
            g.save();
            // On enregistre les contacts et le groupe
            for (Contact c : adapter.getContacts()) {
                c.save();
                new GroupeContact(g, c).save();
            }
            //
            SimpleMessageDialog.newInstance("Groupe enregistré", true).show(getSupportFragmentManager(), "simple");
        }
    }

    private View.OnClickListener add_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // ajout d'un contact, via le repertoire ou via un formulaire rapide
            AlertDialog dialog = new AlertDialog.Builder(NewGroupeActivity.this)
                    .setItems(new String[]{"Manuellement", "Depuis le repertoire"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) new NewContactDialog().show(getSupportFragmentManager(), "NCD");
                            else {
                                /* on part chercher le contact dans la liste, et on remplit la vue de NewContactDialog avec les infos */

                                // Here, thisActivity is the current activity
                                if (ContextCompat.checkSelfPermission(NewGroupeActivity.this,
                                        Manifest.permission.READ_CONTACTS)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(NewGroupeActivity.this,
                                            new String[]{Manifest.permission.READ_CONTACTS},
                                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                    // app-defined int constant. The callback method gets the
                                    // result of the request.
                                } else {
                                    // Permission has already been granted
                                    pickContact();
                                }
                            }
                        }
                    })
                    .create();
            dialog.show();
        }
    };

    private void pickContact() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(i, ARG_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ARG_PICK_CONTACT:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri, new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER }, null, null, null);
                    if (cursor != null && cursor.moveToNext())
                        argNumero = cursor.getString(0);
                    cursor.close();
                    cursor = getContentResolver().query(uri, new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME }, null, null, null);
                    if (cursor != null && cursor.moveToNext())
                        argNom = cursor.getString(0);
                    shouldShowDialog = true;
                }
                break;
            default: super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (shouldShowDialog) {
            shouldShowDialog = false;
            NewContactDialog.newInstance(argNom, argNumero).show(getSupportFragmentManager(), "new");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    pickContact();
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
    public void terminer() {
        MainActivity.fragmentToUpdate = 1;
        finish();
    }

    public static class NewContactDialog extends AppCompatDialogFragment {
        private static final String ARG_NOM = "nom";
        private static final String ARG_NUMERO = "numero";
        private AppCompatEditText nom;
        private AppCompatEditText numero;
        private AppCompatSpinner operateur;

        static NewContactDialog newInstance(String nom, String numero) {
            Bundle args = new Bundle();
            args.putString(ARG_NOM, nom);
            args.putString(ARG_NUMERO, numero);
            NewContactDialog fragment = new NewContactDialog();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_contact, null, false);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                    new String[]{"MTN", "Orange", "Nexttel", "Camtel"});
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            operateur = v.findViewById(R.id.dialog_ng_operateur);
            operateur.setAdapter(adapter);
            nom = v.findViewById(R.id.dialog_ng_nom);
            numero = v.findViewById(R.id.dialog_ng_numero);
            if (getArguments() != null) {
                nom.setText(getArguments().getString(ARG_NOM));
                numero.setText(getArguments().getString(ARG_NUMERO));
            }
            AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                    .setView(v)
                    .setPositiveButton("Enregistrer", null)
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean ok =true;
                            if (!Utils.isGoodStringValue(nom.getText().toString())) {
                                ok = false;
                                nom.setError("Champ vide");
                            }
                            if (Utils.isGoodStringValue(numero.getText().toString())) {
                                if (numero.getText().length() < 9) {
                                    ok = false;
                                    numero.setError("Numéro incorrect");
                                }
                            } else {
                                ok = false;
                                numero.setError("Champ vide");
                            }

                            if (ok)
                                ((NewGroupeActivity) getActivity()).adapter.add(new Contact(
                                        nom.getText().toString(),
                                        numero.getText().toString(),
                                        operateur.getSelectedItem().toString()
                                ));
                            dismiss();
                        }
                    });
                }
            });

            return dialog;
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            nom.setText("");
            numero.setText("");
        }
    }
}
