package ie.ucc.cs1.ojms1.arttrail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapActivity extends Activity implements GoogleMap.OnMapLongClickListener {

    private GoogleMap map;
    private LatLng home;
    private LatLng wgb;
    private DirectionsAPIHelper directionsAPIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        home = new LatLng(51.994762,-8.387729);
        wgb = new LatLng(51.893040, -8.500363);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.activityMap);
        map = mapFragment.getMap();
        map.setOnMapLongClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(getApplicationContext(), "Long Click", Toast.LENGTH_SHORT).show();
        directionsAPIHelper = new DirectionsAPIHelper(home, latLng, map);
        directionsAPIHelper.sendDirectionsAPIRequest(this.getApplicationContext());
    }
}
