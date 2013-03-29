package com.alonapps.muniapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Route
{
	private String mTag;
	private String mTitle;
	private String mInboundName;
	private String mOutboundName;
	private int mColor;
	double mlatMin, mlatMax, mlonMin, mlonMax;
	private List<Stop> mStops = new ArrayList<Stop>();
	private List<Direction> mDirections = new ArrayList<Direction>();

	// public Route(String tag, String title)
	// {
	// this.setTag(tag);
	// this.setTitle(title);
	// }

	// public Route()
	// {
	// // this("", "");
	// }

	// Getters
	public String getTag()
	{
		return mTag;
	}

	public String getTitle()
	{
		return mTitle;
	}

	public String getInboundName()
	{
		return mInboundName;
	}

	public String getOutboundName()
	{
		return mOutboundName;
	}

	public int getColor()
	{
		return this.mColor;
	}

	public double getMinLat()
	{
		return this.mlatMin;
	}

	public double getMaxLat()
	{
		return this.mlatMax;
	}

	public double getMinLon()
	{
		return this.mlonMin;
	}

	public double getMaxLon()
	{
		return this.mlonMax;
	}

	// Setters
	public void setTag(String tag)
	{
		mTag = tag;
	}

	public void setInboundName(String mInboundName)
	{
		this.mInboundName = mInboundName;
	}

	public void setOutboundName(String mOutboundName)
	{
		this.mOutboundName = mOutboundName;
	}

	public void setTitle(String title)
	{
		mTitle = title;
	}

	public void setColor(int c)
	{
		this.mColor = c;
	}

	public void setMinLat(double minLat)
	{
		this.mlatMin = minLat;
	}

	public void setMaxLat(double maxLat)
	{
		this.mlatMax = maxLat;
	}

	public void setMinLon(double minLon)
	{
		this.mlonMin = minLon;
	}

	public void setMaxLon(double maxLon)
	{
		this.mlonMax = maxLon;
	}

	// Stops collection methods
	public void addStop(Stop s)
	{
		this.mStops.add(s);
	}

	public Stop getStopAtIndex(int index)
	{
		return this.mStops.get(index);
	}

	// Directions collection methods
	public void addDirection(Direction d)
	{
		this.mDirections.add(d);
	}

	/***
	 * 
	 * @param name
	 *            - Inbound or Outbound
	 * @return
	 */
	public List<Stop> getStopsPerDirection(String name)
	{
	List<Stop> stops = null;
		for (Direction dir : mDirections)
		{
			if (dir.getName().equalsIgnoreCase(name))
			{
				Integer[] stopsIDs = dir.getStopList();
				stops = MatchTagsToStops(stopsIDs);
				break;
			}
		}

		return stops;
	}

	private List<Stop> MatchTagsToStops(Integer[] stopsIDs)
	{
		List<Stop> stops = new ArrayList<Stop>();

		for (int i = 0; i < stopsIDs.length; i++)
		{
			stops.add(this.getStopByTag(stopsIDs[i]));
		}

		return stops;
	}

	/***
	 * Get the stop by its tag. To be used with direction which specifies only
	 * stop tags in their order.
	 * 
	 * @param Tag
	 *            the ID stored as tag.
	 * @return null if stop was not found.
	 */
	public Stop getStopByTag(int Tag)
	{
		for (Stop stop : this.mStops)
		{
			if (stop.getTag() == Tag)
			{
				return stop;
			}
		}

		return null;
	}
	
	@Override
	public String toString()
	{
		return this.getTitle();
	}

	/**** class STOP - describes a stop ***/
	public class Stop
	{
		// data fields
		private int mTag;
		private String mStopID;
		private String mTitle;
		private double mLat, mLon;

		// ctor
		public Stop(int Tag, String StopID, String Title, double Lat, double Lon)
		{
			this.mTag = Tag;
			this.mStopID = StopID;
			this.mTitle = Title;
			this.mLat = Lat;
			this.mLon = Lon;
		}

		public Stop()
		{
			this(0, "", "", 0.0, 0.0);
		}

		// getters
		public int getTag()
		{
			return mTag;
		}

		public String getStopID()
		{
			return mStopID;
		}

		public String getTitle()
		{
			return mTitle;
		}

		public double getLat()
		{
			return mLat;
		}

		public double getLon()
		{
			return mLon;
		}

		// setters
		public void setTag(int mTag)
		{
			this.mTag = mTag;
		}

		public void setStopID(String stopID)
		{
			this.mStopID = stopID;
		}

		public void setTitle(String title)
		{
			this.mTitle = title;
		}

		public void setLat(double lat)
		{
			this.mLat = lat;
		}

		public void setLon(double lon)
		{
			this.mLon = lon;
		}
		
		@Override
		public String toString()
		{
			return this.getTitle();
		}

	}

	/**** class DIRECTION - describes a direction with stops ordered and with tags ***/
	public class Direction
	{
		// data fields
		private String mTag;
		private String mTitle;
		private String mName;
		Vector<Integer> mStopsByTags = new Vector<Integer>();

		// ctors
		// public Direction(String dirTag, String dirTitle, String dirName)
		// {
		// this.mTag = dirTag;
		// this.mTitle = dirTitle;
		// this.mName = dirName;
		// }

		// getters
		public String getTag()
		{
			return mTag;
		}

		public String getTitle()
		{
			return mTitle;
		}

		public String getName()
		{
			return mName;
		}

		// setters
		public void setTag(String tag)
		{
			this.mTag = tag;
		}

		public void setTitle(String title)
		{
			this.mTitle = title;
		}

		public void setName(String name)
		{
			this.mName = name;
		}

		// Vector control methods
		public void addStopTag(int stopTag)
		{
			this.mStopsByTags.add(stopTag);
		}

		public int getStopAtIndex(int index)
		{
			return this.mStopsByTags.get(index);
		}

		public int getStopsCount()
		{
			return this.mStopsByTags.size();
		}

		public Integer[] getStopList()
		{
			return this.mStopsByTags.toArray(new Integer[0]);
		}

	}
}
