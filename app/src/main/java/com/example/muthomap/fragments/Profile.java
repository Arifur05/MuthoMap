package com.example.muthomap.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.muthomap.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    //views
    ImageView mprofilephoto;
    private TextView pname,pemail,pphone;
    private FloatingActionButton medit;

    //Instance of FirebaseAuth
    private FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //progress Dialog
    ProgressDialog progressDialog;

    //permissions constants
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE=300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE=400;


    //arrays of permissions to be requested
    String cameraPermissions[];
    String storagePermissions[];

    //uri of picked image
    Uri image_uri;

    //to check either profile or cover photo
    String profileOrCoverPhoto;

    //storage
    StorageReference storageReference;

    //path where imagees of profile and cover will  be stored
    String storagePath = "Users_Profile_Cover_Imgs/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mprofilephoto = findViewById(R.id.profile_photo);
        pname= findViewById(R.id.profile_name);
        pemail= findViewById(R.id.profile_email);
        pphone= findViewById(R.id.profile_mobile);
        medit= findViewById(R.id.edit);

        //Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference("user").child("customers");
        storageReference = FirebaseStorage.getInstance().getReference();

        //intitalize arrays of permissions
        cameraPermissions= new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions= new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Updating User.....");

        /*We need to get current signed in users email,name,phone etc. And we retrive user
        detail using email.*/
        /* by using OrderByChild query we will show the detail from a node whose
        key named email has value equal to currently signed in email.
        It will search all nodes, where the key matches it will get it's detail.
         */
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
       query.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               //check until getting required data
               for (DataSnapshot ds : dataSnapshot.getChildren()) {
                   //get data
                   String name = "" + ds.child("name").getValue();
                   String email = "" + ds.child("email").getValue();
                   String phone = "" + ds.child("phone").getValue();
                   String image = "" + ds.child("image").getValue();

                   //set data
                   pname.setText(name);
                   pemail.setText(email);
                   pphone.setText(phone);
                   try {
                       Picasso.get().load(image).into(mprofilephoto);
                   } catch (Exception e) {
                       Toast.makeText(Profile.this, "Error!", Toast.LENGTH_SHORT).show();
                       //if there is any exceptions while getting image then set defaults
                       Picasso.get().load(R.drawable.ic_default_face).into(mprofilephoto);
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        //handleing floating Button
             medit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowEditProfileDialog();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    /*
    Request for Storage Permission
     */
    private void requestStoragePermission(){
        //request runtime storage permission
        requestPermissions(storagePermissions,STORAGE_REQUEST_CODE);
    }

    /*
    Check if Camera permission is granted or not
     */

    private boolean checkCameraPermission(){
        /*check if storage permission is enebled or not
        return true if enebled
        return false if not
         */
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    /*
    Request for Camera Permission
     */
    private void requestCameraPermission(){
        //request runtime camera permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(cameraPermissions,CAMERA_REQUEST_CODE);
        }
    }



    private void ShowEditProfileDialog() {
        /*show dialog of options
               *1. Upload Photo
               *2. Edit Name
               *3. Edit phone
         */

        //options ro show in Dialog

        String options[ ] = {"Upload Photo","Edit Name","Edit phone"};

        //alert Dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        //title
        builder.setTitle("Choose Actions");

        //set items
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0){
                    //Edit Profile picture clicked
                    progressDialog.setMessage("Updating Profile Picture");
                    profileOrCoverPhoto = "image";
                    showImagePicDialog();
                }
                else if (which == 1){
                    //Edit Name clicked
                    progressDialog.setMessage("Updating Name");
                    showNamePhoneUpdateDialog("name");
                }
                else if (which == 2){
                    //Edit Phone clicked
                    progressDialog.setMessage("Updating Phone Number");
                    showNamePhoneUpdateDialog("phone");
                }

            }
        });

        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(final String key) {

        //custom dialog
        AlertDialog.Builder builder= new AlertDialog.Builder(Profile.this);
        builder.setTitle("Update "+ key);
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        final EditText editText = new EditText(Profile.this);
        editText.setHint("Enter "+key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);
        //add button to update
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text
                String val = editText.getText().toString().trim();
                //validate if user has entered somethig or not
                if ((!TextUtils.isEmpty(val))){
                    progressDialog.show();
                    HashMap<String, Object> results = new HashMap<>();
                    results.put(key, val);

                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Profile.this, "Updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Profile.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });

                }
                else {
                    Toast.makeText(Profile.this, "Please Enter ", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //add button to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //create and show dialog
        builder.create().show();

    }

    private void showImagePicDialog() {

        //options containing options Camera and Gallery

        String options[ ] = {"Camera","Gallery"};

        //alert Dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        //title
        builder.setTitle("Pick Image From");

        //set items
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == 0){
                   //Camera Clicked

                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                else if (which == 1){
                    //Gallery Picked

                    if (!checkStoragePermission()){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestStoragePermission();
                        }
                    }
                    else{
                        pickFromGallery();
                    }
                }


            }
        });

        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       /*This method called when user press Allow or Deny from permission request dialog
       here we will handle permissions cases (allowed or denied)
        */

       switch (requestCode){
           case CAMERA_REQUEST_CODE:{
               //picking from camera, first check if camera and storage permission allowed or not
               if (grantResults.length>0){
                   boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                   boolean writeStorageAccepted= grantResults[1]==PackageManager.PERMISSION_GRANTED;
                   if (cameraAccepted && writeStorageAccepted){
                       //permission enebled
                       pickFromCamera();
                   }
                   else {
                       Toast.makeText(this,  "Please eneble Camera and Storage permission",Toast.LENGTH_SHORT).show();
                   }
               }
           }
           break;

           case STORAGE_REQUEST_CODE:{
               //picking from storage, first check if storage permission allowed or not
               if (grantResults.length >0 ){
                   boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                   if (writeStorageAccepted){
                       //permission enebled
                       pickFromGallery();
                   }
                   else {
                       Toast.makeText(this,  "Please eneble Storage permission",Toast.LENGTH_SHORT).show();
                   }

            }

       }
       break;

    }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //This method will be called after picking image from Camera or Gallery
        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE && data.getData() != null) {
                //image is picked from gallery , get uri of image
                image_uri = data.getData();
                uploadProfilePhoto(image_uri);

            }

            if (requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE && data != null & data.getData() != null) {
                //image is picked from Camera, get uri
                uploadProfilePhoto(image_uri);

            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void uploadProfilePhoto(Uri uri) {
        //show progress
        progressDialog.show();

        /*The parameter "image_uri" contains the uri of image picked either from camera or gallery
         *Will use UID of the currently signed in user as name of the image so ther will be only one image
         * profile and one image for cover for each user */

        //path and name of image to be stored in firebase storage
        String filePathAndName = storagePath+ ""+ profileOrCoverPhoto +"_"+  user.getUid();
        StorageReference ref = storageReference.child(filePathAndName);
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();

                //check if uploading is successfull
                if (uriTask.isSuccessful()){
                    //image uploaded
                    //add url in user's database
                    HashMap<String,Object> results = new HashMap<>();

                    results.put(profileOrCoverPhoto, downloadUri.toString());
                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //url in database of user is added successfully
                                    //dismiss progress bar
                                    progressDialog.dismiss();
                                    Toast.makeText(Profile.this, "Image Updated", Toast.LENGTH_SHORT).show();
                                    

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();

                                    Toast.makeText(Profile.this, "Error Updating Image", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
                else {
                    //error
                    progressDialog.dismiss();
                    Toast.makeText(Profile.this, "Some Error Occurd", Toast.LENGTH_SHORT).show();

                }

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void pickFromCamera() {

        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //put image uri
        image_uri=Profile.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        //intent to open camera
        Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_REQUEST_CODE);
    }


    private void pickFromGallery() {

        //pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_REQUEST_CODE);
    }
    public Profile(){


    }

    /*
    Check if Storage permission is granted or not
     */
    private boolean checkStoragePermission() {
        /*check if storage permission is enebled or not
        return true if enebled
        return false if not
         */
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    }


