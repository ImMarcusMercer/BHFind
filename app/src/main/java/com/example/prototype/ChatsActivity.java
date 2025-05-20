package com.example.prototype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private List<String> senderIdList;
    private List<User> senders;
    private DatabaseReference chatsRef;
    private String currentUserId;

    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats_activity);

        //settings button
        findViewById(R.id.settingsIcon).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        senderIdList = new ArrayList<>();
        senders = new ArrayList<>();
        usersAdapter = new UsersAdapter(this, senders);
        recyclerView.setAdapter(usersAdapter);

        //Logic for return button
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference typeRef = FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
                .getReference("users")
                .child(currentUserId)
                .child("type");

        typeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userType = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //return button
        findViewById(R.id.back).setOnClickListener(v -> {
            if (userType.equals("landlord")) {
                startActivity(new Intent(this, LandLordMainActivity.class));
                finish();
            } else if (userType.equals("user")) {
                startActivity(new Intent(this, MainBHFind.class));
                finish();
            }
        });


        chatsRef = FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com/").getReference("chats");

        loadChats();

        usersAdapter.setOnItemClickListener(user -> {
            Intent intent = new Intent(ChatsActivity.this, ChatActivity.class);
            intent.putExtra("username", user.username);
            intent.putExtra("userID", user.userID);
            startActivity(intent);
        });
    }

    private void loadChats() {
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderIdList.clear();
                senders.clear();

                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
//                    Toast.makeText(ChatsActivity.this,  chatSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                    for (DataSnapshot msgSnapshot : chatSnapshot.child("messages").getChildren()) {
//                        Toast.makeText(ChatsActivity.this, msgSnapshot.toString(), Toast.LENGTH_SHORT).show();
                        String recipientId = msgSnapshot.child("recipientuserID").getValue(String.class);
                        String senderId = msgSnapshot.child("senderId").getValue(String.class);

                        if (senderId != null && recipientId != null) {
                            if (recipientId.equals(currentUserId) && !senderIdList.contains(senderId)) {
                                senderIdList.add(senderId); // someone messaged current user
                            } else if (senderId.equals(currentUserId) && !senderIdList.contains(recipientId)) {
                                senderIdList.add(recipientId); // current user messaged someone
                            }
                        }
                    }
                }

                fetchSenderDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void fetchSenderDetails() {
        for (String senderId : senderIdList) {
//            Toast.makeText(ChatsActivity.this, senderId, Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com//").getReference("users")
                    .child(senderId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
//                            Toast.makeText(ChatsActivity.this, snapshot.toString(), Toast.LENGTH_SHORT).show();
                            if (user != null) {
                                senders.add(user);
                                usersAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
        }
    }
}
