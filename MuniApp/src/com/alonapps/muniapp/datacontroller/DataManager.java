package com.alonapps.muniapp.datacontroller;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import com.alonapps.muniapp.StopNotFoundException;
import com.alonapps.muniapp.datacontroller.Route.Stop;
import com.alonapps.muniapp.locationcontroller.GpsManager;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

/**
 * Controls everything related to data here. Mostly access to the XML for stops, routes and predictions and 
 * Any manipulation done on that data such as getting list of ... 
 * @author alon
 *
 */
public class DataManager
{
	private XmlParser mFetcher;
	private static DataManager mManager = null;
	private List<Route> mAllRoutesWithDirections;// = new ArrayList<Route>();
	private List<Stop> mStopsNearLocation = null; // after searching for stops
													// near me - closest stops.
	private List<Predictions> mPredictionsForStopsNearMeInbound = null;
	private List<Predictions> mPredictionsForStopsNearMeOutbound = null;
	private static Predictions mSelectedPrediction;
	private static FavoriteOpenHelper mFavoriteOpenHelper;

	private Context mContext;
	public float mDefaultMaxDistance = 500;

	public enum DIRECTION {
		Inbound, Outbound
	};

	// private ctor for singleton pattern
	private DataManager(Context applicationContext)
	{
		mContext = applicationContext;
		mFetcher = new XmlParser(applicationContext);

	}

	// Singleton method
	public static DataManager getInstance(Context applicationContext)
	{
		if (mManager == null)
		{
			mManager = new DataManager(applicationContext);
			// this will create the table.
			mFavoriteOpenHelper = new FavoriteOpenHelper(applicationContext); 
		}
		return mManager;
	}

	/**
	 * This represents a single route and station to get predictions
	 * 
	 * @return
	 */
	public Predictions getSelectedPrediction()
	{
		if (mSelectedPrediction == null)
			mSelectedPrediction = new Predictions();
		return mSelectedPrediction;
	}

