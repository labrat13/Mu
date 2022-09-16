package com.meraman.gpstestnew;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
//	private TextView text1;
//    private TextView text2;
//    private TextView text3;
//    private TextView text4;
//    private TextView text5;
//    private TextView text6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //prevent device off when app is running
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //open gps device
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //check granted permission - new for this project
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getBaseContext(), "Insufficient permissions error", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        return;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Double lat; Double lon;
            lat = location.getLatitude(); lon = location.getLongitude();
            String strlat = Location.convert(lat, Location.FORMAT_SECONDS);
            String strlon = Location.convert(lon, Location.FORMAT_SECONDS);
            String str = "Latitude: "+ strlat +" Longitude: "+ strlon;
            //String str2 = location.getAltitude() + location.getBearing() + location.getSpeed() + location.getTime();
            Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();

            //get labels
            //

            //change label text
//        text2.setText("Latitude: " + strlat);
//        text3.setText("Longitude: " + strlon);
//        text4.setText("Altitude: " + location.getAltitude() + " m");
//        text5.setText("Bearing: " + location.getBearing() + " degrees");
//        text6.setText("Speed: " + location.getSpeed()+ " m/s");
        }
        catch(java.lang.Exception ex)
        {
            Log.d("Exception", ex.toString());
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(getBaseContext(), "GPS is turned ON ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(getBaseContext(), "GPS is turned OFF", Toast.LENGTH_LONG).show();

//		text1.setText("GPS is OFF");
    }
}
