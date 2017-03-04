package org.uusoftware.thelaunchpadhouse;

import android.app.Application;

import com.google.android.gms.analytics.Tracker;

public class ActivityAnalytics extends Application {
    String trackingId = "UA-66337763-4";
    private Tracker mTracker;

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            com.google.android.gms.analytics.GoogleAnalytics analytics = com.google.android.gms.analytics.GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(trackingId);
        }
        return mTracker;
    }
}