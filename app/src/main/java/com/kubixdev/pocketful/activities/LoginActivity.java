package com.kubixdev.pocketful.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kubixdev.pocketful.R;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private AppCompatButton loginButton, switchToSignupButton, forgotPasswordButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeUI();

        mAuth = FirebaseAuth.getInstance();

        // handle login action
        loginButton.setOnClickListener(v -> loginUser());

        // handle alternative sign up action
        switchToSignupButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        // handle alternative forgot password action
//        forgotPasswordButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), PasswordResetActivity.class);
//            startActivity(intent);
//            overridePendingTransition(0, 0);
//            finish();
//        });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // proceed further if user is logged in
        if (currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void initializeUI() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        switchToSignupButton = findViewById(R.id.switchToSignupButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
    }


    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Login details cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}