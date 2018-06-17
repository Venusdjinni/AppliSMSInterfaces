package com.venus.app.applismsinterfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import com.venus.app.Adapters.GroupesAdapter;
import com.venus.app.Adapters.MessagesAdapter;
import com.venus.app.Modele.Groupe;
import com.venus.app.Modele.Message;

import java.util.Comparator;

public class ListeFragment extends Fragment implements
        MessagesAdapter.OnMessageInteractionListener,
        GroupesAdapter.OnGroupeInteractionListener {
    private static final String ARG_POSITION = "position";
    private RecyclerView rv;
    private int position;

    public static ListeFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        ListeFragment fragment = new ListeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        position = getArguments().getInt(ARG_POSITION);
    }

    public void update() {
        System.out.println("update");
        if (rv != null)
        rv.setAdapter(position == 0 ?
                new MessagesAdapter(Message.find(Message.class, null, null, null, "DATE_HEURE desc", null), this) :
                new GroupesAdapter(Groupe.find(Groupe.class, null, null, null, "nom", null), this));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.activity_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ab_operateurs: break;
            default: break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_liste, container, false);

        rv = v.findViewById(R.id.recyclerview);
        rv.setNestedScrollingEnabled(false);
        //rv.setAdapter(position == 0 ? new MessagesAdapter(ms, this) : new GroupesAdapter(gs, this));
        update();

        return v;
    }

    @Override
    public void onClick(Message message) {
        startActivity(new Intent(getActivity(), MessageActivity.class).putExtra("message", message).putExtra("id", message.getId()));
    }

    @Override
    public void onClick(Groupe groupe) {
        startActivity(new Intent(getActivity(), GroupeActivity.class).putExtra("groupe", groupe).putExtra("id", groupe.getId()));
    }

    private static class MessageComparator implements Comparator<Message> {
        @Override
        public int compare(Message o1, Message o2) {
            return o1.getDateHeure().compareTo(o2.getDateHeure());
        }
    }

    private static class GroupeComparator implements Comparator<Groupe> {
        @Override
        public int compare(Groupe o1, Groupe o2) {
            return o1.getNom().compareTo(o2.getNom());
        }
    }
}
