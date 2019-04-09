package com.example.styledmap;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar mTopToolbar;


    private Button parkingToggle;
    private Button buildingToggle;
    private Button housingToggle;

    private HashMap<String, Building> buildings;
    private HashMap<String, Housing> housing;
    private HashMap<String, Parking> parking;
    private Building[] allLocations;

    @Override
    //create instance
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        buildings = new HashMap<>();
        parking= new HashMap<>();
        housing = new HashMap<>();
        //adjust size later
        allLocations = new Building[20];

        //Below code to add Toast to toggle buttons.
        parkingToggle = findViewById(R.id.parking_toggle);
        parkingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Building b : buildings.values()){
                    b.getShape().setVisible(false);
                }
                for(Housing h : housing.values()){
                    h.getShape().setVisible(false);
                }
                for (Parking g : parking.values()) {
                    if (g.getShape().isVisible()) {
                        g.getShape().setVisible(false);
                    } else {
                        g.getShape().setVisible(true);
                    }
                }
            }
        });

        buildingToggle = findViewById(R.id.building_toggle);
        buildingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Parking b : parking.values()){
                    b.getShape().setVisible(false);
                }
                for(Housing h : housing.values()){
                    h.getShape().setVisible(false);
                }
                //The following loop toggles all the polygons of type "building" into visibility and out.

                for (Building g : buildings.values()) {
                    if (g.getShape().isVisible()) {
                        g.getShape().setVisible(false);
                    } else {
                        g.getShape().setVisible(true);
                    }
                }
            }
        });

        housingToggle = findViewById(R.id.housing_toggle);
        housingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Building b : buildings.values()){
                    b.getShape().setVisible(false);
                }
                for(Parking h : parking.values()){
                    h.getShape().setVisible(false);
                }
                for (Housing g : housing.values()) {
                    if (g.getShape().isVisible()) {
                        g.getShape().setVisible(false);
                    } else {
                        g.getShape().setVisible(true);
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            Toast.makeText(MapsActivity.this, "Insert drop-down menu here", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }

        // Add a marker in the CS lab and move the camera
        LatLng meysci = new LatLng(41.869559, -88.096015);
        mMap.addMarker(new MarkerOptions().position(meysci).title("Meyer Science Center"));

        // Create a LatLngBounds that includes the Campus of Wheaton.
        LatLngBounds WHEATON = new LatLngBounds(
                new LatLng(41.864417, -88.103536), new LatLng(41.873451, -88.088258));
        // Constrain the camera target to the Wheaton.
        mMap.setLatLngBoundsForCameraTarget(WHEATON);

        // Set a preference for minimum and maximum zoom.
        mMap.setMinZoomPreference(15.5f);
        mMap.setMaxZoomPreference(18.5f);

        // Set center point for the map at startup
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(WHEATON.getCenter(), 15.5f));

        locationSetup(mMap);
    }

    private void locationSetup(GoogleMap mMap){

        int highlight = Color.parseColor("#ff9326");
        PolygonOptions polyOpt;
        Polygon poly;
        Building insert;

        /*
        *the first polygon for testing and demo (MeySci)
        * replace "examplePoly" with new name for building
        */
        //Defining coordinates of the polygon
        polyOpt = new PolygonOptions().add(new LatLng(41.869850, -88.096759), new LatLng(41.869851, -88.095732), new LatLng(41.869282, -88.095713), new LatLng(41.869283, -88.096073), new LatLng(41.869634, -88.096077), new LatLng(41.869653, -88.096746),new LatLng(41.869850, -88.096759));
        //Do not adjust the following 4 lines
        polyOpt.strokeWidth(0);
        polyOpt.fillColor(highlight);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        insert = new Building(poly, "Meyer Science Center");  //change name
        buildings.put("Meyer",insert);  //change key
        allLocations[0]= insert;


        polyOpt = new PolygonOptions().add(new LatLng(41.869160, -88.097786), new LatLng(41.869158, -88.097972), new LatLng(41.869118, -88.097971), new LatLng(41.869121, -88.098089), new LatLng(41.868636, -88.098079), new LatLng(41.868639, -88.097766),new LatLng(41.869160, -88.097786));
        //Do not adjust the following 4 lines
        polyOpt.strokeWidth(0);
        polyOpt.fillColor(highlight);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        insert = new Building(poly, "Student Services Building");  //change name
        buildings.put("StudentServices",insert);  //change key
        allLocations[0]= insert;

        //insert more buildings here
    }

}