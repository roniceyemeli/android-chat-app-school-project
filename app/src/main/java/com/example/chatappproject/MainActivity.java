package com.example.chatappproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import models.UserModel;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ArrayList<UserModel> users;
    private ArrayAdapter<String> adapter;

    private UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Redirect to login if no user is logged in
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ListView listView = findViewById(R.id.usersList);
        users = new ArrayList<>();
        userAdapter = new UserAdapter(this, users);
        listView.setAdapter(userAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            UserModel clickedUser = users.get(position);

            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("currentUserId", currentUser.getUid());
            Log.d("ChatActivity", "currentUserId: " + clickedUser.getUserId());
            intent.putExtra("otherUserId", clickedUser.getUserId());
            intent.putExtra("userName", clickedUser.getUserName());
            startActivity(intent);
        });

        fetchUsersFromFirebase();
    }
    private void fetchUsersFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d("FetchUsers", "Fetching users from Firebase");
//                users.clear(); // Clear old data
//                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                    UserModel user = userSnapshot.getValue(UserModel.class);
//                    if (user != null) {
//                        users.add(user); // Add user to the list
//                        Log.d("UserDetails", "User: " + user.getUserName() + ", Email: " + user.getUserMail() + ", ID: " + user.getUserId());
//                    }
//                }
//                userAdapter.notifyDataSetChanged(); // Notify UserAdapter about data changes
//                Log.d("FetchUsers", "Total users fetched: " + users.size());
//            }
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear(); // Clear old data

                // Get the current authenticated user's ID
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String currentUserId = currentUser != null ? currentUser.getUid() : "";

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    UserModel user = userSnapshot.getValue(UserModel.class);
                    if (user != null && !user.getUserId().equals(currentUserId)) {
                        // Exclude the current authenticated user
                        users.add(user);
                        Log.d("UserDetails", "User: " + user.getUserName() + ", Email: " + user.getUserMail() + ", ID: " + user.getUserId());
                    }
                }
                userAdapter.notifyDataSetChanged(); // Notify UserAdapter about data changes
                Log.d("FetchUsers", "Total users fetched: " + users.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to fetch users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}