package com.example.trinetra;//package com.example.trinetra;
//
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.trinetra.model.Incident;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ActiveIncidentsFragment extends Fragment {
//
//    private GoogleMap mMap;
//    private Location mLastLocation;
//    private DatabaseReference mDatabase;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_active_incidents, container, false);
//
//        // Initialize Google Maps API
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        // Initialize Firebase Realtime Database
//        mDatabase = FirebaseDatabase.getInstance().getReference("incidents");
//
//        return view;
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Check if location permission is granted
//        if (ContextCompat.checkSelfPermission(getContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//            // Get user's current location
//            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
//            fusedLocationClient.getLastLocation()
//                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            if (location != null) {
//                                mLastLocation = location;
//
//                                // Query Firebase Realtime Database for active incidents within 5 km radius
//                                Query query = mDatabase.orderByChild("latitude")
//                                        .startAt(mLastLocation.getLatitude() - 0.045)
//                                        .endAt(mLastLocation.getLatitude() + 0.045);
//                                query.addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        for (DataSnapshot incidentSnapshot : dataSnapshot.getChildren()) {
//                                            Incident incident = incidentSnapshot.getValue(Incident.class);
//
//                                            // Add incident marker to map
//                                            LatLng incidentLocation = new LatLng(incident.getLatitude(), incident.getLongitude());
//                                            mMap.addMarker(new MarkerOptions()
//                                                    .position(incidentLocation)
//                                                    .title(incident.getTitle())
//                                                    .snippet(incident.getDescription()));
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        Log.e(TAG, "onCancelled", databaseError.toException());
//                                    }
//                                });
//                            }
//                        }
//                    });
//        } else {
//            // Request location permission
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_LOCATION);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                onMapReady(mMap);
//            } else {
//                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}
