package com.venus.app.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.venus.app.Modele.Contact;
import com.venus.app.ModeleView.ContactView;
import com.venus.app.applismsinterfaces.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactView> {
    private List<Contact> contacts;

    public ContactsAdapter() {
        this.contacts = new ArrayList<>();
    }

    public ContactsAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactView holder, int position) {
        holder.setContact(contacts.get(position));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void add(Contact contact) {
        add(Collections.singletonList(contact));
    }

    public void add(List<Contact> contacts) {
        this.contacts.addAll(contacts);
        notifyDataSetChanged();
    }

    public void remove(Contact contact) {
        this.contacts.remove(contact);
        notifyDataSetChanged();
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}
