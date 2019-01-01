package ma.fstm.ilisi.pico.picoappdriver.model;

import android.support.annotation.NonNull;

/**
 * GpsCoordinate class
 * This class contains the gps coordinates latitude and longitude data
 *
 * @author      Yassine jout
 * @version     1.0
 */
public class GpsCoordinates {

    @NonNull
    private double latitude ;
    @NonNull
    private double longitude ;

    /**
     * GpsCoordinates constructor
     * @param latitude the latitude coordinate to a gps location
     * @param longitude the longitude coordinate to a gps location
     */
    public GpsCoordinates(@NonNull double latitude, @NonNull double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    /**
     * Latitude getter
     * @return the latitude of a location
     */
    @NonNull
    public double getLatitude() {
        return latitude;
    }
    /**
     * Latitude setter
     * @param latitude the latitude of gps location
     */
    public void setLatitude(@NonNull double latitude) {
        this.latitude = latitude;
    }
    /**
     * Longitude getter
     * @return longitude
     */
    @NonNull
    public double getLongitude() {
        return longitude;
    }
    /**
     * Longitude setter
     * @param longitude the longitude of a location
     */
    public void setLongitude(@NonNull double longitude) {
        this.longitude = longitude;
    }
}
