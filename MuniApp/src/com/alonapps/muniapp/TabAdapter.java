package com.alonapps.muniapp;

import java.util.Locale;

import com.alonapps.muniapp.ui.fragments.CompassSelectionFragment;
import com.alonapps.muniapp.ui.fragments.FavoritesFragment;
import com.alonapps.muniapp.ui.fragments.ListRoutesFragment;
import com.alonapps.muniapp.ui.fragments.ListStopsNearMeFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * TabAdapter constructs the tabs (extends FragmentPagerAdapter). Their titles are here and which fragment goes to which position is also decided here.
 * @author alon
 */
public class TabAdapter extends FragmentPagerAdapter
{
	private static final String[] CONTENT = new String[] { "Near me", "by line"/*, "favorites"*/,
			"Direction" };

	public TabAdapter(FragmentManager fm, Context context)
	{
		super(fm);
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
			/*case 2:				
				frag = new FavoritesFragment();
				//frag.setArguments(args);
				break;*/
			case 2:
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
