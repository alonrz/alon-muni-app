package com.alonapps.muniapp.ui;

import com.alonapps.muniapp.LocationTrackerBaseFragmentActivity;
import com.alonapps.muniapp.TabAdapter;
import com.alonapps.muniapp.R;
import com.alonapps.muniapp.datacontroller.DataManager;
import com.alonapps.muniapp.datacontroller.FavoriteOpenHelper;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class TabsActivity extends LocationTrackerBaseFragmentActivity
{
	/** members **/
	private final Handler handler = new UIHandler(); // on UI thread!
	DataManager mDataManager;
	Context mContext;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.mContext = this; 
		
		new Thread(new Runnable() {

			@Override
			public void run()
			{
				Message msg = handler.obtainMessage();
				msg.arg1 = 1;
				handler.sendMessage(msg);
//				mDataManager = DataManager.getInstance(mContext);
//				mDataManager.initAllRoutesWithDetails();// ** Starts a thread!
				msg = handler.obtainMessage();
				msg.arg1 = 0;
				handler.sendMessage(msg);
//				SQLiteDatabase dbFav = DataManager.getInstance(mContext).getFavoriteOpenHelper().getWritableDatabase();
//				ContentValues values = new ContentValues();
//				values.put(FavoriteOpenHelper.KEY_STOP_ID, "stop id 1");
//				values.put(FavoriteOpenHelper.KEY_ROUTE_ID, "route id 1");
				
			}
		}).start();
		
//		ActionBar actionBer = getActionBar();
//		actionBer.show();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
//		MenuInflater inflate = getMenuInflater();
//		inflate.inflate(R.menu.test_tabs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Toast.makeText(this, "Item selected " + item.getTitle(), Toast.LENGTH_SHORT).show();
		return super.onOptionsItemSelected(item);
	}
	
	
	/** inner class UIHandler **/
	private final class UIHandler extends Handler
	{
		public void handleMessage(Message msg)
		{
			if(msg.arg1 == 1 )
			{
				Toast.makeText(mContext, "WAITING for initAllRoutesWithDetails", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Toast.makeText(mContext, "initAllRoutesWithDetails is done", Toast.LENGTH_SHORT).show();
			
			setContentView(R.layout.activity_tabs_pager);
			
			
			FragmentManager fragManager =getSupportFragmentManager();
			FragmentPagerAdapter tabAdater = new TabAdapter(fragManager, mContext);
			
			ViewPager pager = (ViewPager)findViewById(R.id.pager);
			pager.setAdapter(tabAdater);
			
			
			TitlePageIndicator indicator =(TitlePageIndicator) findViewById(R.id.indicator);
			indicator.setViewPager(pager);
//			if (msg.arg1 == 1)
//				changeLoadingProgress(true);
//			else
//				changeLoadingProgress(false);
		}
	};

}
