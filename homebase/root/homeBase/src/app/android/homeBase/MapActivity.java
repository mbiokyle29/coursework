package app.android.homeBase;


import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.location.Location;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MapActivity extends HomeBaseActivity implements OnClickListener, OnMapClickListener, OnMarkerDragListener {
    private ApplicationManager mApplication;
    private GoogleMap googleMap;

    private MarkerOptions marker;
    //Make markers draggable
    LatLng pos;
    TextView tvLocInfo;
    boolean markerClicked;
    Button button;
    Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        location = new  Location("Current House");

        myIntent = getIntent();
        myClassName = "MapActivity";
        mApplication = ApplicationManager.getInstance();

        button=(Button)findViewById(R.id.save_house_button);
        button.setOnClickListener(this);


        try {
            // Loading map
            initilizeMap();
            // latitude and longitude
            double latitude = mApplication.gps.latitude;
            double longitude = mApplication.gps.longitude;

            if(mApplication.hasHouse()) {
                // add second marker if updating house
                LatLng house = new LatLng(mApplication.getHouse().getLatitude(), mApplication.getHouse().getLongitude());
                MarkerOptions oldLocation = new MarkerOptions().position(new LatLng(house.latitude, house.longitude))
                        .title("Current House Anchor")
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                googleMap.addMarker(oldLocation);

            }
            marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Location").draggable(true);

            // adding marker
            googleMap.addMarker(marker);
            pos = marker.getPosition();

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude)).zoom(15).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // DRAGGABLE MARKERS
            tvLocInfo = (TextView)findViewById(R.id.locinfo);

            //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            googleMap.setOnMapClickListener(this);
            //googleMap.setOnMapLongClickListener(this);
            googleMap.setOnMarkerDragListener(this);

            markerClicked = false;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * Location Listeners
     */
    @Override
    public void onMapClick(LatLng point) {
        //tvLocInfo.setText(point.toString());

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        googleMap.clear();

        if(mApplication.hasHouse()) {
            // add second marker if updating house
            LatLng house = new LatLng(mApplication.getHouse().getLatitude(), mApplication.getHouse().getLongitude());
            MarkerOptions oldLocation = new MarkerOptions().position(new LatLng(house.latitude, house.longitude))
                    .title("Current House Anchor")
                    .draggable(false)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            googleMap.addMarker(oldLocation);

        }
        marker = new MarkerOptions().position(point).draggable(true);
        googleMap.addMarker(marker);
        pos = marker.getPosition();

        markerClicked = false;
    }


    @Override
    public void onMarkerDrag(Marker marker) {
       // tvLocInfo.setText("Marker " + marker.getId() + " Drag@" + marker.getPosition());
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //tvLocInfo.setText("Marker " + marker.getId() + " DragEnd");
        pos = marker.getPosition();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        //tvLocInfo.setText("Marker " + marker.getId() + " DragStart");

    }


    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_house, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public  void onClick(View view){
        Log.d("Button Worked!???", pos.latitude + " latitude!!");
        ParseUser curr = ParseUser.getCurrentUser();

        //if house exists update house
        if(mApplication.hasHouse()){
            HomeBaseHouse house = mApplication.getHouse();
            house.setLatitude(pos.latitude);
            house.setLongitude(pos.longitude);
            mApplication.parse.updateHouse(house, MapActivity.this);
            mApplication.setHouse(house);
        } else {
            mApplication.parse.createHouse("KyleParkerHouses", curr.getObjectId(), pos.latitude, pos.longitude, MapActivity.this);
        }

    }


    @Override
    public void onUpdateHouseSuccess(final HomeBaseHouse house)
    {
        ParseUser.getCurrentUser().put("house", house.getId());
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    mApplication.upsertHouseData();
                    mApplication.subscribeToHouseChannel(house.getId());
                    Intent startFeed = new Intent(MapActivity.this, NewsFeedActivity.class);
                    startFeed.putExtra("caller", myClassName);
                    startActivity(startFeed);
                    finish();
                }
                else {
                    Toast.makeText(MapActivity.this, "Error: Could not update user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onUpdateHouseFailure(String e)
    {
        Toast.makeText(MapActivity.this, e, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateHouseSuccess(final HomeBaseHouse house)
    {
        ParseUser.getCurrentUser().put("house", house.getId());
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    mApplication.upsertHouseData();
                    mApplication.subscribeToHouseChannel(house.getId());
                    mApplication.setHouse(house);
                    Intent startFeed = new Intent(MapActivity.this, NewsFeedActivity.class);
                    startFeed.putExtra("caller", myClassName);
                    startActivity(startFeed);
                    finish();
                }
                else {
                    Toast.makeText(MapActivity.this, "Error: Could not update user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onCreateHouseFailure(String e)
    {
        Toast.makeText(MapActivity.this, e, Toast.LENGTH_SHORT).show();
    }
}
