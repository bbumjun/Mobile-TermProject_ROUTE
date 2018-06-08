package com.termproject.route.route;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.nio.ByteBuffer;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LoopbackService extends Service {

    int audioSource = MediaRecorder.AudioSource.MIC;
    int inputHz = 48000;
    int outputHz = 48000;
    int audioEncoding = AudioFormat.ENCODING_PCM_FLOAT;
    int bufferSize = AudioRecord.getMinBufferSize(inputHz, AudioFormat.CHANNEL_IN_MONO, audioEncoding);
    int numFrames = 500;
    int rblock = AudioRecord.READ_NON_BLOCKING;
    int wblock = AudioTrack.WRITE_NON_BLOCKING;


    boolean useArray = false;


    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


    private boolean playBack;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private AudioRecord recorder;
    private AudioTrack player;

    boolean temp = playBack = false;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            recorder = new AudioRecord.Builder()
                    .setAudioSource(audioSource)
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(audioEncoding)
                            .setSampleRate(inputHz)
                            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                            .build())
                    .build();
            player = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA) // so user can control volume separately
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(audioEncoding)
                            .setSampleRate(inputHz)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build())
                    .build();
            player.setPlaybackRate(outputHz);

            recorder.startRecording();
            player.play();
            final int bufferSize = recorder.getBufferSizeInFrames();
            final int sizef;
            sizef = 4;
            recorder.setPositionNotificationPeriod(numFrames);
            recorder.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
                float[] dataf = new float[numFrames];
                ByteBuffer data = ByteBuffer.allocateDirect(bufferSize * sizef);

                @Override
                public void onPeriodicNotification(AudioRecord recorder) {
                    int read = 0;
                    if (useArray) {
                        read = recorder.read(dataf, 0, numFrames, rblock);
                        if (read != AudioRecord.ERROR_INVALID_OPERATION)
                            player.write(dataf, 0, read, wblock);

                    } else {
                        read = recorder.read(data, numFrames * sizef, rblock);
                        if (read != AudioRecord.ERROR_INVALID_OPERATION)
                            player.write(data, read, wblock);
                        data.clear();
                    }
                }

                public void onMarkerReached(AudioRecord recorder) {
                }
            });
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.Intent notificationIntent = new Intent(this, ExampleActivity.class);


        startForeground(1, new Notification());
        HandlerThread thread = new HandlerThread("dur");
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        thread.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }


    @Override

    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        recorder.release();
        player.release();
    }


}
