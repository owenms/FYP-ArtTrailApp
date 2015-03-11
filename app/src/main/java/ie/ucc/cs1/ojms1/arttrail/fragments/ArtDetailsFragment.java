package ie.ucc.cs1.ojms1.arttrail.fragments;


import android.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ie.ucc.cs1.ojms1.arttrail.helpers.DatabaseHandler;
import ie.ucc.cs1.ojms1.arttrail.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArtDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArtDetailsFragment extends Fragment {

    private static final String ART_PARAM = "ART_NAME";

    private TextView artName;
    private TextView artist;
    private TextView artInfo;
    private TextView shopInfo;
    private TextView shopName;
    private Button backButton;
    private Button routeButton;
    private ImageView artPicture;
    private DatabaseHandler db;

    private int artId;

    public static ArtDetailsFragment newInstance(int artName) {
        ArtDetailsFragment fragment = new ArtDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ART_PARAM, artName);
        fragment.setArguments(args);
        return fragment;
    }

    public ArtDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            artId = getArguments().getInt(ART_PARAM);
            Log.d("ART ID: ",""+artId);
        }
        db = new DatabaseHandler(getActivity().getApplicationContext(), null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_art_details, container, false);

        artName = (TextView) view.findViewById(R.id.art);
        artist = (TextView) view.findViewById(R.id.artist);
        shopName = (TextView) view.findViewById(R.id.shop_name);
        shopInfo = (TextView) view.findViewById(R.id.shop_info);
        artInfo = (TextView) view.findViewById(R.id.art_info);
        routeButton = (Button) view.findViewById(R.id.routeButton);
        backButton = (Button) view.findViewById(R.id.backButton);
        artPicture = (ImageView) view.findViewById(R.id.image);

        Cursor cursor = db.getArtDetails(artId);

        String artNameValue = cursor.getString(cursor.getColumnIndex(db.ART_NAME));
        String artistValue = cursor.getString(cursor.getColumnIndex(db.ART_ARTIST));
        String shopNameValue = cursor.getString(cursor.getColumnIndex(db.ART_LOCATION));
        String shopInfoValue = cursor.getString(cursor.getColumnIndex(db.ART_LOC_INFO));
        String artInfoValue = cursor.getString(cursor.getColumnIndex(db.ART_INFO));
        int artPictureValue = cursor.getInt(cursor.getColumnIndex(db.ART_PIC));

        artName.setText(artNameValue);
        artist.setText(artistValue);
        shopName.setText(shopNameValue);
        shopInfo.setText(shopInfoValue);
        artInfo.setText(artInfoValue);
        artPicture.setImageResource(artPictureValue);
        setRouteButton();
        cursor.close();

        return view;
    }

    private void setRouteButton() {
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, MapFragment.newInstance(artId))
                        .commit();
            }
        });
    }

}
