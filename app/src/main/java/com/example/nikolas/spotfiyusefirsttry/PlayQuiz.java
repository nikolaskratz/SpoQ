package com.example.nikolas.spotfiyusefirsttry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class PlayQuiz extends AppCompatActivity implements GamePlayManager {

    MainActivity main;
    private static PlayQuiz playQuiz;
    private SpotifyAppRemote mSpotifyAppRemote;
    private static final String CLIENT_ID = "2b034014a25644488ec9b5e285abf490";
    private static final String REDIRECT_URI = "testschema://callback";

    List<PlaylistTrack> playlistTracks;
    public String playlistID = PlaylistSelect.getPlaylistSelect().getPlaylistID();
    public String playlistUser = PlaylistSelect.getPlaylistSelect().getPlaylistUser();;

    QuizGame quizGame = new QuizGame();

    int minutes;
    int seconds;
    long millis;
    int minutesTotal;
    int secondsTotal;
    int milliesTotal;

    int gamesPlayed=0;
    int correctAnswers=0;
    int wrongAnswers=0;
    int questionNumber=1;

    //timer set up
    TextView timerTextView;
    long startTime = 0;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            millis = System.currentTimeMillis() - startTime;
            seconds = (int) (millis / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            timerTextView.setText(String.format("%d:%02d",seconds, (millis%100)));

            timerHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quiz);
        main= MainActivity.getInstace();
        playQuiz=this;
        timerTextView = (TextView) findViewById(R.id.timer);
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
                        Log.d("onStartTest", "Connected! Yay!");
                        getPlaylistTracks(playlistID,playlistUser);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("onStartTest", throwable.getMessage(), throwable);
                    }
                });
    }

    public void getPlaylistTracks(String playlistID, String playlistUser){
        main.getPlaylistTracks(playlistID,playlistUser,this);
    }

    @Override
    public void setPlaylist(List<PlaylistTrack> playlistTracks){
        this.playlistTracks=playlistTracks;
    }

    @Override
    public void proceed(){
        checkQuizOverwriting();
    }

    public void createQuiz(List<PlaylistTrack> playlistTracks) throws InterruptedException {

        final List<QuizQuestion> questionsList = new ArrayList<>();
        int playlistSize = playlistTracks.size()-1;
        Collections.shuffle(playlistTracks);

        QuizQuestion q1 = new QuizQuestion(playlistTracks.get(0).track.name,playlistTracks.get(0)
                .track.id,false);
        QuizQuestion q2 = new QuizQuestion(playlistTracks.get(1).track.name,playlistTracks.get(1)
                .track.id,false);
        QuizQuestion q3 = new QuizQuestion(playlistTracks.get(2).track.name,playlistTracks.get(2)
                .track.id,false);
        QuizQuestion q4 = new QuizQuestion(playlistTracks.get(3).track.name,playlistTracks.get(3)
                .track.id,true);
        questionsList.add(q1);
        questionsList.add(q2);
        questionsList.add(q3);
        questionsList.add(q4);

        int randomButton = (int) (Math.random() * 4) ;
        Quiz quiz = new Quiz(questionsList,playlistID);
        quizGame.addQuiz(quiz);
        Log.e("getPlaylistTracksTest", "createQuiz");
        setSongsOnButtons(randomButton,questionsList);
        Log.e("getPlaylistTracksTest", "setSongsButtons");
        playQuiz(quiz,randomButton);
    }

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

    public void playQuiz(final Quiz quiz, final int button) throws InterruptedException {
        playTrack("spotify:track:"+quiz.getQuestionList().get(3).getTrackID());
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        TextView questionCount= findViewById(R.id.questionNumber);
        questionCount.setText("Question "+(gamesPlayed+1)+"/3");
//        questionNumber++;

        final Button songAButton = (Button) findViewById(R.id.songAButton);
        songAButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);

                if (button==3) {
                    songAButton.setBackgroundResource(R.drawable.button_pressed_correct);
                    correctAnswers++;
                } else wrongAnswer(button,songAButton);
                pauseTrack();
                try {
                    nextGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        final Button songBButton = (Button) findViewById(R.id.songBButton);
        songBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                if (button==2) {
                    songBButton.setBackgroundResource(R.drawable.button_pressed_correct);
                    correctAnswers++;
                } else wrongAnswer(button,songBButton);
                pauseTrack() ;
                try {
                    nextGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        final Button songCButton = (Button) findViewById(R.id.songCButton);
        songCButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                if (button==1) {
                    songCButton.setBackgroundResource(R.drawable.button_pressed_correct);
                    correctAnswers++;
                } else wrongAnswer(button,songCButton);
                pauseTrack();
                try {
                    nextGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        final Button songDButton = (Button) findViewById(R.id.songDButton);
        songDButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                if (button==0) {
                    songDButton.setBackgroundResource(R.drawable.button_pressed_correct);
                    correctAnswers++;
                } else wrongAnswer(button,songDButton);
                pauseTrack();
                try {
                    nextGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        setTotalTime(minutes,seconds,(int) millis);
//        Connection connection = new Connection(quiz,button);

    }

    public void wrongAnswer(int buttonCorrect,Button button){
        wrongAnswers++;
//        getWindow().getDecorView().setBackgroundColor(Color.RED);
        button.setBackgroundResource(R.drawable.button_pressed_wrong);
        setCorrect(buttonCorrect);
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(500);
        }
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getWindow().getDecorView().setBackgroundColor(getResources().getColor
//                        (R.color.colorPrimaryDark));
//            }
//        }, 500);

    }

    public void setCorrect(int buttonCorrect){
        if(buttonCorrect==3) {
            findViewById(R.id.songAButton).setBackgroundResource(R.drawable.button_pressed_correct);
            Log.e("setCorrectTest","A");
        }
        if(buttonCorrect==2) {
            findViewById(R.id.songBButton).setBackgroundResource(R.drawable.button_pressed_correct);
            Log.e("setCorrectTest","B");
        }
        if(buttonCorrect==1) {
            findViewById(R.id.songCButton).setBackgroundResource(R.drawable.button_pressed_correct);
            Log.e("setCorrectTest","C");
        }
        if(buttonCorrect==0) {
            findViewById(R.id.songDButton).setBackgroundResource(R.drawable.button_pressed_correct);
            Log.e("setCorrectTest","D");
        }
    }

    public void nextGame() throws InterruptedException {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                if(gamesPlayed++<2) {

                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    resetButtonColor();
                    getPlaylistTracks(playlistID,playlistUser);
                } else {
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    setTotalTime(minutes,seconds,(int) millis);
                    Intent intent = new Intent(PlayQuiz.this, QuizSummary.class);
                    intent.putExtra("Minutes", minutesTotal);
                    intent.putExtra("Seconds", secondsTotal);
                    intent.putExtra("Millis", milliesTotal);
                    intent.putExtra("Correct Answers", correctAnswers);
                    intent.putExtra("Wrong Answers",wrongAnswers);
                    intent.putExtra("Quiz",(new Gson()).toJson(quizGame));
                    String me = getIntent().getExtras().getString("me");
                    intent.putExtra("me",me);
                    String vs = getIntent().getExtras().getString("vs");
                    intent.putExtra("vs",vs);
                    startActivity(intent);
                    finish();
                }
            }
        });
        thread.start();

    }

    void checkQuizOverwriting(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // A and B have to be changed to actual users later
        DatabaseReference myRef = database.getReference("Quiz");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("playerAplayerB"+playlistID)) {
                    Log.e("duplicateCheck","quizalready exists, quiz will not start");
                    //following part should be removed later becuase it overwrites the quiz START
                    try {
                        createQuiz(playlistTracks);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //remove later END
                } else {
                    try {
                        createQuiz(playlistTracks);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void setTotalTime (int minutes, int seconds, int millies){
        minutesTotal+=minutes;
        secondsTotal+=seconds;
        milliesTotal+=millies;
        TextView finalTimer = (TextView) findViewById(R.id.totalTimer);
        finalTimer.setText(String.format("%d:%02d:%3d", minutesTotal, secondsTotal,
                (milliesTotal%100)));
    }

    void resetButtonColor(){
        final Button songAButton = (Button) findViewById(R.id.songAButton);
        final Button songBButton = (Button) findViewById(R.id.songBButton);
        final Button songCButton = (Button) findViewById(R.id.songCButton);
        final Button songDButton = (Button) findViewById(R.id.songDButton);
        songAButton.setBackgroundResource(R.drawable.button_border);
        songBButton.setBackgroundResource(R.drawable.button_border);
        songCButton.setBackgroundResource(R.drawable.button_border);
        songDButton.setBackgroundResource(R.drawable.button_border);
    }

    public void playTrack(String trackID){
        mSpotifyAppRemote.getPlayerApi().play(trackID);
    }
    public void pauseTrack() {
        mSpotifyAppRemote.getPlayerApi().pause();
    }
    public static PlayQuiz getInstance(){
        return playQuiz;
    }
}
