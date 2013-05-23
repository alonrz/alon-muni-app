package com.alonapps.muniapp.ui;

import com.alonapps.muniapp.LocationTrackerBaseFragmentActivity;
import com.alonapps.muniapp.TabAdapter;
import com.alonapps.muniapp.R;
import com.alonapps.muniapp.datacontroller.DataManager;
import com.viewpagerindicator.TitlePageIndicator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

public class TabsActivity extends LocationTrackerBaseFragmentActivity
{
	/** members **/
	DataManager mDataManager;
	Context mContext;
	int value = 0;
	
	@Override 
	protected void onCreate(Bundle savedInstanceState)
	{
		int tabid=0;
		Bundle extras = getIntent().getExtras();
		if(extras != null)
			tabid = extras.getInt("tabid");
		super.onCreate(savedInstanceState);
		this.mContext = this;
		setContentView(R.layout.activity_tabs_pager);
		
		
		FragmentManager fragManager = getSupportFragmentManager();
		FragmentPagerAdapter tabAdapter = new TabAdapter(fragManager, mContext);
		
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(tabAdapter);
		
		
		TitlePageIndicator indicator =(TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		
		pager.setCurrentItem(tabid);
	}
}
