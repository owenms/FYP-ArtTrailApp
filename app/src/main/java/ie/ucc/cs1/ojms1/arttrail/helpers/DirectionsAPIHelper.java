package ie.ucc.cs1.ojms1.arttrail.helpers;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owen on 08/02/2015.
 */
public class DirectionsAPIHelper {
    //TODO: Handle Volley Timeout and Retry
    private final static String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private LatLng origin;
    private LatLng destination;
    private Gson gson;
    private RequestQueue queue;
    public List<LatLng> points;
    private GoogleMap map;

    public DirectionsAPIHelper() {}
    public DirectionsAPIHelper(final LatLng origin, final LatLng destination, GoogleMap map) {
        this.origin = origin;
        this.destination = destination;
        this.map = map;
    }

    public String getDestination() {
        return "" + destination.latitude + "," + destination.longitude;
    }

    public String getOrigin() {
        return ""+origin.latitude+","+origin.longitude;
    }

    public void setOrigin(LatLng location) {
            this.origin = location;
    }

    public void setDestination(LatLng location) {
        this.destination = location;
    }

    public void sendDirectionsAPIRequest(final Context context) {
        queue = Volley.newRequestQueue(context);
        String url = DIRECTIONS_URL+"origin="+getOrigin()+"&"+
                        "destination="+getDestination();
        StringRequest directionsApiCall =
                new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        parseResponse(s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY ERROR", error.toString());
            }
        });
        queue.add(directionsApiCall);
    }

    private void displayRoute() {
        if (points != null) {
            PolylineOptions polylineOps = new PolylineOptions();
            for(LatLng latLng : points) {
                polylineOps.add(latLng);
            }
            polylineOps.width(3).color(Color.BLUE);
            map.addPolyline(polylineOps);
        }
    }

    public void parseResponse(String s) {
        //TODO: Use GSON to decode JSON response and draw route to map.
        gson = new Gson();
        DirectionsAPIResponse route = gson.fromJson(s, DirectionsAPIResponse.class);
        String encodedOverviewPolyline = route.route[0].overview_polyline.points;
        points = decodeOverviewPolyline(encodedOverviewPolyline);
        displayRoute();
    }

    private List<LatLng> decodeOverviewPolyline(String encodedString) {
        List<LatLng> listOfPoints= new ArrayList<LatLng>();
        //TODO:Decode
        int index = 0, len = encodedString.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encodedString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encodedString.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            listOfPoints.add(p);
        }
        return listOfPoints;
    }
}
