package com.termproject.route.route;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class SettingActivity extends AppCompatActivity {
float x;
Button logoutBtn;
Button deleteIdBtn,runningBtn,sharingBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        runningBtn=(Button)findViewById(R.id.runText);
        sharingBtn=(Button)findViewById(R.id.shareText);
        logoutBtn = (Button)findViewById(R.id.logoutBtn);
        deleteIdBtn =(Button)findViewById(R.id.deleteAccountBtn);
        NumberPicker picker1 = (NumberPicker)findViewById(R.id.picker1);


        picker1.setMinValue(0);
        picker1.setMaxValue(50);
        picker1.setWrapSelectorWheel(false);

        sharingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SharingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });
        runningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });

    }

    @Override

    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            x=event.getX();
        }


        if(event.getAction()==MotionEvent.ACTION_UP) {
            if(x<event.getX()) {

                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        }
        return true;
    }
}
