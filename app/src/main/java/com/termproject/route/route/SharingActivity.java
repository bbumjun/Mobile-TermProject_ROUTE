package com.termproject.route.route;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class SharingActivity extends AppCompatActivity {
float x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            x=event.getX();
        }
        if(event.getAction()==MotionEvent.ACTION_UP) {
            if(x<event.getX()) {
                Intent intent =new Intent(getApplicationContext(),RunningActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
            else if(x>event.getX()) {
                Intent intent=new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        }
        return true;

    }

}
