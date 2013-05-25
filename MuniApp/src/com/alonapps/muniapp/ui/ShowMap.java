package com.alonapps.muniapp.ui;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.alonapps.muniapp.R;
import com.alonapps.muniapp.locationcontroller.LocationTrackerBaseActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowMap extends LocationTrackerBaseActivity {
	static final LatLng HAMBURG = new LatLng(53.558, 9.927);
	static final LatLng KIEL = new LatLng(53.551, 9.993);
	static final LatLng SFSU = new LatLng(37.822239550388515,
			-122.25);
	static final LatLng CURRENT = new LatLng(0, 0);

	private GoogleMap map;

	LocationManager mLocManager;
	LocationListener mLocListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_layout);

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

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			Marker current = null;

			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub

				if (current == null) {
					current = map.addMarker(new MarkerOptions().position(
							CURRENT).title("You are here"));
				}
				// Move the camera instantly to location  with a zoom of 15.
				current.setPosition(new LatLng(location.getLatitude(), 
						location.getLongitude()));
			
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT, 15));

			}
		};

		MapFragment mapFrag = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map));
		map = mapFrag.getMap();
		if (map != null) {
			// Marker kiel = map.addMarker(new MarkerOptions()
			// .position(KIEL)
			// .title("Kiel")
			// .snippet("Kiel is cool")
			// .icon(BitmapDescriptorFactory
			// .fromResource(R.drawable.ic_launcher)));
			Marker sfsu = map.addMarker(new MarkerOptions().position(SFSU)
					.title("My University").snippet("SFSU is awesome"));

			// Move the camera instantly to hamburg with a zoom of 15.
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(SFSU, 10));
			// Zoom in, animating the camera.
			 //map.animateCamera(CameraUpdateFactory.zoomTo(10), 3000, null);
			 //Zoom in alternative
			
			Handler handler = new Handler();
			
			
		
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
					
				}
			}, 1500);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
