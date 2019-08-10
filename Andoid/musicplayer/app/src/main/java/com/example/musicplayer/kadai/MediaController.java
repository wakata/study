package com.example.musicplayer.kadai;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MediaController {
    private final static String EMPTY_STRING = "";

    public final static int URI_INDEX = 0;

    public final static int TITLE_INDEX = 1;

    public final static int ARTIST_INDEX = 2;

    public final static int ALBUM_INDEX = 3;

    public final static int PATH_INDEX = 4;

    private ContentResolver mContentResolver;

    private AtomicReference<Uri> mUri = new AtomicReference();

    private AtomicReference<String> mTitle = new AtomicReference(EMPTY_STRING);

    private AtomicReference<String> mAlbum = new AtomicReference(EMPTY_STRING);

    private AtomicReference<String> mArtist = new AtomicReference(EMPTY_STRING);

    private AtomicReference<String> mPath = new AtomicReference(EMPTY_STRING);

    private AtomicReference<CopyOnWriteArrayList> mMediaList = new AtomicReference<>(new CopyOnWriteArrayList());

    private Context mContext;

    private SampleService mService;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SampleService.SampleBinder SampleBinder = (SampleService.SampleBinder)service;
            mService = SampleBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    public MediaController(Context context) {
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    public void setup() {
        Intent intent = new Intent(mContext, SampleService.class);
        intent.putExtra("REQUEST_CODE", 1);
        mContext.startForegroundService(intent);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void close() {
        mContext.unbindService(mServiceConnection);
    }

    public void start(String path) {
        if (mService != null && !mService.isPlaying()) {
            setNowPlayingMedia(path);
            mService.start();
        }
    }

    public void start() {
        if (mService != null && !mService.isPlaying()) {
            mService.start();
        }
    }

    public void pause() {
        if (mService != null && mService.isPlaying()) {
            mService.pause();
        }
    }

    public boolean isPlaying() {
        boolean isPlaying = false;
        if (mService != null) {
            isPlaying = mService.isPlaying();
        }
        return isPlaying;
    }

    public Bitmap getCaverArt(String path) {
        Bitmap coverArt = null;

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] data = null;
        if (!path.isEmpty()) {
            try {
                mmr.setDataSource(path);
                data  = mmr.getEmbeddedPicture();
            } catch (IllegalArgumentException e) {

            }
        }

        if (null != data) {
            coverArt = BitmapFactory.decodeByteArray(data, 0, data.length);
        } else {

        }

        return coverArt;
    }

    public Bitmap getCaverArt() {
        return getCaverArt(mPath.get());
    }

    public void setNowPlayingMedia(String path) {
        CopyOnWriteArrayList<ArrayList> mediaList = mMediaList.get();
        ArrayList<String> mediaItemList;

        for (int idx = 0; idx < mediaList.size(); idx++) {
            mediaItemList = mediaList.get(idx);
            if (path.isEmpty() && TextUtils.equals(path, mediaItemList.get(PATH_INDEX))) {
                mUri.set(Uri.parse(path));
                mTitle.set(mediaItemList.get(TITLE_INDEX));
                mArtist.set(mediaItemList.get(ARTIST_INDEX));
                mAlbum.set(mediaItemList.get(ALBUM_INDEX));
                mPath.set(mediaItemList.get(PATH_INDEX));
            }
        }
    }

    public String getNowPlayingPath() {
        return mPath.get();
    }

    public void setUpMedia() {
        Cursor cursor = mContentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.AudioColumns.DATA
                }, null, null, null);

        cursor.moveToFirst();

        mUri.set(Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))));
        mTitle.set(cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Media.TITLE)));
        mArtist.set(cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST)));
        mAlbum.set(cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM)));
        mPath.set(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)));
        CopyOnWriteArrayList mediaList = mMediaList.get();

        do {
            ArrayList<String> mediaItemList = new ArrayList();
            mediaItemList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            mediaItemList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            mediaItemList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            mediaItemList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            mediaItemList.add(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)));
            if (!mediaItemList.contains(mediaItemList)) {
                mediaList.addIfAbsent(mediaItemList);
            }
        } while (cursor.moveToNext());

        mMediaList.set(mediaList);
    }

    public Uri getMediaUri(){
        return mUri.get();
    }

    public String getTitle() {
        return mTitle.get();
    }

    public String getAlbum(){
        return mAlbum.get();
    }

    public String getArtist() {
        return mArtist.get();
    }

    public CopyOnWriteArrayList getMediaList() {
        return mMediaList.get();
    }
}
