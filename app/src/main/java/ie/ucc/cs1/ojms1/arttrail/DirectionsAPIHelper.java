package ie.ucc.cs1.ojms1.arttrail;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by owen on 08/02/2015.
 */
public class DirectionsAPIHelper {
    @SerializedName("Directions_URL")
    private static String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    @SerializedName("origin")
    private final String origin;
    @SerializedName("destination")
    private final String destination;
//    private final String apiKey;
//    private final String travelMode;
//
//    private DirectionsAPIHelper(final String origin, final String destination,
//                                final String apiKey, final String travelMode) {
//        this.origin = origin;
//        this.destination = destination;
//        this.apiKey = apiKey;
//        this.travelMode = travelMode;
//    }

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

//    public String getApiKey() {
//        return apiKey;
//    }
//    public String getTravelMode() {
//        return travelMode;
//    }
//    private String createJSONRequest() {
////        String result = DIRECTIONS_URL+"origin="+getOrigin()+"&"+
////                        "destination="+getDestination();
//
////        return result;
//        return gson.toJson(this);
//    }
    private void displayRoute(GoogleMap map) {

    }
    public static void main(String[] args) {
        DirectionsAPIHelper dah = new DirectionsAPIHelper("Home", "Work");
        Gson gson = new Gson();
        System.out.println(gson.toJson(dah));
    }
}
