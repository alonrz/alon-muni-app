package com.alonapps.muniapp.ui;

import java.util.List;

import com.alonapps.muniapp.GpsManager;
import com.alonapps.muniapp.R;
import com.alonapps.muniapp.R.id;
import com.alonapps.muniapp.R.layout;
import com.alonapps.muniapp.R.menu;
import com.alonapps.muniapp.datacontroller.DataManager;
import com.alonapps.muniapp.datacontroller.Predictions;
import com.alonapps.muniapp.datacontroller.Route;
import com.alonapps.muniapp.datacontroller.DataManager.DIRECTION;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path.Direction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ListStops extends MyDataEnabledListActivity
{

	Context context;
	final Handler handler = new UIHandler();
	boolean showInboundSelected = true;
	MenuInflater inflater = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		context = this;
		// fetcher = new XmlFetcher(this);
		mDataManager = DataManager.getDataManager(context);

		showDirectionInNewThread(DIRECTION.Inbound);
		Toast.makeText(context, "Toggle Inbound/Outbound from menu", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// if (inflater == null)
		// {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_stops, menu);
		// }
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

	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
		{
			case R.id.action_inbound:
				showDirectionInNewThread(DIRECTION.Inbound);
				showInboundSelected = true;
				invalidateOptionsMenu();
				return true;
			case R.id.action_outbound:
				showDirectionInNewThread(DIRECTION.Outbound);
				showInboundSelected = false;
				invalidateOptionsMenu();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}

	}

	public void showDirectionInNewThread(final DIRECTION dir)
	{
		Bundle extras = getIntent().getExtras();
		final String routeTag;
		if (extras != null)
		{
			routeTag = extras.getString("routeTag");
			this.setTitle(routeTag + " " + dir);
		} else
		{
			routeTag = "";
		}

		new Thread(new Runnable() {
			// Do network access point here

			@Override
			public void run()
			{

				List<Route.Stop> routeList = mDataManager.getStopList(routeTag, dir);
				// TODO replace "Inbound" by actuall selection
				if (routeList == null)
					Log.i(this.getClass().toString(), "routeList is null");
				Message msg = handler.obtainMessage();
				msg.obj = routeList;
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/** inner class UIHandler **/
	private final class UIHandler extends Handler
	{

		public void handleMessage(Message msg)
		{
			/**
			 * Retrieve the contents of the message and then update the UI
			 */
			// make UI changes
			// List<Stop> routeList = (List<Stop>) msg.obj;
			final List<Route.Stop> routeList = (List<Route.Stop>) msg.obj;
			ArrayAdapter<Route.Stop> arrAdapter = new ArrayAdapter<Route.Stop>(context,
					R.layout.single_list_item, routeList);

			setListAdapter(arrAdapter);

			ListView list = getListView();
			list.setTextFilterEnabled(true);
			list.setOnItemClickListener(new OnItemClickListener() {

				/** Control the Toast **/
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
				{
					// CharSequence routeName = ((TextView) arg1).getText();
					// Toast.makeText(getApplicationContext(),
					// routeList.get(position).getStopID(),
					// Toast.LENGTH_SHORT).show();
					Predictions currentPred = mDataManager.getSelectedPrediction();
					currentPred.setStopTag(routeList.get(position).getStopTag());
					currentPred.setStopId(routeList.get(position).getStopID());

					Intent intent = new Intent(context, ShowSingleStop.class);
					// intent.putExtra("routeTag", tag);
					if (showInboundSelected)
						intent.putExtra("dir", DIRECTION.Inbound.name());
					else
						intent.putExtra("dir", DIRECTION.Outbound.name());
					startActivity(intent);

				}
			});
		}
	};
}
