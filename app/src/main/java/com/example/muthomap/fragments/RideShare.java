package com.example.muthomap.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.muthomap.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RideShare extends FragmentActivity implements OnMapReadyCallback {

    private final float DEFAULT_ZOOM = 20;
    private GoogleMap mMap;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private List<AutocompletePrediction> predictionList;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient mPlacesClient;
    private LocationCallback locationCallback;
    private LatLng pickupLocation, destinationLocation;
    Marker pickupMarker, destinationMarker;


    //Getting driver
    private LinearLayout mDriverInfo;
    private ImageView mdriverImage;
    private TextView mDriverName, mDriverPhone, mDriverRegno;
    private RatingBar mRatingBar;

    private Boolean requestBol = false;


    //init views
    private Button mgetRide;
    private MaterialSearchBar mdestination;
    private View mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_share);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();


        mdestination = findViewById(R.id.destination);

        mgetRide = findViewById(R.id.getRide);

        mDriverInfo = findViewById(R.id.driverInfo);
        mDriverName = findViewById(R.id.driverName);
        mDriverPhone = findViewById(R.id.driverPhone);
        mDriverRegno = findViewById(R.id.driverRegno);
        mdriverImage = findViewById(R.id.driverImage);
        mRatingBar = findViewById(R.id.driverRating);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RideShare.this);
        Places.initialize(RideShare.this, "AIzaSyA7P9PZHvlxNJ_whuOduejGHFdEfysg6Rg");
        mPlacesClient = Places.createClient(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        mgetRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (requestBol) {
                    endRide();
                } else {
                    String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference dataref = FirebaseDatabase.getInstance().getReference("customer_request");
                    GeoFire geoFire = new GeoFire(dataref);
                    geoFire.setLocation(userid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                Log.e("SF", "There was an error saving the location to GeoFire: " + error);
                            } else {
                                Log.d("SF", "Location saved on server successfully!");
                            }
                        }
                    });

                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pick me here")
                            .icon(bitmapDescriptorFromVector(RideShare.this, R.drawable.ic_pickup)));

                    mgetRide.setText("Searching for Driver");

                    getClosestDriver();


                }
            }
        });


        //material Search bar code starts----> pickup location
        mdestination.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {


            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    mdestination.disableSearch();
                }

            }


        });

        //pickup location search bar

        mdestination.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("bd")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                mPlacesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {

                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestions = new ArrayList<>();
                                for (int i = 0; i < predictionList.size(); i++) {
                                    AutocompletePrediction autocompletePrediction = predictionList.get(i);
                                    suggestions.add(autocompletePrediction.getFullText(null).toString());
                                }
                                mdestination.updateLastSuggestions(suggestions);
                                if (!mdestination.isSuggestionsVisible()) {
                                    mdestination.showSuggestionsList();
                                }

                            }
                        } else {
                            Log.i("mytag", "Predictions fetching task unsuccessful");
                        }

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mdestination.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = mdestination.getLastSuggestions().get(position).toString();
                mdestination.setText(suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mdestination.clearSuggestions();
                    }
                }, 500);

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(mdestination.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    final String placeId = selectedPrediction.getPlaceId();
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);
                    FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                    mPlacesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @Override
                        public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {

                            Place place = fetchPlaceResponse.getPlace();
                            Log.i("mytag", "Found" + place.getName());
                            destinationLocation = place.getLatLng();
                            destination = place.getName();
                            if (destinationLocation != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, DEFAULT_ZOOM));
                                destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLocation)
                                        .draggable(true)
                                        .title("Destination").icon(BitmapDescriptorFactory.defaultMarker()));


                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof ApiException) {
                                ApiException apiException = (ApiException) e;
                                apiException.printStackTrace();
                                int statuscode = apiException.getStatusCode();
                                Log.i("mytag", "Place not found:  " + e.getMessage());
                                Log.i("mytag", "Staus code:  " + statuscode);
                            }
                        }
                    });
                }
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });

    }

    String destination, requestService;

    /* ----->Get Closest Driver method <-------
      |  This method is responsible for getting driver for request |
    */
    private int radius = 100;
    private Boolean driverFound = false;
    private String driverFoundID;
    GeoQuery geoQuery;

    private void getClosestDriver() {
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driverAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol) {
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                            .child("user").child("service_provider").child("uber_driver").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound) {
                                    return;
                                }
                                if (driverMap.get("service").equals(requestService)) {
                                    driverFound = true;
                                    driverFoundID = dataSnapshot.getKey();
                                    String destinationName = destination;
                                    Double destinationLat = destinationLocation.latitude;
                                    Double destinationLng = destinationLocation.longitude;

                                    DatabaseReference driverref = FirebaseDatabase.getInstance().getReference("user").child("service_provider").child("uber_driver")
                                            .child(driverFoundID).child("customer_request");
                                    String customerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("customerRideID", customerID);
                                    hashMap.put("destination", destinationName);
                                    hashMap.put("destinationLat", destinationLat);
                                    hashMap.put("destinationLng", destinationLng);

                                    driverref.updateChildren(hashMap);

                                    getDriverLocation();
                                    getDriverInfo();
                                    getHasRideEnded();
                                    mgetRide.setText("Looking for Ride........ ");

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound) {
                    radius++;
                    getClosestDriver();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }


    private DatabaseReference driveHasEndedRef;
    private ValueEventListener driveHasEndedRefListener;

    private void getHasRideEnded() {

        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("user").child("service_provider").child("uber_driver")
                .child(driverFoundID).child("customer_request").child("customerRideID");
        driveHasEndedRefListener = driveHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                } else {
                    endRide();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /* -----> This method is get Driver Info method <------
                    This method is responsible for getting Driver information
                    */
    private void getDriverInfo() {
        mDriverInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference()
                .child("user").child("service_provider").child("uber_driver").child(driverFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    if (dataSnapshot.child("name") != null) {
                        mDriverName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    if (dataSnapshot.child("phone") != null) {
                        mDriverPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                    if (dataSnapshot.child("car") != null) {
                        mDriverRegno.setText(dataSnapshot.child("car").getValue().toString());
                    }
                    if (dataSnapshot.child("profileImageUrl").getValue() != null) {
                        Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mdriverImage);
                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()) {
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if (ratingsTotal != 0) {
                        ratingsAvg = ratingSum / ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    /* ------> This is driver location method <------

            This function is responsible for fetching driver location and showing it to user

    */

    private Marker mDriverMarker;
    private DatabaseReference driverLocationRef;
    ValueEventListener driverLocationRefListener;

    private void getDriverLocation() {

        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driverWorking").child(driverFoundID).child("l");
        driveHasEndedRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationlat = 0;
                    double locationlng = 0;
                    mgetRide.setText("Driver Found");

                    if (map.get(0) != null) {
                        locationlat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationlng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatlng = new LatLng(locationlat, locationlng);
                    if (mDriverMarker != null) {
                        mDriverMarker.remove();
                    }

                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(driverLatlng.latitude);
                    loc2.setLongitude(driverLatlng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance < 100) {
                        mgetRide.setText("Driver's Here");
                    } else {
                        mgetRide.setText("Driver Found: " + String.valueOf(distance));
                    }


                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatlng).title("your Driver")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /* --------> End Ride Method <--------

            This method is responsible for ending a ride request

    */

    private void endRide() {
        requestBol = false;
        geoQuery.removeAllListeners();
        driverLocationRef.removeEventListener(driverLocationRefListener);
        driveHasEndedRef.removeEventListener(driveHasEndedRefListener);

        if (driverFoundID != null) {
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("user").child("service_provider").child("uber_driver")
                    .child(driverFoundID).child("customer_request");
            driverRef.removeValue();
            driverFoundID = null;
        }

        driverFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customer_request");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        if (pickupMarker != null) {
            pickupMarker.remove();
        }
        if (mDriverMarker != null) {
            mDriverMarker.remove();
        }
        mgetRide.setText("call Uber");

        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverRegno.setText("Destination: --");
        mdriverImage.setImageResource(R.drawable.ic_default_face);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //customizing myLocation Button
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }

        //check if the location of the device is enabled or not

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(RideShare.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(RideShare.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(RideShare.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(RideShare.this, 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                if (mdestination.isSuggestionsVisible()) {
                    mdestination.clearSuggestions();
                }
                if (mdestination.isSearchEnabled()) {
                    mdestination.disableSearch();
                }


                return false;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    private void getDeviceLocation() {

        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastLocation = task.getResult();
                            if (mLastLocation != null) {

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(5000);
                                locationRequest.setFastestInterval(2500);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastLocation = locationResult.getLastLocation();
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), DEFAULT_ZOOM));
                                        pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        } else {
                            Toast.makeText(RideShare.this, "Unable to get your location!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}
