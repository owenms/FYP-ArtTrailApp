package ie.ucc.cs1.ojms1.arttrail.helpers;

import com.google.gson.annotations.SerializedName;

/**
 * Created by owen on 14/02/2015.
 */
public class DirectionsAPIRoute {
    @SerializedName("overview_polyline")
    public DirectionsAPIPolyLine overview_polyline;
    public DirectionsAPIRoute() {}
}
