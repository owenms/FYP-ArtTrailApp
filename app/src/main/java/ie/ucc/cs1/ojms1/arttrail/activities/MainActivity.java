package ie.ucc.cs1.ojms1.arttrail.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import ie.ucc.cs1.ojms1.arttrail.fragments.ArtDetailsFragment;
import ie.ucc.cs1.ojms1.arttrail.fragments.ArtListFragment;
import ie.ucc.cs1.ojms1.arttrail.fragments.MapFragment;
import ie.ucc.cs1.ojms1.arttrail.fragments.NavigationDrawerFragment;
import ie.ucc.cs1.ojms1.arttrail.R;
import ie.ucc.cs1.ojms1.arttrail.services.MyBeaconService;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private boolean navDrawerFix;
    private FragmentManager fragmentManager;
    private Fragment mFragment;

    private ConnectivityManager cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navDrawerFix = false;
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationDrawerItemSelected(1);
            }
        });
        Button artButton = (Button) findViewById(R.id.artButton);
        artButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 startActivity(beaconActivity);
                onNavigationDrawerItemSelected(2);
            }
        });

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                                        (DrawerLayout) findViewById(R.id.drawer_layout));
        navDrawerFix = true;

        Intent intent = getIntent();
        int id = intent.getIntExtra("NOTIFICATION_TYPE", 0);
        intent.removeExtra("NOTIFICATION_TYPE");
        if(id == 1) { //Beacon intent
            int artId = intent.getIntExtra("ART_ID", 0);
            intent.removeExtra("ART_ID");
            if(artId != 0) {
                mFragment = ArtDetailsFragment.newInstance(artId);
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.container, mFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Intent beaconAd = new Intent(this, BeaconAdActivity.class);
                String ad = intent.getStringExtra("SHOP_AD");
                intent.removeExtra("SHOP_AD");
                beaconAd.putExtra("BEACON_AD", ad);

                startActivity(beaconAd);
            }
        } else if(id == 2) { //Geofence intent
            //Toast.makeText(this, "Geofence Intent", Toast.LENGTH_SHORT).show();
            int artId = intent.getIntExtra("ART_ID", 0);
            if(artId != 0) {
                mFragment = ArtDetailsFragment.newInstance(artId);
            } else {
                mFragment = new MapFragment();
            }
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.container, mFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            //Toast.makeText(this, "No Intent", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNetworkWarningDialog(Context context, String messageTitle, String message) {
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setTitle(messageTitle);
        alert.setMessage(message);
        alert.setButton(DialogInterface.BUTTON_NEUTRAL,"OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

    @Override
    public void onResume() { //check for internet connection and bluetooth availability
        super.onResume();
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if( ! (networkInfo != null && networkInfo.isConnectedOrConnecting())) {
            showNetworkWarningDialog(this, "No Internet Connection",
                    "Please have an internet connection before using this application");
        } else {
            BluetoothManager bManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bManager.getAdapter(); //TODO: Remove warning here
            if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                int requestId = 1;
                startActivityForResult(enableBtIntent, requestId);
            }
        }
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if(navDrawerFix) {
            boolean homePressed = false;
            switch (position) {
                case 0:
                    homePressed = true;
                    mTitle = getString(R.string.app_name);
                    break;
                case 1: //create map fragment
                    mFragment = MapFragment.newInstance(0);
                    //Toast.makeText(getApplicationContext(), "Map pressed", Toast.LENGTH_SHORT)
                    //        .show();
                    mTitle = getString(R.string.title_section2);
                    break;
                case 2: //create artlist fragment
                    mFragment = ArtListFragment.newInstance(0);
                    //Toast.makeText(getApplicationContext(), "Art pressed", Toast.LENGTH_LONG)
                    //        .show();
                    mTitle = getString(R.string.title_section3);
                    break;
//                case 3: //create stats fragment
//                    Toast.makeText(getApplicationContext(), "Stats called", Toast.LENGTH_LONG)
//                            .show();
//                    mTitle = getString(R.string.title_section4);
//                    break;
            }
            if (mFragment != null) {
                fragmentManager = getFragmentManager();
                if(homePressed) { //close current fragment
                    fragmentManager.beginTransaction()
                                   .remove(mFragment)
                                   .commit();
                } else { //open new fragment
                    fragmentManager.beginTransaction()
                                   .replace(R.id.container, mFragment)
                                   .addToBackStack(null)
                                   .commit();
                }
            }
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
//            case 4:
//                mTitle = getString(R.string.title_section4);
//                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
