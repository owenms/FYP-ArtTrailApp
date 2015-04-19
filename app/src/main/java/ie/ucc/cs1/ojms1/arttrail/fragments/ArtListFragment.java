package ie.ucc.cs1.ojms1.arttrail.fragments;
import android.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ie.ucc.cs1.ojms1.arttrail.adapters.ArtListViewAdapter;
import ie.ucc.cs1.ojms1.arttrail.helpers.DatabaseHandler;
import ie.ucc.cs1.ojms1.arttrail.R;

/**
 * Displays all of the art pieces in a list
 */
public class ArtListFragment extends Fragment {

    private DatabaseHandler db;

    /**
     * Creates fragment with argument
     * @param position argument
     * @return fragment with argument
     */
    public static ArtListFragment newInstance(int position) {
        ArtListFragment fragment = new ArtListFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Empty constructor - use newInstance instead.
     */
    public ArtListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_art, container, false);

        //populate the list
        db = new DatabaseHandler(getActivity().getApplicationContext(), null);
        Cursor cursor = db.getArtTableContents();
        ArtListViewAdapter myAdapter = new ArtListViewAdapter(getActivity().getApplicationContext(),
                                       cursor);

        ListView artListView = (ListView) view.findViewById(R.id.artListView);
        artListView.setAdapter(myAdapter);

        //open ArtDetailsFragment when a user clicks on an element of the list
        artListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                int artId = cursor.getInt(cursor.getColumnIndex(db.ART_ID));
                cursor.close();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()
                  .replace(R.id.container, ArtDetailsFragment.newInstance(artId))
                  .addToBackStack(null)
                  .commit();
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}