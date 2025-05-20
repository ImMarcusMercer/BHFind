package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainMenuActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

//        DatabaseReference chatRef = FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
//                .getReference("users");

        ImageView settingsIcon = findViewById(R.id.settingsIcon);
        ImageView service1 = findViewById(R.id.service1);

        settingsIcon.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        service1.setOnClickListener(v ->
        {
            startActivity(new Intent(this, MainBHFind.class));
        });


    }
}
