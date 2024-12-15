package com.example.chatappproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import models.MessageModel;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference messagesDatabase;
    private String currentUserId;
    private String otherUserId;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<MessageModel> messageList;

    @SuppressLint({"MissingInflatedId", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        String userName = getIntent().getStringExtra("userName");

        TextView userNameTextView = findViewById(R.id.user_name_conversation);
        if (userName != null) {
            userNameTextView.setText(userName);
        }

        // Get the current logged-in user's ID (Firebase Authentication)
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Get the other user's ID (assuming passed in the intent)
        otherUserId = getIntent().getStringExtra("otherUserId");

        // Initialize the Firebase Database reference for storing and retrieving messages
        messagesDatabase = FirebaseDatabase.getInstance().getReference("Messages");

        // Set up RecyclerView for chat messages
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();

        // Initialize the chat adapter with messageList and currentUserId
        chatAdapter = new ChatAdapter(messageList, currentUserId); // Pass currentUserId here
        chatRecyclerView.setAdapter(chatAdapter);

        Log.d("ChatActivity", "start loading: messages");
        // Retrieve and display previous messages
        loadMessages();

        // Handle send button
        ImageButton sendButton = findViewById(R.id.sendButton);
        EditText messageEditText = findViewById(R.id.messageEditText);

        sendButton.setOnClickListener(view -> {
            String message = messageEditText.getText().toString();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageEditText.getText().clear(); // Clear the message input field
            }
        });
    }

    private void loadMessages() {

        Log.d("ChatActivity", "otherUserId: " + otherUserId);
        String chatId = currentUserId.compareTo(otherUserId) < 0
                ? currentUserId + "_" + otherUserId
                : otherUserId + "_" + currentUserId;
        Log.d("ChatActivity1", "currentUserId: " + currentUserId);
        Log.d("ChatActivity2", "currentUserId: " + otherUserId);
        if (currentUserId == null || otherUserId == null) {
            // Show an error message or log the issue
            return; // Exit the method or activity to prevent further issues
        }

        messagesDatabase.child(chatId)
                .orderByChild("timestamp") // Order messages by timestamp
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messageList.clear(); // Clear the previous messages
                        if (dataSnapshot.exists()) { // Check if dataSnapshot has any data
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                MessageModel message = snapshot.getValue(MessageModel.class);
                                if (message != null) {
                                    messageList.add(message); // Add new messages to the list
                                }
                            }
                            chatAdapter.notifyDataSetChanged(); // Notify the adapter to update the RecyclerView

                            // Scroll to the latest message only if there are messages
                            if (!messageList.isEmpty()) {
                                chatRecyclerView.scrollToPosition(messageList.size() - 1);
                            }
                        } else {
                            // Optional: Handle case where no messages are present
                            chatAdapter.notifyDataSetChanged(); // Update RecyclerView to show an empty state
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Log and handle errors
                        System.err.println("DatabaseError: " + databaseError.getMessage());
                    }
                });
    }


    private void sendMessage(String message) {
        // Derive a consistent chat ID
        String chatId = currentUserId.compareTo(otherUserId) < 0
                ? currentUserId + "_" + otherUserId
                : otherUserId + "_" + currentUserId;

        // Create a unique message ID
        String messageId = messagesDatabase.child(chatId).push().getKey();

        // Prepare the message object
        MessageModel messageData = new MessageModel(currentUserId, message, System.currentTimeMillis());

        if (messageId != null) {
            // Save the message under the chat node
            messagesDatabase.child(chatId).child(messageId).setValue(messageData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ChatActivity", "Message sent");
                    })
                    .addOnFailureListener(e -> {
                        Log.d("ChatActivity", "Failled to send");
                    });
        }
    }



}
