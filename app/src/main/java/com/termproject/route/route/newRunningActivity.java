package com.termproject.route.route;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import android.widget.RelativeLayout;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 D/MediaSessionHelper: dispatched media key KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_HEADSETHOOK, scanCode=226, metaState=0, flags=0x8, repeatCount=0, eventTime=45389022, downTime=45389022, deviceId=2, displayId=0, source=0x101 }
 D/MediaSessionHelper: dispatched media key KeyEvent { action=ACTION_UP, keyCode=KEYCODE_HEADSETHOOK, scanCode=226, metaState=0, flags=0x8, repeatCount=0, eventTime=45389183, downTime=45389022, deviceId=2, displayId=0, source=0x101 }
 */

import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_HEADSETHOOK;
import static com.yongbeam.y_photopicker.util.photopicker.utils.ImageCaptureManager.REQUEST_TAKE_PHOTO;

public class newRunningActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, Parcelable {

    private static final String LOG_TAG = "newRunningActivity";
    //settings
    int audioSource = MediaRecorder.AudioSource.MIC;
    int inputHz;
    int outputHz; /* in Hz*/
    int audioEncoding;
    int bufferSize;
    int numFrames;
    int rblock = AudioRecord.READ_NON_BLOCKING, wblock = AudioTrack.WRITE_NON_BLOCKING;
    boolean useArray = false;
    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private String[] permissionsGPS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private String[] permissionsNET = {Manifest.permission.ACCESS_NETWORK_STATE};
    private String[] permissionsINTERNET = {Manifest.permission.INTERNET};
    private String[] permissionsWS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String[] permissionsRS = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private String[] permissionsCAM = {Manifest.permission.CAMERA};
    ImageView iv_view;
    private BackPressCloseHandler backPressCloseHandler;

    static boolean playBack = false;
    int on = 0;

    public newRunningActivity() {
    }

