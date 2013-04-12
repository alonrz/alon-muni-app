package com.alonapps.muniapp.ui;

import java.util.List;

import com.alonapps.muniapp.ConversionHelper;
import com.alonapps.muniapp.R;
import com.alonapps.muniapp.datacontroller.DataHelper;
import com.alonapps.muniapp.datacontroller.Predictions;
import com.alonapps.muniapp.datacontroller.Route;
import com.alonapps.muniapp.datacontroller.DataManager.DIRECTION;
import com.alonapps.muniapp.datacontroller.Predictions.Direction;
import com.alonapps.muniapp.datacontroller.Route.Stop;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ListStopsNearMe extends MyDataEnabledListActivity
{
	private Location mCurrentLocation;
	private float mMaxDistanceInMeters = 500;
	List<Route.Stop> mStopList = null;
	private List<Predictions> mPredictions = null;
	private boolean showInboundSelected = true;

	/** inner class UIHandler **/
	final class UIHandler extends Handler
	{
		public void handleMessage(Message msg)
		{
			/**
			 * Retrieve the contents of the message and then update the UI
			 */
			// make UI changes

			// List<Route.Stop> stopList = (List<Route.Stop>) msg.obj;
			List<Predictions> predictionsList = (List<Predictions>) msg.obj;

			if (predictionsList == null)
			{
				Log.e("ListStationsearMe", "Object predictionsList is null");
				Toast.makeText(context, "No predictions Found", Toast.LENGTH_LONG).show();
				return;
			}

			// Remove non active
			DataHelper.RemoveNonActive(predictionsList);
			//Remove extra stations which are far away
			DataHelper.RemoveAllButTwoClosestStations(predictionsList);

			// mStopList = stopList;
			mPredictions = predictionsList;

			MyCustomAdapter arrAdapter = new MyCustomAdapter();

			setListAdapter(arrAdapter);

			// showDirection(DIRECTION.Inbound, false);

			// list.setOnItemClickListener(new OnItemClickListener() {

			// /** Control the Click on an Item **/
			// @Override
			// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			// long arg3)
			// {
			//
			// CharSequence routeName = ((TextView) arg1).getText();
			//
			// String tag = "";
			// for (int i = 0; i < stopList.size(); i++)
			// {
			// if (stopList.get(i).getTitle() == routeName.toString())
			// {
			// tag = stopList.get(i).getTag();
			// break;
			// }
			// }
			//
			// // MUST HAVE ROUTE TAG. WORK WITH OBJECTs
			// Intent intent = new Intent(context, ListStops.class);
			// intent.putExtra("routeTag", tag);
			// startActivity(intent);
			// }
			// });

		}

	};

	/** members **/
	private final Handler handler = new UIHandler(); // on UI thread!
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		context = this;
		getListView().setDividerHeight(0);
		Bundle bundle = getIntent().getExtras();
		if (bundle == null)
		{
			Toast.makeText(this, "Error: No location info found", Toast.LENGTH_LONG).show();
			return;
			// Good place to enter error message for user
		}
		mCurrentLocation = (Location) bundle.getParcelable("location");
		Log.i(this.getClass().toString(), "mCurrentLocation == null: " + (mCurrentLocation == null));
		this.setTitle(DIRECTION.Inbound.name());
		new Thread(new Runnable() {
			// Do network access point here
			@Override
			public void run()
			{
				mStopList = mDataManager.getStopsNearLocation(mCurrentLocation,
						mMaxDistanceInMeters);
				Log.i("ListStationsNearMe", String.valueOf("mStopList.size(): " + mStopList.size()));
				List<Predictions> predictionsList = mDataManager.getPredictionsByStopsAsync(
						DIRECTION.Inbound, false);

				Message msg = handler.obtainMessage();
				msg.obj = predictionsList;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.show_stations, menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_stops, menu);
		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if (showInboundSelected == true)
		{
			menu.findItem(R.id.action_inbound).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			menu.findItem(R.id.action_outbound).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else
		{
			menu.findItem(R.id.action_outbound).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			menu.findItem(R.id.action_inbound).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}
DIRECTION mCurrentDirection = DIRECTION.Inbound;
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
			case R.id.action_inbound:
				mCurrentDirection = DIRECTION.Inbound;
				showDirection(DIRECTION.Inbound, true);
				showInboundSelected = true;
				invalidateOptionsMenu();
				return true;
			case R.id.action_outbound:
				showDirection(DIRECTION.Outbound, true);
				mCurrentDirection = DIRECTION.Outbound;
				showInboundSelected = false;
				invalidateOptionsMenu();
				return true;
			case R.id.action_refresh:
				showDirection(mCurrentDirection, true);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	public void showDirection(final DIRECTION dir, final boolean refresh)
	{
		this.setTitle(dir.name());

		// if (refresh == false)
		// return;
		// Must do this in new thread because it will create a network request.
		new Thread(new Runnable() {
			// Do network access point here

			@Override
			public void run()
			{
				// List of near by stops are already saved inside DataManager
				List<Predictions> predictionsList = mDataManager.getPredictionsByStopsAsync(dir,
						refresh);
				Message msg = handler.obtainMessage();
				msg.obj = predictionsList;
				handler.sendMessage(msg);
			}
		}).start();
	}

	public class MyCustomAdapter extends BaseAdapter
	{
		public MyCustomAdapter()
		{
			super();
			// Init the inflater
			this.mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		private LayoutInflater mInflater;
		Location tempLocation = new Location(mCurrentLocation.getProvider());
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			Predictions tempPred = mPredictions.get(position);
			convertView = mInflater.inflate(R.layout.predictions_single_item, null);
			// if(tempPred.getDirTitleBecauseNoPredictions() != null)
			// convertView.setVisibility(View.GONE);

			TextView titleView = (TextView) convertView.findViewById(R.id.stopTitleAndDist);
			TextView titleDistanceView = (TextView) convertView.findViewById(R.id.stopDistance);
			TextView nextTrainTimeView = (TextView) convertView.findViewById(R.id.nextTrainTime);
			TextView moreTrainsView = (TextView) convertView.findViewById(R.id.moreTrainTimes);
			// if(tempPred.get)

			float metersDist = 0f;
			for (Stop s : mStopList)
			{
				if (s.getStopTag().equalsIgnoreCase(tempPred.getStopTag()))
				{
					metersDist = s.getDistFromCurrentLocation();
					break;
				}
			}

			titleDistanceView.setText("("
					+ Math.round(ConversionHelper.convertMetersToFeet(metersDist)) + "ft)");

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
					TextView arrivingInTextView = (TextView) convertView
							.findViewById(R.id.arrivingInText);
					TextView minTextView = (TextView) convertView.findViewById(R.id.minText);
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
			TextView routeTagView = (TextView) convertView.findViewById(R.id.routeTag);
			if (tempPred.getRouteTag().length() > 3)
			{
				routeTagView.setTextSize(R.dimen.smaller_route_tag_text);
				routeTagView.setGravity(Gravity.CENTER);
			}
			
			routeTagView.setText(tempPred.getRouteTag());
			GradientDrawable gd ;
			if(getResources().getString(R.string.train_letters_only).contains(tempPred.getRouteTag().subSequence(0, 1)))
				gd = (GradientDrawable) getResources().getDrawable(R.drawable.circle_line_number);
			else
			{
				gd = (GradientDrawable) getResources().getDrawable(R.drawable.square_line_number);
				android.view.ViewGroup.LayoutParams lp = routeTagView.getLayoutParams();
				
				lp.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
				
				routeTagView.setLayoutParams(lp);
			}
			// gd.setColor(Color.parseColor("#006633"));
			Route tempRouteForColor = mDataManager.getRoute(tempPred.getRouteTag());
			gd.setColor(tempRouteForColor.getColor());
			routeTagView.setBackground(gd);
			routeTagView.setTextColor(tempRouteForColor.getOppColor());

			return convertView;
		}

		@Override
		public int getCount()
		{
			// TODO Auto-generated method stub
			return mPredictions.size();
		}

		@Override
		public Object getItem(int position)
		{
			// TODO Auto-generated method stub
			return mPredictions.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}
	}

}
