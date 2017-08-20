package app.android.homeBase;

import java.util.List;

/**
 * Created by kyle on 3/10/14.
 */
public class HomeBaseHouse {
    private String housename;
    private double latitude;
    private double longitude;
    private String id;
    private String admin;
    private List<String> members;

    public HomeBaseHouse(String housename, String admin, List<String> members, double lat, double lng)
    {
        this.housename = housename;
        this.latitude = lat;
        this.longitude = lng;
        this.admin = admin;
        this.members = members;

    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getHousename() {

        return housename;
    }

    public void setHousename(String housename) {
        this.housename = housename;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


}
