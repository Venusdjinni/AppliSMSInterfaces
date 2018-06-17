package com.venus.app.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.venus.app.Modele.Message;
import com.venus.app.ModeleView.MessageView;
import com.venus.app.applismsinterfaces.R;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessageView> {
    private List<Message> messages;
    private OnMessageInteractionListener listener;

    public MessagesAdapter(List<Message> messages, OnMessageInteractionListener listener) {
        this.messages = messages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int res = viewType == 0 ? R.layout.item_message : R.layout.item_message_send;
        return new MessageView(LayoutInflater.from(parent.getContext()).inflate(res, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageView holder, int position) {
        final int positionF = position;
        holder.setMessage(messages.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick(messages.get(positionF));
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSent() ? 1 : 0;
    }

    public interface OnMessageInteractionListener {
        void onClick(Message message);
    }
}
