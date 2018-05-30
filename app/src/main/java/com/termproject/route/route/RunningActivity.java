package com.termproject.route.route;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ToggleButton;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RunningActivity extends AppCompatActivity implements Parcelable{
    private static final String LOG_TAG = "RunningActivity";
    //settings
    int audioSource = MediaRecorder.AudioSource.MIC;
    int inputHz;
    int outputHz; /* in Hz*/
    int audioEncoding;
    int bufferSize;
    int numFrames;
    int rblock=AudioRecord.READ_NON_BLOCKING,wblock=AudioTrack.WRITE_NON_BLOCKING;
    boolean useArray=false;
    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    float x = 0;

    private boolean playBack;
    ImageButton mapBtn, start, cameraBtn;

    public RunningActivity(){};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }

        if (!permissionToRecordAccepted) finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_running);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        setSettings();




        cameraBtn=(ImageButton) findViewById(R.id.cameraButton);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent2 = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent2);
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

        ImageButton settingBtn = findViewById(R.id.setText);

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
                finish();
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



    public void tog(View view) {
        ToggleButton on = (ToggleButton) view;
        playBack = on.isChecked();
        if (!playBack) {
            stopService(new Intent(this, LoopbackService.class));
            return;
        }
        startService(new Intent(this, LoopbackService.class).putExtra("info", this));

    }

    public void setSettings(){
        boolean temp=playBack;
        playBack=false;
        numFrames=500;
        rblock=AudioRecord.READ_NON_BLOCKING;
        wblock=AudioTrack.WRITE_NON_BLOCKING;

        useArray=false;
        inputHz=48000;
        outputHz=48001;
        audioEncoding=AudioFormat.ENCODING_PCM_FLOAT;
        bufferSize =  AudioRecord.getMinBufferSize(inputHz, AudioFormat.CHANNEL_IN_MONO, audioEncoding);
        audioSource=1;

        if(temp) {
            tog(findViewById(R.id.on));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,LoopbackService.class));
    }

    protected RunningActivity(Parcel in) {
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

    public static final Parcelable.Creator<RunningActivity> CREATOR = new Parcelable.Creator<RunningActivity>() {
        @Override
        public RunningActivity createFromParcel(Parcel in) {
            return new RunningActivity(in);
        }

        @Override
        public RunningActivity[] newArray(int size) {
            return new RunningActivity[size];
        }
    };




}




