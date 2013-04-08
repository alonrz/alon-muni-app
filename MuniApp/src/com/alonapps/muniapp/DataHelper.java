package com.alonapps.muniapp;

import java.util.Hashtable;
import java.util.List;

public class DataHelper
{

	/**
	 * Remove all route which are not running now (have the label
	 * "dirTitleBecauseNoPredictions"
	 * 
	 * @param predictionsList
	 */
	public static void RemoveNonActive(List<Predictions> predictionsList)
	{
		int i = 0;
		while (i < predictionsList.size())
		{
			if (predictionsList.get(i).getDirTitleBecauseNoPredictions() != null)
				predictionsList.remove(i);
			else
				i++;
		}
	}

	public static void RemoveAllButTwoClosestStations(List<Predictions> predictionsList)
	{
		// This will check that no more than the closest 2 stations appear...
		Hashtable<String, Integer> routesAppearanceCount = new Hashtable<String, Integer>();
		
		int i = 0;
		while (i < predictionsList.size())
		{
			int count=0;
			if(routesAppearanceCount.keySet().contains(predictionsList.get(i).getRouteTag()) == true)
			{
				count = routesAppearanceCount.get(predictionsList.get(i).getRouteTag());
			}
			routesAppearanceCount.put(predictionsList.get(i).getRouteTag(), ++count);
			if (count>2)
				predictionsList.remove(i);
			else
				i++;
		}
	}

}
