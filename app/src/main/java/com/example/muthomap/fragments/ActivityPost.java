package com.example.muthomap.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.muthomap.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.HashMap;

public class ActivityPost extends AppCompatActivity {

    private EditText mPostTiltle, mPostDescription;
    private ImageView mpostImage;
    private Button mUpload, mPost;

    String PostTitle, PostDescription;
    String myuid;
    String name, email, image;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference postRef;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    Uri image_uri = null;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mPostTiltle = findViewById(R.id.title_post);
        mPostDescription = findViewById(R.id.description_post);
        mpostImage = findViewById(R.id.post_image);
        mUpload = findViewById(R.id.button_upload);
        mPost = findViewById(R.id.button_post);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!= null){
            myuid = user.getUid();
            email= user.getEmail();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        postRef = firebaseDatabase.getReference("user").child("customers");
        Query query = postRef.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    name = "" + ds.child("name").getValue();
                    email = "" + ds.child("email").getValue();
                    image = "" + ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDialog();

            }
        });
        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PostTitle = mPostTiltle.getText().toString();
                PostDescription = mPostDescription.getText().toString();

                if (image_uri == null) {
                    uploadData(PostTitle, PostDescription, "noImage");
                } else {
                    uploadData(PostTitle, PostDescription, String.valueOf(image_uri));
                }
            }
        });
    }

    private void uploadData(String postTitle, String postDescription, String uri) {

        progressDialog.setMessage("Posting Contents");
        progressDialog.show();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Posts/" + "post_" + timeStamp;
        if (!uri.equals("noImage")) {
            StorageReference reference = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            reference.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    String downloadUri = uriTask.getResult().toString();
                    if (uriTask.isSuccessful()) {
                        HashMap<Object, String> uploadPosts = new HashMap<>();
                        uploadPosts.put("uid", myuid);
                        uploadPosts.put("uName", name);
                        uploadPosts.put("uEmail", email);
                        uploadPosts.put("uImage", image);
                        uploadPosts.put("pID", timeStamp);
                        uploadPosts.put("title", postTitle);
                        uploadPosts.put("description", postDescription);
                        uploadPosts.put("pImage", downloadUri);
                        uploadPosts.put("pTime", timeStamp);
                        uploadPosts.put("pComments", "0");
                        uploadPosts.put("pLikes", "0");


                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts");
                        reference1.child(timeStamp).setValue(uploadPosts).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(ActivityPost.this, "PostPublished", Toast.LENGTH_LONG).show();
                                mPostTiltle.getText().clear();
                                mPostDescription.getText().clear();
                                mpostImage.setImageURI(null);
                                image_uri = null;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ActivityPost.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            });

        } else {
            HashMap<Object, String> uploadPosts = new HashMap<>();
            uploadPosts.put("uid", myuid);
            uploadPosts.put("uName", name);
            uploadPosts.put("uEmail", email);
            uploadPosts.put("upicture", image);
            uploadPosts.put("pID", timeStamp);
            uploadPosts.put("title", postTitle);
            uploadPosts.put("description", postDescription);
            uploadPosts.put("pImage", "noImage");
            uploadPosts.put("pTime", timeStamp);
            uploadPosts.put("pComments", "0");
            uploadPosts.put("pLikes", "0");

            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts");
            reference1.child(timeStamp).setValue(uploadPosts).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(ActivityPost.this, "PostPublished", Toast.LENGTH_LONG).show();
                    mPostTiltle.getText().clear();
                    mPostDescription.getText().clear();
                    mpostImage.setImageURI(null);
                    image_uri = null;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ActivityPost.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void showImagePicDialog() {

        String options[] = {"Camera", "Gallery"};

        //alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //title
        builder.setTitle("Choose Actions");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();

                    }
                }
                if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    }
                } else {

                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    }
                } else {

                }
            }
        }
    }

    private boolean checkStoragePermission() {
        /*check if storage permission is enebled or not
        return true if enebled
        return false if not
         */
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {

        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);

    }


    private boolean checkCameraPermission() {
        /*check if storage permission is enebled or not
        return true if enebled
        return false if not
         */
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {

        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);

    }

    private void pickFromGallery() {

        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_REQUEST_CODE);

    }

    private void pickFromCamera() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Descr");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE) {
                image_uri = data.getData();
                mpostImage.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE) {
                mpostImage.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}