package com.alonapps.muniapp;

import android.app.Activity;
//taken from http://stackoverflow.com/questions/15995311/how-to-keep-gps-turned-on-when-switching-between-activities-but-off-when-when-h

public class ActivityTracker {

    private static ActivityTracker instance = new ActivityTracker();
    private boolean resumed;
    private boolean inForeground;

    private ActivityTracker() { /*no instantiation*/ }

    public static ActivityTracker getInstance() {
        return instance;
    }

    public void onActivityStarted(Activity context) {
        if (!inForeground) {
            /* 
             * Started activities should be visible (though not always interact-able),
             * so you should be in the foreground here.
             *
             * Register your location listener here. 
             */
            inForeground = true;
            GpsManager.getInstance().startListening(context);
        }
    }

    public void onActivityResumed() {
        resumed = true;
    }

    public void onActivityPaused() {
        resumed = false;
    }

    public void onActivityStopped() {
        if (!resumed) {
            /* If another one of your activities had taken the foreground, it would
             * have tripped this flag in onActivityResumed(). Since that is not the
             * case, your app is in the background.
             *
             * Unregister your location listener here.
             */
            inForeground = false;
            GpsManager.getInstance().stopListening();
        }
    }
}