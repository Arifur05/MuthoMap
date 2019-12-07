package com.example.muthomap.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.muthomap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView profilecard, placescard, ridecard, servicecard, socialcard;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //defining cards for the dashboard card view
        profilecard = (CardView) findViewById(R.id.profile_card);
        placescard = (CardView) findViewById(R.id.places_card);
        ridecard = (CardView) findViewById(R.id.ride_card);
        servicecard = (CardView) findViewById(R.id.service_card);
        socialcard = (CardView) findViewById(R.id.social_card);

        //setting on click listener
        profilecard.setOnClickListener(this);
        placescard.setOnClickListener(this);
        ridecard.setOnClickListener(this);
        servicecard.setOnClickListener(this);
        socialcard.setOnClickListener(this);

        checkLocationPermission();
    }



    @Override
    //navigation through layouts
    public void onClick(View view) {
        Intent i;

        switch (view.getId()) {

            case R.id.profile_card:
                i = new Intent(this, Profile.class);
                startActivity(i);
                break;
            case R.id.places_card:
                i = new Intent(this, PlacesSearch.class);
                startActivity(i);
                break;
            case R.id.ride_card:
                i = new Intent(this, RideShare.class);
                startActivity(i);
                break;
            case R.id.service_card:
                i = new Intent(this, Services.class);
                startActivity(i);
                break;
            case R.id.social_card:
                i = new Intent(this, Social.class);
                startActivity(i);
                break;
            default:
                break;

        }
    }

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        return;
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}
