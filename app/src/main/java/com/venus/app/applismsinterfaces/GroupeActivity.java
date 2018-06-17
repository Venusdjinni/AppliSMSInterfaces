package com.venus.app.applismsinterfaces;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import com.venus.app.Adapters.ContactsAdapter;
import com.venus.app.Modele.Contact;
import com.venus.app.Modele.Groupe;
import com.venus.app.Modele.GroupeContact;

import java.util.ArrayList;
import java.util.List;

public class GroupeActivity extends AppCompatActivity {
    private RecyclerView membres;
    private Groupe groupe;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        membres = findViewById(R.id.gpe_membres);

        if (getIntent().hasExtra("groupe")) {
            groupe = getIntent().getParcelableExtra("groupe");
            id = getIntent().getLongExtra("id", 0);

        }

        getSupportActionBar().setTitle(groupe.getNom());
        List<GroupeContact> liste = GroupeContact.find(GroupeContact.class, "GROUPE = ?", String.valueOf(id));
        List<Contact> contacts = new ArrayList<>();
        for (GroupeContact gc : liste)
            contacts.add(gc.getContact());
        membres.setAdapter(new ContactsAdapter(contacts));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: finish(); break;
        }

        return super.onOptionsItemSelected(item);
    }
}
