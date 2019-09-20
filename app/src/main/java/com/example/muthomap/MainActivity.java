package com.example.muthomap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView profilecard, placescard, ridecard,  servicecard, socialcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //defining cards
        profilecard=(CardView) findViewById(R.id.profile_card);
        placescard= (CardView) findViewById(R.id.places_card);
        ridecard=(CardView) findViewById(R.id.ride_card);
        servicecard=(CardView)findViewById(R.id.service_card);
        socialcard=(CardView)findViewById(R.id.social_card);

        //setting on click listener
        profilecard.setOnClickListener(this);
        placescard.setOnClickListener(this);
        ridecard.setOnClickListener(this);
        servicecard.setOnClickListener(this);
        socialcard.setOnClickListener(this);
    }

    @Override
    //navigation through layouts
    public void onClick(View view) {
        Intent i;

        switch (view.getId()){

            case R.id.profile_card: i= new Intent(this,Profile.class);
            startActivity(i);
            break;
            case R.id.places_card: i= new Intent( this, Places.class);
            startActivity(i);
            break;
            case R.id.ride_card: i= new Intent( this, Ride_Share.class);
                startActivity(i);
                break;
            case R.id.service_card: i= new Intent( this, Services.class);
                startActivity(i);
                break;
            case R.id.social_card: i= new Intent( this, Social.class);
                startActivity(i);
                break;
            default: break;

        }


    }
}
