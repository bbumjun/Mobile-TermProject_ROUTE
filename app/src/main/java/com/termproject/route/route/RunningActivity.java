package com.termproject.route.route;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class RunningActivity extends AppCompatActivity {
float x=0;
Button mapBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        mapBtn = (Button)findViewById(R.id.mapButton);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MyLocationActivity.class);
                startActivity(intent);
            }
        });
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
