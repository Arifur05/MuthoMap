package com.example.muthomap.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.muthomap.R;
import com.example.muthomap.adapters.ChatAdapter;
import com.example.muthomap.models.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    Toolbar mToolbar;
    RecyclerView chatList;
    ImageView profileImage;
    TextView username, userstatus;
    EditText messageText;
    ImageButton sentButton;

    String friendsUid, myuid, friendsImage;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<Chats> chatsList;
    ChatAdapter chatAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ChatDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolber);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        chatList = findViewById(R.id.chat_recyclerView);
        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username1);
        userstatus = findViewById(R.id.onlinestatus);
        messageText = findViewById(R.id.messagebox);
        sentButton = findViewById(R.id.sendMessage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        chatList.setHasFixedSize(true);
        chatList.setLayoutManager(linearLayoutManager);




        Intent intent = getIntent();
        friendsUid = intent.getStringExtra("friendsUid");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myuid = user.getUid();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        ChatDatabase = firebaseDatabase.getReference("user").child("customers");

        Query userQuery = ChatDatabase.orderByChild("uid").equalTo(friendsUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    friendsImage = "" + ds.child("image").getValue();

                    username.setText(name);
                    try {
                        Picasso.get().load(friendsImage).into(profileImage);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_face).placeholder(R.drawable.ic_default_face).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(ChatActivity.this, "Please write something to send message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(message);
                }
            }
        });


        DatabaseReference chatRef1 = firebaseDatabase.getInstance().getReference("ChatList")
                .child(myuid).child(friendsUid);

        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef1.child("id").setValue(friendsUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference chatRef2 = firebaseDatabase.getInstance().getReference("ChatList")
                .child(friendsUid).child(myuid);

        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef2.child("id").setValue(myuid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        readMessages();
        seenMessage();
    }


    private void readMessages() {

        chatsList = new ArrayList<>();
        DatabaseReference databaseReference4535 = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference4535.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Chats chats = ds.getValue(Chats.class);
                    if (chats.getReceiver().equals(myuid) && chats.getSender().equals(friendsUid) ||
                            chats.getReceiver().equals(friendsUid) && chats.getSender().equals(myuid)) {
                        chatsList.add(chats);
                    }
                    chatAdapter = new ChatAdapter(ChatActivity.this, chatsList, friendsImage);
                    chatAdapter.notifyDataSetChanged();
                    chatList.setAdapter(chatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }

    private void sendMessage(String message) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> chats = new HashMap<>();
        chats.put("sender", myuid);
        chats.put("receiver", friendsUid);
        chats.put("message", message);
        chats.put("timestamp", timestamp);
        chats.put("isSeen", false);
        databaseReference.child("Chats").push().setValue(chats);

        messageText.getText().clear();
    }

    private void seenMessage() {

        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Chats chatmodel = ds.getValue(Chats.class);
                    if (chatmodel.getReceiver().equals(myuid) && chatmodel.getSender().equals(friendsUid)) {
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}


