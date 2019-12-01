package com.example.muthomap.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muthomap.R;
import com.example.muthomap.adapters.ChatListAdapter;
import com.example.muthomap.models.ChatListModel;
import com.example.muthomap.models.Chats;
import com.example.muthomap.models.Friends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView chatRecycler;
    List<ChatListModel> chatListModelList;
    List<Friends> friendsList;
    DatabaseReference reference;
    FirebaseUser currentUser;

    ChatListAdapter chatListAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatRecycler = view.findViewById(R.id.chatlist_recycler);
        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        chatListModelList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatListModelList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ChatListModel chatListModel = ds.getValue(ChatListModel.class);
                    chatListModelList.add(chatListModel);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void loadChats() {
        friendsList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("user").child("customers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Friends user = ds.getValue(Friends.class);
                    for (ChatListModel chatListModel : chatListModelList) {
                        if (user.getUid() != null && user.getUid().equals(chatListModel.getId())) {
                            friendsList.add(user);
                            break;
                        }
                    }
                    chatListAdapter = new ChatListAdapter(getContext(), friendsList);
                    chatRecycler.setAdapter(chatListAdapter);

                    for (int i = 0; i < friendsList.size(); i++) {
                        lastMessage(friendsList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void lastMessage(String uid) {

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chats");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Chats chats = ds.getValue(Chats.class);
                    if (chats == null) {
                        continue;
                    }
                    String sender = chats.getSender();
                    String receiver = chats.getReceiver();
                    if (sender == null || receiver == null) {
                        continue;
                    }
                    if (chats.getReceiver().equals(currentUser.getUid())
                            && chats.getSender().equals(uid) ||
                            chats.getReceiver().equals(uid)
                                    && chats.getSender().equals(currentUser.getUid())) {
                        theLastMessage = chats.getMessage();
                    }
                }
                chatListAdapter.setLastMessageMap(uid, theLastMessage);
                chatListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
