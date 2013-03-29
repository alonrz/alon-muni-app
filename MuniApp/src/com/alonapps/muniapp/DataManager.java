package com.alonapps.muniapp;

import java.util.List;
import java.util.Vector;

import android.content.Context;

public class DataManager
{

	Context appContext;
	XmlFetcher mFetcher;
	private static DataManager mManager = null;
	List<Route> mAllRoutesWithDirections = null;

	// private ctor for singleton pattern
	private DataManager(Context applicationContext)
	{
		mFetcher = new XmlFetcher(applicationContext);
		//mAllRoutesWithDirections = getAllRoutesWithDetails(); //init the largest object
	}

	// Singleton method
	public static DataManager getDataManager(Context applicationContext)
	{
		if (mManager == null)
		{
			mManager = new DataManager(applicationContext);
		}
		return mManager;
	}

	public List<Route> getAllRoutesWithDetails()
	{
		if (mAllRoutesWithDirections == null)
		{
			mAllRoutesWithDirections = mFetcher.loadAllRoutesWithDirections();
		}
		return mAllRoutesWithDirections;
	}

	/**
	 * 
	 * @param routeTag
	 * @param dirRequested Inbound or Outbound only.
	 * @return
	 */
	public List<Route.Stop> getStopList(String routeTag, String dirRequested)
	{
		Route myroute = this.getRoute(routeTag);
		if(myroute == null)
			return null;
		 return myroute.getStopsPerDirection(dirRequested);
		
	}

	private Route getRoute(String routeTag)
	{
		for (int i = 0; i < this.mAllRoutesWithDirections.size(); i++)
		{
			if(mAllRoutesWithDirections.get(i).getTag().equalsIgnoreCase(routeTag))
				return mAllRoutesWithDirections.get(i);
		}
		return null;
	}
}
