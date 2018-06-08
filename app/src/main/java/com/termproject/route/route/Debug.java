package com.termproject.route.route;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;


public class Debug extends AppCompatActivity implements Parcelable{
    private static final String LOG_TAG = "Debug";
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


    private boolean playBack;
    AudioRecord recorder;
    AudioTrack player;

    public Debug(){};
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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_running);
        setSettings();

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
            tog(findViewById(R.id.soundButton));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,LoopbackService.class));
    }

    protected Debug(Parcel in) {
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

    @SuppressWarnings("unused")
    public static final Creator<Debug> CREATOR = new Creator<Debug>() {
        @Override
        public Debug createFromParcel(Parcel in) {
            return new Debug(in);
        }

        @Override
        public Debug[] newArray(int size) {
            return new Debug[size];
        }
    };
}