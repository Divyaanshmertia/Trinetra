package com.example.trinetra;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mMapView = view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        // Initialize the FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Initialize the Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        updateLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Check for permission to access location
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        // Enable the location layer on the map
        googleMap.setMyLocationEnabled(true);

        // Get the user's last known location
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        // Add a marker at the user's current location
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                        // Move the camera to the user's current location
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        // Save the user's current location to Firebase
                        saveLocationToFirebase(currentLocation);
                    } else {
                        Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, re-load the map
                mMapView.getMapAsync(this);
            } else {
                Toast.makeText(getActivity(), "Location permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveLocationToFirebase(LatLng location) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String address = "current_address";
            DatabaseReference userRef = mDatabase.child("users").child(userId).child(address);
            userRef.child("latitude").setValue(location.latitude);
            userRef.child("longitude").setValue(location.longitude);
        }
    }

    private void updateLocation() {
        // Get the user's current location
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        // Save the user's current location to Firebase
                        saveLocationToFirebase(new LatLng(location.getLatitude(), location.getLongitude()));
                    } else {
                        Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}