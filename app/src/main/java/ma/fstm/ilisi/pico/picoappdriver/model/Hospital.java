package ma.fstm.ilisi.pico.picoappdriver.model;
/**
 * Hospital is a business logic class
 * This class contains all information about a hospital
 *
 * @author      Yassine jout
 * @version     1.0
 */
public class Hospital {
    /**
     * @param _id hospital's ID
     */

    private String _id;
    /**
     * @param name the name of a hospital
     */
    private String name;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param coordinates object containing the latitude and longitude
     */
   // private GpsCoordinates coordinates;
    private Double latitude;
    private Double longitude;

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Hospital class constructor
     * @param _id Hospital ID

     * @param name Hospital name
     * @param coordinates hospital latitude and longitude coordinates
     */


    /**
     * Hospital class constructor
     * @param _id Hospital ID
     * @param name Hospital name
     * @param latitude hospital latitude
     * @param longitude hospital longitude
     */
    public Hospital(String _id, String name, double latitude, double longitude) {
        this._id = _id;
        this.name = name;
       // this.coordinates = new GpsCoordinates(latitude,longitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }
    /**
     * hospital ID getter
     * @return the id of the hospital
     */
    public String get_id() {
        return _id;
    }
    /**
     * hospital ID setter
     * @param _id the new hospital ID
     */
    public void set_id(String _id) {
        this._id = _id;
    }
    /**
     * hospital name getter
     * @return the name of hospital
     */
    public String getName() {
        return name;
    }
    /**
     * hospital name setter
     * @return the name of the hospital
     */
    public void setName(String name) {
        this.name = name;
    }

}
