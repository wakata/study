package com.example.musicplayer.kadai;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private MediaController mMediaController;

    private AtomicBoolean mIsPlay = new AtomicBoolean(false);

    private ImageButton mCtrButton;

    private ImageView mCoverArtView;

    private TextView mMediaAlbum;

    private TextView mMediaTitle;

    private TextView mMediaArtist;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!mIsPlay.get()) {
                mMediaController.start();
            } else {
                mMediaController.pause();
            }
            updateButton();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaController = ((MusicPlayerApplication)getApplicationContext()).getMediaController();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mCtrButton = findViewById(R.id.ctrButton);
        mCtrButton.setOnClickListener(mOnClickListener);

        mCoverArtView = findViewById(R.id.coverArt);
        mMediaAlbum = findViewById(R.id.media_album);
        mMediaTitle = findViewById(R.id.media_title);
        mMediaArtist = findViewById(R.id.media_artist);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButton();
        updateMediaView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateButton() {
        mIsPlay.set(mMediaController.isPlaying());

        if (!mIsPlay.get()) {
            mCtrButton.setImageResource(android.R.drawable.ic_media_play);
        } else {
            mCtrButton.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private void updateMediaView() {
        Bitmap coverArt = null;
        coverArt = mMediaController.getCaverArt();

        if (coverArt != null) {
            mCoverArtView.setImageBitmap(coverArt);
        } else {
            mCoverArtView.setImageResource(R.mipmap.ic_launcher);
        }

        mMediaAlbum.setText(getString(R.string.album));
        mMediaTitle.setText(getString(R.string.title));
        mMediaArtist.setText(getString(R.string.artist));
    }
}