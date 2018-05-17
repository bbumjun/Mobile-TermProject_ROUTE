package com.termproject.route.route;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class RunningActivity extends AppCompatActivity {
float x=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
    }
    public boolean onTouchEvent(MotionEvent event) {

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
