package com.alonapps.muniapp;

import java.util.List;

import android.content.Context;

public class DataManager {

	Context appContext;
	XmlFetcher mFetcher;
	private static DataManager mManager = null;
	
	//private ctor for singleton pattern
	private DataManager(Context applicationContext)
	{
		mFetcher = new XmlFetcher(applicationContext);
		
	}
	
	//Singleton method 
	public static DataManager getDataManager(Context applicationContext)
	{
		if(mManager == null)
		{
			mManager = new DataManager(applicationContext);			
		}
		return mManager;		
	}

	public List<Route> GetRouteList() {
		// TODO Auto-generated method stub
		return mFetcher.GetRouteList();
	}
}
