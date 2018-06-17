package com.venus.app.ModeleView;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.venus.app.Modele.Groupe;
import com.venus.app.Modele.GroupeContact;
import com.venus.app.applismsinterfaces.R;

public class GroupeView extends RecyclerView.ViewHolder {
    public AppCompatTextView ic;
    public AppCompatTextView nom;
    public AppCompatTextView infos;
    public Groupe item;

    public GroupeView(View itemView) {
        super(itemView);
        ic = itemView.findViewById(R.id.grpe_ic);
        nom = itemView.findViewById(R.id.grpe_nom);
        infos = itemView.findViewById(R.id.grpe_infos);
    }

    public GroupeView(View itemView, Groupe item) {
        this(itemView);
        setGroupe(item);
    }

    public void setGroupe(Groupe groupe) {
        this.item = groupe;
        ic.setText(String.valueOf(item.getNom().charAt(0)));
        nom.setText(item.getNom());
        long count = GroupeContact.count(GroupeContact.class, "GROUPE = ?", new String[]{String.valueOf(item.getId())});
        infos.setText(itemView.getContext().getString(count == 1 ? R.string.nb_membre : R.string.nb_membres, count));
    }
}
