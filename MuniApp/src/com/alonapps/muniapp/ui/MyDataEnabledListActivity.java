package com.alonapps.muniapp.ui;

import com.alonapps.muniapp.datacontroller.DataManager;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;

public class MyDataEnabledListActivity extends ListActivity
{

	DataManager mDataManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mDataManager = DataManager.getDataManager(this);

	}

}
