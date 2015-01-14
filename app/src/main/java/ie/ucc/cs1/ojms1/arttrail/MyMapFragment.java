package ie.ucc.cs1.ojms1.arttrail;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.MapFragment;

public class MyMapFragment extends Fragment implements GoogleMap.OnMapLongClickListener {

    private GoogleMap map;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Position of the fragment in the activity.
     * @return A new instance of fragment MyMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyMapFragment newInstance(int position) {
        MyMapFragment fragment = new MyMapFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public MyMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //TODO: Fix inflating view.
        if(container == null) {
            return  null;
        }
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        return view;
    }

    public void setUpMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();
        if(map == null) {
            Toast.makeText(getActivity(), "Problem with Map", Toast.LENGTH_SHORT ).show();
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        if(map == null) {
            setUpMap();
            map.addMarker(new MarkerOptions()
                            .position(new LatLng(0, 0))
                            .title("Center of World")
            );
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        destroyMap();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyMap();
    }

    public void destroyMap() {
        MapFragment mapFrag = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        if(mapFrag != null) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().remove(mapFrag).commit();
        }
    }
    @Override
    public void onMapLongClick(LatLng latLng) {
        String message = latLng.toString();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
             .show();
    }
}