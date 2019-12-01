package com.example.muthomap.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muthomap.R;
import com.example.muthomap.adapters.CommentsAdapter;
import com.example.muthomap.models.Comments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    String myUID, myEmail, myName, myProfilePic, postID, pLikes, friendsImage, friendsName;


    ImageView userPicture, postImage;
    TextView userName, postTime, pTitleTV, pDescriptionTV, postLikes, comment;
    ImageButton moreButton;
    ImageButton likeButton;
    LinearLayout profileLayout;

    ProgressDialog progressDialog;

    boolean mProcessComment= false;
    boolean mProcessLike= false;

    //Comment Views
    EditText commentText;
    ImageButton sendButton;
    ImageView commentAvater;
    RecyclerView commentsRecycler;

    List<Comments> commentsList;
    CommentsAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            myUID = user.getUid();
            myEmail = user.getEmail();

        }

        userPicture = findViewById(R.id.profile_Image_View);
        postImage = findViewById(R.id.images);
        userName = findViewById(R.id.proName);
        postTime = findViewById(R.id.timeTV);
        pTitleTV = findViewById(R.id.title);
        comment=findViewById(R.id.commentsTV);
        pDescriptionTV = findViewById(R.id.description);
        postLikes = findViewById(R.id.likes);
        moreButton = findViewById(R.id.moreButton);
        likeButton = findViewById(R.id.likebtn);
        profileLayout = findViewById(R.id.profileLayout);
        commentsRecycler= findViewById(R.id.comments);

        commentText = findViewById(R.id.comment_Text);
        commentAvater = findViewById(R.id.images);
        sendButton = findViewById(R.id.button_comment);

        loadPostInfo();
        loadUserInfo();
        loadComments();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });

    }

    private void loadComments() {

        LinearLayoutManager layoutManager= new LinearLayoutManager(getApplicationContext());
        commentsRecycler.setLayoutManager(layoutManager);
        commentsList= new ArrayList<>();

        DatabaseReference commentsRef= FirebaseDatabase.getInstance().getReference("Posts").child(postID).child("Comment");
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                for (DataSnapshot comments: dataSnapshot.getChildren()){
                    Comments commentsModel= comments.getValue(Comments.class);

                    commentsList.add(commentsModel);
                    commentsAdapter= new CommentsAdapter(getApplicationContext(),commentsList);
                    commentsRecycler.setAdapter(commentsAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void likePost() {

        mProcessLike= true;
        DatabaseReference likesRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        DatabaseReference postRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessLike){
                    if (dataSnapshot.child(postID).hasChild(myUID)){
                        postRef.child(postID).child("pLikes").setValue(""+(Integer.parseInt(pLikes)-1));
                        likesRef.child(postID).child(myUID).removeValue();
                        mProcessLike= false;
                    }
                    else {
                        postRef.child(postID).child("pLikes").setValue(""+(Integer.parseInt(pLikes)+1));
                        likesRef.child(postID).child(myUID).setValue("Liked");
                        mProcessLike= false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Adding Comment.....");

        String comment= commentText.getText().toString().trim();


        if (TextUtils.isEmpty(comment)){
            Toast.makeText(this, "Comment section is empty.....", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeStamp= String.valueOf(System.currentTimeMillis());

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts").child(postID).child("Comment");

        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("commentId",timeStamp);
        hashMap.put("comment", comment);
        hashMap.put("timeStamp",timeStamp);
        hashMap.put("uID", myUID);
        hashMap.put("uEmail", myEmail);
        hashMap.put("uPIcture", myProfilePic);
        hashMap.put("uName",myName);
        reference.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(PostDetailActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();
                commentText.getText().clear();
                updateCommentCount();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateCommentCount() {
        mProcessComment= true;
        DatabaseReference ref2=FirebaseDatabase.getInstance().getReference("Posts").child(postID);
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessComment){
                    String comments= ""+ dataSnapshot.child("pComments").getValue();
                    int newCommentVal= Integer.parseInt(comments) + 1;
                    ref2.child("pComments").setValue(""+newCommentVal);
                    mProcessComment=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInfo() {

        Query myRef = FirebaseDatabase.getInstance().getReference("user").child("customers");
        myRef.orderByChild("uid").equalTo(myUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                    myName = "" + ds1.child("name").getValue();
                    myProfilePic = "" + ds1.child("image").getValue();
                    try {
                        Picasso.get().load(myProfilePic).placeholder(R.drawable.ic_default_face1).into(commentAvater);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_face1).into(commentAvater);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadPostInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = reference.orderByChild("pID").equalTo(postID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String pTitle = "" + ds.child("title").getValue();
                    String pDescription = "" + ds.child("description").getValue();
                    pLikes = "" + ds.child("pLikes").getValue();
                    String pTimeStamp = "" + ds.child("pTime").getValue();
                    String pImage = "" + ds.child("pImage").getValue();
                    friendsImage = "" + ds.child("uImage").getValue();
                    String uid = "" + ds.child("uid").getValue();
                    String uEmail = "" + ds.child("uEmail").getValue();
                    friendsName = "" + ds.child("uName").getValue();
                    String commentsCount="" + ds.child("pComments").getValue();

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTIme = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

                    pTitleTV.setText(pTitle);
                    pDescriptionTV.setText(pDescription);
                    postLikes.setText(pLikes + "Likes");
                    postTime.setText(pTIme);
                    comment.setText(commentsCount + " Comments");

                    userName.setText(friendsName);
                    if (pImage.equals("noImage")) {
                        postImage.setVisibility(View.GONE);
                    } else {
                        postImage.setVisibility(View.VISIBLE);

                        try {
                            Picasso.get().load(pImage).into(postImage);
                        } catch (Exception e) {

                        }
                    }
                    try {
                        Picasso.get().load(pImage).placeholder(R.drawable.ic_default_face1).into(postImage);
                    } catch (Exception e) {

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
