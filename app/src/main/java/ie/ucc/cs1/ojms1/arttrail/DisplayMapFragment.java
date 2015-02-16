package ie.ucc.cs1.ojms1.arttrail;

import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

//TODO: Clean up Toasts
public class DisplayMapFragment extends Fragment
        implements GoogleMap.OnMapLongClickListener, LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        ResultCallback<Status>{

    //Used for getting and displaying Google Map.
    private MapView mapView;
    private GoogleMap map;

    private Location currLocation;

    //Google Play Services provider
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocRequest;
    private GeofencingRequest mGeoRequest;
    private boolean requestingLocation;
    private PendingIntent mPendIntent;

    public static DisplayMapFragment newInstance(int position) {
        DisplayMapFragment fragment = new DisplayMapFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public DisplayMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show();
        }
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
        map.addCircle(new CircleOptions().center(new LatLng(51.893040, -8.500363)) //wgb
                                         .radius(30)
                                         .visible(true));
        //center camera on location
        MapsInitializer.initialize(this.getActivity());

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

        if(mGoogleApiClient.isConnected() && requestingLocation) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                                                     mLocRequest,
                                                                     this);
            requestingLocation = false;
        } else {
            //Toast.makeText(getActivity(), "Not requesting location", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        //remove updates to location listener when paused
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        requestingLocation = true;
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocRequest,
                this);
    }

    //Google Play Services ConnectionCallbacks and OnConnectionFailed Listener  methods
    @Override
    public void onConnected(Bundle bundle) {
        currLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(currLocation != null) { //use previous location
            Toast.makeText(getActivity(), "From Previous Location", Toast.LENGTH_SHORT).show();
            myCameraUpdater(currLocation);
            LatLng myLoc = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
            map.addMarker(new MarkerOptions().position(myLoc).title("User Location"));
        } else { //begin requesting updates
            Toast.makeText(getActivity(), "Requesting Location Updates", Toast.LENGTH_SHORT).show();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, //G.API client
                                                                     mLocRequest, //location request
                                                                     this); //listener
            requestingLocation = false; //prevent any more request locations calls.
        }
        //TODO Create sample geofence
        Intent intent = new Intent(getActivity().getApplicationContext(), GeofenceHandler.class);
        mPendIntent = PendingIntent.getService(getActivity().getApplicationContext(),
                                               0,
                                               intent,
                                               PendingIntent.FLAG_UPDATE_CURRENT);

        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeoRequest, mPendIntent).setResultCallback(this);
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
            //crete LatLng and center map on location.
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng myLatLng = new LatLng(latitude, longitude);
            String markerTitle = latitude + ", " + longitude;
            myCameraUpdater(location);
            map.clear();
            map.addCircle(new CircleOptions().center(new LatLng(51.994762,-8.387729)) //wgb
                    .radius(30)
                    .visible(true));
            map.addMarker(new MarkerOptions().position(myLatLng).title(markerTitle));
        }
    }

    //---------My methods ----------------
    private void myCameraUpdater(Location location) {
        if(location != null) {
            //call MapInitializer before doing any CameraFactory calls
            //create LatLng from my location and center map on it.
            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate camUpdate = CameraUpdateFactory.newLatLng(myLoc);
            map.animateCamera(camUpdate);
        }
    }

    private void createLocationRequest() {
        mLocRequest = new LocationRequest();
        mLocRequest.setInterval(5000) //5 seconds
                   .setFastestInterval(2500) //2.5 seconds
                   .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createGeofenceRequest() {
        GeofencingRequest.Builder geofenceBuilder = new GeofencingRequest.Builder();
        Geofence geofence = new Geofence.Builder().setCircularRegion(51.893040, -8.500363, 100) //WGB UCC
                                                  .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                  .setLoiteringDelay(5 * 1000) //5 seconds

                                                  .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                                                      Geofence.GEOFENCE_TRANSITION_DWELL)
                                                  .setRequestId("WBG UCC")
                                                  .build();

        Geofence geofence1 = new Geofence.Builder().setCircularRegion(51.994762,-8.387729, 100)//home
                                                   .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                                   .setLoiteringDelay(10 * 1000)
                                                   .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                                                       Geofence.GEOFENCE_TRANSITION_DWELL)
                                                   .setRequestId("Home")
                                                   .build();
        geofenceBuilder.setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                       .addGeofence(geofence)
                       .addGeofence(geofence1);
        mGeoRequest = geofenceBuilder.build();
        Toast.makeText(getActivity(), "Geofence made", Toast.LENGTH_SHORT).show();
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
