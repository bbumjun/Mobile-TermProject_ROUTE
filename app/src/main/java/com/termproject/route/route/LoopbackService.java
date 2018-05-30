package com.termproject.route.route;

import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
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
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private AudioRecord recorder;
    private AudioTrack player;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            final RunningActivity d= (RunningActivity) msg.obj;
            recorder = new AudioRecord.Builder()
                    .setAudioSource(d.audioSource)
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(d.audioEncoding)
                            .setSampleRate(d.inputHz)
                            .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                            .build())
                    .build();
            player = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA) // so user can control volume separately
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(d.audioEncoding)
                            .setSampleRate(d.inputHz)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build())
                    .build();
            player.setPlaybackRate(d.outputHz);

            recorder.startRecording();
            player.play();
            final int bufferSize=recorder.getBufferSizeInFrames();
            final int sizef;
            switch (d.audioEncoding) {
                case AudioFormat.ENCODING_PCM_8BIT:
                    sizef = 1;
                    break;
                case AudioFormat.ENCODING_PCM_16BIT:
                    sizef = 2;
                    break;
                case AudioFormat.ENCODING_PCM_FLOAT:
                    sizef = 4;
                    break;
                default: sizef=0;
            }

            recorder.setPositionNotificationPeriod(d.numFrames);
            recorder.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
                byte[] datab = new byte[d.numFrames];
                short[] datas = new short[d.numFrames];
                float[] dataf = new float[d.numFrames];
                ByteBuffer data = ByteBuffer.allocateDirect(bufferSize * sizef);
                @Override
                public void onPeriodicNotification(AudioRecord recorder){
                    int read=0;
                    if(d.useArray) {
                        switch (d.audioEncoding) {
                            case AudioFormat.ENCODING_PCM_8BIT:
                                read = recorder.read(datab, 0, d.numFrames, d.rblock);
                                if (read != AudioRecord.ERROR_INVALID_OPERATION)
                                    player.write(datab, 0, read, d.wblock);
                                break;
                            case AudioFormat.ENCODING_PCM_16BIT:
                                read = recorder.read(datas, 0, d.numFrames, d.rblock);
                                if (read != AudioRecord.ERROR_INVALID_OPERATION)
                                    player.write(datas, 0, read, d.wblock);
                                break;
                            case AudioFormat.ENCODING_PCM_FLOAT:
                                read = recorder.read(dataf, 0, d.numFrames, d.rblock);
                                if (read != AudioRecord.ERROR_INVALID_OPERATION)
                                    player.write(dataf, 0, read, d.wblock);
                                break;
                        }
                    }else {
                        read = recorder.read(data, d.numFrames * sizef, d.rblock);
                        if (read != AudioRecord.ERROR_INVALID_OPERATION)
                            player.write(data, read, d.wblock);
                        data.clear();
                    }
                }
                public void onMarkerReached(AudioRecord recorder){}
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
        Message msg = mServiceHandler.obtainMessage();
        msg.obj = intent.getExtras().getParcelable("info");
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        recorder.release();
        player.release();
    }

}
