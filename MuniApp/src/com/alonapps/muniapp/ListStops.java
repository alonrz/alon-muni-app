package com.alonapps.muniapp;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ListActivity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListStops extends ListActivity {

	Context context;
	DataManager mDataManager;
	final Handler handler = new UIHandler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		//fetcher = new XmlFetcher(this);
		mDataManager = DataManager.getDataManager(context);
		
		Bundle extras = getIntent().getExtras();
		final String routeTag;
		if(extras!=null)
		{
			routeTag = extras.getString("routeTag");
			this.setTitle("\"" + routeTag + "\" " + this.getTitle());
		}
		else
		{
			routeTag = "";
		}

		new Thread(new Runnable() {
			// Do network access point here

			@Override
			public void run() {
				//List<Stop> routeList = fetcher.GetStopsList(routeTag);
				List<Stop> routeList = mDataManager.getStopList(routeTag, "Inbound");
				Message msg = handler.obtainMessage();
				msg.obj = routeList;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_stops, menu);
		return true;
	}


	/** inner class UIHandler **/
	private final class UIHandler extends Handler {

		public void handleMessage(Message msg) {
			/**
			 * Retrieve the contents of the message and then update the UI
			 */
			// make UI changes
			List<Stop> routeList = (List<Stop>) msg.obj;
			ArrayAdapter<Stop> arrAdapter = new ArrayAdapter<Stop>(context,
					R.layout.single_list_item, routeList);

			setListAdapter(arrAdapter);

			ListView list = getListView();
			list.setTextFilterEnabled(true);
			list.setOnItemClickListener(new OnItemClickListener() {

				/** Control the Toast **/
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					CharSequence routeName = ((TextView) arg1).getText();
					Toast.makeText(getApplicationContext(), routeName,
							Toast.LENGTH_SHORT).show();
					
				}
			});
		}
	};	
}
