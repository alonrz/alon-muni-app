package com.alonapps.muniapp.ui.fragments;

import java.util.Hashtable;

import com.alonapps.muniapp.R;
import com.alonapps.muniapp.datacontroller.DataManager;
import com.alonapps.muniapp.datacontroller.FavoriteOpenHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Favorites feature, for future versions. 
 * @author alon
 *
 */
public class FavoritesFragment extends Fragment
{
	Context mContext;
	
	public Fragment getInstance()
	{
		return new FavoritesFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		SQLiteDatabase dbFav = DataManager.getInstance(mContext).getFavoriteOpenHelper().getReadableDatabase();
		Cursor reader = dbFav.rawQuery("select * from " +FavoriteOpenHelper.FAVORITES_TABLE_NAME, null);//(FavoriteOpenHelper.FAVORITES_TABLE_NAME, null, null, null, null, null, null);
		
		Hashtable<String, String> listStopIDRouteID = new Hashtable<String, String>();
		String stopid, routeid;
		if(reader.getCount() == 0)
		{
			Toast.makeText(mContext, "no values in table", Toast.LENGTH_LONG).show();
			return;
		}
		reader.moveToFirst();
		do
		{
			stopid =  reader.getString(reader.getColumnIndex(FavoriteOpenHelper.KEY_STOP_ID));
			routeid = reader.getString(reader.getColumnIndex(FavoriteOpenHelper.KEY_ROUTE_ID));
			listStopIDRouteID.put(stopid, routeid);
			
		}while(reader.moveToNext() == true);
		
		//Toast.makeText(mContext, listStopIDRouteID.keySet().toArray()[0].toString(), Toast.LENGTH_LONG).show();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_favorites, container, false);
	}
}
