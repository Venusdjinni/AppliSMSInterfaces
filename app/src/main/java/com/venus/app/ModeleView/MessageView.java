package com.venus.app.ModeleView;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.venus.app.Modele.GroupeMessage;
import com.venus.app.Modele.Message;
import com.venus.app.applismsinterfaces.R;

import java.util.List;

public class MessageView extends RecyclerView.ViewHolder {
    public AppCompatTextView ic;
    public AppCompatTextView titre;
    public AppCompatTextView texte;
    public AppCompatTextView cTotal;
    public AppCompatTextView cSent;
    public AppCompatTextView cWrong;
    public Message item;

    public MessageView(View itemView) {
        super(itemView);
        ic = itemView.findViewById(R.id.msg_ic);
        titre = itemView.findViewById(R.id.msg_titre);
        texte = itemView.findViewById(R.id.msg_text);
    }

    public MessageView(View itemView, Message item) {
        this(itemView);
        setMessage(item);
    }

    public void setMessage(Message message) {
        this.item = message;
        if (item.isSent()) {
            cTotal = itemView.findViewById(R.id.msg_c_total);
            cSent = itemView.findViewById(R.id.msg_c_sent);
            cWrong = itemView.findViewById(R.id.msg_c_wrong);
        }
        StringBuilder titre = new StringBuilder();
        List<GroupeMessage> liste = GroupeMessage.find(GroupeMessage.class, "MESSAGE = ?", String.valueOf(item.getId()));
        for (GroupeMessage l : liste)
            titre.append(l.getGroupe().getNom()).append(", ");
        this.titre.setText(titre.toString().substring(0, titre.length() - 2));
        ic.setText(String.valueOf(item.getTexte().charAt(0)));
        texte.setText(item.getTexte());
        if (item.isSent()) {
            cTotal.setText(String.valueOf(item.getTotal()));
            cSent.setText(String.valueOf(item.getSuccess()));
            cWrong.setText(String.valueOf(item.getFailed()));
        }
    }
}
