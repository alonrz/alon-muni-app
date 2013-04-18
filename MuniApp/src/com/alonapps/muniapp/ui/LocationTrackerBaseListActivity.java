package com.alonapps.muniapp.ui;

import com.alonapps.muniapp.ActivityTracker;

import android.app.ListActivity;
import android.os.Bundle;

public class LocationTrackerBaseListActivity extends ListActivity
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
