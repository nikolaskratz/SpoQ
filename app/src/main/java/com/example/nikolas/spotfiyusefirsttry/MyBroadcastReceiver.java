package com.example.nikolas.spotfiyusefirsttry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver {

    String artistName = "";
    String albumName = "";
    String trackName = "";

    static final class BroadcastTypes {
        static final String SPOTIFY_PACKAGE = "com.spotify.music";
        static final String PLAYBACK_STATE_CHANGED = SPOTIFY_PACKAGE + ".playbackstatechanged";
        static final String QUEUE_CHANGED = SPOTIFY_PACKAGE + ".queuechanged";
        static final String METADATA_CHANGED = SPOTIFY_PACKAGE + ".metadatachanged";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This is sent with all broadcasts, regardless of type. The value is taken from
        // System.currentTimeMillis(), which you can compare to in order to determine how
        // old the event is.
        long timeSentInMs = intent.getLongExtra("timeSent", 0L);

        String action = intent.getAction();

        if (action.equals(BroadcastTypes.METADATA_CHANGED)) {
            String trackId = intent.getStringExtra("id");
            artistName = intent.getStringExtra("artist");
            albumName = intent.getStringExtra("album");
            trackName = intent.getStringExtra("track");
//            MainActivity.getInstace().setAlbumName(albumName);
            MainActivity.getInstace().setTrackName(trackName);
            Log.d("MainActivity", "meinTestCase1: trackNameSet" + trackName);
//            MainActivity.getInstace().setArtistName(artistName);
            int trackLengthInSec = intent.getIntExtra("length", 0);
            // Do something with extracted information...
        } else if (action.equals(BroadcastTypes.PLAYBACK_STATE_CHANGED)) {
            boolean playing = intent.getBooleanExtra("playing", false);
            int positionInMs = intent.getIntExtra("playbackPosition", 0);
            // Do something with extracted information
            MainActivity.getInstace().setPlaying(playing);
        } else if (action.equals(BroadcastTypes.QUEUE_CHANGED)) {
            // Sent only as a notification, your app may want to respond accordingly.
        }
    }

}