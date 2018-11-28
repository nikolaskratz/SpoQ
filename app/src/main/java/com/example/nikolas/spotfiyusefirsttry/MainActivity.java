package com.example.nikolas.spotfiyusefirsttry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
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
    private String authToken;
    private String currentPlayer="edo123";
    private String playerToChallenge;

    private static MainActivity ins;

    String trackName = "";
    boolean playing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Main activity instance for later use in other classes via MainAcitvity.getInstance()
        ins=this;

        //timer set up
//        timerTextView = (TextView) findViewById(R.id.timer);
        
        setUpAuthentication();

        setUpBroadcast();

        setUpButtons();


    }

    public void getPlaylistTracks(String playlistID, String playlistUser, final GamePlayManager gamePlayManager)  {
        Log.e("getPlaylistTracksTest", "started getPlaylistTracksTest");
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(authToken);
        SpotifyService spotify = api.getService();
        Log.e("getPlaylistTracksTest", "authToken: "+authToken);
        spotify.getPlaylistTracks(playlistUser, playlistID, new Callback<Pager<PlaylistTrack>>() {

            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                 Log.e("getPlaylistTracksTest", "GOT the tracks in playlist");
                 List<PlaylistTrack> items = playlistTrackPager.items;
                 gamePlayManager.setPlaylist(items);
                 gamePlayManager.proceed();
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e("getPlaylistTracksTest", "error while getting tracks of playlist");
            }
        });
    }

    //receiving quiz
    public void receiveQuiz(){
        final Quiz[] quiz = new Quiz[1];
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("quiz").child("quiz_id").child("q1");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
//                Log.w("receiveQuizLog", "randomButtonNumber: "+snap.getValue());
                quiz[0] = snap.getValue(Quiz.class);
//                Log.w("receiveQuizLog", randomButtonNumber: "+quiz[0].getRandomButtonNumber());
//                setSongsOnButtons(quiz[0].getRandomButtonNumber(),quiz[0].getQuestionList());
//                playQuiz(quiz[0],quiz[0].getRandomButtonNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });

    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Set the connection parameters
//        ConnectionParams connectionParams =
//                new ConnectionParams.Builder(CLIENT_ID).setRedirectUri(REDIRECT_URI).showAuthView(true).build();
//        SpotifyAppRemote.connect(this, connectionParams,
//                new Connector.ConnectionListener() {
//
//                    @Override
//                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
//                        mSpotifyAppRemote = spotifyAppRemote;
//                        Log.d("MainActivity", "Connected! Yay!");
//                        connected();
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//                        Log.e("MainActivity", throwable.getMessage(), throwable);
//                    }
//                });
//    }

//    private void connected(){
//        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>() {
//                    @Override
//                    public void onEvent(PlayerState playerState) {
//                        final Track track = playerState.track;
//                        if (track != null) {
//                            Log.d("MainActivity", track.name + " by " + track.artist.name);
//                            final TextView songName = (TextView) findViewById(R.id.SongName);
//                            songName.setText(track.name);
//                            trackName=track.name;
////                            Log.d("MainActivity", "meinTest: "+track.name);
//                            final TextView artistName = (TextView) findViewById(R.id.ArtistName);
//                            artistName.setText(track.artist.name);
//                        }
//                    }
//        });
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//    }

    //Authentication function which also saves the AccesToken to use with WebApi
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            Log.e("authDebug", "reached auth, response: "+response.getType());

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    authToken = response.getAccessToken();
                    Log.e("authDebug", "reached token");
                    // Handle successful response
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.e("authDebug", "reached error");
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.e("authDebug", "reached default");
                    // Handle other cases
            }
        }
    }

    //set up authentication (Web)
    public void setUpAuthentication(){
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    //moving to select playlist intent
    public void selectPlaylist(View view){
        Intent intent = new Intent(MainActivity.this, PlaylistSelect.class);
        startActivity(intent);
    }

    //set up all onClicks on the buttons (playPause/next/startQuiz/joinQuiz
    public void setUpButtons(){
        final Button playPauseButton = (Button) findViewById(R.id.PlayPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if (playing) setPPButtonPause(); else setPPButtonPlay();
            }
        });

        final Button nextButton = (Button) findViewById(R.id.NextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                mSpotifyAppRemote.getPlayerApi().skipNext();
            }
        });
        final Button startQuiz = (Button) findViewById(R.id.startQuiz);
        startQuiz.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
//                startActivity(new Intent(MainActivity.this, PlayQuiz.class));
//                    initQuiz(playlistID,playlistUser);
//                playTrack("spotify:track:4W4wYHtsrgDiivRASVOINL");
            }
        });

        final Button joinQuiz = (Button) findViewById(R.id.joinButton);
        joinQuiz.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                receiveQuiz();
            }
        });

        final Button playlistSelect = (Button) findViewById(R.id.playlistSelecter);
        playlistSelect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });

        final Button kalayu = (Button) findViewById(R.id.playVsKalayu);
        kalayu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                playerToChallenge="kalayu";
                Intent intent = new Intent(MainActivity.this, PlaylistSelect.class);
                Log.e("abc123","jojojo");
                intent.putExtra("me",currentPlayer);
                intent.putExtra("vs",playerToChallenge);
                startActivity(intent);
            }
        });
    }

    //init the broadcastreceiver
    public void setUpBroadcast(){
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
    }

    //to return a instance of main to be used in broadcast or possibly someweher else
    public static MainActivity  getInstace(){
        return ins;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }



}
