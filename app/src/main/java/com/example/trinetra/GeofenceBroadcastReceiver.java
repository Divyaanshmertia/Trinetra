package com.example.trinetra;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "GeofencingEvent error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();

            for (Geofence geofence : triggeredGeofences) {
                String geofenceId = geofence.getRequestId();
//                sendNotification(context, geofenceId);
            }
        }
    }

//    private void sendNotification(Context context, String geofenceId) {
//        // Create a notification channel (required for Android Oreo and above)
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("geofence_channel", "Geofence Channel", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        // Create the notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "geofence_channel")
//                .setSmallIcon(R.drawable.notification_icon)
//                .setContentTitle("Geofence Triggered")
//                .setContentText("You have entered a geofence: " + geofenceId)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        // Show the notification
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        notificationManagerCompat.notify(123, builder.build());
//    }
}
