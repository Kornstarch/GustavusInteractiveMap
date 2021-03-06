package gustavus.mcs270.gustavusinteractivemap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();

    public static final double MAXIMUM_LATITUDE = 44.333099;
    public static final double MINIMUM_LATITUDE = 44.317553;
    public static final double MAXIMUM_LONGITUDE = -93.964821;
    public static final double MINIMUM_LONGITUDE = -93.986193;

    //NE corner is 44.332026,-93.963881
    //SW corner is 44.317890,-93.985767

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final float INITIAL_ZOOM = 17f;
    private GoogleMap mMap;

    public final LatLngBounds GUSTAVUS = new LatLngBounds(
            new LatLng(44.317890, -93.985767),
            new LatLng(44.332026, -93.963881));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready
        // to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.academic_buildings:
                initAcademicPOIs(mMap);
                return true;
            case R.id.housing_buildings:
                initHousingPOIs(mMap);
                return true;
            case R.id.poi_landmarks:
                initLandMarkPOIS(mMap);
                return true;
            case R.id.remove_pins:
                mMap.clear();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onPOIItemSelect(MenuItem item) {
        //add or remove the markers based on the users selection
        switch(item.getItemId()) {
            case R.id.academic_buildings:
                initAcademicPOIs(mMap);
                return true;
            case R.id.housing_buildings:
                initHousingPOIs(mMap);
                return true;
            case R.id.poi_landmarks:
                initLandMarkPOIS(mMap);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Triggered when the map is ready to be used.
     *
     * @param googleMap The GoogleMap object representing the Google Map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //lock camera to gustavus campus
        mMap.setLatLngBoundsForCameraTarget(GUSTAVUS);
        // Pan the camera to the center of Gustavus
        LatLng gustavus = new LatLng(44.322979, -93.972344);
        //googleMap.addMarker(new MarkerOptions().position(gustavus).title("Gustavus"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gustavus, INITIAL_ZOOM));
        //initLandMarkPOIS(mMap);

        setMapLongClick(mMap); // Set a long click listener for the map;
        setPoiClick(mMap); // Set a click listener for points of interest.
        setMapStyle(mMap); // Set the custom map style.
        enableMyLocation(mMap); // Enable location tracking.
        // Enable going into StreetView by clicking on an InfoWindow from a
        // point of interest.
        setInfoWindowClickToPanorama(mMap);
    }

    /**
     * Adds a blue marker to the map when the user long clicks on it.
     *
     * @param map The GoogleMap to attach the listener to.
     */
    private void setMapLongClick(final GoogleMap map) {

        // Add a blue marker to the map when the user performs a long click.
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String snippet = String.format(Locale.getDefault(),
                        getString(R.string.lat_long_snippet),
                        latLng.latitude,
                        latLng.longitude);

                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.dropped_pin))
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_BLUE)));
            }
        });
    }

    /**
     * Adds housing points of interest to the map
     *
     * @param googleMap the GoogleMap that will be populated
     */
    private void initHousingPOIs(GoogleMap googleMap){

        //housing buildings
        LatLng prairieView = new LatLng(44.322168, -93.974598);
        LatLng southwest = new LatLng(44.322817, -93.975306);
        LatLng norelius = new LatLng(44.326347, -93.969282);
        LatLng pittman = new LatLng(44.319562, -93.972849);
        LatLng sohre = new LatLng(44.319961, -93.972238);
        LatLng collegeView = new LatLng(44.327606, -93.972571);
        LatLng arborView = new LatLng(44.318649, -93.977795);
        LatLng chapelView = new LatLng(44.331271, -93.978889);
        LatLng sorenson = new LatLng(44.324494, -93.967699);
        LatLng gibbs = new LatLng(44.324160, -93.967951);
        LatLng north = new LatLng(44.323887, -93.968300);
        LatLng internationalHouse = new LatLng(44.323055, -93.974120);
        LatLng uhler = new LatLng(44.323841, -93.969448);
        LatLng rundstrom = new LatLng(44.321869, -93.969700);

        //add the markers of academic buildings //can add.title to give more description
        googleMap.addMarker(new MarkerOptions().position(prairieView).title("Prairie View").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(southwest).title("Southwest").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(norelius).title("Noerlius").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(pittman).title("Pittman").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(sohre).title("Sohre").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(collegeView).title("College View").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(arborView).title("Arbor View").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(chapelView).title("Chapel View").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(gibbs).title("Gibbs Hall").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(sorenson).title("Sorenson").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(north).title("North").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(internationalHouse).title("International House").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(uhler).title("Uhler hall").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions().position(rundstrom).title("Rundstrom").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

    }

    /**
     * Adds land mark points of interest to the map
     *
     * @param googleMap the GoogleMap that will be populated
     */
    private void initLandMarkPOIS(GoogleMap googleMap){

        //POI buildings and landmarks
        LatLng arboretum = new LatLng(44.320276, -93.975016);
        LatLng christChapel = new LatLng(44.322794, -93.971358);
        LatLng campusCenter = new LatLng(44.324033, -93.970564);
        LatLng studentUnion = new LatLng(44.323496, -93.971068);
        LatLng tennisBubble = new LatLng(44.328443, -93.973959);
        LatLng footballField = new LatLng(44.325238, -93.973734);
        LatLng soccerField = new LatLng(44.326505, -93.971240);
        LatLng baseballField = new LatLng(44.326900, -93.975939);
        LatLng softballField = new LatLng(44.327073, -93.968901);
        LatLng bigHillFarm = new LatLng(44.329525, -93.977597);
        LatLng physicalPlant = new LatLng(44.3229245, -93.975778);
        LatLng presidentsHouse = new LatLng(44.325432, -93.975606);

        //add the markers of academic buildings //can add.title to give more description
        googleMap.addMarker(new MarkerOptions().position(arboretum).title("Arboretum").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(christChapel).title("Christ Chapel").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(campusCenter).title("Campus Center").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(studentUnion).title("Student Union").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(tennisBubble).title("Tennis Bubble").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(footballField).title("Football Field").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(soccerField).title("Soccer Field").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(baseballField).title("Baseball Field").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(softballField).title("Softball Field").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(bigHillFarm).title("Big Hill Farm").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(physicalPlant).title("Physical Plant").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        googleMap.addMarker(new MarkerOptions().position(presidentsHouse).title("Presidents Housee").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }

    /**
     * Adds academic builiding points of interest to the map
     *
     * @param googleMap the GoogleMap that will be populated
     */
    private void initAcademicPOIs(GoogleMap googleMap){

        //academic buildings.
        LatLng olin = new LatLng(44.322799, -93.973353);
        LatLng nobel = new LatLng(44.322097, -93.972683);
        LatLng lund = new LatLng(44.325133, -93.971368);
        LatLng bjorling = new LatLng(44.320945, -93.974601);
        LatLng library = new LatLng(44.323572, -93.971816);
        LatLng beck = new LatLng(44.323900, -93.973112);
        LatLng confer = new LatLng(44.320810, -93.972345);
        LatLng vicker = new LatLng(44.321110, -93.972087);
        LatLng mattsonHall = new LatLng(44.322038, -93.975252);
        LatLng anderson = new LatLng(44.321764, -93.971602);
        LatLng schaeffer = new LatLng(44.320582, -93.973852);
        LatLng interpretiveCenter = new LatLng(44.319928, -93.975234);

        //add the markers of academic buildings //can add.title to give more description
        googleMap.addMarker(new MarkerOptions().position(olin).title("Olin"));
        googleMap.addMarker(new MarkerOptions().position(nobel).title("Nobel"));
        googleMap.addMarker(new MarkerOptions().position(lund).title("Lund Center"));
        googleMap.addMarker(new MarkerOptions().position(bjorling).title("Bjorling"));
        googleMap.addMarker(new MarkerOptions().position(library).title("Library"));
        googleMap.addMarker(new MarkerOptions().position(beck).title("Beck"));
        googleMap.addMarker(new MarkerOptions().position(confer).title("Confer"));
        googleMap.addMarker(new MarkerOptions().position(vicker).title("Vickner"));
        googleMap.addMarker(new MarkerOptions().position(mattsonHall).title("Mattson Hall"));
        googleMap.addMarker(new MarkerOptions().position(anderson).title("Anderson"));
        googleMap.addMarker(new MarkerOptions().position(schaeffer).title("Schaeffer"));
        googleMap.addMarker(new MarkerOptions().position(interpretiveCenter).title("Interpretive Center"));

    }

    /**
     * Adds a marker when a place of interest (POI) is clicked with the name of
     * the POI and immediately shows the info window.
     *
     * @param map The GoogleMap to attach the listener to.
     */
    private void setPoiClick(final GoogleMap map) {
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                Marker poiMarker = map.addMarker(new MarkerOptions()
                        .position(poi.latLng)
                        .title(poi.name));
                poiMarker.showInfoWindow();
                poiMarker.setTag(getString(R.string.poi));
            }
        });
    }

    /**
     * Loads a style from the map_style.json file to style the Google Map. Log
     * the errors if the loading fails.
     *
     * @param map The GoogleMap object to style.
     */
    private void setMapStyle(GoogleMap map) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    /**
     * Checks for location permissions, and requests them if they are missing.
     * Otherwise, enables the location layer.
     */
    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    /**
     * Starts a Street View panorama when an info window containing the poi tag
     * is clicked.
     *
     * @param map The GoogleMap to set the listener to.
     */
    private void setInfoWindowClickToPanorama(GoogleMap map) {
        map.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        // Check the tag
                        if (marker.getTag() == "poi") {

                            // Set the position to the position of the marker
                            StreetViewPanoramaOptions options =
                                    new StreetViewPanoramaOptions().position(
                                            marker.getPosition());

                            SupportStreetViewPanoramaFragment streetViewFragment
                                    = SupportStreetViewPanoramaFragment
                                    .newInstance(options);

                            // Replace the fragment and add it to the backstack
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container,
                                            streetViewFragment)
                                    .addToBackStack(null).commit();
                        }
                    }
                });
    }
}
