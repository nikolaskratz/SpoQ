package com.example.nikolas.spotfiyusefirsttry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonToken;
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
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "2b034014a25644488ec9b5e285abf490";
    private static final String REDIRECT_URI = "testschema://callback";
    private static final int REQUEST_CODE = 1337;
    private SpotifyAppRemote mSpotifyAppRemote;

    private static MainActivity ins;
    private String authToken;
    private boolean playing=false;
    String playlistTrap = "spotify:user:1146468343:playlist:7bub4LVEY7HIL4m27h3ZXE";
    String playlistSchlager= "spotify:user:1146468343:playlist:3rT1Fcx6X9fhyMof5Za2NN";
    final List<Quiz> quizList = new ArrayList<>();

    String artistName = "";
    String albumName = "";
    String trackName = "";
    String trackNameTemp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ins=this;
        setContentView(R.layout.activity_main);

        //Authentication starts here
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        //------------Authentication Stops here

        //Broadcast code
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

        //ButtonCode
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



    public void initQuiz (String playlist) {
        resetButtonColor();
        final List<String> playlistTracks = new ArrayList<>();
        final List<String> playlistTracksIDs = new ArrayList<>();
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(authToken);
        SpotifyService spotify = api.getService();
        spotify.getPlaylistTracks("1146468343", "7bub4LVEY7HIL4m27h3ZXE", new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
//                Log.e("TEST123", "GOT the tracks in playlist");
                List<PlaylistTrack> items = playlistTrackPager.items;
                for (PlaylistTrack pt : items) {
//                    Log.e("TEST123", pt.track.name + " - " + pt.track.id);
                    playlistTracks.add(pt.track.name);
                    playlistTracksIDs.add(pt.track.id);
                }
                createQuiz(playlistTracks,playlistTracksIDs);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("TEST123", "Could not get playlist tracks");
            }
        });
    }

    public void createQuiz(List<String> playlistTracks, List<String> playlistTracksIDs){

        final List<QuizQuestion> questionsList = new ArrayList<>();
        int playlistSize = playlistTracks.size()-1;
        int randomTrack = (int) (Math.random() * (playlistTracks.size()-1));
        QuizQuestion q1 = new QuizQuestion(playlistTracks.get(randomTrack % playlistSize
        ),playlistTracksIDs.get(randomTrack % playlistTracks.size()),false);
        QuizQuestion q2 = new QuizQuestion(playlistTracks.get((randomTrack+1) % playlistSize),
                playlistTracksIDs.get((randomTrack+1) % playlistTracks.size()),false);
        QuizQuestion q3 = new QuizQuestion(playlistTracks.get((randomTrack+2) % playlistSize),
                playlistTracksIDs.get((randomTrack+2) % playlistTracks.size()),false);
        QuizQuestion q4 = new QuizQuestion(playlistTracks.get((randomTrack+3) % playlistSize),
                playlistTracksIDs.get((randomTrack+3) % playlistTracks.size()),true);
        questionsList.add(q1);
        questionsList.add(q2);
        questionsList.add(q3);
        questionsList.add(q4);

        Quiz quiz = new Quiz(q1,q2,q3,q4);
        quizList.add(quiz);
        int randomButton = (int) (Math.random() * 4) ;
        Log.e("TEST123", "n: "+randomButton+"//"+q1.getTrackName()+"  "+q2.getTrackName()+"  " +
                        ""+q3.getTrackName()+" "+q4.getTrackName());
        final Button buttonSongA = (Button) findViewById(R.id.songAButton);
        buttonSongA.setText(questionsList.get(randomButton).getTrackName());
        final Button buttonSongB = (Button) findViewById(R.id.songBButton);
        buttonSongB.setText(questionsList.get((randomButton+1) % 4).getTrackName());
        final Button buttonSongC = (Button) findViewById(R.id.songCButton);
        buttonSongC.setText(questionsList.get((randomButton+2) % 4).getTrackName());
        final Button buttonSongD = (Button) findViewById(R.id.songDButton);
        buttonSongD.setText(questionsList.get((randomButton+3) % 4).getTrackName());

        mSpotifyAppRemote.getPlayerApi().play("spotify:track:"+q4.getTrackID());
        Log.e("TEST123", "true track: "+q4.getTrackName()+"  trackID: "+q4.getTrackID());
        playQuiz(quiz,randomButton);
    }

    public void playQuiz(Quiz quiz, final int button){

        final Button songAButton = (Button) findViewById(R.id.songAButton);
        songAButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button==3) {
                    songAButton.setBackgroundResource(R.color.SongButtonColorCorrect);
                }
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        });
        final Button songBButton = (Button) findViewById(R.id.songBButton);
        songBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button==2) {
                    songBButton.setBackgroundResource(R.color.SongButtonColorCorrect);
                }
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        });
        final Button songCButton = (Button) findViewById(R.id.songCButton);
        songCButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button==1) {
                    songCButton.setBackgroundResource(R.color.SongButtonColorCorrect);
                }
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        });
        final Button songDButton = (Button) findViewById(R.id.songDButton);
        songDButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button==0) {
                    songDButton.setBackgroundResource(R.color.SongButtonColorCorrect);
                }
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        });
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


    //Authentication function which also saves the AccesToken to use with WebApi
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    authToken = response.getAccessToken();
                    // Handle successful response
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    void resetButtonColor(){
        final Button songAButton = (Button) findViewById(R.id.songAButton);
        final Button songBButton = (Button) findViewById(R.id.songBButton);
        final Button songCButton = (Button) findViewById(R.id.songCButton);
        final Button songDButton = (Button) findViewById(R.id.songDButton);
        songAButton.setBackgroundResource(R.color.SongButtonColor);
        songBButton.setBackgroundResource(R.color.SongButtonColor);
        songCButton.setBackgroundResource(R.color.SongButtonColor);
        songDButton.setBackgroundResource(R.color.SongButtonColor);
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
