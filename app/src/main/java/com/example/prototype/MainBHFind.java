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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainBHFind extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bhfind);


        //Chat Button
        ImageView chatButton = findViewById(R.id.chatButton);
        chatButton.setOnClickListener(v -> startActivity(new Intent(this, ChatsActivity.class)));

        //Settings
        ImageView settingsIcon = findViewById(R.id.settingsIcon);
        settingsIcon.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        TextView returnButton= findViewById(R.id.back);
        returnButton.setOnClickListener(v -> startActivity(new Intent(this, MainMenuActivity.class)));



        RecyclerView recyclerView = findViewById(R.id.userRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Property> propertyList = new ArrayList<>();
        UserAdapter adapter = new UserAdapter(propertyList);
        recyclerView.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
                .getReference("property");

        recyclerView.setAdapter(adapter);

        adapter.setOnInquireClickListener(user -> {
            Intent intent = new Intent(MainBHFind.this, ChatActivity.class);
            intent.putExtra("username", user.name);
            intent.putExtra("userID", user.userID);
            startActivity(intent);
        });


        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                propertyList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Property property = ds.getValue(Property.class);

                    if (property != null) {
                        propertyList.add(property);
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
