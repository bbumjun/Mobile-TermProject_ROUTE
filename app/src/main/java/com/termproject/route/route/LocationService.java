package com.termproject.route.route;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by vipul on 12/13/2015.
 */
public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;


    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation, lStart, lEnd;

    static double distance = 0;
    static double limitSpeed=0; //speed for Limitation
    static int limitCode=0;    //0=off 1=Max 2=Min
    double speed;


    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        return mBinder;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
        }
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        distance = 0;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {
        newRunningActivity.locate.dismiss();
        mCurrentLocation = location;
        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
        } else
            lEnd = mCurrentLocation;

        //Calling the method below updates the  live values of distance and speed to the TextViews.
        updateUI();
        //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
        speed = location.getSpeed() * 18 / 5;

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class LocalBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }


    }

    //The live feed of Distance and Speed are being set in the method below .
    private void updateUI() {
        if (newRunningActivity.p == 0) {
            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
            newRunningActivity.endTime = System.currentTimeMillis();
            long diff = newRunningActivity.endTime - newRunningActivity.startTime;
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);
            //MainActivity.time.setText("Total Time: " + diff + " minutes");
            if (speed >= 0.0) {
                newRunningActivity.speed2.setText(new DecimalFormat("#.#").format(speed));

                if(limitCode==1&&speed>=limitSpeed){//Max Speed Limit
                    newRunningActivity.statusScreen.setBackgroundColor(Color.rgb(255,20,20));
                    displayNotification();

                }else if(limitCode==2&&speed<limitSpeed){//Min Speed Limit
                    newRunningActivity.statusScreen.setBackgroundColor(Color.rgb(255,20,20));
                    displayNotification();
                }else newRunningActivity.statusScreen.setBackgroundColor(Color.rgb(255,255,255));

            }
            if(distance>1) {
                newRunningActivity.dist.setText(new DecimalFormat("#.###").format(distance));
                newRunningActivity.kmText.setText("km ");
                newRunningActivity.calorieText.setText(new DecimalFormat("###").format(distance*21));
            }
            else {
                newRunningActivity.dist.setText(new DecimalFormat("###").format(distance*1000));
                newRunningActivity.kmText.setText("m ");
                newRunningActivity.calorieText.setText(new DecimalFormat("###").format(distance*21));
            }

            lStart = lEnd;

        }

    }


    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart = null;
        lEnd = null;
        distance = 0;
        return super.onUnbind(intent);
    }


    protected void displayNotification()
    {

        Intent i = new Intent(getApplicationContext(), newRunningActivity.class);
        i.putExtra("notificationID", 23);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager nm = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Reminder: Meeting starts in 5 minutes");

        mBuilder.setContentIntent(pendingIntent);
//---100ms delay, vibrate for 250ms, pause for 100 ms and
// then vibrate for 500ms---
        mBuilder.setVibrate(new long[] { 100, 250, 100, 500});
        nm.notify(23, mBuilder.build());
    }
}