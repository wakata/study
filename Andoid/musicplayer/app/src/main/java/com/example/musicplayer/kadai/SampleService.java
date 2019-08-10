package com.example.musicplayer.kadai;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

public class SampleService extends Service {
    private final static String TAG = SampleService.class.getName();

    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private final IBinder mBinder = new SampleBinder();

    private MediaController mMediaController;

    private Notification mNotification;

    private NotificationManager mNotificationManager;

    private RemoteViews mRemoteViews;

    public class SampleBinder extends Binder {
        SampleService getService() {
            return SampleService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaController = ((MusicPlayerApplication)getApplicationContext()).getMediaController();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pause();
        mMediaPlayer.release();
        mMediaPlayer.reset();
    }

    public synchronized void start() {
        try {
            mMediaPlayer.setDataSource(mMediaController.getNowPlayingPath());
            mMediaPlayer.prepare();
        } catch (Exception e) {
            Log.e(TAG ,e.toString());
        }
        mMediaPlayer.start();
        mNotificationManager.notify(1, mNotification);
    }

    public synchronized void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(SampleService.class.getName(), "onStartCommand service");

        mNotificationManager =
                (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Context context = getApplicationContext();

        String channelId = "default";
        String title = context.getString(R.string.app_name);

        NotificationChannel channel =
                new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
        if(mNotificationManager != null)
        {
            mNotificationManager.createNotificationChannel(channel);

            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
            mRemoteViews.setTextViewText(R.id.media_title, "TitleSample");
            mRemoteViews.setImageViewResource(R.id.coverArt, R.mipmap.ic_launcher);

             mNotification = new Notification.Builder(getApplicationContext(), channelId)
                    .setContentTitle(title)
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setCustomContentView(mRemoteViews)
                    .build();
            startForeground(1, mNotification);
        }

        mMediaController.setUpMedia();

        return START_NOT_STICKY;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }
}