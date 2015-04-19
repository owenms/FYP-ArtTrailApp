package ie.ucc.cs1.ojms1.arttrail.helpers;

import com.google.gson.annotations.SerializedName;

/**
 * Holds the overview_polyline element of the JSON string
 * Created by owen on 14/02/2015.
 */
public class DirectionsAPIRoute {
    @SerializedName("overview_polyline")
    public DirectionsAPIPolyLine overview_polyline;

    public DirectionsAPIRoute() {}
}
