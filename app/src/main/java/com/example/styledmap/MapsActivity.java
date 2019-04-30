package com.example.styledmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import android.location.Location;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar mTopToolbar;


    private Button parkingToggle;
    private Button buildingToggle;
    private Button housingToggle;

    private HashMap<String, LocationSpaces> allLocations;
    private DrawerLayout drawerLayout;

    private Marker youAreHere;


    ArrayAdapter<String> adapter;

    /* Object used to receive location updates */
    private FusedLocationProviderClient mFusedLocationClient;
    /* Object that defines important parameters regarding location request. */
    private LocationRequest locationRequest;

    public final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";


    @Override
    //create instance
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mTopToolbar =  findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        allLocations = new HashMap<>();
        drawerLayout = findViewById(R.id.drawer_layout);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        //menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        drawerLayout.closeDrawers();
                        switch (id) {
                            case R.id.dining_button:
                                Intent diningIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wheaton.cafebonappetit.com/"));
                                startActivity(diningIntent);
                                return true;
                            case R.id.housing_options:
                                Intent housingIntent = new Intent(navigationView.getContext(), HousingInfo.class);
                                startActivity(housingIntent);
                                return true;
                            case R.id.campus_events:
                                Intent eventsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.wheaton.edu/calendar-of-events/"));
                                startActivity(eventsIntent);
                                return true;
                        }
                        return true;
                    }
                });
        allLocations = new HashMap<>();


        ArrayList<String> arrayBuilding = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.my_building)));
        adapter = new ArrayAdapter<>(
                MapsActivity.this,
                android.R.layout.simple_list_item_1,
                arrayBuilding
        );


        //Below code to action to Buttons
        parkingToggle = findViewById(R.id.parking_toggle);
        parkingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (LocationSpaces here : allLocations.values()) {
                    if(here.getType().equals("p")) {
                        if (here.getShape().isVisible()) {
                            here.getShape().setVisible(false);
                        } else {
                            here.getShape().setVisible(true);
                        }
                    }
                }
            }
        });

        buildingToggle = findViewById(R.id.building_toggle);
        buildingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (LocationSpaces here : allLocations.values()) {
                    if(here.getType().equals("b")) {
                        if (here.getShape().isVisible()) {
                            here.getShape().setVisible(false);
                        } else {
                            here.getShape().setVisible(true);
                        }
                    }
                }
            }
        });

        housingToggle = findViewById(R.id.housing_toggle);
        housingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (LocationSpaces here : allLocations.values()) {
                    if(here.getType().equals("h")) {
                        if (here.getShape().isVisible()) {
                            here.getShape().setVisible(false);
                        } else {
                            here.getShape().setVisible(true);
                        }
                    }
                }
            }
        });
