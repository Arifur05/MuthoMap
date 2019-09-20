package com.example.muthomap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseauthlistener;
    private TextInputEditText mName, memail_id1, mpass, mphone;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth= FirebaseAuth.getInstance();

        //assinging with id
        register=(Button) findViewById(R.id.register_btn1);
        mName=(TextInputEditText) findViewById(R.id.name);
        memail_id1=(TextInputEditText) findViewById(R.id.email_id1);
        mpass=(TextInputEditText) findViewById(R.id.pass1);
        mphone=(TextInputEditText) findViewById(R.id.phone);


        firebaseauthlistener= new FirebaseAuth.AuthStateListener() {
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
                final  String email_id1= memail_id1.getText().toString();
                final String pass1= mpass.getText().toString();
                final  String name=mName.getText().toString().trim();
                final  String phone= mphone.getText().toString();
                if (TextUtils.isEmpty(email_id1)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pass1)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pass1.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //creating user
                mAuth.createUserWithEmailAndPassword(email_id1, pass1).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Users users= new Users(name,email_id1,phone);

                            DatabaseReference current_user_db=FirebaseDatabase.getInstance().getReference().child("user").child("customers");
                            current_user_db.setValue(users);
                        }
                    }
                });


            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseauthlistener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(firebaseauthlistener);
    }
}


//this class contains the users name, email and phone number
 class Users{
    public String name, email,phone;

    public Users(){

    }


    public Users(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
