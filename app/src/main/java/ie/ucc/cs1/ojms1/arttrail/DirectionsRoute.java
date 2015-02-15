package ie.ucc.cs1.ojms1.arttrail;

import com.google.gson.annotations.SerializedName;

/**
 * Created by owen on 14/02/2015.
 */
public class DirectionsRoute {
    @SerializedName("overview_polyline")
    public DirectionsPolyLine overview_polyline;
    public DirectionsRoute() {}
}
