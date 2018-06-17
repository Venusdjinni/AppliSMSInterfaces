package com.venus.app.ModeleView;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.venus.app.Modele.Contact;
import com.venus.app.applismsinterfaces.R;

public class ContactView extends RecyclerView.ViewHolder {
    public AppCompatTextView nom;
    public AppCompatTextView numero;
    public AppCompatTextView op;
    public Contact item;


    public ContactView(View itemView) {
        super(itemView);
        nom = itemView.findViewById(R.id.cont_nom);
        numero = itemView.findViewById(R.id.cont_numero);
        op = itemView.findViewById(R.id.cont_op);
    }

    public ContactView(View itemView, Contact item) {
        this(itemView);
        setContact(item);
    }

    public void setContact(Contact contact) {
        this.item = contact;
        nom.setText(item.getNom());
        numero.setText(item.getNumero());
        op.setText(item.getOperateur());
    }
}
