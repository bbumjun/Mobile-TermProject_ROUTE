package com.termproject.route.route;

import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//import net.danlew.android.joda.JodaTimeAndroid;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.http.Query;

public class newRunningActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    public static int NEW_LOCATION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private String imageFilePath;
    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름
    private final int CAMERA_CODE = 1111;
    private final int GALLERY_CODE = 1112;


    GoogleMap mMap;
    float x = 0;
    int time = 0, curTime = 0, befTime = 0;
    double bef_lat = 0, bef_long = 0, cur_lat = 0, cur_long = 0, sum_dist = 0, velocity = 0, avg_speed = 0;
    LatLng ex_point, cur_point, first_point;
    boolean isRunning = true, isStarted = false;
    Handler gpsHandler, timeHandler;
    TimeRunnable runnable;
    GPSTracker gps;
    ImageButton startBtn, cameraBtn;
    Button tab1, tab2, tab3, stopBtn, shareBtn, settingBtn;
    TextView timeText, velocityText, distanceText, calorieText;
    FrameLayout container;
    Marker curMarker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_running);

        shareBtn = (Button) findViewById(R.id.shareText);
        settingBtn = (Button) findViewById(R.id.setText);
        timeText = (TextView) findViewById(R.id.timeText);
        calorieText = (TextView) findViewById(R.id.calorieText);
        startBtn = (ImageButton) findViewById(R.id.startButton);
        velocityText = (TextView) findViewById(R.id.velocityText);
        distanceText = (TextView) findViewById(R.id.distanceText);
        container = (FrameLayout) findViewById(R.id.mapLayout);
        tab1 = (Button) findViewById(R.id.runText);
        tab2 = (Button) findViewById(R.id.shareText);
        tab3 = (Button) findViewById(R.id.setText);
        cameraBtn = (ImageButton) findViewById(R.id.cameraButton);
        stopBtn = (Button) findViewById(R.id.stopButton);
        timeHandler = new Handler();
        gpsHandler = new Handler();
        runnable = new TimeRunnable();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.realtimeMap);
        mapFragment.getMapAsync(this);

        gps = new GPSTracker(newRunningActivity.this, gpsHandler);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_CODE);
            }
        });
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start
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
                                        Thread.sleep(3000);

                                        gpsHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                curTime = time;
                                                gps.Update();

                                                double latitude = gps.getLatitude();
                                                double longitude = gps.getLongitude();
                                                cur_lat = latitude;
                                                cur_long = longitude;
                                                if (cur_lat == bef_lat && cur_long == bef_long) {
                                                    Log.d("Same Location", "time : " + time);
                                                    befTime = time;
                                                } else {
                                                    CalDistance calDistance = new CalDistance(bef_lat, bef_long, cur_lat, cur_long);
                                                    double dist = calDistance.getDistance() / 1000;
                                                    dist = (int) (dist * 1000) / 1000.0;
                                                    sum_dist += dist;
                                                    sum_dist = (int) (sum_dist * 1000) / 1000.0;
                                                    avg_speed = dist * 3.6 * (1 / Math.abs(curTime - befTime));
                                                    avg_speed = (int) (avg_speed * 1000) / 1000.0;

                                                    if (curMarker != null) {
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
                                                    curMarker = mMap.addMarker(optCurrent);
                                                    mMap.addMarker(optCurrent).showInfoWindow();

                                                    velocityText.setText(avg_speed + "");
                                                    distanceText.setText(sum_dist + "");
                                                    calorieText.setText(cur_lat + " " + cur_long);
                                                    Log.d("bef time & cur time", befTime + " " + curTime);
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
                // stop
                else {


                    isStarted = false;
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CaptureMapScreen();
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

    private void selectPhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_CODE);
                }
            }

        }
    }

    private void selectGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    private void sendPicture(Uri imgUri) {

        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        //ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기

    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }


    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /*  private void sendTakePhotoIntent() {
          Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
              startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
          }
      }*/
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /*  private void CaptureMapScreen(){
          GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
              @Override
              public void onSnapshotReady(Bitmap snapshot) {
                  Matrix m = new Matrix();
                  Bitmap bitmap1 = Bitmap.createBitmap(snapshot,0,0,snapshot.getWidth(),snapshot.getHeight()-400,m,false);
                  String fileName = System.currentTimeMillis()+".png";
                  try{
                      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                      bitmap1.compress(Bitmap.CompressFormat.PNG,50,bytes);
                      FileOutputStream fileOutputStream = new FileOutputStream("/storage/emulated/0/Pictures/Kakaotalk/"+fileName);
                      fileOutputStream.write(bytes.toByteArray());
                      File image = new File("/storage/emulated/0/Pictures/Kakaotalk/"+fileName);
                      image.isDirectory();
                      Intent shareIntent = new Intent(Intent.ACTION_SEND);
                      shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                      *//*shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(newRunningActivity.this,getApplicationContext().getPackageName()+"com.termproject.route.route",image));*//*
                    shareIntent.putExtra(Intent.EXTRA_STREAM,FileProvider.getUriForFile(getApplicationContext(),"com.termproject.route.route.fileprovider",image));
                    shareIntent.setType("image/png");
                    startActivity(Intent.createChooser(shareIntent,"구글지도 이미지"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        mMap.snapshot(callback);

    }*/
    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/path/" + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();
Log.e("pathh",storageDir.getAbsolutePath().toString() + "dd");
        return storageDir;

    }

    private void getPictureForPhoto() {
        File f;
        try {
            f = createImageFile();
        }catch (Exception e){
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path, "a.jpg");
        ExifInterface exif = null;
        try {
            FileOutputStream out = new FileOutputStream(file);
            if(bitmap !=null)
              bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            else
                Toast.makeText(getApplicationContext(), "널포인트", Toast.LENGTH_SHORT).show();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        // ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_CODE:
                    sendPicture(data.getData()); //갤러리에서 가져오기
                    break;
                case CAMERA_CODE:
                    getPictureForPhoto(); //카메라에서 가져오기
                    break;

                default:
                    break;
            }

        }
    }


    class TimeRunnable implements Runnable {
        public void run() {

            int tempHour, tempMinute, tempSecond;
            tempHour = time / 3600;
            tempMinute = (time % 3600) / 60;
            tempSecond = time % 60;

            timeText.setText(tempHour + "h " + tempMinute + "m " + tempSecond + "s");


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
  /*  @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent(getActivity(), FilterCamera.class));
        } else if (id == R.id.nav_gallery) {
            Uri uri = Uri.parse("content://media/internal/images/media");
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.logout) {
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    FirebaseAuth.getInstance().signOut();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            currActivity.setUI();
                        }
                    });
                }
            });
        }
        DrawerLayout drawer = layout.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
*/


}
