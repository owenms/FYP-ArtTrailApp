package ie.ucc.cs1.ojms1.arttrail.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

import ie.ucc.cs1.ojms1.arttrail.R;
import ie.ucc.cs1.ojms1.arttrail.activities.MainActivity;
import ie.ucc.cs1.ojms1.arttrail.helpers.NotificationHandler;

public class MyBeaconService extends Service {

    private BeaconManager beaconManager;
    private Region beaconRegion = new Region("MyBeacons", "B9407F30-F5F8-466E-AFF9-25556B57FE6D", null,null);
    private NotificationHandler notificationHandler;

    public MyBeaconService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = new BeaconManager(getApplicationContext());
        notificationHandler = new NotificationHandler(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        beginMonitoring();
        Log.d("SERVICE", "Monitoring started");
        return START_STICKY;
    }

    private void beginMonitoring() {
        beaconManager.setBackgroundScanPeriod(1000, 1000);
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                notificationHandler.createBeaconNotifiation();
            }

            @Override
            public void onExitedRegion(Region region) {
                //DO nothing
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startMonitoring(beaconRegion);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
