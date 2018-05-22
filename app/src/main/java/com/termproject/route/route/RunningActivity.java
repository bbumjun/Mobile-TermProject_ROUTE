package com.termproject.route.route;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RunningActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
       // OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{
    float x = 0;
    Button mapBtn;
    Button start;
    TextView timeText, velocityText, distanceText;
    Handler time_handler;
    GoogleMap googleMap ;
    private LatLng current_point,ex_point;
    private double sum_dist; //총 라이딩 거리
    private double avg_speed; //평균속도
    private int timer = 0;
    private String s_lat; //시작지점 경도
    private String s_long; //시작지점 위도
    private String s_time;
    private double f_lat;// 종료지점 경도
    private double f_long;//종료지점 위도
    private double cur_lat,cur_long;
    double bef_lat,bef_long;
    boolean isReset = true, isBtnClickStart = false;
    GPSTracker gps = null;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    public Handler mHandler;

    public static int RENEW_GPS = 1;
    public static int SEND_PRINT = 2;
   // Button btnShowLocation;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        mapBtn = (Button) findViewById(R.id.mapButton);
        start = (Button) findViewById(R.id.startButton);
        timeText = (TextView) findViewById(R.id.timeText);
        velocityText = (TextView) findViewById(R.id.velocityText);
        distanceText = (TextView) findViewById(R.id.distanceText);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.realtimeMap);
        mapFragment.getMapAsync(this);
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }


        MapsInitializer.initialize(getApplicationContext());
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyLocationActivity.class);
                startActivity(intent);
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.startButton) {
                    if (isReset == false) {
                        Toast.makeText(getApplicationContext(), "Reset", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isBtnClickStart == true) {
                        Toast.makeText(getApplicationContext(), "Already started", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Timer started", Toast.LENGTH_SHORT).show();

                    isBtnClickStart = true;
                    isReset = false;

                    if (gps == null) {
                        gps = new GPSTracker(RunningActivity.this, mHandler);
                    } else {
                        gps.Update();
                    }
                    GPSTracker gps = new GPSTracker(getApplicationContext(), time_handler);
                    if (gps.canGetLocation()) {
                        Log.d("GPS start", "찍힘" + timer);
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        //Showing the current Location in Google Map
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        MarkerOptions optFirst = new MarkerOptions();
                        optFirst.alpha(0.5f);
                        optFirst.anchor(0.5f, 0.5f);
                        optFirst.position(latLng);
                        Log.d("Position", latLng.toString());
                        optFirst.title("Running Start Point");
                        optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot));
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

                        /*Start latitude,longitude*/

                    }
                    time_handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {

                            time_handler.sendEmptyMessageDelayed(0, 1000);

                            if (msg.what == RENEW_GPS) {
                                makeNewGpsService();
                            }
                            /*if (msg.what == SEND_PRINT) {
                                logPrint((String) msg.obj);
                            }*/
                            timer++;
                            timeText.setText(timer + "s");
                            if(avg_speed!=0.0) {
                                velocityText.setText(avg_speed + " m/s");
                            }
                            distanceText.setText((int)sum_dist + " m");
                            if (timer % 3 == 0) {
                                GPSTracker gps = new GPSTracker(RunningActivity.this, time_handler);
                                if (gps.canGetLocation()) {
                                    Log.d("GPS start", "찍힘" + timer);
                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();

                                    cur_lat = latitude;
                                    cur_long = longitude;
                                    CalDistance calDistance = new CalDistance(bef_lat, bef_long, cur_lat, cur_long);
                                    double dist = calDistance.getDistance();
                                    dist = (int) (dist * 100) / 100.0;

                                    sum_dist += dist;

                                    /*평균속도 계산하기*/
                                    avg_speed = dist / 3;
                                    avg_speed = (int) (avg_speed * 100) / 100.0;

                                    bef_lat = cur_lat;
                                    bef_long = cur_long;

                                    LatLng latLng = new LatLng(latitude, longitude);



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
                                    optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot));
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
        public void makeNewGpsService() {
            if (gps == null) {
                gps = new GPSTracker(RunningActivity.this, mHandler);
            } else {
                gps.Update();
            }

        }
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (googleMap != null) {
            // Access to the location has been granted to the app.
            googleMap.setMyLocationEnabled(true);
        }
    }
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);

        enableMyLocation();

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
    
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
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
                finish();
            }
        }


        return true;
    }

}