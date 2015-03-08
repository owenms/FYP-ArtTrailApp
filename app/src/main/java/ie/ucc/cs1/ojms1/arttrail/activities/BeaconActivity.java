package ie.ucc.cs1.ojms1.arttrail.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.List;

import ie.ucc.cs1.ojms1.arttrail.R;


public class BeaconActivity extends Activity {

    private TextView blueberryPie;
    private TextView mintCocktail;
    private TextView icyMarshmallow;
    private TextView beaconCount;
    private Button rangingButton;
    private Button monitoringButton;
    private Boolean isRanging = false;
    private Boolean isMonitoring = false;

    public static final String MY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private Region allBeacons = new Region("MyBeacons", MY_UUID, null, null);
    private BeaconManager beaconManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        blueberryPie = (TextView) findViewById(R.id.blueberryPie);
        mintCocktail = (TextView) findViewById(R.id.mintCocktail);
        icyMarshmallow = (TextView) findViewById(R.id.icyMarshmallow);
        beaconCount = (TextView) findViewById(R.id.beaconCount);
        rangingButton = (Button) findViewById(R.id.rangingButton);
        monitoringButton = (Button) findViewById(R.id.monitoringButton);

        rangingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRanging) {
                    isRanging = true;
                    rangingButton.setText("Stop Ranging");
                    try {
                        beaconManager.startRanging(allBeacons);
                        Toast.makeText(getApplicationContext(), "Started Ranging", Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    isRanging = false;
                    rangingButton.setText("Start Ranging");
                    try {
                        beaconManager.stopRanging(allBeacons);
                        Toast.makeText(getApplicationContext(), "Stopped Ranging", Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//        monitoringButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!isMonitoring) {
//                    isMonitoring = true;
//                    monitoringButton.setText("Stop Monitoring");
//                    try {
//                        beaconManager.startMonitoring(allBeacons);
//                        Toast.makeText(getApplicationContext(), "Started Monitoring", Toast.LENGTH_SHORT).show();
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    isMonitoring = false;
//                    monitoringButton.setText("Start Monitoring");
//                    try {
//                        beaconManager.stopMonitoring(allBeacons);
//                        Toast.makeText(getApplicationContext(), "Stopped Monitoring", Toast.LENGTH_SHORT).show();
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setForegroundScanPeriod(1000, 0);
        beaconManager.setBackgroundScanPeriod(5000, 5000);
//        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
//            @Override
//            public void onEnteredRegion(Region region, List<Beacon> beacons) {
//                sendMessage("Entered Beacon Region.", "Beacon region entered.", "You have just entered the region. Please enjoy your stay.");
//            }
//
//            @Override
//            public void onExitedRegion(Region region) {
//                sendMessage("Left Beacon Region.", "Region has been left.", "You have just left the region. Thank you and come again.");
//            }
//        });
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                int numBeaconsFound = 0;
                for(Beacon beacon : beacons) {
                    numBeaconsFound++;
                    int major = beacon.getMajor();
                    int minor = beacon.getMinor();
                    double accuracy = Utils.computeAccuracy(beacon);
                    if(major == 11492 && minor == 17761) {
                        blueberryPie.setText("Dist (m): " + accuracy);
                    } else if(major == 24770 && minor == 63730) {
                        mintCocktail.setText("Dist (m): " + accuracy);
                    } else if(major == 36992 && minor == 9494) {
                        icyMarshmallow.setText("Dist (m): " + accuracy);
                    }
                }
                beaconCount.setText(""+numBeaconsFound);
            }
        });
    }

    private void sendMessage(String title, String tickerText, String message) {
        long[] vibratePat = {0,100,100,100,100,500};
        Notification.Builder builder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setTicker(tickerText)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setVibrate(vibratePat)
                        .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int id = (int) System.currentTimeMillis();
        notificationManager.notify(id, builder.getNotification());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Toast.makeText(getApplicationContext(), "Ready when you are!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected  void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            beaconManager.stopRanging(allBeacons);
            beaconManager.stopMonitoring(allBeacons);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.disconnect();
    }
}
