package ie.ucc.cs1.ojms1.arttrail.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import ie.ucc.cs1.ojms1.arttrail.R;
import ie.ucc.cs1.ojms1.arttrail.activities.MainActivity;

/**
 * Handles all notifications. Has a method for every type of notification that could be
 * created by the app.
 * Created by owen on 01/03/2015.
 */
public class NotificationHandler {

    //IDs will be passed as arguments to the main activity to open the correct fragment/activity.
    private static final int BEACON_ID = 1;
    private static final int BEACON_SHOP_ID = 11;
    private static final int BEACON_ART_ID = 12;
    private static final int GEOFENCE_ID = 2;
    private static final int GEOFENCE_MAP_ID = 21;
    private static final int GEOFENCE_ART_ID = 22;

    private static final String BEACON_TITLE = "Beacon Region Entered";
    private static final String BEACON_TICKER = "You have just entered a beacon region";
    private static final String GEOFENCE_TITLE = "Art Location Found";
    private static final String GEOFENCE_TICKER = "You are near an art location";

    private NotificationCompat.Builder builder;
    private NotificationManager nm;
    private Context context;

    /**
     * Constructor
     * @param context Application context
     */
    public NotificationHandler(Context context) {
        this.context = context;
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context);
    }

//    public void createBeaconNotification(int artId, String ad) {
//        String message = "You are near an art exhibit. \nClick here for details of the art.";
//        int id = (int) System.currentTimeMillis();
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.putExtra("NOTIFICATION_TYPE", BEACON_ID);
//        int pendingId = 0;
//        if(artId != 0) {
//            intent.putExtra("ART_ID", artId);
//            pendingId = BEACON_ART_ID;
//            Log.d("ART_DISPLAY", ""+artId);
//        } else {
//            message = "You are inside the shop. Click here for any shop promotions";
//            intent.putExtra("SHOP_AD", ad);
//            pendingId = BEACON_SHOP_ID;
//            Log.d("SHOP AD", ad);
//        }
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, pendingId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        long[] vibratePat = {0, 100, 100, 100, 100, 500};
//
//        builder.setTicker(BEACON_TICKER)
//                .setContentTitle(BEACON_TITLE)
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setWhen(System.currentTimeMillis())
//                .setContentIntent(pendingIntent)
//                .setContentText(message)
//                .setVibrate(vibratePat)
//                .setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_SOUND);
//
//        nm.notify(id, builder.build());
//        Toast.makeText(context, "Notification sent", Toast.LENGTH_SHORT).show();
//    }

    /**
     * Create notification for when a user enters a shop
     * @param ad message to show user
     */
    public void createBeaconShopNotification(String ad) {
        int id = (int) System.currentTimeMillis();
        Intent shopIntent = new Intent(context, MainActivity.class);
        shopIntent.putExtra("NOTIFICATION_TYPE", BEACON_ID);
        shopIntent.putExtra("SHOP_AD", ad);
        String message = "You are inside the shop. Click here for any shop promotions";
        int pendingId = BEACON_SHOP_ID;
        Log.d("SHOP AD", ad);
        PendingIntent shopPendingIntent = PendingIntent.getActivity(context, pendingId, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long[] vibratePat = {0, 100, 100, 100, 100, 500};

        builder.setTicker(BEACON_TICKER)
                .setContentTitle(BEACON_TITLE)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(shopPendingIntent)
                .setContentText(message)
                .setVibrate(vibratePat)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND);

        nm.notify(id, builder.build());
        Toast.makeText(context, "Notification sent", Toast.LENGTH_SHORT).show();
    }

    /**
     * Create notification for when a user is near an art piece
     * @param artId id of the art piece
     */
    public void createBeaconArtNotification(int artId) {
        String message = "You are near an art exhibit. \nClick here for details of the art.";
        int id = (int) System.currentTimeMillis();
        Intent artIntent = new Intent(context, MainActivity.class);
        artIntent.putExtra("NOTIFICATION_TYPE", BEACON_ID);
        artIntent.putExtra("ART_ID", artId);
        int pendingId = BEACON_ART_ID;
        Log.d("ART_DISPLAY", ""+artId);
        PendingIntent artPendingIntent = PendingIntent.getActivity(context, pendingId, artIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long[] vibratePat = {0, 100, 100, 100, 100, 500};

        builder.setTicker(BEACON_TICKER)
                .setContentTitle(BEACON_TITLE)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(artPendingIntent)
                .setContentText(message)
                .setVibrate(vibratePat)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND);

        nm.notify(id, builder.build());
        Toast.makeText(context, "Shop Notification sent", Toast.LENGTH_SHORT).show();
    }

    /**
     * Create notification for when a user enters a geofence
     * @param artId id of art piece
     * @param requestId geofence id
     * @param message message to show user within notification
     */
    public void createGeofenceNotification(int artId, String requestId, String message) {
        int id = (int) System.currentTimeMillis();
        Intent viewMapIntent = new Intent(context, MainActivity.class);
        viewMapIntent.putExtra("NOTIFICATION_TYPE", GEOFENCE_ID);
        PendingIntent viewMapPendingIntent = PendingIntent.getActivity(context, GEOFENCE_MAP_ID, viewMapIntent,
                                                                       PendingIntent.FLAG_UPDATE_CURRENT);
        Intent viewArtDetailsIntent = new Intent(context, MainActivity.class);
        viewArtDetailsIntent.putExtra("NOTIFICATION_TYPE", GEOFENCE_ID);
        viewArtDetailsIntent.putExtra("ART_ID", artId);
        PendingIntent viewArtDetailsPendingIntent = PendingIntent.getActivity(context, GEOFENCE_ART_ID, viewArtDetailsIntent,
                                                                              PendingIntent.FLAG_UPDATE_CURRENT);
        long[] vibratePat = {0, 500, 500, 500};

        builder.setTicker(GEOFENCE_TICKER)
                .setContentTitle(GEOFENCE_TITLE)
                .setSmallIcon(R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentText(message + ": " + requestId)
                .setVibrate(vibratePat)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(R.drawable.ic_map_gps, "View Map", viewMapPendingIntent)
                .addAction(R.drawable.ic_info, "Art Details", viewArtDetailsPendingIntent);

        nm.notify(id, builder.build());
        Toast.makeText(context, "Art Notification sent", Toast.LENGTH_SHORT).show();
    }

    /**
     * Create notification for when a user disables location providers
     */
    public void createErrorNotification() {
        builder.setTicker("Location service unavailable")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Cannot Monitor Geofences")
                .setContentText("Cannot monitor geofences due to location provider being off.\n " +
                                "Please turn location provider back on.")
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL);
        nm.notify(0, builder.build());
    }
}