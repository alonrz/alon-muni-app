package com.alonapps.muniapp.ui;

import java.util.List;

import com.alonapps.muniapp.GpsManager;
import com.alonapps.muniapp.LocationTrackerBaseActivity;
import com.alonapps.muniapp.R;
import com.alonapps.muniapp.datacontroller.DataHelper;
import com.alonapps.muniapp.datacontroller.DataManager;
import com.alonapps.muniapp.datacontroller.Predictions;
import com.alonapps.muniapp.datacontroller.DataManager.DIRECTION;

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
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends LocationTrackerBaseActivity
{
	Context mContext;
	Criteria mycrit;
	DataManager mDataManager;
	private float mMaxDistanceInMeters = 500;
	TextView txtMessages = null;

	/** inner class UIHandler **/
	private final class UIHandler extends Handler
	{
		
		public void handleMessage(Message msg)
		{
			if (msg.arg1 == 1)
				changeLoadingProgress(true, msg);
			else
			 	changeLoadingProgress(false, msg);
		}
	};

	/** members **/
	private final Handler handler = new UIHandler(); // on UI thread!

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_splash_screen);
		txtMessages = (TextView)findViewById(R.id.txtLoadMessages);

		// This will init the large data object for quicker loading.
		mDataManager = DataManager.getInstance(this);

		new Thread(new Runnable() {

			@Override
			public void run()
			{
				Location lastKnownLoc = GpsManager.getInstance().getLastKnownLocation();
				Message msg = handler.obtainMessage();
				msg.arg1 = 1;
				
				msg.obj = "Loading Routes";
				handler.sendMessage(msg);
				mDataManager.initAllRoutesWithDetails();// ** Starts a thread!
				
				msg = handler.obtainMessage();
				msg.obj = "Getting Nearby Stops";
				handler.sendMessage(msg);
				DataManager.getInstance(mContext).getStopsNearLocation(
						lastKnownLoc, mMaxDistanceInMeters);
				
				msg = handler.obtainMessage();
				msg.obj = "Getting Predictions";
				handler.sendMessage(msg);
				List<Predictions> predictionsList = DataManager.getInstance(mContext)
						.getPredictionsByStopsAsync(DIRECTION.Inbound, true);
				
				if (predictionsList == null)
				{
					Log.e("ListStationsearMe", "Object predictionsList is null");
					Toast.makeText(mContext, "No predictions Found", Toast.LENGTH_LONG).show();
					return;
				}

				// Remove non active
				DataHelper.RemoveNonActive(predictionsList);
				// Remove extra stations which are far away
				DataHelper.RemoveAllButTwoClosestStations(predictionsList);

//				mPredictions = predictionsList;

				
				msg = handler.obtainMessage();
				msg.arg1 = 0;
				msg.obj = "Done!";
				handler.sendMessage(msg);

			}
		}).start();
	}

	private void changeLoadingProgress(boolean isShowProgress,Message msg )
	{
		View progressBarView = findViewById(R.id.progressBar1);
		txtMessages.setText(msg.obj.toString());
		if (isShowProgress == false)
		{
			progressBarView.setVisibility(View.INVISIBLE);
			onClick_showTabs(null);
		} else
		{
			progressBarView.setVisibility(View.VISIBLE);
			
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
	
	public void onClick_showTabs(View v)
	{
		Intent intent = new Intent(this, TabsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	protected void onStop()
	{
		// this will delete this activity from the stack.
		super.onStop();
		this.finish();
	}

}
