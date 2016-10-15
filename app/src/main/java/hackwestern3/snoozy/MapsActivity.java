package hackwestern3.snoozy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_REFRESH_DISTANCE = 10; //td- figure out number to go here
    private static final int LOCATION_REFRESH_TIME = 10; //td- figure out number to go here
    private GoogleMap mMap;
    private LatLng destination;
    private Location dest_loc;
    private Uri notification;
    private Ringtone alarm;
    private Boolean alarm_active = false;
    public int radius;

    private int mInterval = 5000; // 5 seconds by default, can be changed later
    final static int REQUEST_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences proximity_settings = getSharedPreferences("proximity_settings", MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = proximity_settings.edit();
        //Recovering and saving internal disk values for the settings
        if (proximity_settings.contains("radius")) {
            radius = proximity_settings.getInt("radius", 800);
        }
        else if (proximity_settings.contains("default_radius")) {
            radius = proximity_settings.getInt("default_radius", 800);
        }
        else {
            radius = 800;
        }
        /*
        The radius should first attempt to set to the current trip's set radius. If there is no set
        radius, the default value is used and if there is no default value set then it is set as 800m.
         */
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


        // Add a temp marker in not Sydney and move the camera
        double lat = 43.013909;//43.013409;
        double lon = -81.295102;//-81.295102;
        destination = new LatLng(lat, lon);
        dest_loc = new Location("destination");
        dest_loc.setLongitude(lon);
        dest_loc.setLatitude(lat);
        mMap.addMarker(new MarkerOptions().position(destination).title("Marker not in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));


        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                Log.d("mclick", "screen pressed");
                if (alarm_active) {
                    Log.d("mclick", "alarm turning off");
                    alarm.stop();
                    alarm_active = false;
                }
            }
        });
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            float distance = location.distanceTo(dest_loc);
            Log.d("distance", Float.toString(distance));
            if (distance < radius) {
                Log.d("location", "distance close enough");
                if (!alarm_active) {
                    try {
                        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        alarm = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        alarm.play();
                        alarm_active = true;
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

        switch (id) {
            case R.id.exit_on_tap:
                finish();
                return true;
            case R.id.settings:
                Intent settings = new Intent(MapsActivity.this, settings.class);
                Log.d("Settings","Settings Button Pressed");
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}