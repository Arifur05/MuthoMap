package com.example.muthomap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    //views
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mfirebaseauthlistener;
    private TextInputEditText mName, memail_id, mpass, mphone;
    private Button register;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth= FirebaseAuth.getInstance();



        //initialize with id
        register=(Button) findViewById(R.id.register_btn1);
        mName=(TextInputEditText) findViewById(R.id.name);
        memail_id=(TextInputEditText) findViewById(R.id.email_id1);
        mpass=(TextInputEditText) findViewById(R.id.pass1);
        mphone=(TextInputEditText) findViewById(R.id.phone);

        progressDialog= new ProgressDialog(Register.this);


        mfirebaseauthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null){
                    finish();
                    Intent i= new Intent(Register.this, MainActivity.class);
                    startActivity(i);
                }

            }
        };

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //taking users email and password
                final  String email_id= memail_id.getText().toString().trim();
                final  String pass= mpass.getText().toString().trim();
                final  String phone= mphone.getText().toString().trim();
                final  String name=mName.getText().toString().trim();

                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email_id).matches()){
                    //setting error
                    memail_id.setError("Email is Invalid!");
                    memail_id.setFocusable(true);
                }
                else if (pass.length()<6){
                    //setting error and focus to password editText
                    mpass.setError("Password length must be at least 6 characters!");
                    mpass.setFocusable(true);

                }
                else if (name.isEmpty()){
                    mName.setError("Please enter your name.....");
                    mName.setFocusable(true);
                }
                else if (phone.isEmpty()){
                    mphone.setError("Please enter phone number.....");
                    mName.setFocusable(true);
                }
                else {
                    registerUser(email_id,pass,name,phone);  //register the user

                }

            }
        });

    }

    private void registerUser(final String email_id, String pass, final String name, final String phone) {

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email_id, pass)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss dialog and start registration
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //getting user email and uid from auth
                            String email = user.getEmail();
                            String uid= user.getUid();
                            //when user is registered storing user information in firebase database using Hashmap
                            HashMap<Object, String> hashMap= new HashMap<>();

                            //puting info in the hashmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", name);
                            hashMap.put("phone", phone);
                            hashMap.put("image", ""); //will add on profile activity

                            //init database
                            FirebaseDatabase database= FirebaseDatabase.getInstance();
                            //path of database
                            DatabaseReference databaseReference=database.getReference("user").child("customers");
                            //put data within hasmap in database
                            databaseReference.child(uid).setValue(hashMap);

                            Toast.makeText(Register.this, "You are successfully registered" +user.getEmail(),Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Register.this,MainActivity.class));
                                    finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }

                        }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Register.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }



}