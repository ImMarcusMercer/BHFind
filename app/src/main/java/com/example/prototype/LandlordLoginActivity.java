package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LandlordLoginActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button signupButton, clearButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landlord_signup);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
                .getReference("users");

        usernameInput = findViewById(R.id.landlord_username);
        emailInput = findViewById(R.id.landlord_email);
        passwordInput = findViewById(R.id.landlord_password_input);
        confirmPasswordInput = findViewById(R.id.landlord_confirm_password_input);
        EditText locationInput = findViewById(R.id.landlord_location);
        signupButton = findViewById(R.id.landlord_sign_up_button_id);
        clearButton = findViewById(R.id.landlord_clear_button);

        signupButton.setOnClickListener(v -> registerUser(locationInput));
        clearButton.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }

    private void registerUser( EditText locationInput) {
        String username = usernameInput.getText().toString().trim();
        String locationInputText= locationInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String accountType = "landlord";

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null)
                        {
                            String userId = firebaseUser.getUid();
                            User newUser = new User(userId, accountType,username, locationInputText, email);

                            databaseRef.child(userId).setValue(newUser)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LandLordMainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Sign up failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
