package ie.ucc.cs1.ojms1.arttrail;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;


public class BeaconActivity extends Activity {

    private TextView blueberryPie;
    private TextView mintCocktail;
    private TextView icyMarshmallow;
    private TextView beaconCount;
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

        beaconManager = new BeaconManager(getApplicationContext());
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> beacons) {
                int count = 0;
                for(Beacon beacon : beacons) {
                    count++;
                    int major = beacon.getMajor();
                    int minor = beacon.getMinor();
                    if(major == 11492 && minor == 17761) {
                        blueberryPie.setText("In range");
                    } else if(major == 24770 && minor == 63730) {
                        mintCocktail.setText("In range");
                    } else if(major == 36992 && minor == 9494) {
                        icyMarshmallow.setText("In range");
                    }
                }
                beaconCount.setText(""+count);
            }

            @Override
            public void onExitedRegion(Region region) {
                blueberryPie.setText("Left region");
                mintCocktail.setText("Left region");
                icyMarshmallow.setText("Left region");
                Toast.makeText(getApplicationContext(),"Left region", Toast.LENGTH_SHORT).show();
                beaconCount.setText("");
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
                try {
                    beaconManager.startMonitoring(allBeacons);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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
