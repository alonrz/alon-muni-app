package com.alonapps.muniapp.datacontroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.alonapps.muniapp.StopNotFoundException;
import com.alonapps.muniapp.datacontroller.Route.Stop;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class DataManager
{
	private Context appContext;
	private XmlParser mFetcher;
	private static DataManager mManager = null;
	private List<Route> mAllRoutesWithDirections = null;
	private List<Stop> mAllStops = null;
	private List<Stop> mStopsNearLocation = null;
	private List<Predictions> mPredictionsForStopsNearMe = null;

	public enum DIRECTION {
		Inbound, Outbound
	};

	// private ctor for singleton pattern
	private DataManager(Context applicationContext)
	{
		mFetcher = new XmlParser(applicationContext);

	}

	// Singleton method
	public static DataManager getDataManager(Context applicationContext)
	{
		if (mManager == null)
		{
			mManager = new DataManager(applicationContext);
		}
		return mManager;
	}

	public List<Route> initAllRoutesWithDetails()
	{
		if (mAllRoutesWithDirections == null)
		{
			mAllRoutesWithDirections = mFetcher.loadAllRoutesWithDirections();
		}
		return mAllRoutesWithDirections;
	}

	/**
	 * 
	 * @param routeTag
	 * @param dirRequested
	 *            Inbound or Outbound only.
	 * @return
	 */
	public List<Route.Stop> getStopList(String routeTag, DIRECTION dirRequested)
	{
		Route myroute = this.getRoute(routeTag);
		if (myroute == null)
			return null;
		return myroute.getStopsPerDirection(dirRequested);

	}

	public Route getRoute(String routeTag)
	{
		for (int i = 0; i < this.mAllRoutesWithDirections.size(); i++)
		{
			if (mAllRoutesWithDirections.get(i).getTag().equalsIgnoreCase(routeTag))
				return mAllRoutesWithDirections.get(i);
		}
		return null;
	}

	/**
	 * Bring back a list of stops which are closer than specified maxDistance in
	 * meters
	 * 
	 * @param mCurrentLocation
	 *            Base location to measure from. Usually where the user is.
	 * @param maxDistance
	 *            max radios to search for. uses meters.
	 * @return
	 */
	public List<Stop> getStopsNearLocation(Location mCurrentLocation, float maxDistance)
	{
		List<Route.Stop> closestStops = new ArrayList<Route.Stop>();
		// temp location to hold locations to compare distance to. Lat and Lon
		// will be changed each time
		Location tempLocation = new Location(mCurrentLocation.getProvider());

		for (int i = 0; i < mAllRoutesWithDirections.size(); i++)
		{
			Route r = mAllRoutesWithDirections.get(i);

			/**** Debug limit only due to longer loading time for debug mode ****/
			// if(!r.getTag().equalsIgnoreCase("M"))
			// continue;

			List<Stop> tempStops = r.getAllStopsForRoute();

			for (int j = 0; j < tempStops.size(); j++)
			{
				Stop singleStop = tempStops.get(j);
				// singleStop.setDirection(r.set)
				tempLocation.setLatitude(singleStop.getLat());
				tempLocation.setLongitude(singleStop.getLon());
				singleStop.setDistFromCurrentLocation(mCurrentLocation.distanceTo(tempLocation));
				int distanceRounded = Math.round(singleStop.getDistFromCurrentLocation());
				if (singleStop.getDistFromCurrentLocation() < maxDistance)
				{
					if (closestStops.contains(singleStop) == false)
					{
						closestStops.add(singleStop);
						Log.i("diatances",
								String.valueOf(distanceRounded) + "m to " + singleStop.getTitle()
										+ " (" + singleStop.getStopID() + ")" + " (" + r.getTag()
										+ ")");
					}
				}
			}
		}
		Collections.sort(closestStops, new Comparator<Stop>() {

			@Override
			public int compare(Stop s1, Stop s2)
			{
				return Float.compare(s1.getDistFromCurrentLocation(),
						s2.getDistFromCurrentLocation());
			}
		});

		this.mStopsNearLocation = closestStops;
		return closestStops;
	}

	/**
	 * Must be called after a list of all stops nearby has been located.
	 * 
	 * @param stops
	 * @return a list of Predictions objects with info about direction and
	 *         single predictions.
	 */
	private List<Predictions> getPredictionsByStops(List<Route.Stop> stops)
	{
		List<Predictions> preds = new ArrayList<Predictions>();

		for (Route.Stop s : stops)
		{
			preds.addAll(this.mFetcher.loadPredictions(s.getStopID()));
		}

		return preds;
	}

	/**
	 * Must be called after a list of all stops nearby has been located. It will
	 * save near by stops in DataManager and then work off that for predictions
	 * 
	 * @param stops
	 * @return
	 */
	public List<Predictions> getPredictionsByStopsAsync(DIRECTION dir, boolean refreshData)
	{
		List<Predictions> predictionsInDirection = new ArrayList<Predictions>();
		if (this.mStopsNearLocation == null)
		{
			Log.e(this.getClass().toString(),
					"Need to call getStopsNearLocation first to establish a list of stops near location!");
			return null;
		}

		if (mPredictionsForStopsNearMe == null || refreshData == true)
		{
			mPredictionsForStopsNearMe = new ArrayList<Predictions>();
			// Collect predicions for all stations near me.
			// for (Route.Stop s : mStopsNearLocation)
			// {
			mPredictionsForStopsNearMe.addAll(getPredictionsByStops(this.mStopsNearLocation));
			// }
		}

		// filter out directions
		Predictions tempPred;
		for (Predictions p : mPredictionsForStopsNearMe)
		{
			// tempPred = new Predictions();
			for (Predictions.Direction d : p.getAllDirections())
			{
				try
				{
					if (getDirectionByStopID(p).name().equalsIgnoreCase(dir.name()))
					{
						// tempPred.addDirection(d);
						predictionsInDirection.add(p);
						continue;
					}
				} catch (StopNotFoundException e)
				{
					Log.e(this.getClass().toString(), "Stop not found");
					continue;
				}
			}
			if (p.getDirTitleBecauseNoPredictions()!=null && 
					p.getDirTitleBecauseNoPredictions().equals("") == false)
				predictionsInDirection.add(p);
		}

		return predictionsInDirection;
	}

	private DIRECTION getDirectionByStopID(Predictions predObj) throws StopNotFoundException
	{
		String routeTag = predObj.getRouteTag();
		String stopTag = predObj.getStopTag();

		for (Route r : this.mAllRoutesWithDirections)
		{
			if (r.getTag().equalsIgnoreCase(routeTag))
			{
				if (r.isStopIdInDirection(DIRECTION.Inbound, stopTag))
					return DIRECTION.Inbound;
				else if (r.isStopIdInDirection(DIRECTION.Outbound, stopTag))
					return DIRECTION.Outbound;
				else
					throw new StopNotFoundException("Stop did not return from route " + routeTag
							+ " in neither direction");
			}
		}
		throw new StopNotFoundException("Stop did not return from route " + routeTag
				+ " in neither direction");
	}

	public Location getStopLocation(String stopTag, String routeTag, String locationProvider)
			throws StopNotFoundException
	{
		Location loc = new Location(locationProvider);
		for (Route r : this.mAllRoutesWithDirections)
		{
			if (r.getTag().equalsIgnoreCase(routeTag))
			{
				Stop s = r.getStopByTag(stopTag);
				loc.setLatitude(s.getLat());
				loc.setLongitude(s.getLon());
				return loc;
			}
		}
		throw new StopNotFoundException("Stop did not return from route " + routeTag
				+ " in neither direction");

	}
}
