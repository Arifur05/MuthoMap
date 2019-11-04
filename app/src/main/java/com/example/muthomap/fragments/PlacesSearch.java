package com.example.muthomap.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.muthomap.R;

public class PlacesSearch extends AppCompatActivity {

    private Spinner mspinner;

    private Button mSearchButton;
    String placeName;
    String placeType;

    public static final String TAG = "CurrentLocNearByPlaces";
    private static final int LOC_REQ_CODE = 1;


    protected RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_search);


        mSearchButton=findViewById(R.id.search_button);
        mspinner= findViewById(R.id.places_search_spinner);
        String[] places= {"Airport","ATM Booth","Bank","Embassy","Hospitals","Shopping Mall", "Prayer Hall","School","University","Gas Station","Movie","Tourist Place"};
        ArrayAdapter<CharSequence> placeAdapter= new ArrayAdapter<CharSequence>(this,R.layout.spinner_text,places);
        placeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        mspinner.setAdapter(placeAdapter);

      mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              placeName=mspinner.getSelectedItem().toString();
              Toast.makeText(PlacesSearch.this, "You Selected "+placeName, Toast.LENGTH_SHORT).show();
              if(placeName == "Airport"){

                  placeType= "airport";
              }
              else if (placeName=="ATM Booth"){
                  placeType="atm";
              }
              else if (placeName=="Bank"){
                  placeType="bank";
              }
              else if (placeName=="Embassy"){
                  placeType="embassy";
              }
              else if (placeName=="Hospitals"){
                  placeType="hospital";
              }
              else if (placeName=="Shopping Mall"){
                  placeType="shopping_mall";
              }
              else if (placeName=="Prayer Hall"){
                  placeType="mosque";
              }
              else if (placeName=="school"){
                  placeType="school";
              }
              else if (placeName=="University"){
                  placeType="university";
              }
              else if (placeName=="Gas Station"){
                  placeType="gas_station";
              }
              else if (placeName=="Movie"){
                  placeType="movie_theater";
              }
              else if (placeName=="Tourist Place"){
                  placeType="tourist_attraction";
              }
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {

          }
      });


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i= new Intent(PlacesSearch.this,Places.class);
                i.putExtra("key", placeType);
                startActivity(i);

            }
        });

    }

}
