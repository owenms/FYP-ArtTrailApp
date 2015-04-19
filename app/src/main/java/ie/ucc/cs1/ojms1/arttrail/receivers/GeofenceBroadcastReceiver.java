package ie.ucc.cs1.ojms1.arttrail.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import ie.ucc.cs1.ojms1.arttrail.helpers.DatabaseHandler;
import ie.ucc.cs1.ojms1.arttrail.helpers.NotificationHandler;

/**
 * Used to send notifications if a geofence has been entered
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private NotificationHandler notificationHandler;
    private DatabaseHandler db;

    /**
     * Empty constructor - not used
     */
    public GeofenceBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationHandler = new NotificationHandler(context);
        db = new DatabaseHandler(context, null);

        Log.d("GEOFENCE B_CAST", "Geofence B_Cast onReceive called");
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if(event.hasError()) {
            notificationHandler.createErrorNotification();
        } else {
            int transType = event.getGeofenceTransition();
            //only send notifications based on enter events
            if(transType == Geofence.GEOFENCE_TRANSITION_ENTER) {

                List<Geofence> geofenceTriggered = event.getTriggeringGeofences();
                for(Geofence geofence : geofenceTriggered) {
                    //retrieve artID of art piece within the geofence
                    Cursor cursor = db.getArtIdFromName(geofence.getRequestId());
                    int artId = cursor.getInt(cursor.getColumnIndex(db.ART_ID));
                    cursor.close();
                    notificationHandler.createGeofenceNotification(artId,
                                                                   geofence.getRequestId(),
                                                                   "Art");
                }
            }
        }
    }
}
