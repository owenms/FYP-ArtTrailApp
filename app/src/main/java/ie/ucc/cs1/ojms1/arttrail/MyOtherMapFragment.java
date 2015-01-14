package ie.ucc.cs1.ojms1.arttrail;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyOtherMapFragment extends Fragment implements GoogleMap.OnMapLongClickListener {

    private MapView mapView;
    private GoogleMap map;

    public static MyOtherMapFragment newInstance(int position) {
        MyOtherMapFragment fragment = new MyOtherMapFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public MyOtherMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_other_map, container, false);
        //get MapView
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        //get map
        map = mapView.getMap();
        map.setOnMapLongClickListener(this);
        map.getUiSettings().setMyLocationButtonEnabled(false);

        //call MapInitializer before doing any CameraFactory calls
        MapsInitializer.initialize(this.getActivity());

        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(51.8930, -8.4930), 15);
        map.animateCamera(camUpdate);
        return view;
    }

    @Override
    public void onResume() {
        map.addMarker(new MarkerOptions().position(new LatLng(51.8930, -8.4930)).title("UCC"));
        mapView.onResume();
        super.onResume();
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

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(latLng != null) {
            map.addMarker(new MarkerOptions().position(latLng).title("User added!"));
        }
    }
}
