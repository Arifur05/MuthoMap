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
import com.example.muthomap.models.Comments;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    Context mContext;
    List<Comments> commentsList;

    public CommentsAdapter(Context mContext, List<Comments> commentsList) {
        this.mContext = mContext;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_comments,parent,false);
        return new  MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String userId= commentsList.get(position).getuID();
        String name= commentsList.get(position).getuName();
        String email= commentsList.get(position).getuEmail();
        String image= commentsList.get(position).getuPicture();
        String cid= commentsList.get(position).getCommentId();
        String comments= commentsList.get(position).getComment();
        String timeStamp= commentsList.get(position).getTimeStamp();

        Calendar calendar= Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String pTIme= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        holder.commentersName.setText(name);
        holder.commentersComment.setText(comments);
        holder.timeView.setText(pTIme);
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_default_face1).into(holder.commentersImage);
        }
        catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class  MyViewHolder extends RecyclerView.ViewHolder{

        ImageView commentersImage;
        TextView commentersName,commentersComment, timeView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            commentersImage= itemView.findViewById(R.id.commenters_photo);
            commentersName= itemView.findViewById(R.id.commenters_Name);
            commentersComment= itemView.findViewById(R.id.commenters_comment);
            timeView= itemView.findViewById(R.id.timeTV);

        }
    }
}
