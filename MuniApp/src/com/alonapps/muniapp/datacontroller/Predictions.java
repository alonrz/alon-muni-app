package com.alonapps.muniapp.datacontroller;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents predictions. Each predictions have the same info of a route and stop but will 
 * have several next trains/buses coming which is why this is plural. A single prediciton refers to a 
 * specific time of arrival of one car. 
 * @author alon
 *
 */
public class Predictions
{
	private String mAgencyTitle;
	private String mRouteTag;
	private String mRouteTitle;
	private String mStopTitle;
	private String mStopTag;
	private String mStopId;
	private String mDirTitleBecauseNoPredictions;
	private List<Direction> mDirections = new ArrayList<Predictions.Direction>();
	private List<String> mMessages = new ArrayList<String>();

	public String getAgencyTitle()
	{
		return mAgencyTitle;
	}

	/**
	 * example: routeTag="M"
	 * @return example "M"
	 */
	public String getRouteTag()
	{
		return mRouteTag;
	}

	/**
	 * example: stopTitle="West Portal Station"
	 * @return example "West Portal Station" 
	 */
	public String getRouteTitle()
	{
		return mRouteTitle;
	}

	public String getStopTitle()
	{
		return mStopTitle;
	}
	
	public String getStopTag()
	{
		return mStopTag;
	}
	
	public String getStopId()
	{
		return mStopId;
	}

	public String getDirTitleBecauseNoPredictions()
	{
		return mDirTitleBecauseNoPredictions;
	}

	public void setAgencyTitle(String agencyTitle)
	{
		this.mAgencyTitle = agencyTitle;
	}

	public void setRouteTag(String routeTag)
	{
		this.mRouteTag = routeTag;
	}

	public void setRouteTitle(String routeTitle)
	{
		this.mRouteTitle = routeTitle;
	}

	public void setStopTitle(String stopTitle)
	{
		this.mStopTitle = stopTitle;
	}
	
	public void setStopTag(String stopTag)
	{
		this.mStopTag = stopTag;
	}
	
	public void setStopId(String stopID)
	{
		this.mStopId = stopID;
	}

	public void setDirTitleBecauseNoPredictions(String dirTitleBecauseNoPredictions)
	{
		this.mDirTitleBecauseNoPredictions = dirTitleBecauseNoPredictions;
	}
	
	public void addMessage(String msg)
	{
		if(msg.isEmpty() == false)
		{
			this.mMessages.add(msg);
		}
	}
	public void addDirection(Direction dir)
	{
		this.mDirections.add(dir);
	}
	
	public String[] getMessages()
	{
		return this.mMessages.toArray(new String[0]);
	}
	
	public Direction[] getAllDirections()
	{
		return this.mDirections.toArray(new Direction[0]);
	}
	
	
	
	/******** Direction Class *************/
	/**
	 * This is Direction with info relavent to predictions, contains all predictions for that direction as well as the title
	 * to be displayed (This is different than Route.Direction for info about Stops in a direction)
	 * @author alon
	 *
	 */
	public class Direction
	{
		private String mDirectionTitle;
		private List<Prediction> mPredictions = new ArrayList<Predictions.Prediction>();
		
		public String getDirectionTitle()
		{
			return this.mDirectionTitle;
		}
		
		public List<Prediction> getAllPredictions()
		{
			return this.mPredictions;
		}
		
		public void setDirectionTitle(String directionTitle)
		{
			this.mDirectionTitle = directionTitle;
		}
		
		public void addSinglePrediction(Prediction p)
		{
			this.mPredictions.add(p);
		}
		
	}

	/******** Prediction Class *************/
	/**
	 * Contain info about a specific car with its arrival time, vehical number etc...
	 * @author alon
	 *
	 */
	public class Prediction
	{
		private long mEpochTime;
		private int mSeconds;
		private int mMinutes;
		private boolean mIsDeparture;
		private String mDirTag;
		private int mVehicleNumber;
		private String mBlock;//String as defined in manual
		private String mTripTag;//String as defined in manual
		private boolean mAffectedByLayover;
		private boolean mIsScheduleBased;
		private boolean mDelayed;
		
		public Prediction()
		{
			mIsDeparture = false;
			mAffectedByLayover = false;
			mIsScheduleBased = false;
			mDelayed = false;
		}

		public long getEpochTime()
		{
			return mEpochTime;
		}

		public int getSeconds()
		{
			return mSeconds;
		}

		public int getMinutes()
		{
			return mMinutes;
		}

		public boolean isDeparture()
		{
			return mIsDeparture;
		}

		public String getDireTag()
		{
			return mDirTag;
		}

		public int getVehicleNumber()
		{
			return mVehicleNumber;
		}

		//Usually a number but can be MUNSCHED sometimes. 
		public String getBlock()
		{
			return mBlock;
		}

		public String getTripTag()
		{
			return mTripTag;
		}

		public boolean isAffectedByLayover()
		{
			return mAffectedByLayover;
		}

		public boolean isScheduleBased()
		{
			return mIsScheduleBased;
		}

		public boolean isDelayed()
		{
			return mDelayed;
		}

		public void setEpochTime(long epochTime)
		{
			this.mEpochTime = epochTime;
		}

		public void setSeconds(int seconds)
		{
			this.mSeconds = seconds;
		}

		public void setMinutes(int minutes)
		{
			this.mMinutes = minutes;
		}

		public void setIsDeparture(boolean isDeparture)
		{
			this.mIsDeparture = isDeparture;
		}

		public void setDirTag(String dirTag)
		{
			this.mDirTag = dirTag;
		}

		public void setVehicleNumber(int vehicleNumber)
		{
			this.mVehicleNumber = vehicleNumber;
		}

		public void setBlock(String block)
		{
			this.mBlock = block;
		}

		public void setTripTag(String tripTag)
		{
			this.mTripTag = tripTag;
		}

		public void setIsAffectedByLayover(boolean isAffectedByLayover)
		{
			this.mAffectedByLayover = isAffectedByLayover;
		}

		public void setIsScheduleBased(boolean isScheduleBased)
		{
			this.mIsScheduleBased = isScheduleBased;
		}

		public void setIsDelayed(boolean isDelayed)
		{
			this.mDelayed = isDelayed;
		}
	}



	
}
