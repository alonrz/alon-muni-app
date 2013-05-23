package com.alonapps.muniapp.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import com.alonapps.muniapp.R;
import com.alonapps.muniapp.datacontroller.DataManager;
import com.alonapps.muniapp.datacontroller.Predictions;
import com.alonapps.muniapp.datacontroller.Route;
import com.alonapps.muniapp.datacontroller.DataManager.DIRECTION;
import com.alonapps.muniapp.ui.ListStops;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListRoutesFragment extends ListFragment
{
	Context mContext;
	private Location mCurrentLocation;
	private final Handler handler = new UIHandler(); // on UI thread!

//	public ListFragment getInstance()
//	{
//		ListFragment frag = new ListRoutesFragment();
//		return frag;
//	}

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
				List<Route> routeList = DataManager.getInstance(mContext).initAllRoutesWithDetails();
				if (routeList == null)
				{
					routeList = new ArrayList<Route>();
					Log.e(ListRoutesFragment.class.getSimpleName(), "no routes found");
				}
				Message msg = handler.obtainMessage();
				msg.obj = routeList;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_list, container, false);
	}
	
	final class UIHandler extends Handler
	{
		public void handleMessage(Message msg)
		{
			/**
			 * Retrieve the contents of the message and then update the UI
			 */
			// make UI changes
			@SuppressWarnings("unchecked")
			final List<Route> routeList = (List<Route>) msg.obj;

			for (int i = 0; i < routeList.size(); i++)
			{
				// tempList.set(i, routeList.get(i));
			}
			ArrayAdapter<Route> arrAdapter = new ArrayAdapter<Route>(mContext,
					R.layout.single_list_item, routeList);

			setListAdapter(arrAdapter);

			ListView list = getListView();
			list.setTextFilterEnabled(true);
			list.setOnItemClickListener(new OnItemClickListener() {

				/** Control the Click on an Item **/
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
				{

					// CharSequence routeName = ((TextView) arg1).getText();

					String tag = routeList.get(position).getTag();
					// for(int i=0; i<routeList.size(); i++)
					// {
					// if(routeList.get(i).getTitle() == routeName.toString())
					// {
					// tag = routeList.get(i).getTag();
					// break;
					// }
					// }

					// Toast.makeText(context, tag, Toast.LENGTH_LONG).show();

					// MUST HAVE ROUTE TAG. WORK WITH OBJECTs
					Predictions currentPred = DataManager.getInstance(mContext).getSelectedPrediction();
					currentPred.setRouteTag(tag);
					//This will show list of stops to choose from 
					Intent intent = new Intent(mContext, ListStops.class);
					intent.putExtra("routeTag", tag);
					startActivity(intent);
				}
			});

		}

	};

}
