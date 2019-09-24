package com.example.muthomap;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    //views
    private ImageView profilephoto;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        profilephoto=(ImageView) findViewById(R.id.profile_photo);
        pname=(TextView) findViewById(R.id.profile_name);
        pemail=(TextView) findViewById(R.id.profile_email);
        pphone=(TextView) findViewById(R.id.profile_mobile);
        medit=(FloatingActionButton) findViewById(R.id.edit);

        //Initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference("user").child("Customer");

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
                       Picasso.get().load(image).into(profilephoto);
                   } catch (Exception e) {
                       //if there is any exceptions while getting image then set defaults
                       Picasso.get().load(R.drawable.ic_default_face).into(profilephoto);
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

    private boolean checkStoragePermission(){
        /*check if storage permission is enebled or not
        return true if enebled
        return false if not
         */
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

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
    private void requestCameraPermission(){
        //request runtime camera permission
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
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
                    progressDialog.setMessage("Updating Profile Picture");
                    showImagePicDialog();
                }
                else if (which == 1){
                    progressDialog.setMessage("Updating Name");
                }
                else if (which == 2){
                    progressDialog.setMessage("Updating Phone Number");
                }

            }
        });

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
                        requestStoragePermission();
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //This method will be called after picking image from Camera or Gallery
        if (resultCode == RESULT_OK){

            if (resultCode == IMAGE_PICK_GALLERY_REQUEST_CODE){
                //image is picked from gallery , get uri
                image_uri = data.getData();
                uploadProfilePhoto(image_uri);

            }

            if (resultCode == IMAGE_PICK_CAMERA_REQUEST_CODE){
                //image is picked from Camera, get uri

            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void uploadProfilePhoto(Uri image_uri) {


    }

    private void pickFromCamera() {

        //Intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        //put image uri
        image_uri=this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

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
    }


