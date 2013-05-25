package com.alonapps.muniapp.datacontroller;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Support for SQLite. This has the command to open a "favorites" table with 2 columns.<br/> 
 * This is not yet used in this project
 * @author alon
 *
 */
public class FavoriteOpenHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
    public static final String FAVORITES_TABLE_NAME = "favorites";
    public static final String KEY_STOP_ID = "stop_id";
    public static final String KEY_ROUTE_ID = "route_id";
    private static final String FAVORITES_TABLE_CREATE =
                "CREATE TABLE " + FAVORITES_TABLE_NAME + " (" +
                KEY_STOP_ID + " TEXT, " +
                KEY_ROUTE_ID + " TEXT);";
    
    private Context mContext;
    
	public FavoriteOpenHelper(Context context)
	{
		super(context, FAVORITES_TABLE_NAME, null, DATABASE_VERSION);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		try
		{
		db.execSQL(FAVORITES_TABLE_CREATE);
		
		}catch(SQLException e)
		{
			Toast.makeText(this.mContext, "Error creating table" + e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub

	}

}
