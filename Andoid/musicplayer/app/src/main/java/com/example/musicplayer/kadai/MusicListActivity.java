package com.example.musicplayer.kadai;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class MusicListActivity extends Activity {
    private ListView mListView;

    private MusicAdapter mMusicAdapter;

    private MediaController mMediaController;

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        mListView = findViewById(R.id.music_list);
        mMediaController = ((MusicPlayerApplication)getApplicationContext()).getMediaController();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mMusicAdapter = new MusicAdapter(this, mMediaController.getMediaList());
        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mListView.setAdapter(mMusicAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void selectItem(int position) {
        CopyOnWriteArrayList<ArrayList> mediaList = mMediaController.getMediaList();
        ArrayList<String> musicList = mediaList.get(position);
        mMediaController.start(musicList.get(MediaController.PATH_INDEX));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
