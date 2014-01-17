package com.alonapps.muniapp.ui;

import com.alonapps.muniapp.ConversionHelper;
import com.alonapps.muniapp.R;
import com.alonapps.muniapp.StopNotFoundException;
import com.alonapps.muniapp.datacontroller.DataManager;
import com.alonapps.muniapp.datacontroller.DataManager.DIRECTION;
import com.alonapps.muniapp.datacontroller.Predictions;
import com.alonapps.muniapp.datacontroller.Route;
import com.alonapps.muniapp.datacontroller.Predictions.Direction;
import com.alonapps.muniapp.locationcontroller.GpsManager;
import com.alonapps.muniapp.locationcontroller.LocationTrackerBaseActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This is the single stop predictino screen, with any messages and a map to indicate where the station is. 
 * @author alon
 *
 */
public class ShowSingleStop extends LocationTrackerBaseActivity
{

	Context mContext;
	private UIHandler handler = new UIHandler();
	private DataManager mDataManager;
	Predictions mCurrentPred;
	GpsManager mGpsManager;
	DIRECTION dir;
	Location selectedStoploc = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		mContext = this;
		super.onCreate(savedInstanceState);
		getActionBar().setHomeButtonEnabled(true);
		mDataManager = DataManager.getInstance(this);
		mGpsManager = GpsManager.getInstance();


