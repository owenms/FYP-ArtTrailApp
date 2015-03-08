package ie.ucc.cs1.ojms1.arttrail.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ie.ucc.cs1.ojms1.arttrail.R;
import ie.ucc.cs1.ojms1.arttrail.activities.MainActivity;

/**
 * Created by owen on 01/03/2015.
 */
public class NotificationHandler {

    private static final int BEACON_ID = 1;
    private static final int GEOFENCE_ID = 2;
    private static final String BEACON_TITLE = "Beacon Region Entered";
    private static final String BEACON_TICKER = "You have just entered a beacon region";
    private static final String GEOFENCE_TITLE = "Geofence Entered";
    private static final String GEOFENCE_TICKER = "You have just entered a geofence";

    private NotificationCompat.Builder builder;
    private NotificationManager nm;
    private Context context;

    public NotificationHandler(Context context) {
        this.context = context;
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(context);
    }

    public void createBeaconNotifiation() {
        int id = (int)System.currentTimeMillis();
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("NOTICATION_TYPE", BEACON_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long[] vibratePat = {0,100,100,100,100,500};

        builder.setTicker(BEACON_TICKER)
               .setContentTitle(BEACON_TITLE)
               .setSmallIcon(R.drawable.ic_launcher)
               .setWhen(System.currentTimeMillis())
               .setContentIntent(pendingIntent)
               .setContentText("Sample content for intent")
               .setVibrate(vibratePat)
               .setDefaults(Notification.DEFAULT_SOUND);

        nm.notify(id, builder.build());
        Toast.makeText(context, "Noification sent", Toast.LENGTH_SHORT).show();
    }

    public void createGeofenceNotification(String requestId, String sample) {
        int id = (int)System.currentTimeMillis();
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("NOTICATION_TYPE", GEOFENCE_ID);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long[] vibratePat = {0,500,500,500};

        builder.setTicker(GEOFENCE_TICKER)
               .setContentTitle(GEOFENCE_TITLE)
               .setSmallIcon(R.drawable.ic_launcher)
               .setWhen(System.currentTimeMillis())
               .setContentText(sample + ": " + requestId)
               .setVibrate(vibratePat)
               .setDefaults(Notification.DEFAULT_SOUND)
               .setPriority(Notification.PRIORITY_MAX)
               .addAction(R.drawable.ic_info, "Art Details", pendingIntent);

        nm.notify(id, builder.build());
        Toast.makeText(context, "Noification sent", Toast.LENGTH_SHORT).show();
    }

    public void createErrorNotification() {
        builder.setTicker("Location service unavailable")
               .setSmallIcon(R.drawable.ic_launcher)
               .setContentTitle("Cannot Monitor Geofences")
               .setContentText("Cannot monitor geofences due to location provider being off")
               .setAutoCancel(true)
               .setWhen(System.currentTimeMillis())
               .setPriority(Notification.PRIORITY_MAX)
               .setDefaults(Notification.DEFAULT_ALL);
        nm.notify(0, builder.build());
    }

//    private List<PendingIntent> createCommonIntents(Context context) {
//        List<PendingIntent> pendingIntents = new ArrayList<PendingIntent>();
//        Intent viewMapIntent = new Intent(context, MainActivity.class);
//        viewMapIntent.putExtra("")
//    }
}
//    private void generateNotification(Context context, String locationId, String address) {
//        Intent notifyIntent = new Intent(context, MainActivity.class);
//        notifyIntent.putExtra("id", locationId);
//        notifyIntent.putExtra("address", address);
//        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        int id = (int) System.currentTimeMillis();
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        long[] vibratePat = {0,500,500,500};
//        Notification.Builder builder =
//                new Notification.Builder(context)
//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setContentTitle(locationId)
//                        .setContentText(address)
//                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true)
//                        .setVibrate(vibratePat)
//                        .setDefaults(Notification.DEFAULT_SOUND);
//
//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(id, builder.getNotification());
//    }
//
//    private void sendMessage(String title, String tickerText, String message) {
//        long[] vibratePat = {0,100,100,100,100,500};
//        Intent mainAct = new Intent(getApplicationContext(), MainActivity.class);
//        PendingIntent pendIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainAct, PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification.Builder builder =
//                new Notification.Builder(this)
//                        .setSmallIcon(R.drawable.ic_launcher)
//                        .setContentTitle(title)
//                        .setTicker(tickerText)
//                        .setContentText(message)
//                        .setContentIntent(pendIntent)
//                        .setAutoCancel(true)
//                        .setVibrate(vibratePat)
//                        .setDefaults(Notification.DEFAULT_SOUND);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        int id = (int) System.currentTimeMillis();
//        notificationManager.notify(id, builder.getNotification());
//    }
