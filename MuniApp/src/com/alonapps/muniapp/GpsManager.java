package com.alonapps.muniapp;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

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

		final String bestProvider = this.mlocationManager.getBestProvider(criteria, true);

		if (bestProvider != null && bestProvider.length() > 0)
		{
			mlocationManager.requestLocationUpdates(bestProvider, GpsManager.gpsMinTime,
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
		mLastKnownLocation = this.mlocationManager.getLastKnownLocation(bestProvider);
		if (mLastKnownLocation == null)
			Log.e(this.getClass().getSimpleName(), "getLastKnownLocation returned null (provider="
					+ bestProvider + ")");

	}

	public void stopListening()
	{
		try
		{
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

	public Location getLastKnownLocation()
	{
		if (this.mLastKnownLocation == null)
		{
			if (this.mlocationManager == null)
				Log.e(this.getClass().getSimpleName(), "No locatio manager found");
			else
			{
//				this.mLastKnownLocation = this.mlocationManager
//						.getLastKnownLocation(this.mBestProviderName);
				this.mLastKnownLocation = this.mlocationManager
						.getLastKnownLocation("gps");
			}
			if (mLastKnownLocation == null)
			{
				Log.e(this.getClass().getSimpleName(), "Location is null. Adding manual location");
				this.mLastKnownLocation = new Location(this.mBestProviderName) {
					{
						setLatitude(37.7230);
						setLongitude(-122.4842);
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
