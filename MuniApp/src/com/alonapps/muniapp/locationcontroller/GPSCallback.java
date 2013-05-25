package com.alonapps.muniapp.locationcontroller;

import android.location.Location;

public interface GPSCallback
{
	public abstract void onGPSUpdate(Location location);
}
