package com.termproject.route.route;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
float x;
Button logoutBtn, deleteIdBtn, volume;
ImageButton runningBtn,shareBtn;
ImageView userIcon, icon;
TextView userId, userUid;
TextView speedlimit;
ToggleButton toggleLimit;
SeekBar aroundSeek;
RadioGroup minmax;
RadioButton max,min;

Uri photoUrl;
    static int speedValue=20;
    static boolean toggleon=false;
    boolean isloop=false;

com.shawnlin.numberpicker.NumberPicker picker1;

    private static final int RC_SIGN_IN =123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);


        isloop = LoopbackService.isLoop;
        logoutBtn = (Button)findViewById(R.id.logoutBtn);
        deleteIdBtn =(Button)findViewById(R.id.deleteIDBtn);
        userIcon = (ImageView) findViewById(R.id.userIcon) ;
        icon = (ImageView) findViewById(R.id.icon);
        userId=(TextView) findViewById(R.id.userId) ;
        userUid=(TextView) findViewById(R.id.userUid) ;
        speedlimit= findViewById(R.id.speedlimit);
        toggleLimit =findViewById(R.id.toggleLimit);
        volume = findViewById(R.id.volume);

        picker1 = findViewById(R.id.number_picker);

        minmax=findViewById(R.id.minmax);
        min=findViewById(R.id.min);
        max=findViewById(R.id.max);

        aroundSeek = findViewById(R.id.aroundSeek);

        toggleLimit.setChecked(newRunningActivity.onoffBoolean);


        picker1.setValue(newRunningActivity.speedValue2);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {
            String mail  = user.getEmail();
            int ids = mail.indexOf("@");
            String EmailId = mail.substring(0,ids);
            userUid.setText(mail);
        }


        userId.setText(user.getDisplayName());
        String mail  = user.getEmail();
        int ids = mail.indexOf("@");
        String EmailId = mail.substring(0,ids);
        userUid.setText(mail);


        userIcon.setImageURI(photoUrl);

        runningBtn = findViewById(R.id.runText);

        runningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), newRunningActivity.class);
                startActivity(intent);
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
                    toggleon=true;
                    speedValue=picker1.getValue();
                    LocationService.limitSpeed=speedValue;
                    newRunningActivity.speedValue2=speedValue;
                    newRunningActivity.onoffBoolean=true;
                    String minmaxText="";
                    speedlimit.setText(speedValue+"km/h");


                    int radioId = minmax.getCheckedRadioButtonId();

                    if (max.getId()==radioId){
                        minmaxText="Max";

                        newRunningActivity.minmaxValue=1;
                        LocationService.limitCode=1;
                    }
                    if (min.getId()==radioId){
                        minmaxText="Min";
                        LocationService.limitCode=2;
                        newRunningActivity.minmaxValue=2;
                    }

                    speedlimit.setText(minmaxText+" "+speedValue+"km/h");


                } else {
                    speedlimit.setText("OFF");
                    LocationService.limitSpeed=100;
                    LocationService.limitCode=0;
                    newRunningActivity.minmaxValue=0;
                    toggleon=false;
                    newRunningActivity.onoffBoolean=false;
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



        volume.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                     float temp =((float)(aroundSeek.getProgress())/100);
                LoopbackService.gain=temp;
                Toast.makeText(getApplicationContext(),"Around Volume Setting :" + temp*100 + "%",Toast.LENGTH_LONG).show();

            }
        });


    }

}
