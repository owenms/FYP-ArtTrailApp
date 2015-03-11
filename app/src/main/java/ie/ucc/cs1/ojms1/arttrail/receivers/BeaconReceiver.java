package ie.ucc.cs1.ojms1.arttrail.receivers;

import android.content.BroadcastReceiver;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import ie.ucc.cs1.ojms1.arttrail.services.MyBeaconService;

//TODO: Handle Bluetooth already turned on - check in main activity instead?
public class BeaconReceiver extends BroadcastReceiver {
    public BeaconReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent beaconService = new Intent(context, MyBeaconService.class);
        if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            if(state == BluetoothAdapter.STATE_TURNING_OFF) {
                Toast.makeText(context, "Stopping Service", Toast.LENGTH_SHORT).show();
                context.stopService(beaconService);
            } else if(state == BluetoothAdapter.STATE_ON) {
                Toast.makeText(context, "Service Created", Toast.LENGTH_SHORT).show();
                Log.d("BROADCASTRECEVIER", "BeaconReceiver started");
                context.startService(beaconService);
            }
        }
    }
}
