package com.kubixdev.pocketful.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kubixdev.pocketful.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private EditText nameInput, emailInput, passwordInput;
    private AppCompatButton signupButton, switchToLoginButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initializeUI();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        // handle signup action
        signupButton.setOnClickListener(v -> handleSignup());

        // handle alternative login action
        switchToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // proceed further if user is logged in
        if (currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        }
    }


    private void initializeUI() {
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupButton = findViewById(R.id.signupButton);
        switchToLoginButton = findViewById(R.id.switchToLoginButton);
    }


    private void handleSignup() {
        String email = emailInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // simple input check
        if (!validateInput(email, name, password)) {
            return;
        }

        // already registered email check
        checkEmailExists(email, exists -> {
            if (exists) {
                emailInput.setError("Email address already in use");
            }
            else {
                registerUser(email, name, password);
            }
        });
    }


    private boolean validateInput(String email, String name, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            showToast("Account details cannot be empty");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Invalid email address");
            return false;
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
            passwordInput.setError("Password must contain at least one uppercase letter, one digit, one special character, and be at least 8 characters long");
            return false;
        }

        return true;
    }


    // callback interface because of asynchronous firebase query
    private interface EmailCheckCallback {
        void onEmailCheckCompleted(boolean exists);
    }


    private void checkEmailExists(String email, EmailCheckCallback callback) {
        database.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        boolean exists = (querySnapshot != null) && (!querySnapshot.isEmpty());
                        callback.onEmailCheckCompleted(exists);
                    }
                    else {
                        callback.onEmailCheckCompleted(false);
                    }
                });
    }


    private void registerUser(String email, String name, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        if (currentUser != null) {
                            Map<String, Object> user = new HashMap<>();

                            user.put("userID", currentUser.getUid());
                            user.put("name", name);
                            user.put("accountCreationDate", getCurrentDate());
                            user.put("profilePicture", "default.jpg");

                            database.collection("users").document(currentUser.getUid()).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        showToast("Successfully signed up");

                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> showToast("Firestore data write failed"));
                        }

                    }
                    else {
                        showToast("Authentication failed");
                    }
                });
    }


    private String getCurrentDate() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(currentDate);
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}