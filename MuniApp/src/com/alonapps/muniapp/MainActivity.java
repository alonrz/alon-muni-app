package com.alonapps.muniapp;

import java.util.List;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	DataManager mDataManager;
	LocationManager mLocManager;
	LocationListener mLocListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//This will init the large data object for quicker laoding.
		mDataManager = DataManager.getDataManager(this);
		
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
				Toast.makeText(MainActivity.this, "Gps Enabled",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "Gps Disabled",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				TextView txtLat = (TextView) findViewById(R.id.lat);
				txtLat.setText("Lat: " + location.getLatitude());
				TextView txtLon = (TextView) findViewById(R.id.lon);
				txtLon.setText("Lat: " + location.getLongitude());

			}
		};
		mycrit = new Criteria();
		mycrit.setAccuracy(Criteria.ACCURACY_FINE);

		String str = mLocManager.getBestProvider(mycrit, true);
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

		mLocManager.requestLocationUpdates(str, 2000, 0, mLocListener);
	}

	Criteria mycrit;

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
//		Location location = mLocManager.getLastKnownLocation(mLocManager
//				.getBestProvider(mycrit, true));
//		
//		if (location != null)
//			Toast.makeText(getApplicationContext(),
//					location.getLatitude() + ", " + location.getLongitude(),
//					Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// Toast.makeText(this, "GPS is paused", Toast.LENGTH_SHORT).show();
		// mLocManager.removeUpdates(mLocListener);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Toast.makeText(this, "GPS is resumed", Toast.LENGTH_SHORT).show();
		// mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 1000,0, mLocListener);
	}

}
