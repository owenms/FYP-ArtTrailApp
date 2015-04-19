package ie.ucc.cs1.ojms1.arttrail.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

import ie.ucc.cs1.ojms1.arttrail.helpers.DatabaseHandler;
import ie.ucc.cs1.ojms1.arttrail.helpers.NotificationHandler;

/**
 * Monitors beacons in the background
 */
public class MyBeaconService extends Service {

    private DatabaseHandler db;

    private static final String UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private BeaconManager beaconManager;
    private Region artBeacon = new Region("Art Display", UUID, 11492,17761);
    private Region shopBeacon = new Region("Shop Beacon", UUID, 24770,63730);
    private NotificationHandler notificationHandler;

    /**
     * Empty constructor
     */
    public MyBeaconService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //initialise variables
        db = new DatabaseHandler(getApplicationContext(), null);
        beaconManager = new BeaconManager(getApplicationContext());
        notificationHandler = new NotificationHandler(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setMonitoring();
        //start beacon manager and begin monitoring
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startMonitoring(artBeacon);
                    beaconManager.startMonitoring(shopBeacon);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        Log.d("SERVICE", "Monitoring started");
        return START_STICKY;
    }

    /**
     * Set up the MonitorListener and what should happen if a region is entered
     */
    private void setMonitoring() {
        beaconManager.setBackgroundScanPeriod(1000, 1000);
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                int major = region.getMajor();
                int minor = region.getMinor();
                String[] selectionArgs = {""+major, ""+minor};

                if(region.getIdentifier().equals("Art Display")) {

                    Log.d("ART_DISPLAY", "You are near art");
                    Cursor cursor = db.getBeaconArtId(selectionArgs);
                    int artId = cursor.getInt(cursor.getColumnIndex(db.BEACON_ART_ID));
                    notificationHandler.createBeaconArtNotification(artId);
                    cursor.close();

                } else if(region.getIdentifier().equals("Shop Beacon")) {

                    Log.d("SHOP_BEACON", "You are in shop");
                    Cursor cursor = db.getBeaconAd(selectionArgs);
                    String ad = cursor.getString(cursor.getColumnIndex(db.BEACON_AD));
                    notificationHandler.createBeaconShopNotification(ad);
                    cursor.close();

                }
            }

            @Override
            public void onExitedRegion(Region region) {
                //DO nothing
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try { //stop monitoring
            beaconManager.stopMonitoring(artBeacon);
            beaconManager.stopMonitoring(shopBeacon);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.disconnect(); //disconnect the beacon manager
    }
}
