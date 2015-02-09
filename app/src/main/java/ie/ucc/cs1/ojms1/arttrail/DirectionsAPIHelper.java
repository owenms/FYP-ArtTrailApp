package ie.ucc.cs1.ojms1.arttrail;

import android.app.DownloadManager;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;

/**
 * Created by owen on 08/02/2015.
 */
public class DirectionsAPIHelper implements Response.Listener<String>{
    private static String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private final String origin;
    private final String destination;
    private Gson gson;
    private RequestQueue queue;

    private DirectionsAPIHelper(final String origin, final String destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public String getOrigin() {
        return origin;
    }

    private void createJSONRequest(final Context context) {
        queue = Volley.newRequestQueue(context);
        String url = DIRECTIONS_URL+"origin="+getOrigin()+"&"+
                        "destination="+getDestination();
        StringRequest directionsApiCall =
                new StringRequest(Request.Method.GET, url, this, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Unable to create Directions API Request", Toast.LENGTH_SHORT)
                     .show();
            }
        });
        queue.add(directionsApiCall);
    }

    private void displayRoute(GoogleMap map) {

    }

    @Override
    public void onResponse(String s) {
        //TODO: Use GSON to decode JSON response and draw route to map.
        gson = new Gson();

    }
}
