package com.example.muthomap.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muthomap.R;
import com.example.muthomap.fragments.ChatActivity;
import com.example.muthomap.models.Friends;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdaper extends RecyclerView.Adapter<FriendsAdaper.MyViewHolder> {

    Context context;
    List<Friends> friendsList;

    public FriendsAdaper(Context context, List<Friends> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.item_friends, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String friendName= friendsList.get(position).getName();
        String friendEmail= friendsList.get(position).getEmail();
        String friendImage= friendsList.get(position).getImage();
        String usersUid= friendsList.get(position).getUid();

        holder.mFriendsEmail.setText(friendEmail);
        holder.mFriendsName.setText(friendName);
        try {
            Picasso.get()
                    .load(friendImage)
                    .placeholder(R.drawable.ic_default_face)
                    .resize(80,80)
                    .centerCrop()
                    .into(holder.mFriendImage);
        }
        catch (Exception e){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, ChatActivity.class);
                intent.putExtra("friendsUid",usersUid);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView mFriendImage;
        TextView mFriendsName, mFriendsEmail;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            mFriendImage= itemView.findViewById(R.id.profileImage);
            mFriendsName=itemView.findViewById(R.id.personName);
            mFriendsEmail=itemView.findViewById(R.id.friendemail);

        }
    }

}
