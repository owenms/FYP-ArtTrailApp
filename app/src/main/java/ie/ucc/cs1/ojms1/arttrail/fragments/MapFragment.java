package ie.ucc.cs1.ojms1.arttrail.fragments;

import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import ie.ucc.cs1.ojms1.arttrail.helpers.DatabaseHandler;
import ie.ucc.cs1.ojms1.arttrail.R;
import ie.ucc.cs1.ojms1.arttrail.helpers.DirectionsAPIHelper;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

//TODO: Clean up Toasts
//TODO: Handle errors
public class MapFragment extends Fragment
        implements GoogleMap.OnMapLongClickListener, LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        ResultCallback<Status>{

    //Used for getting and displaying Google Map.
    private MapView mapView;
    private GoogleMap map;
    private MarkerOptions userMarker;
    private List<MarkerOptions> artDisplays;

    private Location currLocation;

    //Google Play Services provider
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocRequest;
    private GeofencingRequest mGeoRequest;
    private boolean requestingLocation;
    private PendingIntent mPendIntent;
    private DirectionsAPIHelper directionsAPIHelper;
    private int artId;
    private DatabaseHandler db;
    private Cursor cursor;

    public static MapFragment newInstance(int artId) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt("ART_ID", artId);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            artId = getArguments().getInt("ART_ID");
        }

        directionsAPIHelper = new DirectionsAPIHelper();
        db = new DatabaseHandler(getActivity().getApplicationContext(), null);
        cursor = db.getGeofences();

        //google services
        int resultCode = isGooglePlayServicesAvailable(getActivity());
        if(resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            createLocationRequest(); //create location request
            createGeofenceRequest();// create geofence request
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), resultCode);
            Toast.makeText(getActivity().getApplicationContext(), "Not connected", Toast.LENGTH_SHORT).show();
        }
        userMarker = new MarkerOptions().title("You are here");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        //get MapView
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        //get map
        map = mapView.getMap();
        map.setOnMapLongClickListener(this);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        //center camera on location
        MapsInitializer.initialize(this.getActivity());
        artDisplays = createArtLocationMarkers();
        displayArtLocations();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        Toast.makeText(getActivity().getApplicationContext(), "On Resume", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onPause() {
        super.onPause();
        //remove updates to location listener when paused
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        cursor.close();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    //------OnMapLongClick Listener methods
    @Override
    public void onMapLongClick(LatLng latLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLocation.getLatitude(),
                                                                       currLocation.getLongitude()),
                                                            15));
    }

    //Google Play Services ConnectionCallbacks and OnConnectionFailed Listener  methods
    @Override
    public void onConnected(Bundle bundle) {
        //TODO: Fix null
        currLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(currLocation != null) {
            Log.d("Location", currLocation.toString());
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLocation.getLatitude(),
                                                                           currLocation.getLongitude()),
                                                                15));
        }

        Toast.makeText(getActivity(), "Requesting Location Updates", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, //G.API client
                                                                     mLocRequest, //location request
                                                                     this); //listener

        Intent intent = new Intent("ie.ucc.cs1.ojms1.arttrail.GEOFENCE_NOTIFICATION");
        mPendIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(),
                                                 0,
                                                 intent,
                                                 PendingIntent.FLAG_UPDATE_CURRENT);
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeoRequest, mPendIntent)
                                      .setResultCallback(this);
        if(artId != 0) {
            createRoute();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "No connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Connection suspended", Toast.LENGTH_SHORT).show();
    }

    //------LocationListener methods-------
    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            currLocation = location;
            double latitude = currLocation.getLatitude();
            double longitude = currLocation.getLongitude();
            LatLng myLatLng = new LatLng(latitude, longitude);
            map.clear();
            userMarker.position(myLatLng);
            map.addMarker(userMarker);
            displayArtLocations();
            if(directionsAPIHelper.routeReady()) {
                directionsAPIHelper.displayRoute();
            }
        }
    }

    //---------My methods ----------------
    private void createLocationRequest() {
        mLocRequest = new LocationRequest();
        mLocRequest.setInterval(2000)
                   .setFastestInterval(1000)
                   .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createGeofenceRequest() {
        GeofencingRequest.Builder geofenceBuilder = new GeofencingRequest.Builder();
        geofenceBuilder.setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER);
        cursor.moveToPosition(-1);
        while(cursor.moveToNext()) {
            double latVal = cursor.getDouble(cursor.getColumnIndex(db.GEOFENCE_LAT));
            double longVal = cursor.getDouble(cursor.getColumnIndex(db.GEOFENCE_LONG));
            float radius = cursor.getFloat(cursor.getColumnIndex(db.GEOFENCE_RADIUS));
            String artName = cursor.getString(cursor.getColumnIndex(db.ART_NAME));
            Log.d("Contents", artName + ": (" + latVal + ", " + longVal + ", " + radius + ")" );
            Geofence geofence = new Geofence.Builder().setCircularRegion(latVal, longVal, radius)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setLoiteringDelay(5 * 1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)

                    .setRequestId(artName)
                    .build();

            geofenceBuilder.addGeofence(geofence);
        }
        mGeoRequest = geofenceBuilder.build();
    }

    private List<MarkerOptions> createArtLocationMarkers() {
        List<MarkerOptions> markers = new ArrayList<MarkerOptions>();
        cursor.moveToPosition(-1);
        while(cursor.moveToNext()) {
            double latVal = cursor.getDouble(cursor.getColumnIndex(db.GEOFENCE_LAT));
            double longVal = cursor.getDouble(cursor.getColumnIndex(db.GEOFENCE_LONG));
            String artName = cursor.getString(cursor.getColumnIndex(db.ART_NAME));
            String shopName = cursor.getString(cursor.getColumnIndex(db.ART_LOCATION));

            MarkerOptions marker = new MarkerOptions().position(new LatLng(latVal, longVal))
                                                      .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag))
                                                      .title(artName)
                                                      .snippet(shopName);
            markers.add(marker);
        }
        cursor.close();
        return markers;
    }

    private void displayArtLocations() {
        for(MarkerOptions marker : artDisplays) {
            map.addMarker(marker);
        }
    }

    private void createRoute() {
        Cursor cursor = db.getLatLong(artId);
        //get destination lat and long coordinates
        double latPos = cursor.getDouble(cursor.getColumnIndex(db.GEOFENCE_LAT));
        double longPos = cursor.getDouble(cursor.getColumnIndex(db.GEOFENCE_LONG));

        Log.d("LAT: ", "" + latPos);
        Log.d("LONG: ", "" + longPos);

        LatLng destination = new LatLng(latPos, longPos);
        LatLng origin = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
        directionsAPIHelper.setDestination(destination);
        directionsAPIHelper.setOrigin(origin);
        directionsAPIHelper.setMap(map);
        //directionsAPIHelper = new DirectionsAPIHelper(origin, destination, map);
        directionsAPIHelper.sendDirectionsAPIRequest(getActivity());
    }

    @Override
    public void onResult(Status status) {
        if(status.isSuccess()) {
            Toast.makeText(getActivity(), "Geofences added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Geofences not added", Toast.LENGTH_SHORT).show();
        }
    }
}
