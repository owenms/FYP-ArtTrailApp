package ie.ucc.cs1.ojms1.arttrail;

import android.app.Fragment;
//import android.content.Context;
//import android.location.Criteria;
import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;
//TODO: Clean up Toasts
public class DisplayMapFragment extends Fragment
        implements GoogleMap.OnMapLongClickListener, LocationListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    //Used for getting and displaying Google Map.
    private MapView mapView;
    private GoogleMap map;
    //Used for getting location coordinates.
    //private LocationManager locationManager;
    private Location myLocation;
    //private String provider;
    private static final long MIN_TIME = 60 * 1000;
    //private float MIN_DIST = 1;

    //Google Play Services provider
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocRequest;
    private boolean requestingLocation;

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
        //get LocationManager set up.
        //locationManager = ((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE));
        //set up Criteria for choosing best location provider
        //Criteria criteria = new Criteria();
        //provider = locationManager.getBestProvider(criteria,false);
        //to see provider
        //Toast.makeText(getActivity(), provider, Toast.LENGTH_SHORT).show();
        //get last location from provider
        //myLocation = locationManager.getLastKnownLocation(provider);

        //google services
        int resultCode = isGooglePlayServicesAvailable(getActivity());
        if(resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            createLocationRequest(); //create location request
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
        //center camera on location
        MapsInitializer.initialize(this.getActivity());
        //if(myLocation != null) {
        //    onLocationChanged(myLocation);
        //}
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
        //request location updates at startup
        //locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DIST, this);
        if(mGoogleApiClient.isConnected() && requestingLocation) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                                                     mLocRequest,
                                                                     this);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        //remove updates to location listener when paused
        //locationManager.removeUpdates(this);
        //Toast.makeText(getActivity(), "Updates removed", Toast.LENGTH_SHORT).show();
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
        if(latLng != null) {
            map.addMarker(new MarkerOptions().position(latLng).title("User added!"));
        }
    }

    //Google Play Services ConnectionCallbacks and OnConnectionFailed Listener  methods
    @Override
    public void onConnected(Bundle bundle) {
        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(myLocation != null) { //use previous location
            Toast.makeText(getActivity(), "From Previous Location", Toast.LENGTH_SHORT).show();
            myCameraUpdater(myLocation);
            LatLng myLoc = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            map.addMarker(new MarkerOptions().position(myLoc).title("Your Location"));
        } else { //begin requesting updates
            Toast.makeText(getActivity(), "Requesting Location Updates", Toast.LENGTH_SHORT).show();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, //G API client
                                                                     mLocRequest, //location request
                                                                     this); //listener
            requestingLocation = false; //prevent any more request locations calls.
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "No connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

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
            map.addMarker(new MarkerOptions().position(myLatLng).title(markerTitle));
        }
    }

//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        Toast.makeText(getActivity(), "Enabled new provider " + provider,
//                Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        Toast.makeText(getActivity(), "Disables provider " + provider,
//                Toast.LENGTH_SHORT).show();
//    }

    //---------My methods ----------------
    private void myCameraUpdater(Location location) {
        if(location != null) {
            //call MapInitializer before doing any CameraFactory calls
            //create LatLng from my location and center map on it.
            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(myLoc, 15);
            map.animateCamera(camUpdate);
        }
    }

    private void createLocationRequest() {
        mLocRequest = new LocationRequest();
        mLocRequest.setInterval(MIN_TIME * 2)
                   .setFastestInterval(MIN_TIME)
                   .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
