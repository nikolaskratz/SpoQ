package com.example.nikolas.spotfiyusefirsttry;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class PlaylistSelectLoader extends AsyncTaskLoader<List<Playlist>> {

    private static final String TAG = "LoaderDebug";

    private String url;

    public PlaylistSelectLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public List<Playlist> loadInBackground() {
        Log.d(TAG, "loadInBackground: ");
        if (url == null) {
            return null;
        }

       // List<Playlist> playlists = QueryUtils.fetchUSGSdata(url);
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }
}
