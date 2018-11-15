package com.example.nikolas.spotfiyusefirsttry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.WampClient;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Result;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "2b034014a25644488ec9b5e285abf490";
    private static final String REDIRECT_URI = "testschema://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    private static MainActivity ins;
    private boolean playing=false;
    String playlistTrap = "spotify:user:1146468343:playlist:7bub4LVEY7HIL4m27h3ZXE";
    String playlistSchlager= "spotify:user:1146468343:playlist:3rT1Fcx6X9fhyMof5Za2NN";

    String artistName = "";
    String albumName = "";
    String trackName = "";
    String trackNameTemp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins=this;
        setContentView(R.layout.activity_main);

        MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();

        IntentFilter filter1 = new IntentFilter();
        IntentFilter filter2 = new IntentFilter();
        IntentFilter filter3 = new IntentFilter();
        filter1.addAction("com.spotify.music.playbackstatechanged");
        filter2.addAction("com.spotify.music.metadatachanged");
        filter3.addAction("com.spotify.music.queuechanged");
        registerReceiver(myBroadcastReceiver, filter1);
        registerReceiver(myBroadcastReceiver, filter2);
        registerReceiver(myBroadcastReceiver, filter3);
        final Button playPauseButton = (Button) findViewById(R.id.PlayPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (playing) setPPButtonPause(); else setPPButtonPlay();
            }
        });

        final Button nextButton = (Button) findViewById(R.id.NextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSpotifyAppRemote.getPlayerApi().skipNext();
            }
        });
        final Button trapButton = (Button) findViewById(R.id.TrapButton);
        trapButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                initQuiz(playlistTrap);
            }
        });
        final Button schlagerButton = (Button) findViewById(R.id.SchlagerButton);
        schlagerButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mSpotifyAppRemote.getPlayerApi().play(playlistSchlager);
            }
        });
    }

    /*Not working properly and rubbish code
    */
    public void initQuiz (String playlist){
        int random = (int) (Math.random() * 4) +1;
        Log.d("MainActivity", "meinTestCase2: randomNumber ist: "+random);
        Object[] songA = new Object[] {0,"songA",random};
        Object[] songB = new Object[] {0,"songB",(random+1) % 4};
        Object[] songC = new Object[] {0,"songC",(random+2) % 4};
        Object[] songD = new Object[] {1,"songD",(random+3) % 4};
        mSpotifyAppRemote.getPlayerApi().pause();
        mSpotifyAppRemote.getPlayerApi().play(playlist);
            songA[1] = trackName;
            trackNameTemp = trackName;
            mSpotifyAppRemote.getPlayerApi().pause();
            Log.d("MainActivity", "meinTestCase1: trackName Empfangen1 "+trackName);
            mSpotifyAppRemote.getPlayerApi().skipNext();


            songB[1] = trackName;
            trackNameTemp = trackName;
            Log.d("MainActivity", "meinTestCase1: trackName Empfangen2 " + trackName);
            mSpotifyAppRemote.getPlayerApi().pause();
            mSpotifyAppRemote.getPlayerApi().skipNext();


            songC[1] = trackName;
            trackNameTemp = trackName;
            Log.d("MainActivity", "meinTestCase1: trackName Empfangen3 " + trackName);
            mSpotifyAppRemote.getPlayerApi().pause();
            mSpotifyAppRemote.getPlayerApi().skipNext();


            songD[1] = trackName;
            trackNameTemp = trackName;
            Log.d("MainActivity", "meinTestCase1: trackName Empfangen4 " + trackName);

        final Button buttonSongA = (Button) findViewById(R.id.songAButton);
        buttonSongA.setText(songA[1].toString());
        final Button buttonSongB = (Button) findViewById(R.id.songBButton);
        buttonSongB.setText(songB[1].toString());
        final Button buttonSongC = (Button) findViewById(R.id.songCButton);
        buttonSongC.setText(songC[1].toString());
        final Button buttonSongD = (Button) findViewById(R.id.songDButton);
        buttonSongD.setText(songD[1].toString());

    }

    void setPPButtonPlay(){
        final Button playPauseButton = (Button) findViewById(R.id.PlayPauseButton);
        mSpotifyAppRemote.getPlayerApi().resume();
        playPauseButton.setText("Pause");
        Log.d("MainActivity", "meinTestCase1: playPauseButton resumed");
    }
    void setPPButtonPause(){
        final Button playPauseButton = (Button) findViewById(R.id.PlayPauseButton);
        mSpotifyAppRemote.getPlayerApi().pause();
        playPauseButton.setText("Play");
        Log.d("MainActivity", "meinTestCase1: playPauseButton paused");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID).setRedirectUri(REDIRECT_URI).showAuthView(true).build();
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    private void connected(){
        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                            Log.d("MainActivity", track.name + " by " + track.artist.name);
                            final TextView songName = (TextView) findViewById(R.id.SongName);
                            songName.setText(track.name);
                            trackName=track.name;
//                            Log.d("MainActivity", "meinTest: "+track.name);
                            final TextView artistName = (TextView) findViewById(R.id.ArtistName);
                            artistName.setText(track.artist.name);
                        }
                    }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    public static MainActivity  getInstace(){
        return ins;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
