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
import com.example.muthomap.adapters.FriendsAdaper;
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
public class FriendsFragment extends Fragment {

    RecyclerView friendsRecyclerView;
    FriendsAdaper friendsAdaper;
    List<Friends> friendsList;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsRecyclerView = view.findViewById(R.id.friendsList);
        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        friendsList = new ArrayList<>();

        getAllFriends();

        return view;
    }

    private void getAllFriends() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child("customers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //friendsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Friends friends = ds.getValue(Friends.class);

                    if (!friends.getUid().equals(firebaseUser.getUid())) {
                        friendsList.add(friends);
                    }
                    friendsAdaper = new FriendsAdaper(getActivity(), friendsList);
                    friendsRecyclerView.setAdapter(friendsAdaper);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
