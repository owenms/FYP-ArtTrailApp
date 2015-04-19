package ie.ucc.cs1.ojms1.arttrail.helpers;

import com.google.gson.annotations.SerializedName;

/**
 * Holds the routes element of the JSON string
 * Created by owen on 14/02/2015.
 */
public class DirectionsAPIResponse {
    @SerializedName("routes")
    public DirectionsAPIRoute[] route;

    public DirectionsAPIResponse() {}
}
