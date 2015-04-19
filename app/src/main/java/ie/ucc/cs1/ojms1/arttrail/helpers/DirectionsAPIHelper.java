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
 * Class used to send and receive Directions API requests and responses in order
 * to draw routes on the map.
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

    /**
     * Empty constructor
     */
    public DirectionsAPIHelper() {}

    /**
     * Constructor
     * @param origin Location origin
     * @param destination Location destination
     * @param map Map to draw route on
     */
    public DirectionsAPIHelper(final LatLng origin, final LatLng destination, GoogleMap map) {
        this.origin = origin;
        this.destination = destination;
        this.map = map;
    }

    /**
     * Get destination
     * @return destination in String form
     */
    public String getDestination() {
        return "" + destination.latitude + "," + destination.longitude;
    }

    /**
     * Get origin
     * @return origin in String form
     */
    public String getOrigin() {
        return ""+origin.latitude+","+origin.longitude;
    }

    /**
     * Set the origin
     * @param location location to be origin
     */
    public void setOrigin(LatLng location) {
            this.origin = location;
    }

    /**
     * Set destination
     * @param location location to be destination
     */
    public void setDestination(LatLng location) {
        this.destination = location;
    }

    /**
     * Set the map
     * @param map map to draw route on
     */
    public void setMap(GoogleMap map) {
        this.map = map;
    }

    /**
     * Send a Directions API request
     * @param context application context
     */
    public void sendDirectionsAPIRequest(final Context context) {
        //create queue and request and parse the response
        queue = Volley.newRequestQueue(context);
        String url = DIRECTIONS_URL+"origin="+getOrigin()+"&"+
                        "destination="+getDestination()+"&mode=walking";
        StringRequest directionsApiCall = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
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

    /**
     * If there are location points then the route is ready to be drawn to the map
     * @return state of route's readiness.
     */
    public boolean routeReady() {
        return points != null;
    }

    /**
     * Draw the route to the map
     */
    public void displayRoute() {
        if (points != null) {
            PolylineOptions polylineOps = new PolylineOptions();
            for(LatLng latLng : points) {
                polylineOps.add(latLng);
            }
            polylineOps.width(3).color(Color.BLUE);
            map.addPolyline(polylineOps);
        }
    }

    /**
     * Parse the JSON response from the Directions API
     * @param s JSON string to parse
     */
    public void parseResponse(String s) {
        gson = new Gson();
        DirectionsAPIResponse route = gson.fromJson(s, DirectionsAPIResponse.class);
        String encodedOverviewPolyline = route.route[0].overview_polyline.points;
        Log.d("LINE", encodedOverviewPolyline);
        points = decodeOverviewPolyline(encodedOverviewPolyline);
        displayRoute();
    }

    /**
     * Decodes the encoded polyline into list of points. Courtesy of
     * http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * @param encodedString The polyline to decode
     * @return list of LatLng point to draw to map
     */
    private List<LatLng> decodeOverviewPolyline(String encodedString) {
        List<LatLng> listOfPoints = new ArrayList<LatLng>();
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
