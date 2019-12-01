package com.example.muthomap.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muthomap.R;
import com.example.muthomap.fragments.PostDetailActivity;
import com.example.muthomap.models.Posts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context context;
    List<Posts> postsList;
    boolean mProcessLike= false;
    private DatabaseReference likesRef;
    private  DatabaseReference postRef1;
    String myUid;

    public PostAdapter() {
    }

    public PostAdapter(Context context, List<Posts> postsList) {
        this.context = context;
        this.postsList = postsList;
        myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef1= FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String uid=postsList.get(position).getUid();
        String uEmail=postsList.get(position).getuEmail();
        String uName=postsList.get(position).getuName();
        String uImage=postsList.get(position).getuImage();
        String pID=postsList.get(position).getpID();
        String pTitle=postsList.get(position).getTitle();
        String pDescription=postsList.get(position).getDescription();
        String pImage=postsList.get(position).getpImage();
        String PTImeStamp=postsList.get(position).getpTime();
        String pLikes= postsList.get(position).getpLikes();
        String pComments= postsList.get(position).getpComments();

        Calendar calendar= Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(PTImeStamp));
        String pTIme= DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        holder.userName.setText(uName);
        holder.postTime.setText(pTIme);
        holder.postTitle.setText(pTitle);
        holder.postDescription.setText(pDescription);
        holder.pLikes.setText(pLikes+ " Likes");
        holder.pComments.setText(pComments+ " Comments");
        setLikes( holder,pID);
        try {
            Picasso.get().load(uImage).placeholder(R.drawable.ic_default_face1).into(holder.userImage);
        }
        catch (Exception e){

        }

        if (pImage.equals("noImage")){
            holder.postImage.setVisibility(View.GONE);
        }
        else {
            try {
                Picasso.get().load(pImage).into(holder.postImage);
            }
            catch (Exception e){

            }
        }

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PostDetailActivity.class);
                intent.putExtra("postID",pID);
                context.startActivity(intent);
            }
        });
        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pLikes= Integer.parseInt(postsList.get(position).getpLikes());
                mProcessLike= true;
                final  String postIde= postsList.get(position).getpID();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mProcessLike){
                            if (dataSnapshot.child(postIde).hasChild(myUid)){
                              postRef1.child(postIde).child("pLikes").setValue(""+(pLikes-1));
                              likesRef.child(postIde).child(myUid).removeValue();
                              mProcessLike= false;
                            }
                            else {
                                postRef1.child(postIde).child("pLikes").setValue(""+(pLikes+1));
                                likesRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike= false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void setLikes(MyViewHolder Myholder, String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postKey).hasChild(myUid)){
                    Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView userImage, postImage;
        TextView userName,postTime, postTitle,postDescription, pLikes, pComments;
        ImageButton moreOptions, likes, comment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage=itemView.findViewById(R.id.profile_Image_View);
            postImage=itemView.findViewById(R.id.images);
            userName=itemView.findViewById(R.id.proName);
            postTime=itemView.findViewById(R.id.timeTV);
            postTitle=itemView.findViewById(R.id.title);
            postDescription=itemView.findViewById(R.id.description);
            pLikes=itemView.findViewById(R.id.likes);
            pComments=itemView.findViewById(R.id.commentsTV);
            moreOptions=itemView.findViewById(R.id.moreButton);
            likes=itemView.findViewById(R.id.likebtn);
            comment=itemView.findViewById(R.id.commentbtn);


        }
    }

}