	public FavoriteOpenHelper getFavoriteOpenHelper()
	{
		return mFavoriteOpenHelper;
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

	public List<Stop> getRecentStopsNearLocation()
	{
		return this.mStopsNearLocation;
	}

	/**
	 * Bring back a list of stops which are closer than specified maxDistance in
	 * meters
	 * 
	 * @param mCurrentLocation
	 *            Base location to measure from. Usually where the user is.
	 * @param maxDistanceInMeters
	 *            max radios to search for. uses meters.
	 * @return
	 */
	public List<Stop> getStopsNearLocation(Location mCurrentLocation, float maxDistanceInMeters)
	{
		List<Stop> closestStops = new ArrayList<Route.Stop>();
		// temp location to hold locations to compare distance to. Lat and Lon
		// will be changed each time
		Location tempLocation = null;
		if (mCurrentLocation == null)
		{
			Log.i(this.getClass().toString(), "CurrentLocation var is null. using last known");
			tempLocation = GpsManager.getInstance().getLastKnownLocation((Activity)mContext);
		} else
		{
			tempLocation = new Location(mCurrentLocation.getProvider());
		}

		Log.i(this.getClass().getSimpleName(), "is tempLocation null? " + (tempLocation == null));
		if (tempLocation == null)
			return closestStops;
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
				if (singleStop == null)
				{
					Log.e(this.getClass().toString(), "No stop at loaction " + j + " for route "
							+ r.getTag());
					continue;
				}
				// singleStop.setDirection(r.set)
				tempLocation.setLatitude(singleStop.getLat());
				tempLocation.setLongitude(singleStop.getLon());
				singleStop.setDistFromCurrentLocation(mCurrentLocation.distanceTo(tempLocation));
				int distanceRounded = Math.round(singleStop.getDistFromCurrentLocation());
				if (singleStop.getDistFromCurrentLocation() < maxDistanceInMeters)
				{
					if (closestStops.contains(singleStop) == false)
					{
						closestStops.add(singleStop);
						Log.i("diatances",
								String.valueOf(distanceRounded) + "m to "
										+ singleStop.getStopTitle() + " (" + singleStop.getStopID()
										+ ")" + " (" + r.getTag() + ")");
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
		return this.mStopsNearLocation;
	}

	/**
	 * Returns all lines within range of distance in meter
	 * 
	 * @param mCurrentLocation
	 *            give current location
	 * @param maxDistanceInMeters
	 *            use meters as radius to search
	 * @return a list of Routes by <SimpleEntry<String, DIRECTION>, Stop Object>
	 *         (ex: <<M,Inbound>, Stop). Never returns NULL.
	 */
	public Hashtable<SimpleEntry<String, DIRECTION>, Stop> getRoutesNearLocation(
			Location mCurrentLocation, float maxDistanceInMeters)
	{
		Hashtable<SimpleEntry<String, DIRECTION>, Stop> routesPerStops = new Hashtable<SimpleEntry<String, DIRECTION>, Stop>();

		// Check if no near stations were selected (this is done in
		// getPredictionsByStopsAsync in main activity)
		if (mPredictionsForStopsNearMeInbound == null || mPredictionsForStopsNearMeOutbound == null)
			return routesPerStops;

		List<Predictions> totalPreds = new ArrayList<Predictions>();
		totalPreds.addAll(mPredictionsForStopsNearMeInbound);
		totalPreds.addAll(mPredictionsForStopsNearMeOutbound);

		for (Predictions pred : totalPreds)
		{
			Stop stopObject = getStop(pred.getStopTag(), pred.getRouteTag());
			String stopTag = pred.getStopTag();
			Route r = this.getRoute(pred.getRouteTag());
			DIRECTION dir = DIRECTION.Inbound;
			int i = 0;

			// *** below is logic to see if stop is inbound or outbound.
			List<Stop> stopsPerDirection = r.getStopsPerDirection(DIRECTION.Inbound);
			for (; i < stopsPerDirection.size(); i++)
			{
				if (stopsPerDirection.get(i).getStopTag().equalsIgnoreCase(stopObject.getStopTag()))
				{
					dir = DIRECTION.Inbound;
				}
			}
			if (i == stopsPerDirection.size()) // chekc outbound if reached
												// limit
			{
				stopsPerDirection = r.getStopsPerDirection(DIRECTION.Outbound);
				for (i = 0; i < stopsPerDirection.size(); i++)
				{
					if (stopsPerDirection.get(i).getStopTag()
							.equalsIgnoreCase(stopObject.getStopTag()))
					{
						dir = DIRECTION.Inbound;
					}
				}
			}
			// ** end of direction logic.

			// Check if value exists
			SimpleEntry<String, DIRECTION> newEntry = new SimpleEntry<String, DIRECTION>(
					pred.getRouteTag(), dir);
			if (routesPerStops.containsKey(newEntry) == false)
				routesPerStops.put(new SimpleEntry<String, DIRECTION>(pred.getRouteTag(), dir),
						stopObject);
			// Check if it is the closest station
			else if (((Stop) routesPerStops.get(newEntry)).getDistFromCurrentLocation() > stopObject
					.getDistFromCurrentLocation())
				routesPerStops.put(newEntry, stopObject);
		}

		return routesPerStops;
	}

	/**
	 * Must be called after a list of all stops nearby has been located.
	 * 
	 * @param stops
	 *            - contains stopIDs
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

	public Predictions getPredictionsByStopAndRoute(String stopID, String routeTag)
	{
		List<Predictions> preds = this.mFetcher.loadPredictions(stopID, routeTag);

		if (preds.size() > 1)
			Log.e(this.getClass().getName(),
					"More than one prediction returned for route+stop request");

		if (preds.size() > 0)
			return preds.get(0);
		return null;

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
		if(refreshData == false)
		{
			predictionsInDirection = getLatestPredictions(dir);
			if(predictionsInDirection != null && predictionsInDirection.size() > 0)
			{
				Log.e(this.getClass().getSimpleName(), "preditioncs returned with results, size= "+ predictionsInDirection.size());
			
				return predictionsInDirection;
			}
		}
		
		if (this.mStopsNearLocation == null)
		{
			Log.e(this.getClass().getSimpleName(),
					"Need to call getStopsNearLocation first to establish a list of stops near location!");
			this.getStopsNearLocation(GpsManager.getInstance().getLastKnownLocation((Activity)mContext), this.mDefaultMaxDistance );
		}

		if (mPredictionsForStopsNearMeInbound == null || mPredictionsForStopsNearMeOutbound == null
				|| refreshData == true)
		{
			mPredictionsForStopsNearMeInbound = new ArrayList<Predictions>();
			mPredictionsForStopsNearMeOutbound = new ArrayList<Predictions>();

			if (dir == DIRECTION.Inbound)
				mPredictionsForStopsNearMeInbound
						.addAll(getPredictionsByStops(this.mStopsNearLocation));
			else
				mPredictionsForStopsNearMeOutbound
						.addAll(getPredictionsByStops(this.mStopsNearLocation));

		}

		// filter out directions
		for (Predictions p : ((dir == DIRECTION.Inbound) ? mPredictionsForStopsNearMeInbound
				: mPredictionsForStopsNearMeOutbound))
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
			if (p.getDirTitleBecauseNoPredictions() != null
					&& p.getDirTitleBecauseNoPredictions().equals("") == false)
				predictionsInDirection.add(p);
		}

		return predictionsInDirection;
	}

	public List<Predictions> getLatestPredictions(DIRECTION dir)
	{
		if (dir == DIRECTION.Inbound)
			return this.mPredictionsForStopsNearMeInbound;
		else
			return this.mPredictionsForStopsNearMeOutbound;
	}

	private DIRECTION getDirectionByStopID(Predictions predObj) throws StopNotFoundException
	{
		String routeTag = predObj.getRouteTag();
		String stopTag = predObj.getStopTag();

		for (Route r : this.mAllRoutesWithDirections)
		{
			if (r.getTag().equalsIgnoreCase(routeTag))
			{
				if (r.isStopTagInDirection(DIRECTION.Inbound, stopTag))
					return DIRECTION.Inbound;
				else if (r.isStopTagInDirection(DIRECTION.Outbound, stopTag))
					return DIRECTION.Outbound;
				else
					throw new StopNotFoundException("Stop did not return from route " + routeTag
							+ " in neither direction");
			}
		}
		throw new StopNotFoundException("Stop did not return from route " + routeTag
				+ " in neither direction");
	}

	/**
	 * Returns a Stop object per route and stop tag
	 * 
	 * @param stopTag
	 * @param routeTag
	 * @return the stop object requests. Can return NULL if no stop found.
	 */
	public Stop getStop(String stopTag, String routeTag)
	{
		for (Route r : this.mAllRoutesWithDirections)
		{
			if (r.getTag().equalsIgnoreCase(routeTag))
			{
				return r.getStopByTag(stopTag);
			}
		}
		return null;
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

	/**
	 * Returns the following stop to the route and stop and direction requested
	 * 
	 * @param routeTag
	 * @param direction
	 * @param baseStop
	 * @return return next stop. If last stop it returns itself. can return null
	 */
	public Stop getNextStop(String routeTag, DIRECTION direction, Stop baseStop)
	{
		Route requestedRoute = null;
		for (Route r : this.mAllRoutesWithDirections)
		{
			// Find the correct route
			if (r.getTag().equalsIgnoreCase(routeTag))
			{
				requestedRoute = r;
				break;
			}
		}

		// find the station in direction, then return the next one.
		List<Stop> stops = requestedRoute.getStopsPerDirection(direction);
		for (int i = 0; i < stops.size(); i++)
		{
			if (i == stops.size() - 1) // Last stop.
				return stops.get(i);
			if (stops.get(i).getStopTag().equalsIgnoreCase(baseStop.getStopTag()))
				return stops.get(i + 1);
		}

		return null;
	}

}
