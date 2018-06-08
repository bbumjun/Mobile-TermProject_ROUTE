package com.termproject.route.route;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/*

 D/MediaSessionHelper: dispatched media key KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_HEADSETHOOK, scanCode=226, metaState=0, flags=0x8, repeatCount=0, eventTime=45389022, downTime=45389022, deviceId=2, displayId=0, source=0x101 }
 D/MediaSessionHelper: dispatched media key KeyEvent { action=ACTION_UP, keyCode=KEYCODE_HEADSETHOOK, scanCode=226, metaState=0, flags=0x8, repeatCount=0, eventTime=45389183, downTime=45389022, deviceId=2, displayId=0, source=0x101 }


 */

import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_HEADSETHOOK;

public class newRunningActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    public static int NEW_LOCATION = 1;
    GoogleMap mMap;
    float x = 0;
    int time = 0, curTime = 0, befTime = 0;
    double bef_lat = 0, bef_long = 0, cur_lat = 0, cur_long = 0, sum_dist = 0, velocity = 0, avg_speed = 0;
    LatLng ex_point, cur_point, first_point;
    boolean isRunning = true, isStarted = false;
    Handler gpsHandler, timeHandler;
    TimeRunnable runnable;
    GPSTracker gps;
    ImageButton shareBtn, settingBtn;
    Button tab1, tab2, tab3, startBtn, stopBtn;
    TextView timeText, velocityText, distanceText;
    Marker curMarker;

    //

    private Uri imgUri, photoURI, albumURI;

    private boolean playBack;

    LocationService myService;
    static boolean status;
    LocationManager locationManager;
    static TextView dist, time2, speed2 ,kmText, calorieText;
    Button start, pause, stop;
    static long startTime, endTime;
    static ProgressDialog locate;
    static int p = 0;


    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            myService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            status = false;
        }
    };

    void bindService() {
        if (status == true)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        bindService(i, sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    void unbindService() {
        if (status == false)
            return;
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        unbindService(sc);
        status = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (status == true)
            unbindService();
    }

    @Override
    public void onBackPressed() {
        if (status == false)
            super.onBackPressed();
        else
            moveTaskToBack(true);
    }


    //


    float speed = 0;
    public Animation fab_open, fab_close;
    public Boolean isFabOpen = false;
    public FloatingActionButton fab, on, cameraButton;


    int MY_LOCATION_REQUEST_CODE;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        timeText = (TextView) findViewById(R.id.timeText);
        calorieText = (TextView) findViewById(R.id.calorieText);
        startBtn = (Button) findViewById(R.id.dustButton);
        shareBtn = (ImageButton) findViewById(R.id.shareText);
        settingBtn = (ImageButton) findViewById(R.id.setText);
        //velocityText = (TextView) findViewById(R.id.velocityText);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        cameraButton = (FloatingActionButton) findViewById(R.id.cameraButton);
        on = (FloatingActionButton) findViewById(R.id.on);


        dist = (TextView) findViewById(R.id.distanceText);
        //time2 = (TextView) findViewById(R.id.timetext);
        speed2 = (TextView) findViewById(R.id.velocityText);
        kmText = (TextView) findViewById(R.id.kmText);


        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);



        timeHandler = new Handler();
        gpsHandler = new Handler();
        runnable = new TimeRunnable();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.realtimeMap);
        mapFragment.getMapAsync(this);

        gps = new GPSTracker(newRunningActivity.this, gpsHandler);


        startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                    startActivity(intent);
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


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim();
            }

        });




        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                public void takePhoto(){
//
//                    // 촬영 후 이미지 가져옴
//
//                    String state = Environment.getExternalStorageState();
//
//
//                    if(Environment.MEDIA_MOUNTED.equals(state)){
//
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                        if(intent.resolveActivity(getPackageManager())!=null){
//
//                            File photoFile = null;
//
//                            try{
//
//                                photoFile = createImageFile();
//
//                            }catch (IOException e){
//
//                                e.printStackTrace();
//
//                            }
//
//                            if(photoFile!=null){
//
//                                Uri providerURI = FileProvider.getUriForFile(this,getPackageName(),photoFile);
//
//                                imgUri = providerURI;
//
//                                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, providerURI);
//
//                                startActivityForResult(intent, FROM_CAMERA);
//
//                            }
//                        }
//                    }else{
//
//                        Log.v("알림", "저장공간에 접근 불가능");
//                        return;
//
//                    }
//                }
//          }

            }
        });




        /*


        on.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(playBack == false){
                    playBack=true;
                }else playBack =false;

                if (!playBack) {
                    stopService(new Intent(this, LoopbackService.class);
                    return;
                }
                else startService(new Intent(this, LoopbackService.class).putExtra(this,"hi"));

            }

        });


     */


        //


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!isStarted) {
                    MarkerOptions optFirst = new MarkerOptions();
                    optFirst.alpha(0.5f);
                    optFirst.anchor(0.5f, 0.5f);
                    optFirst.position(first_point);
                    Log.d("Position", first_point.toString());
                    optFirst.title("Running Start Point");
                    optFirst.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot));
                    mMap.addMarker(optFirst).showInfoWindow();


                    isStarted = true;

                    try {

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
                                        Thread.sleep(1000);

                                        gpsHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                curTime = time;
                                                gps.Update();


                                                //speed=Location.getSpeed();
                                                double cur_speed = 0.0;
                                                double latitude = gps.getLatitude();
                                                double longitude = gps.getLongitude();
                                                cur_lat = latitude;
                                                cur_long = longitude;


                                                if (cur_lat == bef_lat && cur_long == bef_long) {
                                                    Log.d("Same Location", "time : " + time);
                                                    befTime = time;
                                                } else {
                                                    /*
                                                    CalDistance calDistance = new CalDistance(bef_lat, bef_long, cur_lat, cur_long);
                                                    double dist = calDistance.getDistance() / 100;
                                                    dist = (int) (dist * 100) / 100.0;
                                                    sum_dist += dist;
                                                    sum_dist = (int) (sum_dist * 1000) / 10000.0;
                                                    if ((curTime - befTime) != 0) {
                                                        avg_speed = dist * 3.6 * (1 / Math.abs(curTime - befTime));
                                                    }
                                                    avg_speed = (int) (avg_speed * 1000) / 1000.0;
                                                    if ((curTime - befTime) != 0) {
                                                        cur_speed = dist / 0.36;
                                                    }
                                                    cur_speed = (int) (cur_speed * 10000) / 1000.0;

                                                    */
                                                    if (curMarker != null) {
                                                        curMarker.remove();
                                                    }

                                                    LatLng befLatLng = new LatLng(bef_lat, bef_long);
                                                    ex_point = befLatLng;
                                                    bef_lat = cur_lat;
                                                    bef_long = cur_long;

                                                    LatLng curLatLng = new LatLng(cur_lat, cur_long);
                                                    cur_point = curLatLng;

                                                    mMap.addPolyline(new PolylineOptions().color(0xFFFF0000).width(20.0f).geodesic(true).add(cur_point).add(ex_point));

                                                    MarkerOptions optCurrent = new MarkerOptions();
                                                    optCurrent.alpha(0.5f);
                                                    optCurrent.anchor(0.5f, 0.5f);
                                                    optCurrent.position(cur_point);
                                                    optCurrent.title("now");
                                                    optCurrent.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot));
                                                    curMarker = mMap.addMarker(optCurrent);
                                                    mMap.addMarker(optCurrent).showInfoWindow();


                                                    //velocityText.setText(gps.location.getSpeed()+"");

                                                    Log.d("Speed", gps.location.getSpeed() + "");
                                                    //velocityText.setText(cur_speed + "");
                                                    //distanceText.setText(sum_dist + "");
                                                    //calorieText.setText(cur_lat + " " + cur_long);
                                                    Log.d("bef time & cur time", befTime + " " + curTime);

                                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(curLatLng));
                                                }
                                            }
                                        });


                                    } catch (Exception e) {
                                    }
                                }
                            }
                        });
                        timeThread.start();
                        GPSThread.start();
                    } catch (Exception e) {

                        Log.e("newRunningActivity", "Exception in processing message", e);

                    }

                }


                //The method below checks if Location is enabled on device or not. If not, then an alert dialog box appears with option
                //to enable gps.
                checkGps();
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                    return;
                }


                if (status == false)
                    //Here, the Location Service gets bound and the GPS Speedometer gets Active.
                    bindService();
                locate = new ProgressDialog(newRunningActivity.this);
                locate.setIndeterminate(true);
                locate.setCancelable(false);
                locate.setMessage("Getting Location...");
                locate.show();
                start.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                pause.setText("Pause");
                stop.setVisibility(View.VISIBLE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isStarted = false;


                if (pause.getText().toString().equalsIgnoreCase("pause")) {
                    pause.setText("Resume");
                    p = 1;

                } else if (pause.getText().toString().equalsIgnoreCase("Resume")) {
                    checkGps();
                    isStarted = true;
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pause.setText("Pause");
                    p = 0;

                }
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                isStarted = false;

                if (status == true)
                    unbindService();
                start.setVisibility(View.VISIBLE);
                pause.setText("Pause");
                pause.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
                time=0;
                LocationService.distance=0;
                p = 0;
            }
        });


        //
    }


    class TimeRunnable implements Runnable {
        public void run() {

            int tempHour, tempMinute, tempSecond;
            tempHour = time / 3600;
            tempMinute = (time % 3600) / 60;
            tempSecond = time % 60;


            String hour = "", min = "", sec = "";
            if (tempHour < 10) {
                hour = "0" + tempHour;
            } else hour = "" + tempHour;
            if (tempMinute < 10) {
                min = "0" + tempMinute;
            } else min = "" + tempMinute;
            if (tempSecond < 10) {
                sec = "0" + tempSecond;
            } else sec = "" + tempSecond;


            timeText.setText(" " + hour + ":" + min + "'" + sec + "''" + " ");
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            // Show rationale and request permission.
        }

        mMap.setOnMyLocationButtonClickListener(this);


        mMap.setOnMyLocationClickListener(this);
        if (gps.canGetLocation()) {
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


    public void anim() {

        if (isFabOpen) {
            cameraButton.startAnimation(fab_close);
            on.startAnimation(fab_close);
            cameraButton.setClickable(false);
            on.setClickable(false);
            isFabOpen = false;
        } else {
            cameraButton.startAnimation(fab_open);
            on.startAnimation(fab_open);
            cameraButton.setClickable(true);
            on.setClickable(true);
            isFabOpen = true;
        }
    }


    //This method leads you to the alert dialog box.
    void checkGps() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            showGPSDisabledAlertToUser();
        }
    }

    //This method configures the Alert Dialog box.
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable GPS to use application")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}

