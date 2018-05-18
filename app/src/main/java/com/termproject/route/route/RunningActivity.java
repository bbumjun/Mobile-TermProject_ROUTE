package com.termproject.route.route;

import android.app.Fragment;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RunningActivity extends AppCompatActivity {
float x=0;
Button mapBtn;
Button start;
TextView timeText,velocityText,distanceText;
Handler time_handler;
GoogleMap googleMap;
private double sum_dist; //총 라이딩 거리
private double avg_speed; //평균속도
private int timer=0;
private double s_lat; //시작지점 경도
private double s_long; //시작지점 위도
private double f_lat;// 종료지점 경도
private double f_long;//종료지점 위도
boolean isReset=true,isBtnClickStart=false;
calDistance caldistance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        mapBtn = (Button) findViewById(R.id.mapButton);
        start = (Button) findViewById(R.id.startButton);
        timeText = (TextView) findViewById(R.id.timeText);
        velocityText = (TextView) findViewById(R.id.velocityText);
        distanceText = (TextView) findViewById(R.id.distanceText);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyLocationActivity.class);
                startActivity(intent);
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.runningFragment) {
                    if (isReset == false) {
                        Toast.makeText(getApplicationContext(), "Reset", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isBtnClickStart == true) {
                        Toast.makeText(getApplicationContext(), "Already started", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Timer started", Toast.LENGTH_SHORT).show();
                    return;

                    isBtnClickStart = true;
                    isReset = false;
                    GPSTracker gps = new GPSTracker(getApplicationContext(), time_handler);
                    if (gps.canGetLocation()) {
                        Log.d("GPS start", "찍힘" + timer);
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);

                        MarkerOptions optFirst = new MarkerOptions();
                        optFirst.alpha(0.5f);
                        optFirst.anchor(0.5f, 0.5f);
                        optFirst.position(latLng);
                        optFirst.title("Running Start Point");
                        optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                        googleMap.addMarker(optFirst).showInfoWindow();

                        /* store the GPS info store*/
                        bef_lat = latitude;
                        bef_long = longitude;
                        s_lat = String.valueOf(latitude);
                        s_long = String.valueOf(longitude);

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        s_time = sdfNow.format(date);
                    }
                    /*Start latitude,longitude*/


                    time_handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            time_handler.sendEmptyMessageDelayed(0, 1000);

                            timer++;
                            velocityText.setText(" m");
                            distanceText.setText("km");
                            if (timer % 3 == 0) {
                                GPSTracker gps = new GPSTracker(getApplicationContext(), time_handler);
                                if (gps.canGetLocation()) {
                                    Log.d("GPS start", "찍힘" + timer);
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();

                                    cur_lat = latitude;
                                    cur_long = longitude;
                                    caldistance = new calDistance(bef_lat, bef_long, cur_lat, cur_long);
                                    double dist = calDistance.getDistance();
                                    dist = (int) (dist * 100) / 100.0;

                                    sum_dist += dist;

                                    /*평균속도 계산하기*/
                                    avg_speed = dist / timer;
                                    avg_speed = (int) (avg_spped * 100) / 100.0;

                                    bef_lat = cur_lat;
                                    bef_long = cur_long;

                                    LatLng latLng = new LatLng(latitude, longitude);

                                    //Showing the current Location in Google Map
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                                    /*이전과 현재의 point로 폴리라인을 긋는다.*/
                                    current_point = latLng;
                                    googleMap.addPolyline(new PolylineOptions().color(0xFFFF0000).width(30.0f).geodesic(true).add(latLng).add(ex_point));

                                    ex_point = latLng;
                                    //마커 설정

                                    MarkerOptions optFirst = new MarkerOptions();
                                    optFirst.alpha(0.5f);
                                    optFirst.anchor(0.5f, 0.5f);
                                    optFirst.position(latLng);
                                    optFirst.title("Running Start Point");
                                    optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
                                    googleMap.addMarker(optFirst).showInfoWindow();

                                }
                            }
                        }

                    };
                    time_handler.sendEmptyMessage(0);
                }
            }
        });

    }
        public boolean onTouchEvent (MotionEvent event){

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                x = event.getX();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (x > event.getX()) {
                    Intent intent = new Intent(getApplicationContext(), SharingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }


            return true;
        }


}
