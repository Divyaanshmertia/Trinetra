package com.example.trinetra;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class GeofenceHelper {
    private static final String TAG = "GeofenceHelper";

    private Context context;
    private GeofencingClient geofencingClient;

    public GeofenceHelper(Context context) {
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    public void addGeofence(String geofenceId, double latitude, double longitude, float radius) {
        if (hasLocationPermission()) {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(geofenceId)
                    .setCircularRegion(latitude, longitude, radius)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build();

            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence)
                    .build();

            Task<Void> task = geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent());
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Geofence added successfully");
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to add geofence: " + e.getMessage());
                }
            });
        } else {
            Log.e(TAG, "Missing location permission");
        }
    }

    public void removeGeofence(String geofenceId) {
        List<String> geofenceIds = new ArrayList<>();
        geofenceIds.add(geofenceId);

        Task<Void> task = geofencingClient.removeGeofences(geofenceIds);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Geofence removed successfully");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to remove geofence: " + e.getMessage());
            }
        });
    }

    private boolean hasLocationPermission() {
        String permission = android.Manifest.permission.ACCESS_FINE_LOCATION;
        int granted = PackageManager.PERMISSION_GRANTED;
        return context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid()) == granted;
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
