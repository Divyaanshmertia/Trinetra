package com.example.trinetra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the IncidentDetailsActivity when the notification is clicked.
        Intent incidentDetailsIntent = new Intent(context, IncidentDetailsActivity.class);
        incidentDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(incidentDetailsIntent);
    }
}