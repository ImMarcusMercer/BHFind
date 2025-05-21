


package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private EditText inputMessage;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private DatabaseReference chatRef;
    private String chatId;
    private String currentUserId;
    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        //Header
        String username = getIntent().getStringExtra("username");
        String recipientUserId = getIntent().getStringExtra("userID");

        TextView userTextView = findViewById(R.id.chatUserTextView);
        userTextView.setText(username);

        inputMessage = findViewById(R.id.edit_message);
        recyclerView = findViewById(R.id.recycler_view);
        ImageButton sendButton = findViewById(R.id.btn_send);

        ImageView settings_button= findViewById(R.id.settingsIcon);
        TextView returnBtn = findViewById(R.id.return_from_chat);

        settings_button.setOnClickListener(task ->{
            startActivity(new Intent(this, SettingsActivity.class));
        });
        returnBtn.setOnClickListener(task ->{
            startActivity(new Intent(this, MainBHFind.class));
        });

        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        
        chatId = generateChatId(currentUserId, recipientUserId);

        chatRef = FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
                .getReference("chats").child(chatId).child("messages");


        messageAdapter = new MessageAdapter(this, messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> sendMessage(recipientUserId));

        loadMessages();
    }

    private void sendMessage(String recipient) {
        String text = inputMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            String messageId = chatRef.push().getKey();
            Message message = new Message(recipient, currentUserId, text, System.currentTimeMillis());

            if (messageId != null) {
                chatRef.child(messageId).setValue(message);
                inputMessage.setText("");
            }
        }
    }
    private String generateChatId(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
    }

    private void loadMessages() {
        chatRef = FirebaseDatabase
                .getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
                .getReference("chats")
                .child(chatId)
                .child("messages");

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Message msg = data.getValue(Message.class);
                    if (msg != null) {
                        messageList.add(msg);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
