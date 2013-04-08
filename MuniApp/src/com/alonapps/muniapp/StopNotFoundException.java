package com.alonapps.muniapp;

import android.util.Log;

public class StopNotFoundException extends Exception
{

	public StopNotFoundException()
	{
		super();
	}

	public StopNotFoundException(String detailMessage)
	{
		super(detailMessage);
		Log.e("stop not found", detailMessage);
	}

	public StopNotFoundException(Throwable throwable)
	{
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	public StopNotFoundException(String detailMessage, Throwable throwable)
	{
		super(detailMessage, throwable);
		// TODO Auto-generated constructor stub
		Log.e("stop not found ["+throwable.getClass()+"]", detailMessage);
	}

}
