package ie.ucc.cs1.ojms1.arttrail;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by ojms1 on 30/01/2015.
 */
public class GeofenceHandler extends IntentService {

    private static final String TRANSITION_INTENT_SERVICE = "ReceiveTransitionsIntentService";

    public GeofenceHandler() {
        super(TRANSITION_INTENT_SERVICE );
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if(event.hasError()) {
            //TODO: Handle errors here
            Toast.makeText(getApplicationContext(), "No events triggered", Toast.LENGTH_SHORT).show();
            generateNotification("Geofence error", "Error handling geofences");
        } else {
            Toast.makeText(getApplicationContext(), "Geofence triggered", Toast.LENGTH_SHORT).show();
            int transType = event.getGeofenceTransition();
            if(transType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                List<Geofence> geofenceTriggered = event.getTriggeringGeofences();
                for(Geofence geofence : geofenceTriggered) {
                    generateNotification(geofence.getRequestId(), "Geofence Enter!");
                }
            } else if (transType == Geofence.GEOFENCE_TRANSITION_DWELL){
                List<Geofence> geofenceTriggered = event.getTriggeringGeofences();
                for(Geofence geofence : geofenceTriggered) {
                    generateNotification(geofence.getRequestId(), "Geofence Dwell!");
                }
            } else {
                generateNotification("Leaving Geofence", "You have just left a geofence area");
            }
        }
    }

    private void generateNotification(String locationId, String address) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.putExtra("id", locationId);
        notifyIntent.putExtra("address", address);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        int id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long[] vibratePat = {0,500,500,500};
        Notification.Builder builder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(locationId)
                        .setContentText(address)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setVibrate(vibratePat)
                        .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.getNotification());
    }
}
