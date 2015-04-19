package ie.ucc.cs1.ojms1.arttrail.activities;

import android.app.Activity;
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

/**
 * Class used to test beacon ranging.
 */
public class BeaconActivity extends Activity {

    private Button rangingButton;
    private Boolean isRanging = false;

    private TextView bpRSSI;
    private TextView bpDist;
    private TextView bpProx;

    private TextView imRSSI;
    private TextView imDist;
    private TextView imProx;

    private int imCount = 0;
    private int bpCount = 0;

    public static final String MY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private Region allBeacons = new Region("All", MY_UUID, null, null);
    private BeaconManager beaconManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        rangingButton = (Button) findViewById(R.id.rangeButton);
        Button clrButton = (Button) findViewById(R.id.clrButton);

        bpRSSI = (TextView) findViewById(R.id.bpRSSI);
        bpDist = (TextView) findViewById(R.id.bpDist);
        bpProx = (TextView) findViewById(R.id.bpProx);

        imRSSI = (TextView) findViewById(R.id.imRSSI);
        imDist = (TextView) findViewById(R.id.imDist);
        imProx = (TextView) findViewById(R.id.imProx);

        clrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imProx.setText("");
                imDist.setText("");
                imRSSI.setText("");

                bpProx.setText("");
                bpDist.setText("");
                bpRSSI.setText("");
            }
        });

        rangingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRanging) {
                    isRanging = true;
                    rangingButton.setText("Stop Ranging");
                    try {
                        beaconManager.startRanging(allBeacons);
                        Toast.makeText(getApplicationContext(),
                                       "Started Ranging",
                                       Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    isRanging = false;
                    rangingButton.setText("Start Ranging");
                    try {
                        beaconManager.stopRanging(allBeacons);
                        Toast.makeText(getApplicationContext(),
                                       "Stopped Ranging",
                                       Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setForegroundScanPeriod(1000, 0);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                for(Beacon b : beacons) {
                    if(b.getMajor() == 11492) {
                        int rssi = b.getRssi();
                        double dist = Utils.computeAccuracy(b);
                        Utils.Proximity prox = Utils.computeProximity(b);

                        bpProx.setText(prox.name());
                        bpDist.setText("" + dist + " //// " + ++bpCount);
                        bpRSSI.setText("" + rssi);
                    } else if(b.getMajor() == 36992) {
                        int rssi = b.getRssi();
                        double dist = Utils.computeAccuracy(b);
                        Utils.Proximity prox = Utils.computeProximity(b);

                        imProx.setText(prox.name());
                        imDist.setText("" + dist + " //// " + ++imCount);
                        imRSSI.setText("" + rssi);
                    }
                }
            }
        });
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
                Toast.makeText(getApplicationContext(),
                               "Ready when you are!",
                               Toast.LENGTH_SHORT).show();
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
