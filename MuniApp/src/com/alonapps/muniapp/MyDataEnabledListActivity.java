package com.alonapps.muniapp;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;

public class MyDataEnabledListActivity extends ListActivity {

	DataManager mDataManager = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(mDataManager == null)
		{
			mDataManager= DataManager.getDataManager(this);
		}
		
	}
	

}
