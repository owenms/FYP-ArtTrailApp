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
import android.widget.Button;
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


//TODO: Handle errors

/**
 * Class used to display the map fragment within the app.
 */
public class MapFragment extends Fragment implements
        GoogleMap.OnMapLongClickListener, LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        ResultCallback<Status> {

    //Used for getting and displaying Google Map.
    private MapView mapView;
    private GoogleMap mMap;
    private MarkerOptions userMarker;
    private List<MarkerOptions> artDisplays;

    //keep track of current location.
    private Location currLocation;

    //Google Play Services provider
    private GoogleApiClient mGoogleApiClient;

    //location and geofence request
    private LocationRequest mLocRequest;
    private GeofencingRequest mGeoRequest;
    private PendingIntent mPendIntent;

    //database related and route drawing variables
    private DirectionsAPIHelper directionsAPIHelper;
    private int artId;
    private DatabaseHandler db;
    private Cursor cursor;

    /**
     * Creates the fragment with an argument.
     * @param artId The argument that will correspond to an art piece.
     * @return The fragment with an argument.
     */
    public static MapFragment newInstance(int artId) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt("ART_ID", artId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Empty constructor - required. Use newInstance to create fragment instead.
     */
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
        int resultCode = isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if(resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity().getApplicationContext())
                                                  .addApi(LocationServices.API)
                                                  .addConnectionCallbacks(this)
                                                  .addOnConnectionFailedListener(this)
                                                  .build();
            createLocationRequest(); //create location request
            createGeofenceRequest();// create geofence request
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), resultCode);
            Toast.makeText(getActivity().getApplicationContext(),
                           "Not connected",
                           Toast.LENGTH_SHORT).show();
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

        //setup map type buttons
        Button normalMapButton = (Button) view.findViewById(R.id.normalMap);
        Button hybridMapButton = (Button) view.findViewById(R.id.hybridMap);
        normalMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        hybridMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        //get map
        mMap = mapView.getMap();
        mMap.setOnMapLongClickListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
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

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //center the map on the user's location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLocation.getLatitude(),
                           currLocation.getLongitude()),
                           15));
    }

    @Override
    public void onConnected(Bundle bundle) {
        //get last known position
        currLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //display last known position to user before requesting location updates
        if(currLocation != null) {
            Log.d("Location", currLocation.toString());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currLocation.getLatitude(),
                            currLocation.getLongitude()),
                    15));
            userMarker.position(new LatLng(currLocation.getLatitude(), currLocation.getLongitude()));
            mMap.addMarker(userMarker);
        }
        Toast.makeText(getActivity(), "Requesting Location Updates", Toast.LENGTH_SHORT).show();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                                                 mLocRequest,
                                                                 this);

        //create geofences to monitor.
        Intent intent = new Intent("ie.ucc.cs1.ojms1.arttrail.GEOFENCE_NOTIFICATION");
        mPendIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(),
                                                 0,
                                                 intent,
                                                 PendingIntent.FLAG_UPDATE_CURRENT);
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeoRequest, mPendIntent)
                                      .setResultCallback(this);

        //draw route to art exhibit
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


    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            currLocation = location;
            double latitude = currLocation.getLatitude();
            double longitude = currLocation.getLongitude();
            LatLng myLatLng = new LatLng(latitude, longitude);
            mMap.clear();
            userMarker.position(myLatLng);
            mMap.addMarker(userMarker);
            displayArtLocations();
            if(directionsAPIHelper.routeReady()) {
                directionsAPIHelper.displayRoute();
            }
        }
    }

    @Override
    public void onResult(Status status) {
        if(status.isSuccess()) {
            Toast.makeText(getActivity().getApplicationContext(),
                           "Geofences added successfully",
                           Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                           "Geofences not added. Check that location provider is turned on",
                           Toast.LENGTH_SHORT).show();
        }
    }

    //---------My methods ----------------

    /**
     * Create location request
     */
    private void createLocationRequest() {
        mLocRequest = new LocationRequest();
        mLocRequest.setInterval(1000)
                   .setFastestInterval(500)
                   .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Create geofence request
     */
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

    /**
     * Get all the art exhibits from database and create markers for them to be used in the map
     * @return list of markers to be used on the map
     */
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

    /**
     * Display the markers in the map
     */
    private void displayArtLocations() {
        for(MarkerOptions marker : artDisplays) {
            mMap.addMarker(marker);
        }
    }

    /**
     * create a route to an art exhibit
     */
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
        directionsAPIHelper.setMap(mMap);

        directionsAPIHelper.sendDirectionsAPIRequest(getActivity());
    }
}