		new Thread(new Runnable() {

			@Override
			public void run()
			{
				DataManager dMan = DataManager.getInstance(mContext);
				mCurrentPred = dMan.getPredictionsByStopAndRoute(dMan.getSelectedPrediction()
						.getStopId(), dMan.getSelectedPrediction().getRouteTag());
				try
				{
					selectedStoploc = mDataManager.getStopLocation(mCurrentPred.getStopTag(),
							mCurrentPred.getRouteTag(), GpsManager.getInstance()
									.getmBestProviderName());
				} catch (StopNotFoundException e)
				{
					Log.e(this.getClass().toString(), "Error fetching location");
					e.printStackTrace();
				}
				Message msg = handler.obtainMessage();
				msg.obj = mCurrentPred;
				handler.sendMessage(msg);

			}
		}).start();
	}

	// private LayoutInflater mInflater;
	final class UIHandler extends Handler
	{
		public void handleMessage(Message msg)
		{
			setContentView(R.layout.activity_show_single_stop);
			Predictions tempPred = (Predictions) msg.obj;

			/**** control prediction box ****/
			fillPredictionUI(tempPred);

			/**** Control map ****/
			fillMapUI(tempPred);

			/**** Control messages ****/
			ListView myListView = (ListView) findViewById(R.id.list_messages);
			MyCustomAdapter adapter = new MyCustomAdapter();
			myListView.setAdapter(adapter);

		}

		private void fillPredictionUI(Predictions tempPred)
		{
			Bundle extras = getIntent().getExtras();
			if (extras == null)
			{
				Log.e(this.getClass().toString(), "Error initializing directions. No extras given");
			} else
			{
				dir = DIRECTION.valueOf(extras.getString("dir"));
			}

			TextView titleView = (TextView) findViewById(R.id.stopTitleAndDist);
			TextView titleDistanceView = (TextView) findViewById(R.id.stopDistance);
			TextView nextTrainTimeView = (TextView) findViewById(R.id.nextTrainTime);
			TextView moreTrainsView = (TextView) findViewById(R.id.moreTrainTimes);


			float metersDist = 0f;
			Location myLoc = GpsManager.getInstance().getLastKnownLocation((Activity)mContext);
			if (myLoc == null)
			{
				Log.e(this.getClass().toString(), "my loc is null");
				titleDistanceView.setText("no gps");
			} else
			{
				metersDist = selectedStoploc.distanceTo(myLoc);
				titleDistanceView.setText("("
						+ Math.round(ConversionHelper.convertMetersToFeet(metersDist)) + "ft)");
			}

			titleView.setText(tempPred.getStopTitle());
			if (tempPred.getDirTitleBecauseNoPredictions() != null)
			{
				nextTrainTimeView.setText("no");
				moreTrainsView.setText("Service not running now");
			} else
			{
				Direction[] dirs = tempPred.getAllDirections();
				int minutes = dirs[0].getAllPredictions().get(0).getMinutes();
				if (minutes == 0)
				{
					TextView arrivingInTextView = (TextView) findViewById(R.id.arrivingInText);
					TextView minTextView = (TextView) findViewById(R.id.minText);
					nextTrainTimeView.setText("Now");
					arrivingInTextView.setText("Arriving ");
					minTextView.setVisibility(View.INVISIBLE);

				} else
				{
					nextTrainTimeView.setText("" + minutes);
				}
				String nextTrains = "Next: ";

				for (int i = 1; i < dirs[0].getAllPredictions().size(); i++)
				{
					nextTrains += dirs[0].getAllPredictions().get(i).getMinutes();
					if (i < dirs[0].getAllPredictions().size() - 1)
						nextTrains += ", ";
					if (i == dirs[0].getAllPredictions().size() - 1)
						nextTrains += " min";
				}
				if (dirs[0].getAllPredictions().size() <= 1)
					nextTrains = "No more predictions available";
				moreTrainsView.setText(nextTrains);
			}
			TextView routeTagView = (TextView) findViewById(R.id.routeTag);
			if (tempPred.getRouteTag().length() > 3)
			{
				routeTagView.setTextSize(R.dimen.smaller_route_tag_text);
				routeTagView.setGravity(Gravity.CENTER);
			}

			routeTagView.setText(tempPred.getRouteTag());
			GradientDrawable gd;
			if (getResources().getString(R.string.train_letters_only).contains(
					tempPred.getRouteTag().subSequence(0, 1)))
				gd = (GradientDrawable) getResources().getDrawable(R.drawable.circle_line_number);
			else
			{
				gd = (GradientDrawable) getResources().getDrawable(R.drawable.square_line_number);
				android.view.ViewGroup.LayoutParams lp = routeTagView.getLayoutParams();

				lp.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
						getResources().getDisplayMetrics());

				routeTagView.setLayoutParams(lp);
			}
			Route tempRouteForColor = mDataManager.getRoute(tempPred.getRouteTag());
			gd.setColor(tempRouteForColor.getColor());
			routeTagView.setBackground(gd);
			routeTagView.setTextColor(tempRouteForColor.getOppColor());

			/*** set the title of the activity ***/
			if (tempPred.getAllDirections().length > 0)
				ShowSingleStop.this
						.setTitle(mCurrentPred.getAllDirections()[0].getDirectionTitle());
			else
				ShowSingleStop.this.setTitle(mCurrentPred.getDirTitleBecauseNoPredictions());

		}

		private void fillMapUI(Predictions tempPred)
		{
			GoogleMap map;
			MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(
					R.id.map_per_stop);

			map = mapFrag.getMap();
			if (selectedStoploc == null)
				return;

			LatLng MYPOSITION = null;
			Location myLoc = GpsManager.getInstance().getLastKnownLocation((Activity)mContext);
			if (myLoc == null)
			{
				Log.e(this.getClass().toString(), "my loc is null");

			} else
			{
				MYPOSITION = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
			}

			LatLng CURRENTSTOP = new LatLng(selectedStoploc.getLatitude(),
					selectedStoploc.getLongitude());

			if (map != null)
			{
				MarkerOptions markerStop = new MarkerOptions();
				markerStop.position(CURRENTSTOP);
				markerStop.title(tempPred.getStopTitle());
				markerStop.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pin_red));
				if (tempPred.getAllDirections().length > 0)
					markerStop.snippet(tempPred.getAllDirections()[0].getDirectionTitle());
				else
					markerStop.snippet(tempPred.getDirTitleBecauseNoPredictions());

				map.addMarker(markerStop);
				if (myLoc != null)
				{
					MarkerOptions markerMe = new MarkerOptions();
					markerMe.position(MYPOSITION);
					markerMe.title("You Are Here");
					//markerMe.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
					BitmapDescriptor bd =BitmapDescriptorFactory.fromResource(R.drawable.walking_icon_hi);
					markerMe.icon(bd);
					
					map.addMarker(markerMe);

					/**** zoom to include 2 points *****/
					LatLngBounds bounds = new LatLngBounds.Builder()
							.include(new LatLng(CURRENTSTOP.latitude, CURRENTSTOP.longitude))
							.include(new LatLng(MYPOSITION.latitude, MYPOSITION.longitude)).build();

					Point displaySize = new Point();
					getWindowManager().getDefaultDisplay().getSize(displaySize);

					map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250,
							30));
				} else
				{
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENTSTOP, 15f));
				}
			}
		}
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
			case android.R.id.home:
				Intent intent = new Intent(this, TabsActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("tabid", 1);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}
	
	class MyCustomAdapter extends BaseAdapter
	{
		public MyCustomAdapter()
		{
			super();
			// Init the inflater
			this.mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		private LayoutInflater mInflater;

		@Override
		public int getCount()
		{

			return mCurrentPred.getMessages().length;
		}

		@Override
		public Object getItem(int position)
		{

			return mCurrentPred.getMessages()[position];
		}

		@Override
		public long getItemId(int position)
		{

			return mCurrentPred.getMessages()[position].hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			convertView = mInflater.inflate(R.layout.message_item, null);
			TextView messageTextView = (TextView) convertView
					.findViewById(R.id.single_message_text);
			messageTextView.setText(mCurrentPred.getMessages()[position]);
			return convertView;
		}
	}
}
