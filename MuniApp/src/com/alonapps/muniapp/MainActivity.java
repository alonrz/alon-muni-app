package com.alonapps.muniapp;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{

	Criteria mycrit;
	DataManager mDataManager;
	LocationManager mLocManager;
	LocationListener mLocListener;
	Location mLastKnownLocation;

	/** inner class UIHandler **/
	private final class UIHandler extends Handler
	{
		public void handleMessage(Message msg)
		{
			if (msg.arg1 == 1)
				changeLoadingProgress(true);
			else
				changeLoadingProgress(false);
		}
	};

	/** members **/
	private final Handler handler = new UIHandler(); // on UI thread!

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// This will init the large data object for quicker loading.
		mDataManager = DataManager.getDataManager(this);

		new Thread(new Runnable() {

			@Override
			public void run()
			{
				Message msg = handler.obtainMessage();
				msg.arg1 = 1;
				handler.sendMessage(msg);
				mDataManager.initAllRoutesWithDetails();// ** Starts a thread!
				msg = handler.obtainMessage();
				msg.arg1 = 0;
				handler.sendMessage(msg);

			}
		}).start();

		// Call method to start GPS
		initLocationManager();
	}

	// ProgressDialog dialog;
	private void changeLoadingProgress(boolean isShowProgress)
	{
		// if (dialog == null)
		// {
		// dialog = new ProgressDialog(this);
		// dialog.setCancelable(false);
		//
		// }

		View progressBarView = findViewById(R.id.pbLoading2);
		View ButtonShowLinesView = findViewById(R.id.btnShowLines);
		View ButtonShowNearStationsView = findViewById(R.id.btnShowNearStations);

		// ProgressBar pb = (ProgressBar)v;
		if (isShowProgress == false)
		{

			// pb.setVisibility(ProgressBar.INVISIBLE);
			progressBarView.setVisibility(View.INVISIBLE);
			ButtonShowLinesView.setEnabled(true);
			ButtonShowNearStationsView.setEnabled(true);
		} else
		{

			// pb.setVisibility(ProgressBar.VISIBLE);
			progressBarView.setVisibility(View.VISIBLE);
			ButtonShowLinesView.setEnabled(false);
			ButtonShowNearStationsView.setEnabled(false);
		}
	}

	private void initLocationManager()
	{
		// Get GPS working. Need manager and implementation of Listener.
		mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		mLocListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras)
			{
				// TODO Auto-generated method stub
			}

			@Override
			public void onProviderEnabled(String provider)
			{
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "Gps Enabled",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onProviderDisabled(String provider)
			{
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "Gps Disabled",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLocationChanged(Location location)
			{
				// TODO Auto-generated method stub
				mLastKnownLocation = location;
				TextView txtLat = (TextView) findViewById(R.id.lat);
				txtLat.setText("Lat: " + location.getLatitude());
				TextView txtLon = (TextView) findViewById(R.id.lon);
				txtLon.setText("Lat: " + location.getLongitude());

			}
		};
		mycrit = new Criteria();
		mycrit.setAccuracy(Criteria.ACCURACY_FINE);

		String strProviderName = mLocManager.getBestProvider(mycrit, true);
		Toast.makeText(this, strProviderName, Toast.LENGTH_SHORT).show();

		mLocManager.requestLocationUpdates(strProviderName, 2000, 0,
				mLocListener);
		mLastKnownLocation = mLocManager.getLastKnownLocation(strProviderName);
		if (mLastKnownLocation != null)
		{
			TextView txtLat = (TextView) findViewById(R.id.lat);
			txtLat.setText("Lat: " + mLastKnownLocation.getLatitude()
					+ "(last known)");
			TextView txtLon = (TextView) findViewById(R.id.lon);
			txtLon.setText("Lat: " + mLastKnownLocation.getLongitude()
					+ "(last known)");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick_showLines(View v)
	{

		Intent intent = new Intent(this, ListRoutes.class);
		startActivity(intent);
	}

	public void onClick_showMap(View v)
	{
		Intent intent = new Intent(this, ShowMap.class);
		startActivity(intent);
		// Location location = mLocManager.getLastKnownLocation(mLocManager
		// .getBestProvider(mycrit, true));
	}

	public void onClick_showStatiosNearMe(View v)
	{
		Intent intent = new Intent(this, ListStationsNearMe.class);
		if (mLastKnownLocation != null)
		{
			intent.putExtra("lat", mLastKnownLocation.getLatitude());
			intent.putExtra("lon", mLastKnownLocation.getLongitude());
		}
		startActivity(intent);
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		// Toast.makeText(this, "GPS is paused", Toast.LENGTH_SHORT).show();
		// mLocManager.removeUpdates(mLocListener);

	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		// Toast.makeText(this, "GPS is resumed", Toast.LENGTH_SHORT).show();
		// mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 1000,0, mLocListener);
	}

}
