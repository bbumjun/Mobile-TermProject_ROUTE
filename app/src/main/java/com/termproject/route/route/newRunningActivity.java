package com.termproject.route.route;

import android.content.Intent;
import android.location.Location;
import android.media.Image;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;

public class newRunningActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMyLocationClickListener {

    public static int NEW_LOCATION=1;
    GoogleMap mMap;
    float x = 0;
    int time=0,curTime=0,befTime=0;
    double bef_lat=0,bef_long=0,cur_lat=0,cur_long=0,sum_dist=0,velocity=0,avg_speed=0;
    LatLng ex_point,cur_point,first_point;
    boolean isRunning=true,isStarted=false;
    Handler gpsHandler,timeHandler;
    TimeRunnable runnable;
    GPSTracker gps;
    ImageButton shareBtn,settingBtn;
    Button tab1,tab2,tab3,startBtn,stopBtn;
    TextView timeText,velocityText,distanceText,calorieText;

    Marker curMarker;
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_running);

    timeText=(TextView)findViewById(R.id.timeText);
    calorieText=(TextView)findViewById(R.id.calorieText);
    startBtn = (Button) findViewById(R.id.startButton);
    shareBtn=(ImageButton)findViewById(R.id.shareText);
    settingBtn=(ImageButton)findViewById(R.id.setText);
    velocityText = (TextView) findViewById(R.id.velocityText);
    distanceText = (TextView) findViewById(R.id.distanceText);

    timeHandler= new Handler();
    gpsHandler=new Handler();
    runnable = new TimeRunnable();

    SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.realtimeMap);
    mapFragment.getMapAsync(this);

        gps = new GPSTracker(newRunningActivity.this, gpsHandler);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start
             if(!isStarted) {
                 MarkerOptions optFirst = new MarkerOptions();
                 optFirst.alpha(0.5f);
                 optFirst.anchor(0.5f, 0.5f);
                 optFirst.position(first_point);
                 Log.d("Position", first_point.toString());
                 optFirst.title("Running Start Point");
                 optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot));
                 mMap.addMarker(optFirst).showInfoWindow();

                 isStarted=true;

                 try{

                     Thread timeThread = new Thread(new Runnable() {
                         @Override
                         public void run() {
                             while (isStarted) {
                                 try {
                                     Thread.sleep(1000);
                                     timeHandler.post(runnable);
                                     time++;


                                 } catch (Exception e) {
                                 }
                             }
                         }
                     });
                     Thread GPSThread = new Thread(new Runnable() {
                         @Override
                         public void run() {
                             while (isStarted) {
                                 try {
                                     Thread.sleep(3000);

                                         gpsHandler.post(new Runnable() {
                                             @Override
                                             public void run() {
                                                 curTime=time;
                                                 gps.Update();

                                                 double latitude=gps.getLatitude();
                                                 double longitude=gps.getLongitude();
                                                 cur_lat=latitude;
                                                 cur_long=longitude;
                                                 if(cur_lat==bef_lat&&cur_long==bef_long) {
                                                     Log.d("Same Location","time : "+time);
                                                     befTime=time;
                                                 }
                                                 else {
                                                     CalDistance calDistance = new CalDistance(bef_lat, bef_long, cur_lat, cur_long);
                                                     double dist = calDistance.getDistance()/1000;
                                                     dist = (int) (dist * 1000) / 1000.0;
                                                     sum_dist += dist;
                                                     sum_dist = (int) (sum_dist * 1000) / 1000.0;
                                                     avg_speed = dist*3.6*(1/Math.abs(curTime-befTime)) ;
                                                     avg_speed = (int) (avg_speed * 1000) / 1000.0;

                                                     if(curMarker!=null) {
                                                         curMarker.remove();
                                                     }
                                                     LatLng befLatLng = new LatLng(bef_lat, bef_long);
                                                     ex_point = befLatLng;
                                                     bef_lat = cur_lat;
                                                     bef_long = cur_long;

                                                     LatLng curLatLng = new LatLng(cur_lat, cur_long);
                                                     cur_point = curLatLng;

                                                     mMap.addPolyline(new PolylineOptions().color(0xFFFF0000).width(10.0f).geodesic(true).add(cur_point).add(ex_point));

                                                     MarkerOptions optCurrent = new MarkerOptions();
                                                     optCurrent.alpha(0.5f);
                                                     optCurrent.anchor(0.5f, 0.5f);
                                                     optCurrent.position(cur_point);
                                                     optCurrent.title("now");
                                                     optCurrent.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot));
                                                     curMarker=mMap.addMarker(optCurrent);
                                                     mMap.addMarker(optCurrent).showInfoWindow();

                                                     velocityText.setText(avg_speed + "");
                                                     distanceText.setText(sum_dist + "");
                                                     calorieText.setText(cur_lat + " " + cur_long);
                                                     Log.d("bef time & cur time",befTime+" "+curTime);

                                                 }
                                             }
                                         });


                                 }catch (Exception e) {}
                             }
                         }
                     });
                     timeThread.start();
                     GPSThread.start();
                 }catch (Exception e) {

                     Log.e("newRunningActivity","Exception in processing message",e);

                 }

             }
             // stop
             else {





                 isStarted=false;
             }
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SharingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

}
class TimeRunnable implements Runnable {
    public  void run() {

            int tempHour, tempMinute, tempSecond;
            tempHour = time / 3600;
            tempMinute = (time % 3600) / 60;
            tempSecond = time % 60;

            timeText.setText(tempHour + ": " + tempMinute + "' " + tempSecond + "''");


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

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));


                }
            }, 2000);
        }

    }



}
