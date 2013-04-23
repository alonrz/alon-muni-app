package com.alonapps.muniapp.ui;

import com.alonapps.muniapp.LocationTrackerBaseFragmentActivity;
import com.alonapps.muniapp.TabAdapter;
import com.alonapps.muniapp.R;
import com.viewpagerindicator.TabPageIndicator;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;

public class TabsActivity extends LocationTrackerBaseFragmentActivity
{
	

	@Override 
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs_pager);
		
		FragmentManager fragManager =getSupportFragmentManager();
		FragmentPagerAdapter tabAdater = new TabAdapter(fragManager);
		
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(tabAdater);
		
		
		TabPageIndicator indicator =(TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		
	}
}
