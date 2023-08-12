package com.example.trinetra;//package com.example.trinetra;
//
//import android.os.Bundle;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final String TAG = "MainActivity";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        // Get the incident location from the database
//        String incidentKey = incidentRef.getKey();
//
//        FirebaseDatabase.getInstance().getReference("incidents").child("incident_id").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                double incidentLat = dataSnapshot.child("latitude").getValue(Double.class);
//                double incidentLng = dataSnapshot.child("longitude").getValue(Double.class);
//
//                // Get the users within 5 km radius of the incident
//                FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        List<String> deviceTokens = new ArrayList<>();
//
//                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                            double userLat = userSnapshot.child("latitude").getValue(Double.class);
//                            double userLng = userSnapshot.child("longitude").getValue(Double.class);
//
//                            double distance = distance(incidentLat, incidentLng, userLat, userLng);
//
//                            if (distance <= 5) {
//                                String deviceToken = userSnapshot.child("deviceToken").getValue(String.class);
//                                deviceTokens.add(deviceToken);
//                            }
//                        }
//
//                        // Send a notification to all the users within 5 km radius of the incident
//                        sendNotification(deviceTokens);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Log.e(TAG, "onCancelled", databaseError.toException());
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e(TAG, "onCancelled", databaseError.toException());
//            }
//        });
//    }
//
//    private void sendNotification(List<String> deviceTokens) {
//        for (String deviceToken : deviceTokens) {
//            FirebaseMessaging.getInstance().send(new RemoteMessage.Builder()
//                    .setToken(deviceToken)
//                    .setNotification(new RemoteMessage.Notification.Builder()
//                            .setTitle("Incident Alert")
//                            .setBody("An incident has been reported near your location.")
//                            .build())
//                    .build());
//        }
//    }
//
//    private static double distance(double lat1, double lng1, double lat2, double lng2) {
//        double earthRadius = 6371; // km
//
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLng = Math.toRadians(lng2 - lng1);
//
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
//
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//
//        return earthRadius * c;
//    }
//}
