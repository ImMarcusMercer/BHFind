package com.example.prototype;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private Handler handler;
    private FirebaseAuth mAuth;
    private Runnable timeoutRunnable;
    private boolean isNavigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler(Looper.getMainLooper());
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        timeoutRunnable = () -> {
            dismissDialogSafely();
            if (!isNavigated) {
                Toast.makeText(SplashActivity.this, "Operation timed out", Toast.LENGTH_SHORT).show();
                navigateTo(LoginActivity.class);
            }
        };
        handler.postDelayed(timeoutRunnable, 5000);

        checkUserLoginStatus();
    }

    private void checkUserLoginStatus()
    {
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            navigateTo(LoginActivity.class);
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            navigateTo(LoginActivity.class);
            return;
        }

        String userId = user.getUid();
        DatabaseReference typeRef = FirebaseDatabase
                .getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
                .getReference("users").child(userId).child("type");

        typeRef.get().addOnCompleteListener(task -> {
            handler.removeCallbacks(timeoutRunnable);
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    String type = snapshot.getValue(String.class);
                    if ("user".equals(type)) {
                        saveUserToPreferences(user);
                        navigateTo(MainMenuActivity.class);
                    } else if ("landlord".equals(type)) {
                        saveUserToPreferences(user);
                        navigateTo(LandLordMainActivity.class);
                    } else {
                        Toast.makeText(this, "Unknown user type: " + type, Toast.LENGTH_SHORT).show();
                        navigateTo(LoginActivity.class);
                    }
                    finish();
                }
                else
                {
                    Log.w("FIREBASE", "Snapshot does not exist");
                    navigateTo(LoginActivity.class);
                }
            } else {
                Log.e("FIREBASE", "Failed to retrieve data", task.getException());
                navigateTo(LoginActivity.class);
            }
        });
    }

    private void navigateTo(Class<?> targetActivity) {
        if (!isNavigated && !isFinishing() && !isDestroyed()) {
            isNavigated = true;
            dismissDialogSafely();
            startActivity(new Intent(this, targetActivity));
            finish();
        }
    }

    private void saveUserToPreferences(FirebaseUser user) {
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userId", user.getUid());
        editor.putString("email", user.getEmail());
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void dismissDialogSafely() {
        handler.removeCallbacks(timeoutRunnable);
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                Log.w("SplashActivity", "Dialog dismissal issue", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        dismissDialogSafely();
        super.onDestroy();
    }
}
