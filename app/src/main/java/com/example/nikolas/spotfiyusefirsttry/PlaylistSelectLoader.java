package com.example.nikolas.spotfiyusefirsttry;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class PlaylistSelectLoader extends AsyncTaskLoader<List<Playlist>> {

    private static final String TAG = "LoaderDebug";

    private String url;
    private String authToken;

    public PlaylistSelectLoader(Context context, String url, String authToken) {
        super(context);
        this.url = url;
        this.authToken = authToken;
    }

    @Override
    public List<Playlist> loadInBackground() {
        if (url == null || authToken == null) {
            return null;
        }

        List<Playlist> playlists = SpotifyWebApiUtils.getFeaturedPlaylists(url, authToken);

        Log.d(TAG, "loadInBackground: " + playlists);
        return playlists;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
