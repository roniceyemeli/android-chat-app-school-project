package com.example.chatappproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import models.UserModel;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    private Button registerBtn;
    private EditText registerMail, registerPassword, registerUsername, registerAbout;
    private TextView redirectLoginBtn;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Initialize UI Components
        registerBtn = findViewById(R.id.register_btn);
        registerMail = findViewById(R.id.register_mail);
        registerPassword = findViewById(R.id.register_password);
        redirectLoginBtn = findViewById((R.id.redirect_login_text));
        registerUsername = findViewById(R.id.register_username);
        registerAbout = findViewById(R.id.register_about);

        // Set OnClickListener for Register Button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = registerMail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();
                String username = registerUsername.getText().toString().trim();
                String about = registerAbout.getText().toString().trim();

                // Validate inputs
                if (email.isEmpty() || password.isEmpty()|| username.isEmpty() || about.isEmpty()) {
                    Toast.makeText(Register.this, "All fields are required.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sign in with Firebase
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId =  task.getResult().getUser().getUid();
                                    // Sign up success
                                    Log.d("RegisterActivity", "signUpWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    UserModel userModel = new UserModel(username,email,userId,"R.drawable.user",about);

                                    sharedPreferences = getSharedPreferences("token_saved",MODE_PRIVATE);
                                    String tokenInMain =  sharedPreferences.getString("token_new","mynull");
                                    userModel.setToken(tokenInMain);

                                    firebaseDatabase.getReference()
                                            .child("Users")
                                            .child(userId)
                                            .setValue(userModel)
                                            .addOnCompleteListener(dbTask -> {
                                                if (dbTask.isSuccessful()) {
                                                    Toast.makeText(Register.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                                    // Navigate to Login Activity
                                                    Intent intent = new Intent(Register.this, Login.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Log.e("RegisterActivity", "Database error: " + dbTask.getException());
                                                    Toast.makeText(Register.this, "Failed to save user info.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Log.w("RegisterActivity", "signUpWithEmail:failure", task.getException());
                                    Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        redirectLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });
    }
}