//        diningButton = findViewById(R.id.dining_button);
//        diningButton.setMenuItemClickListener(new MenuItem.OnMenuItemClickListener())


        //location stuff:
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // 1 second delay between each request
        locationRequest.setFastestInterval(1000); // 1 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(1); // 1 meter minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests

        sendUpdatedLocationMessage();

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
            drawerLayout.openDrawer(GravityCompat.END);
            //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wheaton.cafebonappetit.com/"));
            //startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
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
        //LatLng meysci = new LatLng(41.869559, -88.096015);
        //mMap.addMarker(new MarkerOptions().position(meysci).title("Meyer Science Center"));

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


        locationSetup();

        // location marker:
        youAreHere = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.869559, -88.096015))
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
        youAreHere.setVisible(false);
    }

    int bHighlightOrange = Color.argb(200, 255, 147, 38);
    int pHighlightGrey = Color.argb(200, 64, 64, 64);
    int hHighlightBlue = Color.argb(255, 38, 53, 141);
    PolygonOptions polyOpt;
    Polygon poly;
    Building bInsert;
    Parking pInsert;
    Housing hInsert;
    int strokeWidth = 0;

    private class Coordinate {
        double first, second;
        Coordinate(double first, double second){
            this.first=first;
            this.second=second;
        }
    }

    private void newBuilding(String title, String textFile, Coordinate[] coordinates){
        List<LatLng> toAdd = new ArrayList<>(coordinates.length);
        for(int i = 0; i<coordinates.length; i++){
            toAdd.add(new LatLng(coordinates[i].first, coordinates[i].second));
        }
        polyOpt = new PolygonOptions().addAll(toAdd);
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);

        bInsert = new Building(this, mMap, poly, title, textFile);  //change name

        allLocations.put(bInsert.getName(),bInsert);
        poly.setClickable(true);
        poly.setTag(bInsert);
    }

    private void newHousing(String title, String category, Coordinate[] coordinates){
        List<LatLng> toAdd = new ArrayList<>(coordinates.length);
        for(int i = 0; i<coordinates.length; i++){
            toAdd.add(new LatLng(coordinates[i].first, coordinates[i].second));
        }
        polyOpt = new PolygonOptions().addAll(toAdd);
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);

        hInsert = new Housing(this, mMap, poly, title, category, "");
        allLocations.put(hInsert.getName(),hInsert);
        poly.setClickable(true);
        poly.setTag(hInsert);
    }

    private void newParking(String title, Coordinate[] coordinates) {
        List<LatLng> toAdd = new ArrayList<>(coordinates.length);
        for(int i = 0; i<coordinates.length; i++){
            toAdd.add(new LatLng(coordinates[i].first, coordinates[i].second));
        }
        polyOpt = new PolygonOptions().addAll(toAdd);
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(pHighlightGrey);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(this, mMap, poly, title);
        allLocations.put(pInsert.getName(), pInsert);
        poly.setClickable(true);
        poly.setTag(pInsert);
    }

    private void locationSetup(){

        final GoogleMap map = mMap;

        //--------------------------------------------------------------------------------------------------------------------------------------------------


        //--------------------------------------------------------------------------------------------------------------------------------------------------
        // Building Section

        //Meyer Science Center
        newBuilding("Student Health Services", "shs.txt", new Coordinate[] {
                new Coordinate(41.872973, -88.097991), new Coordinate(41.872972, -88.097378), new Coordinate(41.872400, -88.097379),
                new Coordinate(41.872395, -88.097502), new Coordinate(41.87242, -88.097506), new Coordinate(41.872422, -88.097707),
                new Coordinate(41.872373, -88.097711), new Coordinate(41.872372, -88.097980)});

        //Meyer Science Center
        newBuilding("Meyer Science Center", "meyer.txt", new Coordinate[] {
                new Coordinate(41.869850, -88.096759), new Coordinate(41.869851, -88.095732), new Coordinate(41.869282, -88.095713),
                new Coordinate(41.869283, -88.096073), new Coordinate(41.869634, -88.096077), new Coordinate(41.869653, -88.096746),
                new Coordinate(41.869850, -88.096759)});

        /**
         * No desc file.
         */
        //Student Services Building
        newBuilding("Student Services Building", "", new Coordinate[] {
                new Coordinate(41.869160, -88.097786), new Coordinate(41.869158, -88.097972), new Coordinate(41.869118, -88.097971),
                new Coordinate(41.869121, -88.098089), new Coordinate(41.868636, -88.098079), new Coordinate(41.868639, -88.097766),
                new Coordinate(41.869160, -88.097786)});

        //Adams Hall
        newBuilding("Adams Hall", "adams.txt", new Coordinate[] {
                new Coordinate(41.869286, -88.100006), new Coordinate(41.869194, -88.100006), new Coordinate(41.869192, -88.100045),
                new Coordinate(41.869035, -88.100044), new Coordinate(41.869035, -88.099936), new Coordinate(41.868987, -88.099942),
                new Coordinate(41.868991, -88.099775), new Coordinate(41.869035, -88.099797), new Coordinate(41.869037, -88.099692),
                new Coordinate(41.869193, -88.099692), new Coordinate(41.869195, -88.099730), new Coordinate(41.869286, -88.099732),
                new Coordinate(41.869288, -88.099799), new Coordinate(41.869296, -88.099801), new Coordinate(41.869294, -88.099936),
                new Coordinate(41.869287, -88.099937)});

        // Armerding/Conserv
        newBuilding("Armerding", "armerding.txt", new Coordinate[] {
                new Coordinate(41.870289, -88.098995), new Coordinate(41.870289, -88.098779), new Coordinate(41.870391, -88.098777),
                new Coordinate(41.870393, -88.098736), new Coordinate(41.870572, -88.098736), new Coordinate(41.870579, -88.098591),
                new Coordinate(41.870460, -88.098590), new Coordinate(41.870465, -88.098462), new Coordinate(41.870423, -88.098455),
                new Coordinate(41.870423, -88.098305), new Coordinate(41.870468, -88.098305), new Coordinate(41.870465, -88.098166),
                new Coordinate(41.870550, -88.098171), new Coordinate(41.870552, -88.097931), new Coordinate(41.870687, -88.097931),
                new Coordinate(41.870687, -88.098171), new Coordinate(41.870728, -88.098171), new Coordinate(41.870727, -88.098586),
                new Coordinate(41.870684, -88.098590), new Coordinate(41.870681, -88.099040), new Coordinate(41.870392, -88.099035),
                new Coordinate(41.870390, -88.099004)});

        //Memorial Student Center
        newBuilding("Memorial Student Center", "msc.txt", new Coordinate[] {
                new Coordinate(41.869025, -88.098800), new Coordinate(41.869029, -88.098650), new Coordinate(41.869065, -88.098651),
                new Coordinate(41.869072, -88.098532), new Coordinate(41.869215, -88.098540), new Coordinate(41.869217, -88.098657),
                new Coordinate(41.869258, -88.098657), new Coordinate(41.869253, -88.098811), new Coordinate(41.869212, -88.098812),
                new Coordinate(41.869208, -88.098930), new Coordinate(41.869067, -88.098923), new Coordinate(41.869066, -88.098803)});

        //Edman Chapel
        newBuilding("Edman Chapel", "edman.txt", new Coordinate[] {
                new Coordinate(41.869605, -88.100807), new Coordinate(41.869659, -88.100813), new Coordinate(41.869663, -88.100896),
                new Coordinate(41.870147, -88.100909), new Coordinate(41.870147, -88.100885), new Coordinate(41.870258, -88.100888),
                new Coordinate(41.870258, -88.100796), new Coordinate(41.870290, -88.100794), new Coordinate(41.870294, -88.100552),
                new Coordinate(41.870262, -88.100544), new Coordinate(41.870264, -88.100450), new Coordinate(41.870158, -88.100442),
                new Coordinate(41.870156, -88.100134), new Coordinate(41.870172, -88.100129), new Coordinate(41.870176, -88.100019),
                new Coordinate(41.870009, -88.100016), new Coordinate(41.870007, -88.100126), new Coordinate(41.870025, -88.100126),
                new Coordinate(41.870017, -88.100418), new Coordinate(41.869669, -88.100397), new Coordinate(41.869669, -88.100485),
                new Coordinate(41.869611, -88.100483)});

        //Blanchard Hall
        newBuilding("Blanchard Hall", "msc2.txt", new Coordinate[] {
                new Coordinate(41.868385, -88.099688), new Coordinate(41.868383, -88.099504), new Coordinate(41.868450, -88.099503),
                new Coordinate(41.868554, -88.099505), new Coordinate(41.868601, -88.099507), new Coordinate(41.868600, -88.099692),
                new Coordinate(41.868561, -88.099863), new Coordinate(41.868631, -88.099863), new Coordinate(41.868632, -88.100037),
                new Coordinate(41.868428, -88.100041), new Coordinate(41.868428, -88.099989), new Coordinate(41.868408, -88.099966),
                new Coordinate(41.868408, -88.099936), new Coordinate(41.868424, -88.099910), new Coordinate(41.868429, -88.099857),
                new Coordinate(41.868445, -88.099857), new Coordinate(41.868441, -88.099688), new Coordinate(41.868451, -88.099324),
                new Coordinate(41.868424, -88.099326), new Coordinate(41.868423, -88.099269), new Coordinate(41.868409, -88.099254),
                new Coordinate(41.868408, -88.099224), new Coordinate(41.868424, -88.099206), new Coordinate(41.868428, -88.099155),
                new Coordinate(41.868627, -88.099155), new Coordinate(41.868628, -88.099325), new Coordinate(41.868555, -88.099327)});

        //Buswell Library
        newBuilding("Buswell Library", "buswell.txt", new Coordinate[] {
                new Coordinate(41.869961, -88.099709), new Coordinate(41.869962, -88.099800), new Coordinate(41.870339, -88.099805),
                new Coordinate(41.870340, -88.099656), new Coordinate(41.870386, -88.099656), new Coordinate(41.870386, -88.099615),
                new Coordinate(41.870372, -88.099613), new Coordinate(41.870350, -88.099595), new Coordinate(41.870341, -88.099595),
                new Coordinate(41.870340, -88.099460), new Coordinate(41.870318, -88.099461), new Coordinate(41.870304, -88.099449),
                new Coordinate(41.869933, -88.099448), new Coordinate(41.869932, -88.099488), new Coordinate(41.869876, -88.099487),
                new Coordinate(41.869875, -88.099447), new Coordinate(41.869848, -88.099431), new Coordinate(41.869604, -88.099421),
                new Coordinate(41.869598, -88.099747), new Coordinate(41.869634, -88.099767), new Coordinate(41.869680, -88.099770),
                new Coordinate(41.869683, -88.099707)});

        //Jenks
        newBuilding("Jenks", "jenks.txt", new Coordinate[] {
                new Coordinate(41.869310, -88.095212), new Coordinate(41.869392, -88.095213), new Coordinate(41.869393, -88.095201),
                new Coordinate(41.869430, -88.095203), new Coordinate(41.869432, -88.095154), new Coordinate(41.869460, -88.095154),
                new Coordinate(41.869460, -88.095287), new Coordinate(41.869661, -88.095287), new Coordinate(41.869661, -88.095133),
                new Coordinate(41.869640, -88.095133), new Coordinate(41.869640, -88.095061), new Coordinate(41.869674, -88.095061),
                new Coordinate(41.869675, -88.094866), new Coordinate(41.869694, -88.094864), new Coordinate(41.869695, -88.094581),
                new Coordinate(41.869680, -88.094581), new Coordinate(41.869680, -88.094493), new Coordinate(41.869590, -88.094493),
                new Coordinate(41.869591, -88.094467), new Coordinate(41.869422, -88.094466), new Coordinate(41.869422, -88.094625),
                new Coordinate(41.869583, -88.094626), new Coordinate(41.869582, -88.094864), new Coordinate(41.869635, -88.094866),
                new Coordinate(41.869636, -88.094909), new Coordinate(41.869515, -88.094909), new Coordinate(41.869516, -88.094813),
                new Coordinate(41.869458, -88.094812), new Coordinate(41.869459, -88.094666), new Coordinate(41.869392, -88.094664),
                new Coordinate(41.869312, -88.094653)});

        // Beamer
        newBuilding("Beamer Center", "beamer.txt", new Coordinate[] {
                new Coordinate(41.869678, -88.097455), new Coordinate(41.869678, -88.096927), new Coordinate(41.869418, -88.096939),
                new Coordinate(41.869418, -88.096958), new Coordinate(41.869366, -88.096957), new Coordinate(41.869365, -88.096824),
                new Coordinate(41.869088, -88.096820), new Coordinate(41.869090, -88.096787), new Coordinate(41.868964, -88.096786),
                new Coordinate(41.868964, -88.096820), new Coordinate(41.868741, -88.096819), new Coordinate(41.868739, -88.096983),
                new Coordinate(41.868715, -88.096987), new Coordinate(41.868715, -88.097154), new Coordinate(41.868740, -88.097154),
                new Coordinate(41.868738, -88.097398), new Coordinate(41.868973, -88.097399), new Coordinate(41.868974, -88.097434),
                new Coordinate(41.869121, -88.097435), new Coordinate(41.869122, -88.097364), new Coordinate(41.869316, -88.097366),
                new Coordinate(41.869317, -88.097421), new Coordinate(41.869412, -88.097425), new Coordinate(41.869414, -88.097456),
                new Coordinate(41.869678, -88.097455)});

        // SRC
        newBuilding("SRC", "src.txt", new Coordinate[] {
                new Coordinate(41.871656, -88.097079), new Coordinate(41.871447, -88.097078), new Coordinate(41.871448, -88.097092),
                new Coordinate(41.871109, -88.097085), new Coordinate(41.871115, -88.097074), new Coordinate(41.871026, -88.097076),
                new Coordinate(41.871026, -88.097001), new Coordinate(41.870698, -88.096995), new Coordinate(41.870698, -88.097040),
                new Coordinate(41.870562, -88.097040), new Coordinate(41.870561, -88.096884), new Coordinate(41.870201, -88.096880),
                new Coordinate(41.870201, -88.096117), new Coordinate(41.870562, -88.096116), new Coordinate(41.870559, -88.096162),
                new Coordinate(41.870962, -88.096165), new Coordinate(41.870962, -88.096470), new Coordinate(41.871119, -88.096472),
                new Coordinate(41.871121, -88.096456), new Coordinate(41.871448, -88.096462), new Coordinate(41.871450, -88.096473),
                new Coordinate(41.871657, -88.096473), new Coordinate(41.871656, -88.097079)});

        // BGC
        newBuilding("Billy Graham Center", "bgc.txt", new Coordinate[] {
                new Coordinate(41.866961, -88.099586), new Coordinate(41.866965, -88.099199), new Coordinate(41.866893, -88.099202),
                new Coordinate(41.866893, -88.099118), new Coordinate(41.866882, -88.099117), new Coordinate(41.866882, -88.098950),
                new Coordinate(41.866807, -88.098943), new Coordinate(41.866795, -88.098943), new Coordinate(41.866812, -88.098944),
                new Coordinate(41.866812, -88.098900), new Coordinate(41.866843, -88.098900), new Coordinate(41.866847, -88.098768),
                new Coordinate(41.866814, -88.098766), new Coordinate(41.866812, -88.098723), new Coordinate(41.866797, -88.098722),
                new Coordinate(41.866796, -88.098690), new Coordinate(41.866476, -88.098690), new Coordinate(41.866476, -88.098721),
                new Coordinate(41.866458, -88.098720), new Coordinate(41.866458, -88.098941), new Coordinate(41.866473, -88.098942),
                new Coordinate(41.866472, -88.099845), new Coordinate(41.866455, -88.099846), new Coordinate(41.866457, -88.100067),
                new Coordinate(41.866472, -88.100067), new Coordinate(41.866471, -88.100101), new Coordinate(41.866792, -88.100103),
                new Coordinate(41.866794, -88.100070), new Coordinate(41.866810, -88.100069), new Coordinate(41.866809, -88.100026),
                new Coordinate(41.866843, -88.100025), new Coordinate(41.866841, -88.099892), new Coordinate(41.866812, -88.099889),
                new Coordinate(41.866807, -88.099843), new Coordinate(41.866795, -88.099844), new Coordinate(41.866795, -88.099818),
                new Coordinate(41.866880, -88.099820), new Coordinate(41.866881, -88.099674), new Coordinate(41.866892, -88.099674),
                new Coordinate(41.866894, -88.099586), new Coordinate(41.866961, -88.099586)});

        //--------------------------------------------------------------------------------------------------------------------------------------------------
        //Parking Section

        //Parking for Houses near Student Health Services
        newParking("SHS Houses Parking", new Coordinate[] {
                new Coordinate(41.873050, -88.098123), new Coordinate(41.873049, -88.098337), new Coordinate(41.872603, -88.098344),
                new Coordinate(41.872596, -88.098305), new Coordinate(41.872528, -88.098308), new Coordinate(41.872533, -88.098138)});

        //West Fischer Street Parking
        newParking("West Fischer Street Parking", new Coordinate[] {
                new Coordinate(41.873000, -88.098144), new Coordinate(41.872100, -88.097194),
                new Coordinate(41.872610, -88.097199), new Coordinate(41.872610, -88.097175), new Coordinate(41.872533, -88.098141)});

        //South Fischer E Lot
        newParking("South Fischer E Lot", new Coordinate[] {
                new Coordinate(41.872067, -88.097162), new Coordinate(41.872136, -88.097066),
                new Coordinate(41.872137, -88.096369), new Coordinate(41.871790, -88.096378),
                new Coordinate(41.871784, -88.096408), new Coordinate(41.871678, -88.096406),
                new Coordinate(41.871680, -88.097116), new Coordinate(41.871697, -88.097157)});

        //Fischer U Parking
        newParking("Fischer U Parking", new Coordinate[] {
                new Coordinate(41.873170, -88.096819), new Coordinate(41.873300, -88.096823),
                new Coordinate(41.873198, -88.096687), new Coordinate(41.873079, -88.096688)});

        //North Fischer E Lot
        newParking("North Fischer E Lot", new Coordinate[] {
                new Coordinate(41.873488, -88.097128), new Coordinate(41.873499, -88.096566),
                new Coordinate(41.873455, -88.096550), new Coordinate(41.873452, -88.097110)});

        //Health Center Parking
        newParking("Health Center Parking", new Coordinate[] {
                new Coordinate(41.873436, -88.097351), new Coordinate(41.873437, -88.097298),
                new Coordinate(41.871073, -88.097273), new Coordinate(41.871077, -88.097325),
                new Coordinate(41.872347, -88.097333), new Coordinate(41.872398, -88.097355)});

        //Edman Chapel parking
        newParking("Edman Chapel Parking", new Coordinate[] {
                new Coordinate(41.870309, -88.100750), new Coordinate(41.870896, -88.100772),
                new Coordinate(41.870894, -88.100628), new Coordinate(41.871101, -88.100614),
                new Coordinate(41.871103, -88.099432), new Coordinate(41.870457, -88.099420),
                new Coordinate(41.870450, -88.099855), new Coordinate(41.870251, -88.099854),
                new Coordinate(41.870238, -88.100275), new Coordinate(41.870317, -88.100432)});

        //Tennis court parking
        newParking("Tennis Court Parking", new Coordinate[] {
                new Coordinate(41.871144, -88.098451), new Coordinate(41.871164, -88.097390),
                new Coordinate(41.871000, -88.097380), new Coordinate(41.870976, -88.098449)});

        //Conserv Parking Lot
        newParking("Conserve Court Parking", new Coordinate[] {
                new Coordinate(41.870797, -88.098812), new Coordinate(41.870846, -88.098808),
                new Coordinate(41.870859, -88.098105), new Coordinate(41.870801, -88.098104)});

        //Initializing Blanchard Parking Lot 1
        newParking("Blanchard parking 1", new Coordinate[] {
                new Coordinate(41.868379, -88.098382), new Coordinate(41.868326, -88.098956),
                new Coordinate(41.868622, -88.098960), new Coordinate(41.868610, -88.098467),
                new Coordinate(41.868588, -88.097944), new Coordinate(41.868435, -88.097897)});

        //Initializing Blanchard Parking Lot 2
        newParking("Blanchard Parking 2", new Coordinate[] {
                new Coordinate(41.868563, -88.100200), new Coordinate(41.868359, -88.100168),
                new Coordinate(41.868369, -88.100878), new Coordinate(41.868509, -88.100921)});

        //North Washington Parking
        newParking("North Washington Parking", new Coordinate[] {
                new Coordinate(41.868399, -88.101160), new Coordinate(41.868399, -88.101086),
                new Coordinate(41.867504, -88.101084), new Coordinate(41.867492, -88.101141)});

        //College Ave Parking
        newParking("College Ave Parking 1", new Coordinate[] {
                new Coordinate(41.868373, -88.095299), new Coordinate(41.868387, -88.095006),
                new Coordinate(41.868048, -88.094999), new Coordinate(41.868029, -88.095298)});


        //--------------------------------------------------------------------------------------------------------------------------------------------------
        // Housing Section

        /**
         * No desc file for any of the houses.
         */

        // Williston Hall
        newHousing("Williston Hall", "Upperclassmen", new Coordinate[] {
                new Coordinate(41.869177, -88.098268), new Coordinate(41.869175, -88.098102),
                new Coordinate(41.868767, -88.098107), new Coordinate(41.868766, -88.098259),
                new Coordinate(41.868912, -88.098259), new Coordinate(41.868926, -88.098323),
                new Coordinate(41.868997, -88.098330), new Coordinate(41.869019, -88.098270)});

        // Fischer Hall
        newHousing("Fischer Hall", "Underclassmen", new Coordinate[] {
                new Coordinate(41.873356, -88.096951), new Coordinate(41.872813, -88.096946),
                new Coordinate(41.872813, -88.096557), new Coordinate(41.873372, -88.096571),
                new Coordinate(41.873367, -88.096357), new Coordinate(41.872650, -88.096363),
                new Coordinate(41.872657, -88.097130), new Coordinate(41.873372, -88.097123)});

        // Smith-Traber Hall
        newHousing("Smith-Traber Hall", "Underclassmen", new Coordinate[] {
                new Coordinate(41.870361, -88.094306), new Coordinate(41.870321, -88.094452),
                new Coordinate(41.870687, -88.094663), new Coordinate(41.870660, -88.094756),
                new Coordinate(41.870625, -88.094819), new Coordinate(41.870667, -88.094875),
                new Coordinate(41.870708, -88.094824), new Coordinate(41.870754, -88.094853),
                new Coordinate(41.870789, -88.094928), new Coordinate(41.870780, -88.094963),
                new Coordinate(41.870977, -88.095076), new Coordinate(41.870992, -88.095040),
                new Coordinate(41.871037, -88.095064), new Coordinate(41.871094, -88.094885),
                new Coordinate(41.871049, -88.094859), new Coordinate(41.871067, -88.094813),
                new Coordinate(41.870875, -88.094703), new Coordinate(41.870905, -88.094613),
                new Coordinate(41.870843, -88.094573), new Coordinate(41.870826, -88.094616),
                new Coordinate(41.870763, -88.094572), new Coordinate(41.870930, -88.094061),
                new Coordinate(41.870814, -88.093996), new Coordinate(41.870666, -88.094485),
                new Coordinate(41.870361, -88.094306)});

        // McManis-Evans Hall
        newHousing("McManis-Evans", "Upperclassmen", new Coordinate[] {
                new Coordinate(41.869627, -88.098021), new Coordinate(41.869751, -88.098021),
                new Coordinate(41.869751, -88.097972), new Coordinate(41.869979, -88.097973),
                new Coordinate(41.869979, -88.097993), new Coordinate(41.870091, -88.097997),
                new Coordinate(41.870092, -88.097973), new Coordinate(41.870327, -88.097968),
                new Coordinate(41.870332, -88.098021), new Coordinate(41.870447, -88.098020),
                new Coordinate(41.870447, -88.097769), new Coordinate(41.870327, -88.097766),
                new Coordinate(41.870325, -88.097819), new Coordinate(41.870092, -88.097814),
                new Coordinate(41.870091, -88.097774), new Coordinate(41.869981, -88.097772),
                new Coordinate(41.869981, -88.097814), new Coordinate(41.869750, -88.097814),
                new Coordinate(41.869748, -88.097762), new Coordinate(41.869629, -88.097767)});

        // Saint and Elliot
        newHousing("Saint and Elliot", "Apartment", new Coordinate[] {
                new Coordinate(41.869974, -88.092459), new Coordinate( 41.869982, -88.092162 ),
                new Coordinate( 41.869443, -88.092164 ), new Coordinate( 41.869452, -88.092456),
                new Coordinate(41.869974, -88.092459)});

        // Michigan
        newHousing("Michigan", "Apartment", new Coordinate[] {
                new Coordinate( 41.865870, -88.096228), new Coordinate( 41.865877, -88.095568),
                new Coordinate( 41.865719, -88.095573), new Coordinate(41.865717, -88.095730),
                new Coordinate(41.865739, -88.095732), new Coordinate(41.865733, -88.096062),
                new Coordinate(41.865713, -88.096064), new Coordinate(41.865710, -88.096221),
                new Coordinate( 41.865870, -88.096228 )});

        // Crescent (try to merge with Michigan??)
        newHousing("Crescent", "Apartment", new Coordinate[] {
                new Coordinate(41.866074, -88.096241), new Coordinate(41.866105, -88.096090),
                new Coordinate(41.866083, -88.096080), new Coordinate(41.866147, -88.095759),
                new Coordinate(41.866169, -88.095767), new Coordinate(41.866200, -88.095614),
                new Coordinate(41.866045, -88.095560), new Coordinate(41.865919, -88.096184),
                new Coordinate(41.866074, -88.096241)});

        // College Ave
        newHousing("College Ave 802", "Apartment", new Coordinate[] {
                new Coordinate(41.868377, -88.095459), new Coordinate(41.868374, -88.095309),
                new Coordinate(41.868326, -88.095308), new Coordinate(41.868327, -88.095328),
                new Coordinate(41.868073, -88.095327), new Coordinate(41.868073, -88.095310),
                new Coordinate(41.868008, -88.095308), new Coordinate(41.868001, -88.095457)});
        newHousing("College Ave 814", "Apartment", new Coordinate[] {
                new Coordinate(41.868410, -88.094954), new Coordinate(41.868410, -88.094783),
                new Coordinate(41.868059, -88.094779), new Coordinate(41.868050, -88.094949)});
        newHousing("College Ave 818", "Apartment", new Coordinate[] {
                new Coordinate(41.868379, -88.094702), new Coordinate(41.868391, -88.094589),
                new Coordinate(41.868185, -88.094548), new Coordinate(41.868169, -88.094730),
                new Coordinate(41.868252, -88.094747), new Coordinate(41.868260, -88.094679)});
        newHousing("College Ave 904", "Apartment", new Coordinate[] {
                new Coordinate(41.868567, -88.093646), new Coordinate(41.868565, -88.093480),
                new Coordinate(41.868270, -88.093475), new Coordinate(41.868271, -88.093644)});
        newHousing("College Ave 916", "Apartment", new Coordinate[] {
                new Coordinate(41.868598, -88.093367), new Coordinate(41.868599, -88.093199),
                new Coordinate(41.868303, -88.093197), new Coordinate(41.868302, -88.093363)});



        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                LocationSpaces temp = (LocationSpaces) poly.getTag();
                temp.doClick();
            }
        });

    }



    /*
     * The following location permissions method adapted from the Google Maps Platform
     * https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
     */

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (!(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }
        sendUpdatedLocationMessage();
    }


    /*
     * This method gets user's current location
     */
    private void sendUpdatedLocationMessage() {
        Log.d("SEND", "sendUpdatedLocationMessage() in process");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SEND", "marker#1");
            getLocationPermission();
            Log.d("SEND", "marker#2");
            return;
        }
        Log.d("SEND", "marker#3");
        mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                Log.d("ONLOCATIONRESULT", "reached onLocationResult");
                // Add a marker on the user's current location
                LatLng whereYouAre = new LatLng(location.getLatitude(), location.getLongitude());
                youAreHere.setPosition(whereYouAre);
                youAreHere.setVisible(true);
            }
        }, Looper.myLooper());
    }



}