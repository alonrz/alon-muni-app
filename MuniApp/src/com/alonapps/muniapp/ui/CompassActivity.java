package com.alonapps.muniapp.ui;

import com.alonapps.muniapp.R;
import com.alonapps.muniapp.datacontroller.Route;
import com.alonapps.muniapp.datacontroller.Route.Stop;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

public class CompassActivity extends Activity
{


	private static SensorManager sensorService;
	ImageView img;
	private Sensor sensor;
	double mOffsetToNorth = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// compassView = new CompassView(this);
		
		setContentView(R.layout.activity_compass);
		img = (ImageView)findViewById(R.id.compassArrow);
		sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		if (sensor != null)
		{
			sensorService.registerListener(mySensorEventListener, sensor,
					SensorManager.SENSOR_DELAY_UI);
			Log.i("Compass MainActivity", "Registerered for ORIENTATION Sensor");

		} else
		{
			Log.e("Compass MainActivity", "ORIENTATION Sensor not found");
			Toast.makeText(this, "ORIENTATION Sensor not found", Toast.LENGTH_LONG).show();
			finish();
		}
		
		//decide on direction of inbound (the new north)
		double nextStopInboundLat = getIntent().getExtras().getDouble("inbound-lat");
		double nextStopInboundLon = getIntent().getExtras().getDouble("inbound-lon");
		double nextStopOutboundLat = getIntent().getExtras().getDouble("inbound-lat");
		double nextStopOutboundLon = getIntent().getExtras().getDouble("inbound-lon");
		this.mOffsetToNorth = calculateOffsetToNorth(nextStopInboundLat, nextStopInboundLon, 
				nextStopOutboundLat, nextStopOutboundLon);
	}

	private double calculateOffsetToNorth(double inboundLat, double inboundLon, double outboundLat, double outboundLon)
	{
		double opp, adj;

		opp = Math.abs(outboundLon - inboundLon);
		adj = Math.abs(inboundLat - outboundLat);
		if(opp == 0 || adj == 0)
			return 0.0;
		Log.i("inbound", "inbound ["+inboundLat+","+inboundLon+"]");
		Log.i("ountbount", "outbound ["+outboundLat+","+outboundLon+"]" );
		
		double invtan = Math.atan(opp/adj);
		double deg = Math.toDegrees(invtan);
		Log.i(this.getClass().getSimpleName(), "offset = " + deg);
		return deg;
	}
	
	private SensorEventListener mySensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			/*** angle between the magnetic north direction
			 *   0=North, 90=East, 180=South, 270=West
			 ***/
			float azimuth = event.values[0];
			//CompassView compView = (CompassView) findViewById(R.id.compassView); // compassView.updateData(azimuth);
			
			img.setRotation((float)(360 - event.values[0] + mOffsetToNorth));
			//compView.updateData(azimuth);
		}
	};

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (sensor != null)
		{
			sensorService.unregisterListener(mySensorEventListener);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compass, menu);
		return true;
	}

}
