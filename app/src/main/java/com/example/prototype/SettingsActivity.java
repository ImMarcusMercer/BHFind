package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.common.returnsreceiver.qual.This;

public class SettingsActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button logoutButton = findViewById(R.id.logoutbutton);
        Button profileButt= findViewById(R.id.gotoprofile);

        //Profile
        profileButt.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
        });


        //Logout
        logoutButton.setOnClickListener(v -> {
            LogoutManager.logout(this);
            Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();

            SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();
            finish();
        });
    }

}
