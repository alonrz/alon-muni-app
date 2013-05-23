package com.alonapps.muniapp;

import java.util.List;
import java.util.Locale;

import com.alonapps.muniapp.datacontroller.DataManager;
import com.alonapps.muniapp.datacontroller.DataManager.DIRECTION;
import com.alonapps.muniapp.datacontroller.Predictions;
import com.alonapps.muniapp.ui.fragments.CompassSelectionFragment;
import com.alonapps.muniapp.ui.fragments.FavoritesFragment;
import com.alonapps.muniapp.ui.fragments.ListRoutesFragment;
import com.alonapps.muniapp.ui.fragments.ListStopsNearMeFragment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class TabAdapter extends FragmentPagerAdapter
{
	private Context mContext;
	private static final String[] CONTENT = new String[] { "Near me", "by line", "favorites",
			"Direction" };

	public TabAdapter(FragmentManager fm, Context context)
	{
		super(fm);
		this.mContext = context;
	}

	@Override
	public Fragment getItem(int position)
	{
		Fragment frag = null;
		switch (position)
		{
			case 0:
				frag = new ListStopsNearMeFragment();				
				//frag.setArguments(args);
				break;
			case 1:
				frag = new ListRoutesFragment();
				//frag.setArguments(args);
				break;
			case 2:				
				frag = new FavoritesFragment();
				//frag.setArguments(args);
				break;
			case 3:
				frag = new CompassSelectionFragment();
				//frag.setArguments(args);
				break;

		}
		return frag;

	}

	@Override
	public CharSequence getPageTitle(int position)
	{
		return CONTENT[position % CONTENT.length].toUpperCase(Locale.US); 
	}

	@Override
	public int getCount()
	{
		return CONTENT.length;
	}
}
