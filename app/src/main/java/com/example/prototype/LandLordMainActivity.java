package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LandLordMainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_lord_main);

        //Chat button
        ImageView chat = findViewById(R.id.chatButton);
        chat.setOnClickListener(v -> startActivity(new Intent(this, ChatsActivity.class)));
        //Add new property
        TextView addProperty = findViewById(R.id.add_new_property);
        addProperty.setOnClickListener(v -> startActivity(new Intent(this, PropertyCreation.class)));

        //Settings
        ImageView settingsIcon = findViewById(R.id.settingsIcon);
        settingsIcon.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));


        RecyclerView recyclerView = findViewById(R.id.propertyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Property> propertyList = new ArrayList<>();
        PropertyAdapter adapter = new PropertyAdapter(propertyList);
        recyclerView.setAdapter(adapter);

        adapter.setOnInquireClickListener(user -> {
            Intent intent = new Intent(LandLordMainActivity.this, ChatActivity.class);
            intent.putExtra("username", user.name);
            intent.putExtra("userID", user.userID);
            startActivity(intent);
        });

        DatabaseReference ref = FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
                .getReference("property");

        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                propertyList.clear();

                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Property property = ds.getValue(Property.class);
                    String userID = ds.child("userID").getValue(String.class);
                    if(userID.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                    {
                        if (property != null)
                        {
                            propertyList.add(property);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





    }
}
