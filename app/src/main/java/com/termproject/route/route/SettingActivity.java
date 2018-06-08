package com.termproject.route.route;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
float x;
Button logoutBtn;
Button deleteIdBtn;
ImageButton runningBtn,shareBtn;
ImageView userIcon, icon;
TextView userId, userUid;
TextView speedlimit;
ToggleButton toggleLimit;
RadioGroup minmax;
RadioButton max,min;
Uri photoUrl;
boolean emailVerified;
String uid;
    private static final int RC_SIGN_IN =123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        logoutBtn = (Button)findViewById(R.id.logoutBtn);
        deleteIdBtn =(Button)findViewById(R.id.deleteIDBtn);
        userIcon = (ImageView) findViewById(R.id.userIcon) ;
        icon = (ImageView) findViewById(R.id.icon);
        userId=(TextView) findViewById(R.id.userId) ;
        userUid=(TextView) findViewById(R.id.userUid) ;
        speedlimit= findViewById(R.id.speedlimit);
        toggleLimit =findViewById(R.id.toggleLimit);



        minmax=findViewById(R.id.minmax);
        min=findViewById(R.id.min);
        max=findViewById(R.id.max);






        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {

            //photoUrl = user.getPhotoUrl();
            String mail  = user.getEmail();
            int ids = mail.indexOf("@");
            String EmailId = mail.substring(0,ids);
            userUid.setText(mail);
        }


        //photoUrl=user.getPhotoUrl();

        userId.setText(user.getDisplayName());
        String mail  = user.getEmail();
        int ids = mail.indexOf("@");
        String EmailId = mail.substring(0,ids);
        userUid.setText(mail);


        userIcon.setImageURI(photoUrl);









/*
        06-07 15:18:18.092 11902-11902/com.termproject.route.route D/MediaSessionHelper: dispatched media key KeyEvent { action=ACTION_DOWN, keyCode=KEYCODE_HEADSETHOOK, scanCode=226, metaState=0, flags=0x8, repeatCount=0, eventTime=45042678, downTime=45042678, deviceId=2, displayId=0, source=0x101 }
        06-07 15:18:18.109 11902-11902/com.termproject.route.route D/MediaSessionHelper: dispatched media key KeyEvent { action=ACTION_UP, keyCode=KEYCODE_HEADSETHOOK, scanCode=226, metaState=0, flags=0x8, repeatCount=0, eventTime=45042838, downTime=45042678, deviceId=2, displayId=0, source=0x101 }

*/


        //user.getPhotoUrl();









        runningBtn = findViewById(R.id.runText);

        runningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });



        shareBtn = findViewById(R.id.shareText);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SharingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });


        toggleLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleLimit.isChecked()){

                    com.shawnlin.numberpicker.NumberPicker picker1 = findViewById(R.id.number_picker);
                    int speedValue=picker1.getValue();

                    String minmaxText="";
                    speedlimit.setText(speedValue+"km/h");


                    int radioId = minmax.getCheckedRadioButtonId();

                    if (max.getId()==radioId){
                        minmaxText="Max";
                    }

                    if (min.getId()==radioId){
                        minmaxText="Min";
                    }

                    speedlimit.setText(minmaxText+" "+speedValue+"km/h");


                } else {
                    speedlimit.setText("Speed Safety Function OFF");
                }

            }
        });




        logoutBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Logout success",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    }
                });
                List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),RC_SIGN_IN);

            }

        });



        deleteIdBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AuthUI.getInstance().delete(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"Delete account success",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    }
                });
                List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),RC_SIGN_IN);

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
