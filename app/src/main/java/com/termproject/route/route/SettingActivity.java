package com.termproject.route.route;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

public class SettingActivity extends AppCompatActivity {
float x;
Button logoutBtn;
Button deleteIdBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        logoutBtn = (Button)findViewById(R.id.logoutBtn);
        deleteIdBtn =(Button)findViewById(R.id.deleteAccountBtn);
        NumberPicker picker1 = (NumberPicker)findViewById(R.id.picker1);


        picker1.setMinValue(0);
        picker1.setMaxValue(50);
        picker1.setWrapSelectorWheel(false);




        ImageButton runningBtn = findViewById(R.id.runText);

        runningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RunningActivity.class);
                startActivity(intent);
                finish();
            }
        });



        ImageButton shareBtn = findViewById(R.id.shareText);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SharingActivity.class);
                startActivity(intent);
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
                Intent intent =new Intent(getApplicationContext(),SharingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        }
        return true;
    }
}
