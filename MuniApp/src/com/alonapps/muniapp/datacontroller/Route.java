package com.alonapps.muniapp.datacontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.os.Parcel;
import android.os.Parcelable;

import com.alonapps.muniapp.datacontroller.DataManager.DIRECTION;

public class Route
{
	public static final Parcelable.Creator<Stop> CREATOR = new Parcelable.Creator<Stop>() {
		public Stop createFromParcel(Parcel in)
		{
			return new Route().new Stop(in);
		}

		public Stop[] newArray(int size)
		{
			return new Stop[size];
		}
	};

	
	private String mTag;
	private String mTitle;
	private String mInboundName;
	private String mOutboundName;
	private int mColor;
	private int mOppColor;
	double mlatMin, mlatMax, mlonMin, mlonMax;
	private List<Stop> mStops = new ArrayList<Stop>();
	private List<Direction> mDirections = new ArrayList<Direction>();

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

	public int getOppColor()
	{
		return this.mOppColor;
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

	public void setOppColor(int c)
	{
		this.mOppColor = c;
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
	public List<Stop> getStopsPerDirection(DIRECTION direction)
	{
		List<Stop> stops = null;
		for (Direction dir : mDirections)
		{
			if (dir.getDirection() == direction)
			{
				String[] stopsTags = dir.getStopList();
				stops = MatchTagsToStops(stopsTags);
				break;
			}
		}

		return stops;
	}

	public List<Stop> getAllStopsForRoute()
	{
		return mStops;
	}

	private List<Stop> MatchTagsToStops(String[] stopsTags)
	{
		List<Stop> stops = new ArrayList<Stop>();

		for (int i = 0; i < stopsTags.length; i++)
		{
			stops.add(this.getStopByTag(stopsTags[i]));
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
	public Stop getStopByTag(String Tag)
	{
		for (Stop stop : this.mStops)
		{
			if (stop.getStopTag().equalsIgnoreCase(Tag))
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

	public boolean isStopTagInDirection(DIRECTION dirRequested, String stopTag)
	{
		for (Direction d : mDirections)
		{
			if (d.getDirection() == dirRequested)
			{
				return d.mStopsByTags.contains(stopTag);
			}
		}
		return false;
	}

	/**** class STOP - describes a stop ***/
	public class Stop implements Comparable<Stop>, Parcelable
	{
		// data fields
		private String mTag;// Can have non numeric char. Must be able to handle
		private String mStopID;
		private String mTitle;
		private double mLat, mLon;
		private float mDistFromCurrentLocation;
		private boolean isInbound;

		// ctor
		public Stop(String Tag, String StopID, String Title, double Lat, double Lon, boolean isInbound)
		{
			this.mTag = Tag;
			this.mStopID = StopID;
			this.mTitle = Title;
			this.mLat = Lat;
			this.mLon = Lon;
			this.isInbound = isInbound;
		}
		public Stop(Parcel p)
		{
			mTag = p.readString();
			mStopID = p.readString();
			mTitle = p.readString();
			mLat= p.readDouble();
			mLon = p.readDouble();
			mDistFromCurrentLocation = p.readFloat();
			isInbound = p.readByte() == 1;
		}

		public Stop()
		{
			this("0", "", "", 0.0, 0.0, true);
		}

		// getters
		public String getStopTag()
		{
			return mTag;
		}

		public String getStopID()
		{
			return mStopID;
		}

		public String getStopTitle()
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

		public float getDistFromCurrentLocation()
		{
			return this.mDistFromCurrentLocation;
		}
		public boolean IsInbound()
		{
			return this.isInbound;
		}

		// setters
		public void setTag(String mTag)
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

		public void setDistFromCurrentLocation(float dist)
		{
			this.mDistFromCurrentLocation = dist;
		}
		public void setIsInbound(boolean isInbound)
		{
			this.isInbound = isInbound;
		}

		@Override
		public String toString()
		{
			return this.getStopTitle();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o.getClass() != this.getClass())
				return false;
			Stop stop = (Stop) o;

			return this.getStopID().equals(stop.getStopID());
		}

		@Override
		public int hashCode()
		{
			return this.getStopID().hashCode();
		}

		@Override
		public int compareTo(Stop s)
		{

			return this.getStopID().compareTo(s.getStopID());
		}

		@Override
		public int describeContents()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		
		@Override
		public void writeToParcel(Parcel dest, int flags)
		{
			dest.writeString(mTag);
			dest.writeString(mStopID);
			dest.writeString(mTitle);
			dest.writeDouble(mLat);
			dest.writeDouble(mLon);
			dest.writeFloat(mDistFromCurrentLocation);
			dest.writeByte((byte)(this.isInbound?1:0));
		}
	}

	/**** class DIRECTION - describes a direction with stops ordered and with tags ***/
	public class Direction
	{
		// data fields
		private String mTag;
		private String mTitle;
		private DIRECTION mDirName;
		Vector<String> mStopsByTags = new Vector<String>();

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

		public DIRECTION getDirection()
		{
			return mDirName;
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

		public void setDirection(DIRECTION dir)
		{
			this.mDirName = dir;
		}

		// Vector control methods
		public void addStopTag(String stopTag)
		{
			this.mStopsByTags.add(stopTag);
		}

		public String getStopAtIndex(int index)
		{
			return this.mStopsByTags.get(index);
		}

		public int getStopsCount()
		{
			return this.mStopsByTags.size();
		}

		public String[] getStopList()
		{
			return this.mStopsByTags.toArray(new String[0]);
		}

	}

}
