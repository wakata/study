package com.example.musicplayer.kadai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class MusicAdapter extends BaseAdapter {
    private CopyOnWriteArrayList<ArrayList> mMusicList;

    private Context mContext;

    private MediaController mMediaController;

    public MusicAdapter(Context context, CopyOnWriteArrayList<ArrayList> musicList) {
        super();
        mMusicList = musicList;
        mContext = context;
        mMediaController = ((MusicPlayerApplication) context.getApplicationContext()).getMediaController();
    }

    @Override
    public int getCount() {
        return mMusicList != null ? mMusicList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mMusicList != null ? mMusicList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return mMusicList != null ? position : -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        updateMediaView(convertView, mMusicList.get(position));
        return convertView;
    }

    private void updateMediaView(View view, ArrayList<String> mediaList) {
        Bitmap coverArt = mMediaController.getCaverArt(mediaList.get(MediaController.PATH_INDEX));

        ImageView coverArtView = view.findViewById(R.id.coverArt);
        TextView mediaAlbum = view.findViewById(R.id.media_album);
        TextView mediaTitle = view.findViewById(R.id.media_title);
        TextView mediaArtist = view.findViewById(R.id.media_artist);

        if (coverArt != null) {
            coverArtView.setImageBitmap(coverArt);
        } else {
            coverArtView.setImageResource(R.mipmap.ic_launcher);
        }

        mediaAlbum.setText(mContext.getString(R.string.album));
        mediaTitle.setText(mContext.getString(R.string.title));
        mediaArtist.setText(mContext.getString(R.string.artist));
    }
}
