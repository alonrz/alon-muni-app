package com.alonapps.muniapp;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;

public class MyDataEnabledListActivity extends ListActivity {

	Context context;
	DataManager mDataManager = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		context = this;
		if(mDataManager == null)
		{
			mDataManager= DataManager.getDataManager(context);
		}
		
	}
	

}
