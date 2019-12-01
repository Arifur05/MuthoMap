package com.example.muthomap.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muthomap.R;
import com.example.muthomap.models.Chats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<Chats> chatsList;
    String imageUrl;

    FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<Chats> chatsList, String imageUrl) {
        this.context = context;
        this.chatsList = chatsList;
        this.imageUrl = imageUrl;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new MyViewHolder(view);
        }
        else{
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new MyViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String messages=chatsList.get(position).getMessage();
        String timeStamp= chatsList.get(position).getTimestamp();

        Calendar calendar= Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTimeformat= DateFormat.format("dd/MM/yyyyy hh:mm  aa",calendar ).toString();

        holder.messegaebody.setText(messages);
        holder.dateAndTime.setText(dateTimeformat);
        try {
            Picasso.get().load(imageUrl).into(holder.profilePichture);
        }
        catch (Exception e){
        }

        if (position == chatsList.size() - 1) {

            if (chatsList.get(position).isSeen()){
                holder.isSeen.setText("Seen");
            }
            else {
                holder.isSeen.setText("Delivered");
            }
        }
        else {
            holder.isSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatsList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView profilePichture;
        TextView messegaebody,dateAndTime,isSeen;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePichture= itemView.findViewById(R.id.profile_image);
            messegaebody=itemView.findViewById(R.id.messageTV);
            dateAndTime=itemView.findViewById(R.id.dateTime);
            isSeen=itemView.findViewById(R.id.seenOrNot);

        }
    }
}
