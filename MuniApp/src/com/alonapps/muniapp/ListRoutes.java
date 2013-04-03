package com.alonapps.muniapp;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListRoutes extends MyDataEnabledListActivity {

	/** inner class UIHandler **/
	final class UIHandler extends Handler {

		public void handleMessage(Message msg) {
			/**
			 * Retrieve the contents of the message and then update the UI
			 */
			// make UI changes
			final List<Route> routeList = (List<Route>) msg.obj;
			
			for(int i=0; i<routeList.size(); i++)
			{
				//tempList.set(i, routeList.get(i));
			}
			ArrayAdapter<Route> arrAdapter = new ArrayAdapter<Route>(context,
					R.layout.single_list_item, routeList);

			setListAdapter(arrAdapter);

			ListView list = getListView();
			list.setTextFilterEnabled(true);
			list.setOnItemClickListener(new OnItemClickListener() {

				/** Control the Click on an Item **/
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					CharSequence routeName = ((TextView) arg1).getText();

					String tag = "";
					for(int i=0; i<routeList.size(); i++)
					{
						if(routeList.get(i).getTitle() == routeName.toString())
						{
							tag = routeList.get(i).getTag();
							break;
						}
					}
					
					//MUST HAVE ROUTE TAG. WORK WITH OBJECTs
					Intent intent = new Intent(context, ListStops.class);
					intent.putExtra("routeTag", tag);
					startActivity(intent);
				}
			});

		}

	};

	/** members **/
	private final Handler handler = new UIHandler(); // on UI thread!
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		new Thread(new Runnable() {
			// Do network access point here
			@Override
			public void run() {
				List<Route> routeList = mDataManager.initAllRoutesWithDetails();
				Message msg = handler.obtainMessage();
				msg.obj = routeList;
				handler.sendMessage(msg);
			}
		}).start();
	}

}
