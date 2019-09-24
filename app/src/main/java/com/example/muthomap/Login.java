package com.example.muthomap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseauthlistener;

    //initailizing components
    private TextInputEditText memail, mpass;
    private Button mlogin, mregister;
    private TextView mfpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //defining components as per id
        memail=(TextInputEditText) findViewById(R.id.email_id);
        mpass= (TextInputEditText) findViewById(R.id.password);
        mlogin=(Button)findViewById(R.id.login_btn);
        mregister=(Button)findViewById(R.id.reg);
        mfpass=(TextView)findViewById(R.id.tv1);

        //firebase auth
        mAuth = FirebaseAuth.getInstance();

        firebaseauthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null){
                    Intent i= new Intent(Login.this, MainActivity.class);
                    startActivity(i);

                }

            }
        };
        //onclick listener
        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email_id= memail.getText().toString();
                final String password= mpass.getText().toString();

                if (TextUtils.isEmpty(email_id)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email_id,password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            Toast.makeText(Login.this, "Log in error!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Intent intent= new Intent(Login.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });


        mregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();
            }
        });


    }



}
