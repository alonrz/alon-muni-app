package com.alonapps.muniapp.locationcontroller;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Similar to DataManager, this class controls the GPS. Includes LocationListener and LocationManager. 
 * You can find here the last known location and get the string of the best provider's name.
 * @author alon
 *
 */
public class GpsManager
{
	private static final int gpsMinTime = 500;
	private static final int gpsMinDistance = 0;

	private LocationManager mlocationManager = null;
	private LocationListener mlocationListener = null;
	private GPSCallback gpsCallback = null;
	private Location mLastKnownLocation;
	private static GpsManager mGpsManager = null;
	private String mBestProviderName;

	public static GpsManager getInstance()
	{
		if (mGpsManager == null)
		{
			mGpsManager = new GpsManager();
		}
		return mGpsManager;
	}

	private GpsManager()
	{
		mlocationListener = new LocationListener() {
			public void onProviderDisabled(final String provider)
			{
			}

			public void onProviderEnabled(final String provider)
			{
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras)
			{
			}

			public void onLocationChanged(final Location location)
			{
				if (location != null)
					mLastKnownLocation = location;
				if (location != null && gpsCallback != null)
				{
					Log.e("if location " + location, "if gpscallback" + gpsCallback);
					gpsCallback.onGPSUpdate(location);
				} else
				{
					Log.e("else location " + location, "else gpscallback" + gpsCallback);
				}
			}

		};

	}

	public void startListening(final Activity activity)
	{
		if (mlocationManager == null)
		{
			mlocationManager = (LocationManager) activity
					.getSystemService(Context.LOCATION_SERVICE);
		}

		final Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		this.mBestProviderName = this.mlocationManager.getBestProvider(criteria, true);

		if (mBestProviderName != null && mBestProviderName.length() > 0)
		{
			mlocationManager.requestLocationUpdates(mBestProviderName, GpsManager.gpsMinTime,
					GpsManager.gpsMinDistance, mlocationListener);
		} else
		{
			final List<String> providers = this.mlocationManager.getProviders(true);

			for (final String provider : providers)
			{
				this.mlocationManager.requestLocationUpdates(provider, GpsManager.gpsMinTime,
						GpsManager.gpsMinDistance, mlocationListener);
			}
		}
		this.mLastKnownLocation = this.getLastKnownLocation(activity);
	}

	public void stopListening(Activity activity)
	{
		try
		{
			SharedPreferences lastLocationPref = activity.getPreferences(Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor =  lastLocationPref.edit();
			editor.putFloat("lat", (float)mLastKnownLocation.getLatitude());
			editor.putFloat("lon", (float)mLastKnownLocation.getLongitude());
			Log.i(this.getClass().getSimpleName(), "Saved location to preferences");
			editor.commit();
			if (this.mlocationManager != null && mlocationListener != null)
			{
				this.mlocationManager.removeUpdates(mlocationListener);
			}

			// locationManager = null;
		} catch (final Exception ex)
		{
			Log.e(this.getClass().toString(), "Error removing location listener");
		}
	}

	public void setGPSCallback(final GPSCallback gpsCallback)
	{
		this.gpsCallback = gpsCallback;
	}

	public GPSCallback getGPSCallback()
	{
		return gpsCallback;
	}

	public Location getLastKnownLocation(Activity activity)
	{
		this.mLastKnownLocation = this.mlocationManager.getLastKnownLocation(mBestProviderName);
		if (this.mLastKnownLocation == null)
		{
			if (this.mlocationManager == null)
				Log.e(this.getClass().getSimpleName(), "No locatio manager found");
			else
			{
				this.mLastKnownLocation = this.mlocationManager
						.getLastKnownLocation("gps");
			}
			if (mLastKnownLocation == null)
			{
				Log.e(this.getClass().getSimpleName(), "Location is null. Trying preference or adding manual location");
				
				SharedPreferences lastLocationPref =  activity.getPreferences(Activity.MODE_PRIVATE);
				final float lat = lastLocationPref.getFloat("lat", 37.7633f);
				final float lon = lastLocationPref.getFloat("lon", -122.4350f);
				this.mLastKnownLocation = new Location(this.mBestProviderName) {
					{
						setLatitude(lat);
						setLongitude(lon);
					}
				};
			}
		}
		return this.mLastKnownLocation;
	}

	public String getmBestProviderName()
	{
		return mBestProviderName;
	}

	public void setmBestProviderName(String mBestProviderName)
	{
		this.mBestProviderName = mBestProviderName;
	}
}
