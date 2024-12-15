package com.example.chatappproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import models.MessageModel;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<MessageModel> messageList;
    private String currentUserId; // You should have currentUserId available to check which message belongs to current user

    public ChatAdapter(List<MessageModel> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;  // Pass currentUserId to the adapter
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel message = messageList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            // Current user's message
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.messageTextViewReceiver.setVisibility(View.GONE);
            holder.messageTextView.setText(message.getMessage());
        } else {
            // Other user's message
            holder.messageTextView.setVisibility(View.GONE);
            holder.messageTextViewReceiver.setVisibility(View.VISIBLE);
            holder.messageTextViewReceiver.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messageTextViewReceiver;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextViewSender);
            messageTextViewReceiver = itemView.findViewById(R.id.messageTextViewReceiver); // Initialize receiver's message TextView
        }
    }
}
