package com.alonapps.muniapp.ui.fragments;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.alonapps.muniapp.R;
import com.alonapps.muniapp.datacontroller.DataManager;
import com.alonapps.muniapp.datacontroller.Route;
import com.alonapps.muniapp.datacontroller.DataManager.DIRECTION;
import com.alonapps.muniapp.datacontroller.Route.Stop;
import com.alonapps.muniapp.locationcontroller.GpsManager;
import com.alonapps.muniapp.ui.CompassActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This is the compass fragment, shows a list of nearby routes and leads to the compass view. 
 * @author alon
 *
 */
public class CompassSelectionFragment extends ListFragment
{
	private Context mContext;
	private Location mCurrentLocation;
	private final Handler handler = new UIHandler(); // on UI thread!
	private Hashtable<SimpleEntry<String, DIRECTION>, Stop> mRouteTagStopsTable;

	public ListFragment getInstance()
	{
		ListFragment frag = new ListRoutesFragment();
		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		mCurrentLocation = GpsManager.getInstance().getLastKnownLocation();
		Log.i(this.getClass().toString(), "mCurrentLocation == null: " + (mCurrentLocation == null));
		
		getActivity().setTitle(DIRECTION.Inbound.name());
		
		new Thread(new Runnable() {
			// Do network access point here
			@Override
			public void run()
			{
				mRouteTagStopsTable = DataManager.getInstance(mContext).getRoutesNearLocation(
						mCurrentLocation, 500.0f);
				if (mRouteTagStopsTable.size() == 0)
				{
					Log.e(CompassSelectionFragment.class.getSimpleName(), "No routes nearby found");
				}
				Message msg = handler.obtainMessage();
				msg.obj = mRouteTagStopsTable;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_list_header_text, container, false);
	}

	@Override
	public void onListItemClick(ListView listView, View v, int position, long id)
	{
		// Get closest stop associated with the route selected as the home
		// station.
		Stop nearestStop = null;
		DataManager dataMan = DataManager.getInstance(mContext);
		String routeTitle = listView.getItemAtPosition(position).toString();
		for (SimpleEntry<String, DIRECTION> key : mRouteTagStopsTable.keySet())
		{
			if (dataMan.getRoute(key.getKey()).getTitle().equalsIgnoreCase(routeTitle))
			{
				nearestStop = mRouteTagStopsTable.get(key);

				Stop nextStopInbound = dataMan.getNextStop(key.getKey(), DIRECTION.Inbound,
						nearestStop);
				Stop nextStopOutbound = dataMan.getNextStop(key.getKey(), DIRECTION.Outbound,
						nearestStop);

				Intent intent = new Intent(mContext, CompassActivity.class);
				intent.putExtra("inbound-lat", nextStopInbound.getLat());
				intent.putExtra("inbound-lon", nextStopInbound.getLon());
				intent.putExtra("outbound-lat", nextStopOutbound.getLat());
				intent.putExtra("outbound-lon", nextStopOutbound.getLon());
				startActivity(intent);
				break;
			}
		}
		Toast.makeText(getActivity(), routeTitle, Toast.LENGTH_SHORT).show();
	}

	final class UIHandler extends Handler
	{

		public void handleMessage(Message msg)
		{
			/**
			 * Retrieve the contents of the message and then update the UI
			 */
			// make UI changes
			final Hashtable<SimpleEntry<String, DIRECTION>, Stop> routeList = (Hashtable<SimpleEntry<String, DIRECTION>, Stop>) msg.obj;

			List<String> routeListAsArray = new ArrayList<String>();
			for (SimpleEntry<String, DIRECTION> key : routeList.keySet())
			{
				routeListAsArray.add(DataManager.getInstance(mContext).getRoute(key.getKey())
						.getTitle());
			}

			ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(mContext,
					R.layout.single_list_item, routeListAsArray);

			setListAdapter(arrAdapter);

			ListView list = getListView();
			list.setTextFilterEnabled(true);
		}
	};
}
