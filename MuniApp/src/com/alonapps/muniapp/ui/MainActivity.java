package com.alonapps.muniapp.ui;

import com.alonapps.muniapp.GpsManager;
import com.alonapps.muniapp.LocationTrackerBaseActivity;
import com.alonapps.muniapp.R;
import com.alonapps.muniapp.datacontroller.DataManager;

import android.location.Criteria;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends LocationTrackerBaseActivity
{

	Criteria mycrit;
	DataManager mDataManager;

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
		mDataManager = DataManager.getInstance(this);

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

		// First, get an instance of the SensorManager
		SensorManager sMan = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		// Second, get the sensor you're interested in
		final Sensor magnetField = sMan.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		// Third, implement a SensorEventListener class
		SensorEventListener magnetListener = new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event)
			{
				// implement what you want to do here
				TextView txtLat = (TextView) findViewById(R.id.lat);
				txtLat.setText("event.values[0] - " + event.values[0]);

			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy)
			{
				// do things if you're interested in accuracy changes
				TextView txtLon = (TextView) findViewById(R.id.lon);
				txtLon.setText("compass: " + sensor.getClass().toString());

			}
		};

		// Finally, register your listener
		sMan.registerListener(magnetListener, magnetField, SensorManager.SENSOR_DELAY_NORMAL);

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
		View ButtonShowTabs = findViewById(R.id.btnShowTabs);

		// ProgressBar pb = (ProgressBar)v;
		if (isShowProgress == false)
		{

			// pb.setVisibility(ProgressBar.INVISIBLE);
			progressBarView.setVisibility(View.INVISIBLE);
			ButtonShowLinesView.setEnabled(true);
			ButtonShowNearStationsView.setEnabled(true);
			ButtonShowTabs.setEnabled(true);
		} else
		{

			// pb.setVisibility(ProgressBar.VISIBLE);
			progressBarView.setVisibility(View.VISIBLE);
			ButtonShowLinesView.setEnabled(false);
			ButtonShowNearStationsView.setEnabled(false);
			ButtonShowTabs.setEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		getActionBar().setDisplayHomeAsUpEnabled(false);
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
		try
		{
			Intent intent = new Intent(this, ListStopsNearMe.class);
			Location lastKnownLoc =GpsManager.getInstance().getLastKnownLocation(); 
			if (lastKnownLoc != null)
			{
				intent.putExtra("location", lastKnownLoc);
			}
			startActivity(intent);
		} catch (Exception e)
		{
			Log.e(this.getClass().toString(), "error");
		}
	}
	
	public void onClick_showTabs(View v)
	{
		//Intent intent = new Intent(this, ListStopsNearMe2.class);
		Intent intent = new Intent(this, TestTabs.class);
		startActivity(intent);
	}

}
