package com.alonapps.muniapp.ui.fragments;

import java.util.List;

import com.alonapps.muniapp.ConversionHelper;
import com.alonapps.muniapp.R;
import com.alonapps.muniapp.datacontroller.DataManager;
import com.alonapps.muniapp.datacontroller.FavoriteOpenHelper;
import com.alonapps.muniapp.datacontroller.Predictions;
import com.alonapps.muniapp.datacontroller.Route;
import com.alonapps.muniapp.datacontroller.DataManager.DIRECTION;
import com.alonapps.muniapp.datacontroller.Route.Stop;
import com.alonapps.muniapp.datacontroller.Predictions.Direction;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListStopsNearMeFragment extends ListFragment
{
	private Location mCurrentLocation;

	List<Route.Stop> mStopList = null;
	private List<Predictions> mPredictions = null;
	private boolean showInboundSelected = true;
	DIRECTION mCurrentDirection = DIRECTION.Inbound;
	Context mContext;

	/** members **/
	private final Handler handler = new UIHandler(); // on UI thread!

	public ListFragment getInstance()
	{
		ListFragment frag = new ListStopsNearMeFragment();
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		setHasOptionsMenu(true); // add menus - create call to
									// onCreateOptionsMenu

		// Bundle bundle = getArguments();
		if (mPredictions == null || mPredictions.size() == 0)
		{
			Toast.makeText(mContext, "ListStopsNearMeFragment: mPredictions == null", Toast.LENGTH_LONG).show();
			Log.e(this.getClass().getSimpleName(), "ListStopsNearMeFragment: mPredictions == null");
			// Good place to enter error message for user
		}
		else if(mPredictions.size() == 0)
		{
			Toast.makeText(mContext, "ListStopsNearMeFragment: mPredictions.size == 0", Toast.LENGTH_LONG).show();
		}

		getActivity().setTitle(DIRECTION.Inbound.name());
		new Thread(new Runnable() {
			// Do network access point here
			@Override
			public void run()
			{
				mStopList = DataManager.getInstance(mContext).getRecentStopsNearLocation();
				mPredictions = DataManager.getInstance(mContext).getPredictionsByStopsAsync(DIRECTION.Inbound, false);
				
				// mCurrentLocation, mMaxDistanceInMeters);
				// Log.i("ListStationsNearMe",
				// String.valueOf("mStopList.size(): " + mStopList.size()));
				// List<Predictions> predictionsList =
				// DataManager.getInstance(mContext)
				// .getPredictionsByStopsAsync(DIRECTION.Inbound, false);

				Message msg = handler.obtainMessage();
				msg.obj = mPredictions;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.list_stops, menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO add menu actions
		return true;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		// TODO change menus as needed.
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		// TODO Auto-generated method stub
		Toast.makeText(getActivity(), getListView().getItemAtPosition(position).toString(),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// setlistadapter here
		getListView().setDividerHeight(0);
	}

	
	private class MyCustomAdapter extends BaseAdapter
	{
		List<Predictions> innerPredictions = null;
		public MyCustomAdapter(List<Predictions> listPredictions)
		{
			super();
			// Init the inflater
			innerPredictions = listPredictions;
			Object obj = getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.mInflater = (LayoutInflater) obj;

		}

		private LayoutInflater mInflater;

		// Location tempLocation = new Location(mCurrentLocation.getProvider());

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{

			Predictions tempPred = mPredictions.get(position);
			convertView = mInflater.inflate(R.layout.predictions_single_item, null);
			if (tempPred.getDirTitleBecauseNoPredictions() != null)
				convertView.setVisibility(View.GONE);

			CheckBox star = (CheckBox) convertView.findViewById(R.id.star);
			star.setOnClickListener(startListenFav);

			TextView titleView = (TextView) convertView.findViewById(R.id.stopTitleAndDist);
			titleView.setTextColor(Color.BLACK);
			TextView stopId = (TextView) convertView.findViewById(R.id.stopId);
			TextView titleDistanceView = (TextView) convertView.findViewById(R.id.stopDistance);
			titleDistanceView.setTextColor(Color.argb(255, 100, 100, 100));
			TextView nextTrainTimeView = (TextView) convertView.findViewById(R.id.nextTrainTime);
			nextTrainTimeView.setTextColor(Color.BLACK);
			TextView moreTrainsView = (TextView) convertView.findViewById(R.id.moreTrainTimes);
			moreTrainsView.setTextColor(Color.BLACK);

			/****
			 * Inflating the static label to fix the forecolor due to tab style
			 ***/
			TextView arrivingInText = (TextView) convertView.findViewById(R.id.arrivingInText);
			arrivingInText.setTextColor(Color.BLACK);
			TextView minText = (TextView) convertView.findViewById(R.id.minText);
			minText.setTextColor(Color.BLACK);

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
			stopId.setText(tempPred.getStopTag()); // hidden field for stop tag.
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
			// gd.setColor(Color.parseColor("#006633"));
			Route tempRouteForColor = DataManager.getInstance(mContext).getRoute(
					tempPred.getRouteTag());
			gd.setColor(tempRouteForColor.getColor());
			routeTagView.setBackground(gd);
			routeTagView.setTextColor(tempRouteForColor.getOppColor());

			return convertView;
		}

		private OnClickListener startListenFav = new OnClickListener() {

			@Override
			public void onClick(View view)
			{
				if (view.getClass() != CheckBox.class)
					return;

				if (!view.getClass().getName().equalsIgnoreCase(CheckBox.class.getName()))
				{
					return;
				}
				// add info to DB

				// View parent = (View)view.getParent();
				TextView stopId = (TextView) getActivity().findViewById(R.id.stopId);
				TextView routeTag = (TextView) getActivity().findViewById(R.id.routeTag);

				SQLiteDatabase dbFav = DataManager.getInstance(mContext).getFavoriteOpenHelper()
						.getWritableDatabase();
				if (((CheckBox) view).isChecked() == true)
				{
					ContentValues values = new ContentValues();
					values.put(FavoriteOpenHelper.KEY_ROUTE_ID, routeTag.getText().toString());
					values.put(FavoriteOpenHelper.KEY_STOP_ID, stopId.getText().toString());
					dbFav.insert(FavoriteOpenHelper.FAVORITES_TABLE_NAME, null, values);
					Toast.makeText(
							getActivity(),
							"ADD: stopid: " + stopId.getText() + ", routetag: "
									+ routeTag.getText() + ", is checked: "
									+ ((CheckBox) view).isChecked(), Toast.LENGTH_SHORT).show();
				} else
				{
					dbFav.delete(FavoriteOpenHelper.FAVORITES_TABLE_NAME, FavoriteOpenHelper.KEY_ROUTE_ID + "=? AND " + FavoriteOpenHelper.KEY_STOP_ID + "=?", 
							new String[] {stopId.getText().toString(), routeTag.getText().toString(), } );
					Toast.makeText(
							getActivity(),
							"DELETE: stopid: " + stopId.getText() + ", routetag: " + routeTag.getText()
									+ ", is checked: " + ((CheckBox) view).isChecked(),
							Toast.LENGTH_SHORT).show();
				}

				dbFav.close();

				

			}
		};

		@Override
		public int getCount()
		{
			return mPredictions.size();
		}

		@Override
		public Object getItem(int position)
		{
			return mPredictions.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			// TODO Auto-generated method stub
			return position;
		}
	}

	/** inner class UIHandler **/
	final class UIHandler extends Handler
	{
		public void handleMessage(Message msg)
		{
			/**
			 * Retrieve the contents of the message and then update the UI
			 */
			// make UI changes 
			List<Predictions> predictionsList = (List<Predictions>) msg.obj;

			MyCustomAdapter arrAdapter = new MyCustomAdapter(predictionsList);

			setListAdapter(arrAdapter);
			arrAdapter.notifyDataSetChanged();
		}
	};
}
