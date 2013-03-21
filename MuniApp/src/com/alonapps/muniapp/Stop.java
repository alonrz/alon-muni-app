package com.alonapps.muniapp;

public class Stop {

	private String _tag;
	private String _title;
	private double _lat;
	private double _lon;
	private int _stopId;

	public Stop(String tag, String title, double lat, double lon, int stopId)
	{
		this.setTag(tag);
		this.setTitle(title);
		this.setLat(lat);
		setLong(lon);
		setStopId(stopId);		
	}
	
	public Stop()
	{
		this("", "", 0.0, 0.0, 0);
	}
	
	public String getTag()
	{
		return _tag;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public void setTag(String tag)
	{
		_tag = tag;
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}
	public double getLat() {
		return _lat;
	}

	public void setLat(double lat) {
		this._lat = lat;
	}

	public double getLong() {
		return _lon;
	}

	public void setLong(double lon) {
		this._lon = lon;
	}

	public int getStopId() {
		return _stopId;
	}

	public void setStopId(int stopId) {
		this._stopId = stopId;
	}

	@Override
	public String toString() {
		return this.getTitle();
	}
	
	
}
