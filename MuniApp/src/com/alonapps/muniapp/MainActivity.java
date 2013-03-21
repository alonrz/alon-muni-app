package com.alonapps.muniapp;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    
	XmlFetcher fetcher; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onClick(View v)
    {	
    	Intent intent = new Intent(this, ListRoutes.class);
    	startActivity(intent);
    }
    
    public void onClick2(View v)
    {	
    	Intent intent = new Intent(this, ShowMap.class);
    	startActivity(intent);
    }
    
}
