package com.termproject.route.route;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;

public class newRunningActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener {

    GoogleMap mMap;
    float x = 0;
    int time=0;
    double bef_lat=0,bef_long=0,cur_lat=0,cur_long=0,sum_dist=0,velocity=0,avg_speed=0;
    LatLng ex_point,cur_point,first_point;
    boolean isRunning=true;
    Handler infoHandler =new Handler();
    GPSTracker gps;
    ImageButton startBtn;
    Button tab1,tab2,tab3;
    TextView timeText,velocityText,distanceText,calorieText;
    FrameLayout container;
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_running);
    timeText=(TextView)findViewById(R.id.timeText);
    calorieText=(TextView)findViewById(R.id.calorieText);
    startBtn = (ImageButton)findViewById(R.id.startButton);
    velocityText = (TextView) findViewById(R.id.velocityText);
    distanceText = (TextView) findViewById(R.id.distanceText);
    container = (FrameLayout)findViewById(R.id.mapLayout);
    tab1=(Button)findViewById(R.id.runText);
    tab2=(Button)findViewById(R.id.shareText);
    tab3=(Button)findViewById(R.id.setText);


    SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.realtimeMap);
    mapFragment.getMapAsync(this);

        gps = new GPSTracker(newRunningActivity.this, infoHandler);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));


                    MarkerOptions optFirst = new MarkerOptions();
                    optFirst.alpha(0.5f);
                    optFirst.anchor(0.5f, 0.5f);
                    optFirst.position(first_point);
                    Log.d("Position", first_point.toString());
                    optFirst.title("Running Start Point");
                    optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot));
                    mMap.addMarker(optFirst).showInfoWindow();




                RunningThread thread = new RunningThread();
                thread.start();
            }
        });



}




class RunningThread extends Thread {

        public void run() {

            try{

                CalDistance calDistance ;
                while(isRunning) {
                    time++;
                    gps.Update();
                        if(gps.canGetLocation()) {

                            cur_lat = gps.getLatitude();
                            cur_long = gps.getLongitude();
                            calDistance = new CalDistance(bef_lat, bef_long, cur_lat, cur_long);
                            double dist = calDistance.getDistance();
                            dist = (int) (dist * 1000) / 1000.0;
                            sum_dist+=dist;
                            avg_speed = dist *3.6;
                            avg_speed=(int)(avg_speed*1000)/1000.0;

                            LatLng befLatLng = new LatLng(bef_lat, bef_long);
                            ex_point = befLatLng;
                            bef_lat = cur_lat;
                            bef_long = cur_long;

                            LatLng curLatLng = new LatLng(cur_lat, cur_long);
                            cur_point = curLatLng;

                            infoHandler.post(new Runnable() {
                                @Override
                                public void run() {


                                    mMap.addPolyline(new PolylineOptions().color(0xFFFF0000).width(10.0f).geodesic(true).add(cur_point).add(ex_point));
                                    velocityText.setText(avg_speed + "");
                                    distanceText.setText(sum_dist + "");
                                    calorieText.setText(cur_lat + " " + cur_long);

                                    int tempTime=time;
                                    int tempHour,tempMinute,tempSecond;
                                    tempHour=time/3600;
                                    tempMinute=(time%3600)/60;
                                    tempSecond=time%60;

                                    timeText.setText(tempHour+"h "+tempMinute+"m "+tempSecond+"s");
                                    Log.d("before gps", bef_lat + " " + bef_long);
                                    Log.d("current gps", cur_lat + " " + cur_long);
                                    Log.d("distance", velocity + "");
                                }
                            });

                        }


                    Thread.sleep(1000);

                }

            }catch (Exception e) {

                Log.e("newRunningActivity","Exception in processing message",e);

            }
        }

}







    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }



    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);

        mMap.setOnMyLocationClickListener(this);
        if(gps.canGetLocation()) {
            gps.Update();
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            bef_lat = latitude;
            bef_long = longitude;
            first_point = new LatLng(latitude, longitude);
            //Showing the current Location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(first_point));

        }

    }



    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x = event.getX();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (x > event.getX()) {
                Intent intent = new Intent(getApplicationContext(), SharingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        }

        return true;
    }


}
