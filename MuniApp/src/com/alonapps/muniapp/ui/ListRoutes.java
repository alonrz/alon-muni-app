package com.alonapps.muniapp.ui;

import java.util.ArrayList;
import java.util.List;

import com.alonapps.muniapp.GpsManager;
import com.alonapps.muniapp.R;
import com.alonapps.muniapp.R.layout;
import com.alonapps.muniapp.datacontroller.DataManager;
import com.alonapps.muniapp.datacontroller.Predictions;
import com.alonapps.muniapp.datacontroller.Route;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListRoutes extends MyDataEnabledListActivity
{

	/** members **/
	private final Handler handler = new UIHandler(); // on UI thread!
	Context context;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		context = this;

		new Thread(new Runnable() {
			// Do network access point here
			@Override
			public void run()
			{
				List<Route> routeList = mDataManager.initAllRoutesWithDetails();
				if (routeList == null)
				{
					routeList = new ArrayList<Route>();
					Log.e(ListRoutes.class.toString(), "No routes found");
				}
				Message msg = handler.obtainMessage();
				msg.obj = routeList;
				handler.sendMessage(msg);
			}
		}).start();
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
			final List<Route> routeList = (List<Route>) msg.obj;

			for (int i = 0; i < routeList.size(); i++)
			{
				// tempList.set(i, routeList.get(i));
			}
			ArrayAdapter<Route> arrAdapter = new ArrayAdapter<Route>(context,
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
					Predictions currentPred = mDataManager.getSelectedPrediction();
					currentPred.setRouteTag(tag);
					Intent intent = new Intent(context, ListStops.class);
					intent.putExtra("routeTag", tag);
					startActivity(intent);
				}
			});

		}

	};

}
