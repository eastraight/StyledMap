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
    private Location[] allLocations;

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
        allLocations = new Location[100];

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

        int bHighlight = Color.parseColor("#ff9326");
        int pHighlight = Color.parseColor("#26358D");
        int hHighlight = Color.parseColor("#4d5cdd");
        PolygonOptions polyOpt;
        Polygon poly;
        Building bInsert;
        Parking pInsert;
        Housing hInsert;

        int numBuildings = 0;

        /*
        *the first polygon for testing and demo (MeySci)
        * replace "examplePoly" with new name for building
        */
        //Defining coordinates of the polygon
        polyOpt = new PolygonOptions().add(new LatLng(41.869850, -88.096759), new LatLng(41.869851, -88.095732), new LatLng(41.869282, -88.095713), new LatLng(41.869283, -88.096073), new LatLng(41.869634, -88.096077), new LatLng(41.869653, -88.096746),new LatLng(41.869850, -88.096759));
        //Do not adjust the following 4 lines
        polyOpt.strokeWidth(0);
        polyOpt.fillColor(bHighlight);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Meyer Science Center");  //change name
        buildings.put("Meyer",bInsert);  //change key
        allLocations[numBuildings]= bInsert;
        numBuildings++;

        //Initializing the Student Services Building
        polyOpt = new PolygonOptions().add(new LatLng(41.869160, -88.097786), new LatLng(41.869158, -88.097972), new LatLng(41.869118, -88.097971), new LatLng(41.869121, -88.098089), new LatLng(41.868636, -88.098079), new LatLng(41.868639, -88.097766),new LatLng(41.869160, -88.097786));
        polyOpt.strokeWidth(0);
        polyOpt.fillColor(bHighlight);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Student Services Building");
        buildings.put("StudentServices",bInsert);
        allLocations[numBuildings]= bInsert;
        numBuildings++;

        //Initializing Blanchard Parking Lot 1
        polyOpt = new PolygonOptions().add(new LatLng(41.868379, -88.098382), new LatLng(41.868326, -88.098956), new LatLng(41.868622, -88.098960), new LatLng(41.868610, -88.098467), new LatLng(41.868588, -88.097944), new LatLng(41.868435, -88.097897));
        polyOpt.strokeWidth(0);
        polyOpt.fillColor(pHighlight);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "Blanchard Parking I");
        parking.put("BlanchardParkingI",pInsert);
        allLocations[numBuildings]= pInsert;
        numBuildings++;

        //Initializing Blanchard Parking Lot 2
        polyOpt = new PolygonOptions().add(new LatLng(41.868563, -88.100200), new LatLng(41.868359, -88.100168), new LatLng(41.868369, -88.100878), new LatLng(41.868509, -88.100921));
        polyOpt.strokeWidth(0);
        polyOpt.fillColor(pHighlight);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "Blanchard Parking II");
        parking.put("BlanchardParking2",pInsert);
        allLocations[numBuildings]= pInsert;
        numBuildings++;

        polyOpt = new PolygonOptions().add(new LatLng(41.868399, -88.101160), new LatLng(41.868399, -88.101086), new LatLng(41.867504, -88.101084), new LatLng(41.867492, -88.101141));
        polyOpt.strokeWidth(0);
        polyOpt.fillColor(pHighlight);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "North Washington Parking");
        parking.put("NWashingtonParking",pInsert);
        allLocations[numBuildings]= pInsert;
        numBuildings++;

        //insert more buildings here

        polyOpt = new PolygonOptions().add(new LatLng(41.869177, -88.098268), new LatLng(41.869175, -88.098102), new LatLng(41.868767, -88.098107), new LatLng(41.868766, -88.098259), new LatLng(41.868912, -88.098259), new LatLng(41.868926, -88.098323), new LatLng(41.868997, -88.098330), new LatLng(41.869019, -88.098270));
        polyOpt.strokeWidth(0);
        polyOpt.fillColor(hHighlight);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Williston Hall", "Upperclassmen");
        housing.put("Williston",hInsert);
        allLocations[numBuildings]= pInsert;
        numBuildings++;
    }

}