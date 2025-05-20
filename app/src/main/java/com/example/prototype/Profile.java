package com.example.prototype;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Profile extends AppCompatActivity {

    private String currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        ImageView LLProfilePic = findViewById(R.id.llprofile_pic);
        TextView LLUsername = findViewById(R.id.llusername);
        TextView LLLocation = findViewById(R.id.lllocation);
        TextView LLEmail = findViewById(R.id.llemail);
        TextView id = findViewById(R.id.id);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Handle the case where the user is not authenticated
            finish();
            return;
        }

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference chatRef = FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
                .getReference("users").child(currentUserId);

        chatRef.get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String location = dataSnapshot.child("location").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String id1 = dataSnapshot.child("userID").getValue(String.class);

                        LLUsername.setText(username != null ? "Username: "+username : "Username: N/A");
                        LLLocation.setText(location != null ? "Location: "+location : "Location: N/A");
                        LLEmail.setText(email != null ? "Email: "+email : "Email: N/A");
                        id.setText("ID: "+id1);
                    } else {
                        LLUsername.setText("Unknown User");
                        LLLocation.setText("Unknown Location");
                        LLEmail.setText("Unknown Email");
                    }
                })
                .addOnFailureListener(e -> {
                    LLUsername.setText("Error loading profile");
                    LLLocation.setText("-");
                    LLEmail.setText("-");
                    e.printStackTrace();
                });
    }
}

//
//public class Profile extends AppCompatActivity {
//
//    private String currentUserId;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.profile_activity);
//
//        ImageView LLProfilePic = findViewById(R.id.llprofile_pic);
//        TextView LLUsername = findViewById(R.id.llusername);
//        TextView LLLocation = findViewById(R.id.lllocation);
//        TextView LLEmail = findViewById(R.id.llemail);
//
//        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//        DatabaseReference chatRef = FirebaseDatabase.getInstance("https://appdev-69-default-rtdb.europe-west1.firebasedatabase.app")
//                .getReference("users").child(currentUserId);
//
//        chatRef.get().addOnSuccessListener(dataSnapshot -> {
//            if (dataSnapshot.exists()) {
//                String username = dataSnapshot.child("username").getValue(String.class);
//                String location = dataSnapshot.child("location").getValue(String.class);
//                String email = dataSnapshot.child("email").getValue(String.class);
//
//                LLUsername.setText(username);
//                LLLocation.setText(location);
//                LLEmail.setText(email);
//
//            }
//        });
//
//    }
//
//}
