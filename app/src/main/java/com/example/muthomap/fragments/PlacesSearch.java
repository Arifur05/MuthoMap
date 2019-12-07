
package com.example.muthomap.fragments;
import com.example.muthomap.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muthomap.models.Places_list;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class PlacesSearch extends AppCompatActivity {


    private Spinner mspinner;

    private Button mSearchButton;
    String placeName;
    String placeType;



    private FusedLocationProviderClient fusedLocationProviderClient;
    double latitude, longitude;

    private RecyclerView mPlaces;

    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mPlacesData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_search);

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                latitude=location.getLatitude();
                longitude=location.getLongitude();

                Toast.makeText(PlacesSearch.this, "Your Lattitude: " +latitude + "and longitude: " +longitude, Toast.LENGTH_LONG).show();

            }
        });




        mSearchButton = findViewById(R.id.search_button);
        mspinner = findViewById(R.id.places_search_spinner);
        String[] places = {"Airport", "ATM Booth", "Embassy", "Hospitals", "Shopping Mall", "Prayer Hall", "School", "University", "Movie", "Tourist Place"};
        ArrayAdapter<CharSequence> placeAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_text, places);
        placeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        mspinner.setAdapter(placeAdapter);


        mPlaces = findViewById(R.id.placesList);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mPlaces.setLayoutManager(layoutManager);


        mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                placeName = mspinner.getSelectedItem().toString();

                if (placeName == "Airport") {

                    placeType = "airport";
                } else if (placeName == "ATM Booth") {
                    placeType = "atm";
                } else if (placeName == "Bank") {
                    placeType = "bank";
                } else if (placeName == "Embassy") {
                    placeType = "embassy";
                } else if (placeName == "Hospitals") {
                    placeType = "hospital";
                } else if (placeName == "Shopping Mall") {
                    placeType = "shopping_mall";
                } else if (placeName == "Prayer Hall") {
                    placeType = "mosque";
                } else if (placeName == "School") {
                    placeType = "school";
                } else if (placeName == "University") {
                    placeType = "university";
                } else if (placeName == "Gas Station") {
                    placeType = "gas_station";
                } else if (placeName == "Movie") {
                    placeType = "movie_theater";
                } else if (placeName == "Tourist Place") {
                    placeType = "tourist_attraction";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPlacesData = FirebaseDatabase.getInstance().getReference().child("places").child(placeType);
                FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Places_list>().setQuery(mPlacesData, Places_list.class).build();


                FirebaseRecyclerAdapter<Places_list, PlacesViewHolder> placeAdapter = new FirebaseRecyclerAdapter<Places_list, PlacesViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PlacesViewHolder holder, int position, @NonNull Places_list model) {

                        String PlacesType = getRef(position).getKey();
                        mPlacesData.child(PlacesType).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("name")) {
                                    String mPlaceName = dataSnapshot.child("name").getValue().toString();
                                    String mPlaceAddress = dataSnapshot.child("address").getValue().toString();
                                    Double mLat = (Double) dataSnapshot.child("lat").getValue();
                                    Double mLng = (Double) dataSnapshot.child("lng").getValue();



                                    if (latitude == mLat & longitude == mLng) {
                                        holder.mPlacesRadius.setText("No Result");
                                    } else {
                                        double latDistance = Math.toRadians(mLat-latitude);
                                        double lngDistance = Math.toRadians(mLng-longitude);

                                        double distance = Math.pow(Math.sin(latDistance/ 2), 2)
                                                + Math.cos(latitude) * Math.cos(mLat)
                                                * Math.pow(Math.sin(lngDistance / 2),2);
                                        double distance1 = 2 * Math.asin(Math.sqrt(distance));
                                        distance1 = Math.round(6371 * distance1);
                                        holder.mPlacesRadius.setText(distance1 + "km away");

                                    }

                                    holder.mPlacesName.setText(mPlaceName);
                                    holder.mPlacesAddress.setText(mPlaceAddress);
                                    holder.directionButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent=new Intent(v.getContext(),Places.class);
                                            intent.putExtra("latitude",mLat);
                                            intent.putExtra("longitude",mLng);
                                            startActivity(intent);
                                        }
                                    });

                                } else {
                                    Toast.makeText(PlacesSearch.this, "Your code entered into this", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }


                    @NonNull
                    @Override
                    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_places, parent, false);
                        PlacesViewHolder placesViewHolder = new PlacesViewHolder(view);
                        return placesViewHolder;
                    }
                };

                mPlaces.setAdapter(placeAdapter);

                placeAdapter.startListening();

            }
        });
    }



    public class PlacesViewHolder extends RecyclerView.ViewHolder  {

        TextView mPlacesName, mPlacesAddress, mPlacesRadius;
        Button directionButton;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            mPlacesName = itemView.findViewById(R.id.place_name);
            mPlacesAddress = itemView.findViewById(R.id.place_address);
            mPlacesRadius = itemView.findViewById(R.id.radius);
            directionButton=itemView.findViewById(R.id.direction);



        }



    }
}