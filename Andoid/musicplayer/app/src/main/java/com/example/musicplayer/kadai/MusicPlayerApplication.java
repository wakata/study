package com.example.musicplayer.kadai;

import android.app.Application;

public class MusicPlayerApplication extends Application {
    private static MediaController mMediaController;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaController = new MediaController(this);
        mMediaController.setup();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mMediaController.close();
        mMediaController = null;
    }

    public MediaController getMediaController() {
        return mMediaController;
    }
}
