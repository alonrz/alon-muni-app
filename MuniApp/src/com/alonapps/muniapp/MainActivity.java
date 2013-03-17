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

//	/**inner class UIHandler **/
//	private final class UIHandler extends Handler {
//		 
//        public void handleMessage(Message msg) {
//            
//            
//        }
// 
//    };
    
    
    
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
    
    
//    final private Handler handler = new UIHandler();
    
    public void onClick(View v)
    {
//    	new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				List<String> routeList = fetcher.GetRouteList();		
//			}
//		});
    	
    	Intent intent = new Intent(this, ListRoutes.class);
//    	intent.putExtra("routeList", routeList.toArray(new String[0]));
    	startActivity(intent);
    	
    }
    
}
