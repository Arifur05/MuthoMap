package com.example.muthomap.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muthomap.R;
import com.example.muthomap.fragments.ChatActivity;
import com.example.muthomap.models.Friends;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    Context mContext;
    List<Friends> friendsList1;
    private HashMap<String,String> lastMessageMap;

    public ChatListAdapter(Context mContext, List<Friends> friendsList1) {
        this.mContext = mContext;
        this.friendsList1 = friendsList1;
        this.lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mContext).inflate(R.layout.item_chatlist,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String friendsUID= friendsList1.get(position).getUid();
        String friendsName= friendsList1.get(position).getName();
        String friendsImage= friendsList1.get(position).getImage();
        String lastMessage= lastMessageMap.get(friendsUID);

        holder.profilename.setText(friendsName);
        if (lastMessage==null || lastMessage.equals("default")){
            holder.lastmessage.setVisibility(View.GONE);
        }
        else {
            holder.lastmessage.setVisibility(View.VISIBLE);
            holder.lastmessage.setText(lastMessage);
        }
        try {
            Picasso.get().load(friendsImage).placeholder(R.drawable.ic_default_face1).into(holder.profileImageview);
        }
        catch (Exception e){
            Picasso.get().load(R.drawable.ic_default_face1).into(holder.profileImageview);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ChatActivity.class);
                intent.putExtra("friendsUid",friendsUID);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList1.size();
    }

    public void setLastMessageMap(String userId,String lastMessage){
        lastMessageMap.put(userId,lastMessage);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImageview;
        TextView profilename, lastmessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageview=itemView.findViewById(R.id.profileImageView);
            profilename=itemView.findViewById(R.id.nameTextView);
            lastmessage=itemView.findViewById(R.id.lastMessage);
        }
    }


}
