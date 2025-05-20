package com.example.prototype;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText username,emailEditText, passwordEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email_input);
        passwordEditText = findViewById(R.id.password_input);
        Button loginButton = findViewById(R.id.login_button_id);
        Button signupButton = findViewById(R.id.sign_up_button_id);
        Button guestButton = findViewById(R.id.guest_button_id);

        loginButton.setOnClickListener(v ->
        {
                if (!InternetManager.isInternetAvailable(this))
                {
                    Toast.makeText(this, "Internet connection required!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    loginUser();
                }
        });
        signupButton.setOnClickListener(v ->
        {
            if (!InternetManager.isInternetAvailable(this))
            {
                Toast.makeText(this, "Internet connection required!", Toast.LENGTH_LONG).show();
            }
            else
            {
                startActivity(new Intent(this, SignupActivity.class));
            }
        });
        guestButton.setOnClickListener(v ->
        {
            if (!InternetManager.isInternetAvailable(this))
            {
                Toast.makeText(this, "Internet connection required!", Toast.LENGTH_LONG).show();
            }
            else
            {
                startActivity(new Intent(this, MainMenuActivity.class));
                Toast.makeText(this, "Logged in as guest!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveUserToPreferences(FirebaseUser user) {
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("userId", user.getUid());
        editor.putString("email", user.getEmail());
        editor.putBoolean("isLoggedIn", true);

        editor.apply();
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Set a timeout handler
        Handler handler = new Handler();
        Runnable timeoutRunnable = () -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                Toast.makeText(this, "Request timed out.", Toast.LENGTH_LONG).show();
            }
        };
        handler.postDelayed(timeoutRunnable, 7000); // 7 seconds timeout

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user1 = mAuth.getCurrentUser();
                        if (user1 != null) {
                            String userId = user1.getUid();

                            DatabaseReference typeRef =FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
                                    .getReference("users").child(userId).child("type");
                            typeRef.get().addOnCompleteListener(task2 ->
                            {
                                if (task2.isSuccessful())
                                {
                                    DataSnapshot snapshot = task2.getResult();
                                    if (snapshot.exists())
                                    {
                                        String type = snapshot.getValue(String.class);
                                        if ("user".equals(type))
                                        {
                                            saveUserToPreferences(user1);
                                            startActivity(new Intent(this, MainMenuActivity.class));

                                        }
                                        else if ("landlord".equals(type))
                                        {
                                            saveUserToPreferences(user1);
                                            startActivity(new Intent(this, LandLordMainActivity.class));

                                        }
                                        else
                                        {
                                            Toast.makeText(this, "Unknown user type: " + type, Toast.LENGTH_SHORT).show();
                                        }
                                        handler.removeCallbacks(timeoutRunnable);
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                    else
                                    {
                                        Log.w("FIREBASE", "Snapshot does not exist");
                                        handler.removeCallbacks(timeoutRunnable);
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                }
                                else
                                {
                                    Log.e("FIREBASE", "Failed to retrieve data", task2.getException());
                                    handler.removeCallbacks(timeoutRunnable);
                                    progressDialog.dismiss();
                                    finish();
                                }
                            });

                        }
                        else
                        {
                            handler.removeCallbacks(timeoutRunnable);
                            progressDialog.dismiss();
                            Toast.makeText(this, "User is null after successful login.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        handler.removeCallbacks(timeoutRunnable);
                        progressDialog.dismiss();
                        Toast.makeText(this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }



//    private void loginUser()
//    {
//        String email = emailEditText.getText().toString().trim();
//        String password = passwordEditText.getText().toString().trim();
//
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Logging in...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//        Handler handler = new Handler();
//        Runnable timeoutRunnable = () -> {
//            if (progressDialog.isShowing()) {
//                progressDialog.dismiss();
//                Toast.makeText(this, "Request timed out. Please try again.", Toast.LENGTH_LONG).show();
//            }
//        };
//        handler.postDelayed(timeoutRunnable, 7000);
//
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(task ->
//                {
//                    FirebaseUser user1 = mAuth.getCurrentUser();
//                    if(user1!=null){
//                        if (task.isSuccessful())
//                        {
//                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//                            String userId = user1.getUid().trim();
//                            database.child("users").child(userId).child("type").get().addOnCompleteListener(task2 -> {
//                                if (task2.isSuccessful()) {
//                                    DataSnapshot snapshot = task2.getResult();
//                                    if (snapshot.exists()) {
//                                        String type = snapshot.getValue(String.class);
//                                        if(type.equals("user")){
//                                            startActivity(new Intent(this, MainMenuActivity.class));
//                                        }
//                                        else if(type.equals("landlord")){
//                                            startActivity(new Intent(this, LandLordMainActivity.class));
//                                        }
//
//                                        saveUserToPreferences(user1);
//                                        finish();
//                                    } else {
//                                        Log.d("Firebase", "No type found for user.");
//                                    }
//                                } else {
//                                    progressDialog.dismiss();
//                                    Log.e("Firebase", "Failed to read value.", task2.getException());
//                                }
//                            });
//                        }
//                        else
//                        {
//                            progressDialog.dismiss();
//                            Toast.makeText(this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
//    }
}