package hackwestern3.snoozy;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_REFRESH_DISTANCE = 5; //td- figure out number to go here
    private static final int LOCATION_REFRESH_TIME = 5; //td- figure out number to go here
    private GoogleMap mMap;
    private LatLng coordinates;
    private Location dest_loc;
    private Uri notification;
    private Ringtone alarm;
    private Boolean alarm_active = false;
    private int radius = 300; //radius of the circle around the destination
    private int mInterval = 5000; // 5 seconds by default, can be changed later
    final static int REQUEST_LOCATION = 0;
    private View search_button;

    private Destination destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        destination = new Destination(null, null, null);
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

        /*search_button = findViewById(R.id.button1);

        search_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                PlaceAutocompleteFragment loc_input = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                //String g = loc_input.getText().toString();

                //Log.d("It says", g);


            }
        });*/
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            mMap.setMyLocationEnabled(true);
        } else {
            // permission has been granted, continue as usual
            mMap.setMyLocationEnabled(true);
        }

        /*
        // Add a temp marker in not Sydney and move the camera
        double lat = 43.013909;//43.013409;
        double lon = -81.295102;//-81.295102;

        destination = new LatLng(lat, lon);*/
        dest_loc = new Location("destination");
        /*
        dest_loc.setLongitude(lon);
        dest_loc.setLatitude(lat);
        mMap.addMarker(new MarkerOptions().position(destination).title("Marker not in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
        */


        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng selected_latlng) {
                Log.d("mclick", "screen pressed");
                if (destination.getAlarm_active()) {
                    Log.d("mclick", "alarm turning off");
                    destination.getAlarm().stop();
                    destination.setAlarm_active(false);
                } else {
                    // testing adding marker with touch
                    notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    alarm = RingtoneManager.getRingtone(getApplicationContext(), notification);

                    Location selected_loc = new Location("destination");
                    selected_loc.setLongitude(selected_latlng.longitude);
                    selected_loc.setLatitude(selected_latlng.latitude);

                    mMap.clear();
                    Marker newmarker = mMap.addMarker(new MarkerOptions().position(selected_latlng).title("New Marker").draggable(true));

                    destination = new Destination(newmarker, selected_loc, alarm);

                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(selected_latlng));
                    dest_loc.setLongitude(selected_latlng.longitude);
                    dest_loc.setLatitude(selected_latlng.latitude);
                }
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latlng = marker.getPosition();

                Location loc = new Location("destination"); //TODO: use the previous location name
                loc.setLongitude(latlng.longitude);
                loc.setLatitude(latlng.latitude);
                destination.setLocation(loc);
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Here", "Place: " + place.getName());
                String g = (String) place.getName();

                Geocoder geocoder = new Geocoder(getBaseContext());
                List<Address> addresses = null;

                try {
                    // Getting a maximum of 3 Address that matches the input
                    // text
                    addresses = geocoder.getFromLocationName(g, 3);
                    if (addresses != null && !addresses.equals(""))
                        search(addresses);

                } catch (Exception e) {
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Here", "An error occurred: " + status);
            }
        });
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            float distance = location.distanceTo(destination.getLocation());
            Log.d("distance", Float.toString(distance));
            if (distance < destination.getRadius()) {
                Log.d("location", "distance close enough");
                if (!destination.getAlarm_active()) {
                    try {
                        destination.getAlarm().play();
                        destination.setAlarm_active(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.d("location", "distance too far");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.exit_on_tap) {
            finish();
            return true;
        }
        else if (id == R.id.settings) {
            Intent settings = new Intent(MapsActivity.this, settings.class);
            MapsActivity.this.startActivity(settings);
        }

        return super.onOptionsItemSelected(item);

    }

    protected void search(List<Address> addresses) {

        Address address = (Address) addresses.get(0);
        double home_long = address.getLongitude();
        double home_lat = address.getLatitude();
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        String place_autocomplete_fragment = String.format(
                "%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address
                        .getAddressLine(0) : "", address.getCountryName());

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);
        markerOptions.title(place_autocomplete_fragment);
        markerOptions.draggable(true);

        dest_loc.setLongitude(latLng.longitude);
        dest_loc.setLatitude(latLng.latitude);

        mMap.clear();
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

    }


    class Destination {
        private Marker marker;
        private int radius = 300;
        private Location location;
        private Ringtone alarm;
        private Boolean alarm_active = false;

        Destination (Marker m, Location l) {
            marker = m;
            location = l;
            alarm = RingtoneManager.getRingtone(getApplicationContext(), notification);
        }

        Destination (Marker m, Location l, Ringtone r) {
            marker = m;
            location = l;
            alarm = r;
        }

        public Marker getMarker() {
            return marker;
        }

        public int getRadius() {
            return radius;
        }

        public Location getLocation() {
            return location;
        }

        public Ringtone getAlarm() {
            return alarm;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public void setAlarm(Ringtone alarm) {
            this.alarm = alarm;
        }

        public Boolean getAlarm_active() {
            return alarm_active;
        }

        public void setAlarm_active(Boolean alarm_active) {
            this.alarm_active = alarm_active;
        }
    }

}
