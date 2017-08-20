package app.android.homeBase;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class testParker extends Activity {

    Button btnShowLocation;

    // GPSTracker class
    //GPSservice gps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_parker);

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object

                // Find latitude and longitude
                Geocoder gc = new Geocoder(testParker.this, Locale.getDefault());
                if (gc.isPresent()) {
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocationName("444 W. Mifflin St., 53703", 1);
                    } catch (IOException E) {
                        Log.d("GeoCoder", "Not a proper address");
                    }

                    Address fullAddress = list.get(0);

                    double lat = fullAddress.getLatitude();
                    double lng = fullAddress.getLongitude();
                    //double lat = 2.2;
                    //double lng = 3.4;
                    String gpsString = lat + " - " +lng;
                    Log.d("GeoCoder", gpsString);
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + gpsString, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Not Present", Toast.LENGTH_LONG).show();
                }


                /*
                gps = new GPSservice(testParker.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    ParseBase par = new ParseBase();
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
                */
            }
        });
    }

}