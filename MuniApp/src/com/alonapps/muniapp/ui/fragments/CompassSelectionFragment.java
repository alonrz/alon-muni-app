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

		Bundle bundle = getArguments();
		if (bundle == null)
		{
			Toast.makeText(mContext, "Error: No location info found", Toast.LENGTH_LONG).show();
			return;
			// return super.onCreateView(inflater, container,
			// savedInstanceState);
			// Good place to enter error message for user
		}
		mCurrentLocation = (Location) bundle.getParcelable("location");
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

				intent.putExtra("inbound", nextStopInbound);
				intent.putExtra("outbound", nextStopOutbound);
				startActivity(intent);
				break;
			}
		}
		Toast.makeText(getActivity(), listView.getItemAtPosition(position).toString(),
				Toast.LENGTH_SHORT).show();
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
			// list.setOnItemClickListener(new OnItemClickListener() {
			//
			// /** Control the Click on an Item **/
			// @Override
			// public void onItemClick(AdapterView<?> arg0, View arg1, int
			// position, long arg3)
			// {
			//
			// // CharSequence routeName = ((TextView) arg1).getText();
			//
			// String tag = routeList.get(position).getTag();
			// // for(int i=0; i<routeList.size(); i++)
			// // {
			// // if(routeList.get(i).getTitle() == routeName.toString())
			// // {
			// // tag = routeList.get(i).getTag();
			// // break;
			// // }
			// // }
			//
			// // Toast.makeText(context, tag, Toast.LENGTH_LONG).show();
			//
			// // MUST HAVE ROUTE TAG. WORK WITH OBJECTs
			// Predictions currentPred =
			// DataManager.getInstance(mContext).getSelectedPrediction();
			// currentPred.setRouteTag(tag);
			// Intent intent = new Intent(mContext, ListStops.class);
			// intent.putExtra("routeTag", tag);
			// startActivity(intent);
			// }
			// });

		}

	};

}
