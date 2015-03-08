package ie.ucc.cs1.ojms1.arttrail.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import ie.ucc.cs1.ojms1.arttrail.R;
import ie.ucc.cs1.ojms1.arttrail.activities.MainActivity;
import ie.ucc.cs1.ojms1.arttrail.helpers.NotificationHandler;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private NotificationHandler notificationHandler;

    public GeofenceBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationHandler = new NotificationHandler(context);
        Log.d("GEOFENCE)B_CAST", "Geofence B_Cast onReceive called");
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if(event.hasError()) {
            //TODO Handle error
            Toast.makeText(context, "No events triggered", Toast.LENGTH_SHORT).show();
            notificationHandler.createErrorNotification();
        } else {
            Toast.makeText(context, "Geofence triggered", Toast.LENGTH_SHORT).show();
            int transType = event.getGeofenceTransition();
            if(transType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                List<Geofence> geofenceTriggered = event.getTriggeringGeofences();
                for(Geofence geofence : geofenceTriggered) {
                    notificationHandler.createGeofenceNotification(geofence.getRequestId(),"Geofence Enter");
                }
            } else if (transType == Geofence.GEOFENCE_TRANSITION_DWELL){
                List<Geofence> geofenceTriggered = event.getTriggeringGeofences();
                for(Geofence geofence : geofenceTriggered) {
                    notificationHandler.createGeofenceNotification(geofence.getRequestId(),"Geofence Dwell!");
                }
            }
        }
    }
}
