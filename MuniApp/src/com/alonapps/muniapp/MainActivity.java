package com.alonapps.muniapp;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	XmlFetcher fetcher;
	LocationManager mLocManager;
	LocationListener mLocListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Get GPS working. Need manager and implementation of Listener.
		 mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		 mLocListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT ).show();
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
			}

			@Override
			public void onLocationChanged(Location location) {
				 // TODO Auto-generated method stub
				 TextView txtLat = (TextView)findViewById(R.id.lat);
				 txtLat.setText("Lat: "+ location.getLatitude());
				 TextView txtLon = (TextView)findViewById(R.id.lon);
				 txtLon.setText("Lat: "+ location.getLongitude());

			}
		};
		mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,0, mLocListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View v) {
		Intent intent = new Intent(this, ListRoutes.class);
		startActivity(intent);
	}

	public void onClick2(View v) {
		Intent intent = new Intent(this, ShowMap.class);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//Toast.makeText(this, "GPS is paused", Toast.LENGTH_SHORT).show();
		//mLocManager.removeUpdates(mLocListener);
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//Toast.makeText(this, "GPS is resumed", Toast.LENGTH_SHORT).show();
		//mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0, mLocListener);
	}
	
	

}
