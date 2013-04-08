package com.alonapps.muniapp;

import java.text.DecimalFormat;

public class ConversionHelper
{
	public static double convertMetersToFeet(float meters)
	{
		// function converts Feet to Meters.
		float toFeet;
		toFeet = meters * 3.2808f; // official conversion rate of Meters to Feet
		String formattedNumber = new DecimalFormat("0.0000").format(toFeet); 
		// return with 4 decimal places
		float d = Float.valueOf(formattedNumber.trim()).floatValue();
		return d;
	}
}
