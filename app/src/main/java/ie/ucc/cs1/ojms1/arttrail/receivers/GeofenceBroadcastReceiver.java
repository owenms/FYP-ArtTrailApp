package ie.ucc.cs1.ojms1.arttrail.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import ie.ucc.cs1.ojms1.arttrail.helpers.DatabaseHandler;
import ie.ucc.cs1.ojms1.arttrail.helpers.NotificationHandler;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private NotificationHandler notificationHandler;
    private DatabaseHandler db;

    public GeofenceBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationHandler = new NotificationHandler(context);
        db = new DatabaseHandler(context, null);
        Log.d("GEOFENCE B_CAST", "Geofence B_Cast onReceive called");
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if(event.hasError()) {
            //TODO Handle error
            //Toast.makeText(context, "No events triggered", Toast.LENGTH_SHORT).show();
            notificationHandler.createErrorNotification();
        } else {
            //Toast.makeText(context, "Geofence triggered", Toast.LENGTH_SHORT).show();
            int transType = event.getGeofenceTransition();
            if(transType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                List<Geofence> geofenceTriggered = event.getTriggeringGeofences();
                for(Geofence geofence : geofenceTriggered) {
                    Cursor cursor = db.getArtIdFromName(geofence.getRequestId());
                    int artId = cursor.getInt(cursor.getColumnIndex(db.ART_ID));
                    cursor.close();
                    notificationHandler.createGeofenceNotification(artId, geofence.getRequestId(),"Art");
                }
            }
        }
    }
}
