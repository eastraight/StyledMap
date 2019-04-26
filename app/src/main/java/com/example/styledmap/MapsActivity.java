package com.example.styledmap;

import android.Manifest;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar mTopToolbar;


    private Button parkingToggle;
    private Button buildingToggle;
    private Button housingToggle;

    private HashMap<String, LocationSpaces> allLocations;
    private DrawerLayout drawerLayout;

    private Marker youAreHere;

    ListView search_building;
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
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wheaton.cafebonappetit.com/"));
                                startActivity(browserIntent);
                                return true;
                            case R.id.housing_options:
                                Intent housingIntent = new Intent(navigationView.getContext(), HousingInfo.class);
                                startActivity(housingIntent);

                        }
                        return true;
                    }
                });
        allLocations = new HashMap<>();

        search_building = (ListView) findViewById(R.id.search_building);
        ArrayList<String> arrayBuilding = new ArrayList<>();
        arrayBuilding.addAll(Arrays.asList(getResources().getStringArray(R.array.my_building)));
        adapter = new ArrayAdapter<String>(
                MapsActivity.this,
                android.R.layout.simple_list_item_1,
                arrayBuilding
        );
        search_building.setAdapter(adapter);

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


        locationSetup(mMap);

        // location marker:
        youAreHere = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.869559, -88.096015))
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
        youAreHere.setVisible(false);
    }

    private void locationSetup(GoogleMap mMap) {
//        int campusOutLine = Color.argb(0, 255, 147, 38);
        int bHighlightOrange = Color.argb(200, 255, 147, 38);
        int pHighlightGrey = Color.argb(200, 64, 64, 64);
        int hHighlightBlue = Color.argb(255, 38, 53, 141);
        PolygonOptions polyOpt;
        Polygon poly;
        Building bInsert;
        Parking pInsert;
        Housing hInsert;

        int strokeWidth = 0;

        final GoogleMap map = mMap;


        //Campus outline
//        //Meyer Science Center
//        polyOpt = new PolygonOptions().add(new LatLng(41.869850, -88.096759), new LatLng(41.869851, -88.095732), new LatLng(41.869282, -88.095713), new LatLng(41.869283, -88.096073), new LatLng(41.869634, -88.096077), new LatLng(41.869653, -88.096746),new LatLng(41.869850, -88.096759));
//        //Do not adjust the following 4 lines
//        polyOpt.strokeWidth(0);
//        polyOpt.fillColor(bHighlightOrange);
//        poly = mMap.addPolygon(polyOpt);
//        poly.setVisible(true);
//


        //--------------------------------------------------------------------------------------------------------------------------------------------------


        //--------------------------------------------------------------------------------------------------------------------------------------------------
        // Building Section

        //Meyer Science Center
        polyOpt = new PolygonOptions().add(new LatLng(41.869850, -88.096759), new LatLng(41.869851, -88.095732), new LatLng(41.869282, -88.095713), new LatLng(41.869283, -88.096073), new LatLng(41.869634, -88.096077), new LatLng(41.869653, -88.096746),new LatLng(41.869850, -88.096759));
        //Do not adjust the following 4 lines
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Meyer Science Center", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) meysci", Toast.LENGTH_SHORT).show();
            }
        });  //change name
        allLocations.put(bInsert.getName(),bInsert);
        poly.setClickable(true);
        poly.setTag(bInsert);



        //Student Services Building
        polyOpt = new PolygonOptions().add(new LatLng(41.869160, -88.097786), new LatLng(41.869158, -88.097972), new LatLng(41.869118, -88.097971), new LatLng(41.869121, -88.098089), new LatLng(41.868636, -88.098079), new LatLng(41.868639, -88.097766),new LatLng(41.869160, -88.097786));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Student Services Building", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) ssb", Toast.LENGTH_SHORT).show();
            }
        } );
        allLocations.put(bInsert.getName(),bInsert);
        poly.setClickable(true);
        poly.setTag(bInsert);

        //Adams Hall
        polyOpt = new PolygonOptions().add(new LatLng(41.869286, -88.100006), new LatLng(41.869194, -88.100006), new LatLng(41.869192, -88.100045), new LatLng(41.869035, -88.100044), new LatLng(41.869035, -88.099936), new LatLng(41.868987, -88.099942),new LatLng(41.868991, -88.099775), new LatLng(41.869035,-88.099797), new LatLng(41.869037, -88.099692), new LatLng(41.869193, -88.099692), new LatLng(41.869195, -88.099730), new LatLng(41.869286, -88.099732), new LatLng(41.869288, -88.099799), new LatLng(41.869296, -88.099801), new LatLng(41.869294, -88.099936), new LatLng(41.869287, -88.099937));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Adams Hall", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) ah", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(bInsert.getName(),bInsert);
        poly.setClickable(true);
        poly.setTag(bInsert);

        // Armerding/Conserv
        polyOpt = new PolygonOptions().add( new LatLng(41.870289, -88.098995), new LatLng(41.870289, -88.098779), new LatLng(41.870391, -88.098777), new LatLng(41.870393, -88.098736), new LatLng(41.870572, -88.098736), new LatLng(41.870579, -88.098591), new LatLng(41.870460, -88.098590), new LatLng(41.870465, -88.098462), new LatLng(41.870423, -88.098455), new LatLng(41.870423, -88.098305), new LatLng(41.870468, -88.098305), new LatLng(41.870465, -88.098166), new LatLng(41.870550, -88.098171), new LatLng(41.870552, -88.097931), new LatLng(41.870687, -88.097931), new LatLng(41.870687, -88.098171), new LatLng(41.870728, -88.098171), new LatLng(41.870727, -88.098586), new LatLng(41.870684, -88.098590), new LatLng(41.870681, -88.099040), new LatLng(41.870392, -88.099035), new LatLng(41.870390, -88.099004));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Armerding", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) c", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(bInsert.getName(),bInsert);
        poly.setClickable(true);
        poly.setTag(bInsert);

        //Memorial Student Center
        polyOpt = new PolygonOptions().add( new LatLng(41.869025, -88.098800), new LatLng(41.869029, -88.098650), new LatLng(41.869065, -88.098651), new LatLng(41.869072, -88.098532), new LatLng(41.869215, -88.098540), new LatLng(41.869217, -88.098657), new LatLng(41.869258, -88.098657), new LatLng(41.869253, -88.098811), new LatLng(41.869212, -88.098812), new LatLng(41.869208, -88.098930), new LatLng(41.869067, -88.098923), new LatLng(41.869066, -88.098803));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Memorial Student Center", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) msc", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(bInsert.getName(),bInsert);
        poly.setClickable(true);
        poly.setTag(bInsert);

        //Edman Chapel
        polyOpt = new PolygonOptions().add( new LatLng(41.869605, -88.100807), new LatLng(41.869659, -88.100813), new LatLng(41.869663, -88.100896), new LatLng(41.870147, -88.100909), new LatLng(41.870147, -88.100885), new LatLng(41.870258, -88.100888), new LatLng(41.870258, -88.100796), new LatLng(41.870290, -88.100794), new LatLng(41.870294, -88.100552), new LatLng(41.870262, -88.100544), new LatLng(41.870264, -88.100450), new LatLng(41.870158, -88.100442), new LatLng(41.870156, -88.100134), new LatLng(41.870172, -88.100129), new LatLng(41.870176, -88.100019), new LatLng(41.870009, -88.100016), new LatLng(41.870007, -88.100126), new LatLng(41.870025, -88.100126), new LatLng(41.870017, -88.100418), new LatLng(41.869669, -88.100397), new LatLng(41.869669, -88.100485), new LatLng(41.869611, -88.100483));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "EdmanChapel", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) msc", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(bInsert.getName(),bInsert);
        poly.setClickable(true);
        poly.setTag(bInsert);

        //--------------------------------------------------------------------------------------------------------------------------------------------------
        //Parking Section


        //Tennis court parking
        polyOpt = new PolygonOptions().add(new LatLng(41.871144, -88.098451), new LatLng(41.871164, -88.097390), new LatLng(41.871000, -88.097380), new LatLng(41.870976, -88.098449));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(pHighlightGrey);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "Tennis Court Parking", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) tcp", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(pInsert.getName(),pInsert);
        poly.setClickable(true);
        poly.setTag(pInsert);

        //Convserv Parking Lot
        polyOpt = new PolygonOptions().add(new LatLng(41.870797, -88.098812), new LatLng(41.870846, -88.098808), new LatLng(41.870859, -88.098105), new LatLng(41.870801, -88.098104));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(pHighlightGrey);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "Conserve Court Parking", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) cpl", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(pInsert.getName(),pInsert);
        poly.setClickable(true);
        poly.setTag(pInsert);

        //Initializing Blanchard Parking Lot 1
        polyOpt = new PolygonOptions().add(new LatLng(41.868379, -88.098382), new LatLng(41.868326, -88.098956), new LatLng(41.868622, -88.098960), new LatLng(41.868610, -88.098467), new LatLng(41.868588, -88.097944), new LatLng(41.868435, -88.097897));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(pHighlightGrey);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "Blanchard Parking I", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) bpl1", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(pInsert.getName(),pInsert);
        poly.setClickable(true);
        poly.setTag(pInsert);

        //Initializing Blanchard Parking Lot 2
        polyOpt = new PolygonOptions().add(new LatLng(41.868563, -88.100200), new LatLng(41.868359, -88.100168), new LatLng(41.868369, -88.100878), new LatLng(41.868509, -88.100921));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(pHighlightGrey);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "Blanchard Parking II", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) bpl2", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(pInsert.getName(),pInsert);
        poly.setClickable(true);
        poly.setTag(pInsert);

        //North Washington Parking
        polyOpt = new PolygonOptions().add(new LatLng(41.868399, -88.101160), new LatLng(41.868399, -88.101086), new LatLng(41.867504, -88.101084), new LatLng(41.867492, -88.101141));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(pHighlightGrey);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "North Washington Parking", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) nwp", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(pInsert.getName(),pInsert);
        poly.setClickable(true);
        poly.setTag(pInsert);


        //--------------------------------------------------------------------------------------------------------------------------------------------------
        // Housing Section

        // Williston Hall
        polyOpt = new PolygonOptions().add(new LatLng(41.869177, -88.098268), new LatLng(41.869175, -88.098102), new LatLng(41.868767, -88.098107), new LatLng(41.868766, -88.098259), new LatLng(41.868912, -88.098259), new LatLng(41.868926, -88.098323), new LatLng(41.868997, -88.098330), new LatLng(41.869019, -88.098270));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Williston Hall", "Upperclassmen", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) wh", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(hInsert.getName(),hInsert);
        poly.setClickable(true);
        poly.setTag(hInsert);

        // Fischer Hall
        polyOpt = new PolygonOptions().add(new LatLng(41.873356, -88.096951), new LatLng(41.872813, -88.096946), new LatLng(41.872813, -88.096557), new LatLng(41.873372, -88.096571), new LatLng(41.873367, -88.096357), new LatLng(41.872650, -88.096363), new LatLng(41.872657, -88.097130), new LatLng(41.873372, -88.097123));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Fischer Hall", "Underclassmen", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) fh", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(hInsert.getName(),hInsert);
        poly.setClickable(true);
        poly.setTag(hInsert);

        // Smith-Traber Hall
        polyOpt = new PolygonOptions().add(new LatLng(41.870361, -88.094306), new LatLng(41.870321, -88.094452), new LatLng(41.870687, -88.094663), new LatLng(41.870660, -88.094756), new LatLng(41.870625, -88.094819), new LatLng(41.870667, -88.094875), new LatLng(41.870708, -88.094824), new LatLng(41.870754, -88.094853),
                new LatLng(41.870789, -88.094928), new LatLng(41.870780, -88.094963), new LatLng(41.870977, -88.095076), new LatLng(41.870992, -88.095040), new LatLng(41.871037, -88.095064), new LatLng(41.871094, -88.094885), new LatLng(41.871049, -88.094859), new LatLng(41.871067, -88.094813), new LatLng(41.870875, -88.094703),
                new LatLng(41.870905, -88.094613), new LatLng(41.870843, -88.094573), new LatLng(41.870826, -88.094616), new LatLng(41.870763, -88.094572), new LatLng(41.870930, -88.094061), new LatLng(41.870814, -88.093996), new LatLng(41.870666, -88.094485), new LatLng(41.870361, -88.094306));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Smith-Traber Hall", "Underclassmen", new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) sth", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(hInsert.getName(),hInsert);
        poly.setClickable(true);
        poly.setTag(hInsert);

        // McManis-Evans Hall
        polyOpt = new PolygonOptions().add(new LatLng(41.869627, -88.098021), new LatLng(41.869751, -88.098021), new LatLng(41.869751, -88.097972), new LatLng(41.869979, -88.097973), new LatLng(41.869979, -88.097993), new LatLng(41.870091, -88.097997), new LatLng(41.870092, -88.097973), new LatLng(41.870327, -88.097968), new LatLng(41.870332, -88.098021), new LatLng(41.870447, -88.098020), new LatLng(41.870447, -88.097769), new LatLng(41.870327, -88.097766), new LatLng(41.870325, -88.097819), new LatLng(41.870092, -88.097814), new LatLng(41.870091, -88.097774), new LatLng(41.869981, -88.097772), new LatLng(41.869981, -88.097814), new LatLng(41.869750, -88.097814), new LatLng(41.869748, -88.097762), new LatLng(41.869629, -88.097767));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "McManis-Evans", "Upperclassmen",new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) eh", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(hInsert.getName(),hInsert);
        poly.setClickable(true);
        poly.setTag(hInsert);

        // Saint and Elliot
        polyOpt = new PolygonOptions().add(new LatLng(41.869974, -88.092459), new LatLng( 41.869982, -88.092162 ),
                new LatLng( 41.869443, -88.092164 ), new LatLng( 41.869452, -88.092456), new LatLng(41.869974, -88.092459));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Saint and Elliot", "Apartment",new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) sne", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(hInsert.getName(),hInsert);
        poly.setClickable(true);
        poly.setTag(hInsert);

        // Michigan
        polyOpt = new PolygonOptions().add(new LatLng( 41.865870, -88.096228), new LatLng( 41.865877, -88.095568),
                new LatLng( 41.865719, -88.095573), new LatLng(41.865717, -88.095730), new LatLng(41.865739, -88.095732),
                new LatLng(41.865733, -88.096062), new LatLng(41.865713, -88.096064), new LatLng(41.865710, -88.096221),
                new LatLng( 41.865870, -88.096228 ));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Michigan", "Apartment",new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) m", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(hInsert.getName(),hInsert);
        poly.setClickable(true);
        poly.setTag(hInsert);

        // Crescent (try to merge with Michigan??)
        polyOpt = new PolygonOptions().add(new LatLng(41.866074, -88.096241), new LatLng(41.866105, -88.096090),
                new LatLng(41.866083, -88.096080), new LatLng(41.866147, -88.095759), new LatLng(41.866169, -88.095767),
                new LatLng(41.866200, -88.095614), new LatLng(41.866045, -88.095560), new LatLng(41.865919, -88.096184),
                new LatLng(41.866074, -88.096241));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Crescent", "Apartment",new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                Toast.makeText(MapsActivity.this, "Confirmation placeholder toast! ( ͡° ͜ʖ ͡°) c", Toast.LENGTH_SHORT).show();
            }
        });
        allLocations.put(hInsert.getName(),hInsert);
        poly.setClickable(true);
        poly.setTag(hInsert);





        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){
            public void onPolygonClick(Polygon poly){
                LocationSpaces temp = (LocationSpaces) poly.getTag();
                temp.getClick().onPolygonClick(poly);
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
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
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