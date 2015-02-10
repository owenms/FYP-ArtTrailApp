package ie.ucc.cs1.ojms1.arttrail;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

/**
 * Created by owen on 08/02/2015.
 */
public class DirectionsAPIHelper {
    //TODO: Handle Volley Timeout and Retry
    private static String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private final LatLng origin;
    private final LatLng destination;
    private Gson gson;
    private RequestQueue queue;

    public DirectionsAPIHelper(final LatLng origin, final LatLng destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public String getDestination() {
        return "" + destination.latitude + "," + destination.longitude;
    }

    public String getOrigin() {
        return ""+origin.latitude+","+origin.longitude;
    }

    public void createJSONRequest(final Context context) {
        queue = Volley.newRequestQueue(context);
        String url = DIRECTIONS_URL+"origin="+getOrigin()+"&"+
                        "destination="+getDestination();
        StringRequest directionsApiCall =
                new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Toast.makeText(context, s.substring(0, 10), Toast.LENGTH_SHORT).show();
                        Log.d("VOLLEY RESPONSE", s);
                        parseResponse(s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Unable to create Directions API Request", Toast.LENGTH_SHORT)
                     .show();
                Log.d("VOLLEY ERROR", error.toString());
            }
        });
        queue.add(directionsApiCall);
    }

    private void displayRoute(GoogleMap map) {

    }

    public void parseResponse(String s) {
        //TODO: Use GSON to decode JSON response and draw route to map.
        gson = new Gson();

    }
}
