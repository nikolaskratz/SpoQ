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
    private String authToken;

    Object o = new Object();


    private static MainActivity ins;

//    SpotifyService spotify;

    private boolean playing=false;
    String playlistID="37i9dQZF1DX0XUsuxWHRQd";
    String playlistUser="spotify";
    final List<Quiz> quizList = new ArrayList<>();
    List<String> pTracks;

    String artistName = "";
    String albumName = "";
    String trackName = "";
    String trackNameTemp = "";

    int minutes;
    int seconds;

    //timer set up
    TextView timerTextView;
    long startTime = 0;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            seconds = (int) (millis / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            timerTextView.setText(String.format("%d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Main activity instance for later use in other classes via MainAcitvity.getInstance()
        ins=this;

        //timer set up
        timerTextView = (TextView) findViewById(R.id.timer);
        
        setUpAuthentication();

        setUpBroadcast();

        setUpButtons();


    }
    
    //quiz initilization, using authentication token for getting playlist tracks
    public void initQuiz (String playlistID, String playlistUser) {

            resetButtonColor();
            final List<String> playlistTracks = new ArrayList<>();
            final List<String> playlistTracksIDs = new ArrayList<>();
            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(authToken);
            SpotifyService spotify = api.getService();

            Log.e("TEST1234", "start t1");
            spotify.getPlaylistTracks(playlistUser, playlistID, new Callback<Pager<PlaylistTrack>>() {
                @Override
                public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                    Log.e("TEST1234", "GOT the tracks in playlist");
                    List<PlaylistTrack> items = playlistTrackPager.items;
                    for (PlaylistTrack pt : items) {
//                    Log.e("TEST123", pt.track.name + " - " + pt.track.id);
                        playlistTracks.add(pt.track.name);
                        playlistTracksIDs.add(pt.track.id);
                    }
                    createQuiz(playlistTracks, playlistTracksIDs);
//                    quizPlay.setPlaylistTracks(playlistTracks);
//                    quizPlay.proceed();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("TEST123", "Could not get playlist tracks");
                }
            });



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

    //actually building the quiz with questions etc, for the 1st player
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

        int randomButton = (int) (Math.random() * 4) ;
        Quiz quiz = new Quiz(questionsList);
        quizList.add(quiz);
        Log.e("TEST123", "n: "+randomButton+"//"+q1.getTrackName()+"  "+q2.getTrackName()+"  " +
                        ""+q3.getTrackName()+" "+q4.getTrackName());
        setSongsOnButtons(randomButton,questionsList);

        Log.e("TEST123", "true track: "+q4.getTrackName()+"  trackID: "+q4.getTrackID());
        playQuiz(quiz,randomButton);
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
                setSongsOnButtons(quiz[0].getRandomButtonNumber(),quiz[0].getQuestionList());
                playQuiz(quiz[0],quiz[0].getRandomButtonNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });

    }

    //setting songs on buttons according to random number
    public void setSongsOnButtons (int randomButton, List<QuizQuestion> questionsList){
        final Button buttonSongA = (Button) findViewById(R.id.songAButton);
        buttonSongA.setText(questionsList.get(randomButton).getTrackName());
        final Button buttonSongB = (Button) findViewById(R.id.songBButton);
        buttonSongB.setText(questionsList.get((randomButton+1) % 4).getTrackName());
        final Button buttonSongC = (Button) findViewById(R.id.songCButton);
        buttonSongC.setText(questionsList.get((randomButton+2) % 4).getTrackName());
        final Button buttonSongD = (Button) findViewById(R.id.songDButton);
        buttonSongD.setText(questionsList.get((randomButton+3) % 4).getTrackName());
    }

    //playing quiz
    public void playQuiz(Quiz quiz, final int button){
        mSpotifyAppRemote.getPlayerApi().play("spotify:track:"+quiz.getQuestionList().get(3)
                .getTrackID
                ());
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        final Button songAButton = (Button) findViewById(R.id.songAButton);
        songAButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                setLastTimer(minutes,seconds);
                if (button==3) {
                    songAButton.setBackgroundResource(R.color.SongButtonColorCorrect);
                } else wrongAnswer();
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        });
        final Button songBButton = (Button) findViewById(R.id.songBButton);
        songBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                setLastTimer(minutes,seconds);
                if (button==2) {
                    songBButton.setBackgroundResource(R.color.SongButtonColorCorrect);
                } else wrongAnswer();
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        });
        final Button songCButton = (Button) findViewById(R.id.songCButton);
        songCButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                setLastTimer(minutes,seconds);
                if (button==1) {
                    songCButton.setBackgroundResource(R.color.SongButtonColorCorrect);
                } else wrongAnswer();
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        });
        final Button songDButton = (Button) findViewById(R.id.songDButton);
        songDButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                setLastTimer(minutes,seconds);
                if (button==0) {
                    songDButton.setBackgroundResource(R.color.SongButtonColorCorrect);
                } else wrongAnswer();
                mSpotifyAppRemote.getPlayerApi().pause();
            }
        });
        Connection connection = new Connection(quiz,button);
    }

    //reaction to wrong answer
    public void wrongAnswer(){
        getWindow().getDecorView().setBackgroundColor(Color.RED);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            }
        }, 500);
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
//                        if (track != null) {
//                            Log.d("MainActivity", track.name + " by " + track.artist.name);
//                            final TextView songName = (TextView) findViewById(R.id.SongName);
//                            songName.setText(track.name);
//                            trackName=track.name;
////                            Log.d("MainActivity", "meinTest: "+track.name);
//                            final TextView artistName = (TextView) findViewById(R.id.ArtistName);
//                            artistName.setText(track.artist.name);
//                        }
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

    //set up authentication
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

    //setting timerLast textView
    void setLastTimer (int minutes, int seconds){
        TextView finalTimer = (TextView) findViewById(R.id.timerLast);
        finalTimer.setText(String.format("%d:%02d", minutes, seconds));
    }

    //set button colour to original colour after each quiz
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

    //set up all onClicks on the buttons (playPause/next/startQuiz/joinQuiz
    public void setUpButtons(){
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
        final Button startQuiz = (Button) findViewById(R.id.startQuiz);
        startQuiz.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
//                startActivity(new Intent(MainActivity.this, PlayQuiz.class));
                    initQuiz(playlistID,playlistUser);
            }
        });

        final Button joinQuiz = (Button) findViewById(R.id.joinButton);
        joinQuiz.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                receiveQuiz();
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

    //setter for Current Playlist TextView
    public void setCurrentPlaylistText(String playlist){
        final TextView currentPlaylistButton = (TextView) findViewById(R.id.currentPlaylist);
        currentPlaylistButton.setText(playlist);
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
    public void setPlaylistID(String playlistID) {
        this.playlistID = playlistID;
    }
    public void setPlaylistUser(String playlistUser) {
        this.playlistUser = playlistUser;
    }

    public String getAuthToken() {
        return authToken;
    }

//    public SpotifyService getSpotifyService(){
//        return spotify;
//    }
}
