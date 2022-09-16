package com.example.gpstest2;

import com.example.gpstest2.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity implements LocationListener {

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

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//prevent device off when app is running
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//get textview controls
//		text1 = (TextView) findViewById(R.id.textView1);
//        text2 = (TextView) findViewById(R.id.textView2);
//        text3 = (TextView) findViewById(R.id.textView3);
//        text4 = (TextView) findViewById(R.id.textView4);
//        text5 = (TextView) findViewById(R.id.textView5);
//        text6 = (TextView) findViewById(R.id.textView6);
        
//        if((text1 == null) || (text2 == null) || (text3 == null) || (text4 == null) || (text5 == null))
//        {
//        	Log.d("MainActivity", "Control fail" );
//        	
//        }
		//open gps device		
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
		
	}

	@Override
	public void onLocationChanged(Location location) {
	    // TODO Auto-generated method stub
		try {
		Double lat; Double lon;
		lat = location.getLatitude(); lon = location.getLongitude();
		String strlat = Location.convert(lat, Location.FORMAT_SECONDS);
		String strlon = Location.convert(lon, Location.FORMAT_SECONDS);
		String str = "Latitude: "+ strlat +" Longitude: "+ strlon;
		//String str2 = location.getAltitude() + location.getBearing() + location.getSpeed() + location.getTime();
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
        
        //get labels
        //контролы перенесены в глобальные переменные, так как во время работы они часто бывают null, и приложеие падает.
        
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
/***************************************************************************************/
	@Override
	public void onProviderDisabled(String provider) {
	    // TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "Gps turned OFF", Toast.LENGTH_LONG).show();
		
//		text1.setText("GPS is OFF");
	}

	@Override
	public void onProviderEnabled(String provider) {
	    // TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "Gps turned ON ", Toast.LENGTH_LONG).show();
		
//		text1.setText("GPS is ON");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub

	}
	
	
	
	
	
	
	
	
	
	
	/***************************************************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	
	
	
	
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
