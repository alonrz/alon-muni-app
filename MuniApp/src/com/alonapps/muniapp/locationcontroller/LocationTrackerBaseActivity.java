package com.alonapps.muniapp.locationcontroller;


import android.app.Activity;
import android.os.Bundle;

/**
 * Base class for activities which includes GPS operations built in with the life cycle events.
 * @author alon
 *
 */
public class LocationTrackerBaseActivity extends Activity
{
	private ActivityTracker activityTracker;

	protected void onCreate(Bundle saved)
	{
		super.onCreate(saved);
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
