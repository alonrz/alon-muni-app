package com.alonapps.muniapp;

import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentActivity;

public class LocationTrackerBaseFragmentActivity extends FragmentActivity
{
	private ActivityTracker activityTracker;

	protected void onCreate(Bundle saved)
	{
		super.onCreate(saved);
		/* ... */
		activityTracker = ActivityTracker.getInstance();
	}

	protected void onStart()
	{
		super.onStart();
		activityTracker.onActivityStarted(this);
	}

	protected void onResume()
	{
		super.onResume();
		activityTracker.onActivityResumed();
	}

	protected void onPause()
	{
		super.onPause();
		activityTracker.onActivityPaused();
	}

	protected void onStop()
	{
		super.onStop();
		activityTracker.onActivityStopped();
	}

}