    ;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }


    //for Sound

    public static int NEW_LOCATION = 1;
    GoogleMap mMap;
    float x = 0;
    static int time = 0, curTime = 0, befTime = 0;
    double bef_lat = 0, bef_long = 0, cur_lat = 0, cur_long = 0, sum_dist = 0, velocity = 0, avg_speed = 0;
    LatLng ex_point, cur_point, first_point;
    static boolean isStarted = false;
    Handler gpsHandler, timeHandler;
    TimeRunnable runnable;
    GPSTracker gps;
    ImageButton shareBtn, settingBtn;
    Button tab1, tab2, tab3, startBtn, stopBtn;
    TextView timeText, velocityText, distanceText;
    Marker curMarker,startMarker;
    static RelativeLayout statusScreen;
    Thread timeThread = new Thread();
    Thread GPSThread = new Thread();
    static int speedValue2 = 100;
    static boolean onoffBoolean = false;
    static int minmaxValue = 0;
    String mCurrentPhotoPath;
    //

    private Uri imageUri, photoURI, albumURI;


    LocationService myService;
    static boolean status;
    LocationManager locationManager;
    static TextView dist, time2, speed2, kmText, calorieText;
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
    protected void onStop() {
        super.onStop();

    }



    @Override
    public void onBackPressed() {
        if (status == false) {
           // super.onBackPressed();
            backPressCloseHandler.onBackPressed();
        }
            else
            moveTaskToBack(true);
    }


    float speed = 0;
    public Animation fab_open, fab_close;
    public Boolean isFabOpen = false;
    public FloatingActionButton fab, soundBtn, cameraButton;


    private String state;


    int MY_LOCATION_REQUEST_CODE;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        backPressCloseHandler =new BackPressCloseHandler(this);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(newRunningActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();



            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(newRunningActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };


        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .check();




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
        soundBtn = findViewById(R.id.on);


        statusScreen = findViewById(R.id.status);

        speedValue2 = SettingActivity.speedValue;

        dist = (TextView) findViewById(R.id.distanceText);
        //time2 = (TextView) findViewById(R.id.timetext);
        speed2 = (TextView) findViewById(R.id.velocityText);
        kmText = (TextView) findViewById(R.id.kmText);


        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        stop = (Button) findViewById(R.id.stop);


        //

        TextView minmax = findViewById(R.id.minMax);
        TextView limitSpeed2 = findViewById(R.id.limitSpeed);
        TextView onoff = findViewById(R.id.onoff);


        limitSpeed2.setText(speedValue2 + "");
        onoff.setText(onoffBoolean + "");
        minmax.setText(minmaxValue + "");


        LocationService.limitSpeed = speedValue2; // speed Value for Limit
        LocationService.limitCode = minmaxValue; // 1 for Max 2 for Min
        //




        timeHandler = new Handler();
        gpsHandler = new Handler();
        runnable = new TimeRunnable();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.realtimeMap);
        mapFragment.getMapAsync(this);

        gps = new GPSTracker(newRunningActivity.this, gpsHandler);


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                captureCamera();
            }
        });


        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (on) {
                    case 0:
                        playBack = true;
                        setSettings();
                        Log.d("info1", "토글 온 ");
                        on = 1;
                        break;
                    case 1:
                        playBack = false;
                        setSettings();
                        Log.d("info1", "토글 오프 ");
                        on = 0;
                        break;
                }
            }
        });


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
                    mMap.addMarker(optFirst).showInfoWindow();


                    isStarted = true;

                    try {

                        timeThread = new Thread(new Runnable() {
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



                        GPSThread = new Thread(new Runnable() {
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

                                                double cur_speed = 0.0;
                                                double latitude = gps.getLatitude();
                                                double longitude = gps.getLongitude();
                                                cur_lat = latitude;
                                                cur_long = longitude;


                                                if (cur_lat == bef_lat && cur_long == bef_long) {
                                                    Log.d("Same Location", "time : " + time);
                                                    befTime = time;
                                                } else {

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


                                                    Log.d("Speed", gps.location.getSpeed() + "");
                                                    Log.d("bef time & cur time", befTime + " " + curTime);

                                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(curLatLng));
                                                }
                                            }
                                        });

                                    } catch (Exception e) {
                                    }
                                }
                                //

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

                if (pause.getText().toString().equalsIgnoreCase("pause")) {
                    pause.setText("Resume");
                    Toast.makeText(getApplicationContext(), "Pause Exercise", Toast.LENGTH_SHORT).show();
                    p = 1;
                    isStarted=false;
                    timeThread.interrupt();

                } else if (pause.getText().toString().equalsIgnoreCase("Resume")) {
                    isStarted = true;
                    checkGps();
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pause.setText("Pause");
                    Toast.makeText(getApplicationContext(), "Resume Exercise", Toast.LENGTH_SHORT).show();
                    isStarted = true;
                    p = 0;
                    timeThread.run();



                }
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try{
                    CaptureMapScreen();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("TTAAGG","Can't capture");
                }

                if (status == true)
                    unbindService();
                start.setVisibility(View.VISIBLE);
                pause.setText("Pause");
                pause.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);

                p = 0;

                stopService(new Intent(getApplicationContext(), LocationService.class));
                dist.setText("0.0");
                calorieText.setText(("0"));
                isStarted = false;
                time = 0;
                timeText.setText("  00:00'00'' ");
                mMap.clear();
                Toast.makeText(getApplicationContext(), "Stop Exercise", Toast.LENGTH_SHORT).show();
                LocationService.distance = 0;


            }
        });

    }
    public void CaptureMapScreen() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;
            @Override

            public void onSnapshotReady(Bitmap snapshot) {
                bitmap = snapshot;
                try {
                    File imageFile =null;
                    File storageDir =new File(Environment.getExternalStorageDirectory()+"/Pictures/RouteImage","Route");
                    storageDir.toString();
                    if(!storageDir.exists()) {
                        Log.i("mCurrentPhotoPath1",storageDir.toString());
                        storageDir.mkdirs();
                    }
                    String path = storageDir.toString()+System.currentTimeMillis() + ".png";
                    FileOutputStream out = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mMap.snapshot(callback);
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

        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
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
            soundBtn.startAnimation(fab_close);
            cameraButton.setClickable(false);
            soundBtn.setClickable(false);
            isFabOpen = false;
        } else {
            cameraButton.startAnimation(fab_open);
            soundBtn.startAnimation(fab_open);
            cameraButton.setClickable(true);
            soundBtn.setClickable(true);
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


    public void wow() {
        if (!playBack) {
            stopService(new Intent(this, LoopbackService.class));
            return;
        }
        startService(new Intent(this, LoopbackService.class).putExtra("info", this));

    }

    public void setSettings() {
        boolean temp = playBack;
        Log.d("wow", "" + temp);

        numFrames = 500;
        rblock = AudioRecord.READ_NON_BLOCKING;
        wblock = AudioTrack.WRITE_NON_BLOCKING;
        useArray = false;
        inputHz = 48000;
        outputHz = 48001;
        audioEncoding = AudioFormat.ENCODING_PCM_FLOAT;
        bufferSize = AudioRecord.getMinBufferSize(inputHz, AudioFormat.CHANNEL_IN_MONO, audioEncoding);
        audioSource = 1;


        wow();
        playBack = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (status == true)
            unbindService();
        if(LoopbackService.isLoop){
            unbindService(sc);
        }
        stopService(new Intent(this,LoopbackService.class));
    }



    protected newRunningActivity(Parcel in) {
        audioSource = in.readInt();
        inputHz = in.readInt();
        outputHz = in.readInt();
        audioEncoding = in.readInt();
        bufferSize = in.readInt();
        numFrames = in.readInt();
        rblock = in.readInt();
        useArray = in.readByte() != 0x00;
        permissionToRecordAccepted = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(audioSource);
        dest.writeInt(inputHz);
        dest.writeInt(outputHz);
        dest.writeInt(audioEncoding);
        dest.writeInt(bufferSize);
        dest.writeInt(numFrames);
        dest.writeInt(rblock);
        dest.writeByte((byte) (useArray ? 0x01 : 0x00));
        dest.writeByte((byte) (permissionToRecordAccepted ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Creator<newRunningActivity> CREATOR = new Creator<newRunningActivity>() {
        @Override
        public newRunningActivity createFromParcel(Parcel in) {
            return new newRunningActivity(in);
        }
        @Override
        public newRunningActivity[] newArray(int size) {
            return new newRunningActivity[size];
        }
    };
    private void captureCamera() {
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if(takePictureIntent.resolveActivity(getPackageManager())!=null){
                File photoFile =null;
                try{
                    photoFile = createImageFile();
                }catch (IOException e) {
                    Log.e("captureCamera Error", e.toString());
                }
                if(photoFile!=null) {
                    Uri providerURI = FileProvider.getUriForFile(this,getPackageName(),photoFile);
                    imageUri=providerURI;


                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,providerURI);

                    startActivityForResult(takePictureIntent,REQUEST_TAKE_PHOTO);
                }
            }
        } else {
            Toast.makeText(this, "저장공간 접근 불가", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public File createImageFile() throws  IOException {
        String timeStamp = new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date());
        String imageFileName = timeStamp+".jpg";
        File imageFile =null;
        File storageDir =new File(Environment.getExternalStorageDirectory()+"/Pictures","Route");

        if(!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1",storageDir.toString());
            storageDir.mkdirs();

        }
        imageFile =new File(storageDir,imageFileName) ;
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;

    }

    private void galleryAddPic() {
        Log.i ("galleryAddPic","Call");
        Intent mediaScanIntent =new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f =new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this ,"저장되었습니다.",Toast.LENGTH_SHORT).show();

    }

    protected  void onActivityResult(int requestCode,int resultCode, Intent data ) {
        if(requestCode==REQUEST_TAKE_PHOTO) {
            if(resultCode== Activity.RESULT_OK) {
                try{
                    Log.i("REQUEST_TAKE_PHOTO","OK");
                    galleryAddPic();

                    iv_view.setImageURI(imageUri);
                } catch (Exception e) {
                    Log.e("REQUEST_TAKE_PHOTO",e.toString());

                }
            } else {
                Toast.makeText(this,"취소",Toast.LENGTH_SHORT).show();
            }

        }

    }




}

