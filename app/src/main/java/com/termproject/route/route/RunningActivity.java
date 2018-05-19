package com.termproject.route.route;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    float x = 0;
    ImageButton mapBtn, start;
    TextView timeText, velocityText, distanceText;
    Handler time_handler;

    GoogleMap googleMap;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        mapBtn = (ImageButton) findViewById(R.id.mapButton);

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyLocationActivity.class);
                startActivity(intent);
            }

        });
    }





        public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            x=event.getX();
        }
        if(event.getAction()==MotionEvent.ACTION_UP) {
            if(x>event.getX()) {
                Intent intent = new Intent(getApplicationContext(),SharingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        }


        return true;
    }


}




