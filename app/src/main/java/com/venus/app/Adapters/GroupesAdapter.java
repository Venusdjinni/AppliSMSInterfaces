package com.venus.app.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.venus.app.Modele.Groupe;
import com.venus.app.ModeleView.GroupeView;
import com.venus.app.applismsinterfaces.R;

import java.util.ArrayList;
import java.util.List;

public class GroupesAdapter extends RecyclerView.Adapter<GroupeView> {
    private List<Groupe> groupes;
    private OnGroupeInteractionListener listener;

    public GroupesAdapter() {
        groupes = new ArrayList<>();
    }

    public GroupesAdapter(List<Groupe> groupes, OnGroupeInteractionListener listener) {
        this.groupes = groupes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupeView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupeView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_groupe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupeView holder, int position) {
        final int positionF = position;
        holder.setGroupe(groupes.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(groupes.get(positionF));
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupes.size();
    }

    public void addGroupe(Groupe groupe) {
        if (!groupes.contains(groupe)) {
            groupes.add(groupe);
            notifyItemInserted(groupes.size() - 1);
        }
    }

    public List<Groupe> getGroupes() {
        return groupes;
    }

    public interface OnGroupeInteractionListener {
        void onClick(Groupe groupe);
    }
}
