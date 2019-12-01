package com.example.muthomap.fragments;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.muthomap.R;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class Places extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    View mapView;
    Double latitude,longitude,lat,lng,lat1,lng1;
    Routing routing;
    private Button mroute;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProvider;
    private LocationCallback locationCallback;
    private LatLng mylocation,destination,start;

    private final float DEFAULT_ZOOM = 18;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mroute=findViewById(R.id.route);


        latitude= getIntent().getDoubleExtra("latitude",0);
        longitude= getIntent().getDoubleExtra("longitude", 1);

        mFusedLocationProvider= LocationServices.getFusedLocationProviderClient(Places.this);



    }






    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //customizing myLocation Button
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 180, 40, 180);
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(Places.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(Places.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(Places.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(Places.this, 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        destination = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        mMap.addMarker(new MarkerOptions().position(destination).title("Position"));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                routing= new Routing.Builder().travelMode(AbstractRouting.TravelMode.DRIVING)
                        .withListener(Places.this)
                        .waypoints(mylocation,destination)
                        .alternativeRoutes(true)
                        .key("AIzaSyA7P9PZHvlxNJ_whuOduejGHFdEfysg6Rg")
                        .build();

                routing.execute();
                return  false;
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

        mFusedLocationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location!=null){

                    lat=location.getLatitude();
                    lng=location.getLongitude();
                    mylocation=new LatLng(lat,lng);
                    mMap.addMarker(new MarkerOptions().position(mylocation).title("My Location"));

                }

            }
        })
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
                                        mylocation= new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                                        mMap.addMarker(new MarkerOptions().position(mylocation).title("My position"));
                                        mFusedLocationProvider.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        } else {
                            Toast.makeText(Places.this, "Unable to get your location!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }



    //Rounting
    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {
        Log.d("check","onRoutingStart");

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestroute) {

        Log.d("check","onRoutingStart");
        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLng(new LatLng(lat,lng));
        CameraUpdate zoom= CameraUpdateFactory.zoomTo(16);
        List<Polyline> polylines=new ArrayList<>();

        mMap.moveCamera(cameraUpdate);
        mMap.animateCamera(zoom);
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(R.color.colorPrimaryDark));
            polyOptions.width(5);
            polyOptions.startCap(new SquareCap());
            polyOptions.endCap(new SquareCap());
            polyOptions.jointType(JointType.ROUND);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }

        mMap.clear();
        MarkerOptions options = new MarkerOptions();
        options.position(mylocation);
        options.icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(options);

        options= new MarkerOptions();
        options.position(destination);
        options.icon(BitmapDescriptorFactory.defaultMarker());
        mMap.addMarker(options);


    }

    @Override
    public void onRoutingCancelled() {

        Log.e("check", "onRoutingCancelled");

    }
}
