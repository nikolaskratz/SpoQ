package com.example.nikolas.spotfiyusefirsttry;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.UserApi;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class PlayQuiz extends AppCompatActivity implements GamePlayManager {

    private boolean initialActivityState = false;
    private boolean summaryActivityState = false;

    PlaylistSelectActivity playlistSelect;
    private static PlayQuiz playQuiz;
    private SpotifyAppRemote mSpotifyAppRemote;
    private static final String CLIENT_ID = "2b034014a25644488ec9b5e285abf490";
    private static final String REDIRECT_URI = "testschema://callback";

    List<PlaylistTrack> playlistTracks;
    public String playlistID;
    public String playlistUser;
    private static final String TAG = "PlayQuizLog";

    QuizGame quizGame = new QuizGame();

    int minutes;
    int seconds;
    long millis;
    int minutesTotal;
    int secondsTotal;
    int milliesTotal;

    int points = 0;

    int gamesPlayed = 0;
    int correctAnswers = 0;
    int wrongAnswers = 0;
    int questionNumber = 1;

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
            timerTextView.setText(String.format("%d:%02d", seconds, (millis % 100)));

            timerHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quiz);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        playlistSelect = PlaylistSelectActivity.getPlaylistSelect();
        playQuiz = this;
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
                        checkSubscriptionStatus(mSpotifyAppRemote);

                        boolean invite = getIntent().getBooleanExtra("invite", false);
                        if (invite) {
                            receiveQuiz();
                        } else {
                            playlistID = PlaylistSelectActivity.getPlaylistSelect().getPlaylistID();
                            playlistUser = PlaylistSelectActivity.getPlaylistSelect().getPlaylistUser();
                            Log.e("getPlaylistTracksTest", "start2");
                            getPlaylistTracks(playlistID, playlistUser);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("onStartTest", throwable.getMessage(), throwable);
                    }
                });

    }

    public void getPlaylistTracks(String playlistID, String playlistUser) {
        playlistSelect.getPlaylistTracks(playlistID, playlistUser, this);
    }

    @Override
    public void setPlaylist(List<PlaylistTrack> playlistTracks) {
        this.playlistTracks = playlistTracks;
    }

    @Override
    public void proceed() {
        try {
            createQuiz(playlistTracks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        checkQuizOverwriting();
    }

    public void createQuiz(List<PlaylistTrack> playlistTracks) throws InterruptedException {

        final List<QuizQuestion> questionsList = new ArrayList<>();
        int playlistSize = playlistTracks.size() - 1;
        Collections.shuffle(playlistTracks);

        QuizQuestion q1 = new QuizQuestion(playlistTracks.get(0).track.name, playlistTracks.get(0)
                .track.id, false);
        QuizQuestion q2 = new QuizQuestion(playlistTracks.get(1).track.name, playlistTracks.get(1)
                .track.id, false);
        QuizQuestion q3 = new QuizQuestion(playlistTracks.get(2).track.name, playlistTracks.get(2)
                .track.id, false);
        QuizQuestion q4 = new QuizQuestion(playlistTracks.get(3).track.name, playlistTracks.get(3)
                .track.id, true);
        questionsList.add(q1);
        questionsList.add(q2);
        questionsList.add(q3);
        questionsList.add(q4);

        int randomButton = (int) (Math.random() * 4);
        Quiz quiz = new Quiz(questionsList, playlistID, randomButton);
        quizGame.addQuiz(quiz);
        Log.e("getPlaylistTracksTest", "createQuiz");
        setSongsOnButtons(randomButton, questionsList);
        Log.e("getPlaylistTracksTest", "setSongsButtons");
        playQuiz(quiz, randomButton);
    }

    public void setSongsOnButtons(final int randomButton, final List<QuizQuestion> questionsList) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final Button buttonSongA = (Button) findViewById(R.id.songAButton);
                buttonSongA.setText(questionsList.get(randomButton).getTrackName());
                final Button buttonSongB = (Button) findViewById(R.id.songBButton);
                buttonSongB.setText(questionsList.get((randomButton + 1) % 4).getTrackName());
                final Button buttonSongC = (Button) findViewById(R.id.songCButton);
                buttonSongC.setText(questionsList.get((randomButton + 2) % 4).getTrackName());
                final Button buttonSongD = (Button) findViewById(R.id.songDButton);
                buttonSongD.setText(questionsList.get((randomButton + 3) % 4).getTrackName());
            }
        });

    }

    public void playQuiz(final Quiz quiz, final int button) throws InterruptedException {
        playTrack("spotify:track:" + quiz.getQuestionList().get(3).getTrackID());
        seekTrack();

        startTime = System.currentTimeMillis();
        final boolean[] correct = {false};
        timerHandler.postDelayed(timerRunnable, 0);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                TextView questionCount = findViewById(R.id.questionNumber);
                questionCount.setText("Question " + (gamesPlayed + 1) + "/3");
            }
        });


        final Button songAButton = (Button) findViewById(R.id.songAButton);
        songAButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);

                if (button == 3) {
                    songAButton.setBackgroundResource(R.drawable.button_pressed_correct);
                    correctAnswers++;
                    correct[0] = true;
                } else wrongAnswer(button, songAButton);
                setTotalTime(minutes, seconds, (int) millis, correct[0]);
                pauseTrack();
                showCover();
                try {
                    nextGame(correct[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        final Button songBButton = (Button) findViewById(R.id.songBButton);
        songBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                if (button == 2) {
                    songBButton.setBackgroundResource(R.drawable.button_pressed_correct);
                    correctAnswers++;
                    correct[0] = true;
                } else wrongAnswer(button, songBButton);
                setTotalTime(minutes, seconds, (int) millis, correct[0]);
                pauseTrack();
                showCover();
                try {
                    nextGame(correct[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        final Button songCButton = (Button) findViewById(R.id.songCButton);
        songCButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                if (button == 1) {
                    songCButton.setBackgroundResource(R.drawable.button_pressed_correct);
                    correctAnswers++;
                    correct[0] = true;
                } else wrongAnswer(button, songCButton);
                setTotalTime(minutes, seconds, (int) millis, correct[0]);
                pauseTrack();
                showCover();
                try {
                    nextGame(correct[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        final Button songDButton = (Button) findViewById(R.id.songDButton);
        songDButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timerHandler.removeCallbacks(timerRunnable);
                if (button == 0) {
                    songDButton.setBackgroundResource(R.drawable.button_pressed_correct);
                    correctAnswers++;
                    correct[0] = true;
                } else wrongAnswer(button, songDButton);
                setTotalTime(minutes, seconds, (int) millis, correct[0]);
                pauseTrack();
                showCover();
                try {
                    nextGame(correct[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

//        Log.e("test2","after all buttons; s"+seconds+", correct:"+correct[0]);
//        setTotalTime(minutes,seconds,(int) millis,correct[0]);
//        Log.e("test2","complete");
        final TextView pointView = findViewById(R.id.Points);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                pointView.setText("" + points);
            }
        });
//        Connection connection = new Connection(quiz,button);

    }

    public void receiveQuiz() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Quiz").child
                (getIntent().getExtras().getString("quizID"));
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                quizGame = snap.getValue(QuizGame.class);

                try {
                    joinQuiz(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("myTag", "Failed to read value.");
            }
        });
    }

    public void joinQuiz(final int i) throws InterruptedException {
        setSongsOnButtons(quizGame.getQuizList().get(i).getRandomButtonNumber(),
                quizGame.getQuizList().get(i).getQuestionList());
        Log.e("randomButton: ", "" + quizGame.getQuizList().get(i).getRandomButtonNumber());
        playQuiz(quizGame.getQuizList().get(i), quizGame.getQuizList().get(i).getRandomButtonNumber());
    }

    public void wrongAnswer(int buttonCorrect, Button button) {
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

    public void setCorrect(int buttonCorrect) {
        if (buttonCorrect == 3) {
            findViewById(R.id.songAButton).setBackgroundResource(R.drawable.button_pressed_correct);
            Log.e("setCorrectTest", "A");
        }
        if (buttonCorrect == 2) {
            findViewById(R.id.songBButton).setBackgroundResource(R.drawable.button_pressed_correct);
            Log.e("setCorrectTest", "B");
        }
        if (buttonCorrect == 1) {
            findViewById(R.id.songCButton).setBackgroundResource(R.drawable.button_pressed_correct);
            Log.e("setCorrectTest", "C");
        }
        if (buttonCorrect == 0) {
            findViewById(R.id.songDButton).setBackgroundResource(R.drawable.button_pressed_correct);
            Log.e("setCorrectTest", "D");
        }
    }

    public void nextGame(boolean correct) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            boolean invite = getIntent().getBooleanExtra("invite", false);

            @Override
            public void run() {
                if (gamesPlayed++ < 2) {
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    resetButtonColor();
                    if (invite) {
                        try {
                            joinQuiz(gamesPlayed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else getPlaylistTracks(playlistID, playlistUser);
                } else {
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    summaryActivityState = true;
                    setTotalTime(minutes, seconds, (int) millis, correct);
                    Intent intent = new Intent(PlayQuiz.this, QuizSummary.class);
                    intent.putExtra("Minutes", minutesTotal);
                    intent.putExtra("Seconds", secondsTotal);
                    intent.putExtra("Millis", milliesTotal);
                    intent.putExtra("Points", points);
                    intent.putExtra("Correct Answers", correctAnswers);
                    intent.putExtra("Wrong Answers", wrongAnswers);
                    intent.putExtra("invite", invite);
                    intent.putExtra("Quiz", (new Gson()).toJson(quizGame));
                    String me = getIntent().getExtras().getString("me");
                    intent.putExtra("me", me);
                    String vs = getIntent().getExtras().getString("vs");
                    intent.putExtra("vs", vs);
                    startActivity(intent);
                    finish();
                }
            }
        });
        thread.start();

    }

    void checkQuizOverwriting() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // A and B have to be changed to actual users later
        DatabaseReference myRef = database.getReference("Quiz");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("playerAplayerB" + playlistID)) {
                    Log.e("duplicateCheck", "quizalready exists, quiz will not start");
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

    void setTotalTime(int minutes, int seconds, int millies, boolean correct) {
        minutesTotal += minutes;
        secondsTotal += seconds;
        milliesTotal += millies;
        TextView finalTimer = (TextView) findViewById(R.id.totalTimer);
        finalTimer.setText(String.format("%d:%02d:%3d", minutesTotal, secondsTotal, (milliesTotal % 100)));

        int add;
        if (seconds == 0) {
            add = 100;
            Log.e("test2", "s=0");
        } else if (100 / seconds >= 10) {
            add = 100 / seconds;
            Log.e("test2", "100/s >=10");
        } else add = 10;

        if (correct) {
            points = points + add;
            Log.e("test2", "correct");
        } else points = points + 5;
        Log.e("test2", "finsih, result:" + correct + ", points:" + points);

    }

    void resetButtonColor() {
        final ImageView hidingCover = (ImageView) findViewById(R.id.spotifyBlur);
        hidingCover.setImageAlpha(250);
        final Button songAButton = (Button) findViewById(R.id.songAButton);
        final Button songBButton = (Button) findViewById(R.id.songBButton);
        final Button songCButton = (Button) findViewById(R.id.songCButton);
        final Button songDButton = (Button) findViewById(R.id.songDButton);
        songAButton.setBackgroundResource(R.drawable.button_border);
        songBButton.setBackgroundResource(R.drawable.button_border);
        songCButton.setBackgroundResource(R.drawable.button_border);
        songDButton.setBackgroundResource(R.drawable.button_border);
    }

    public void playTrack(String trackID) {
        mSpotifyAppRemote.getPlayerApi().play(trackID);
    }

    public void seekTrack() {
        final boolean[] once = {true};
        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(new Subscription.EventCallback<PlayerState>() {

            @Override
            public void onEvent(PlayerState playerState) {

                final Track track = playerState.track;
                final ImageView cover = (ImageView) findViewById(R.id.imageView4);
                if (track != null) {
                    mSpotifyAppRemote.getImagesApi().getImage(track.imageUri).setResultCallback(
                            new CallResult.ResultCallback<Bitmap>() {
                                @Override
                                public void onResult(Bitmap bitmap) {
                                    cover.setImageBitmap(bitmap);
                                }
                            });
                }
                if (!playerState.isPaused && once[0]) {
                    mSpotifyAppRemote.getPlayerApi().seekTo(50000);
                    once[0] = false;
                }
            }
        });
    }

    public void pauseTrack() {
        mSpotifyAppRemote.getPlayerApi().pause();
    }

    public void showCover() {
        final ImageView hidingCover = (ImageView) findViewById(R.id.spotifyBlur);
        hidingCover.setImageAlpha(0);
    }

    //checking if user is premium or not
    public void checkSubscriptionStatus(SpotifyAppRemote spotifyAppRemote) {
        UserApi userApi = spotifyAppRemote.getUserApi();
        userApi.getCapabilities().setResultCallback(new CallResult.ResultCallback<Capabilities>() {
            @Override
            public void onResult(Capabilities capabilities) {
                if (capabilities.canPlayOnDemand) {
                    Log.d(TAG, "can play on demand");
                } else {
                    Log.d(TAG, "not play on demand");
                    Intent intent = new Intent(PlayQuiz.this, MainAppActivity.class);
                    intent.putExtra("premium", false);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // condition canceling behaviour of onWindowFocusChanged when the activity is starting
        if (!initialActivityState) {
            initialActivityState = true;
            return;
        }

        // condition when the activity is about to finish
        if (summaryActivityState){
            return;
        }

        Log.d(TAG, "onWindowFocusChanged!!!: ");
        if (!hasFocus) {
            pauseTrack();
            Intent intent = new Intent(this, MainAppActivity.class);
            intent.putExtra("cheater", true);
            intent.putExtra("premium", true);
            cheaterHandling();
            startActivity(intent);
            this.finish();
        }
    }

    void cheaterHandling() {
        boolean invite = getIntent().getBooleanExtra("invite", false);
        if (quizGame != null && invite) {
            QuizResult quizResult = quizGame.getQuizResult();
            quizResult.setPointsP2(0);
            String vs = getIntent().getExtras().getString("vs");
            String me = getIntent().getExtras().getString("me");
            String playlistID = quizGame.getQuizList().get(0).getPlaylistID();
            String quizIDrev = vs + "-" + me + "-" + playlistID;
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Quiz").child
                    (quizIDrev);
            myRef.child("quizResult").setValue(quizResult);

            DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("Users").child(me);
            myRef2.child("results").child(quizIDrev).setValue(quizResult);
            myRef2.child("results").child(quizIDrev).child("timestamp").setValue(ServerValue.TIMESTAMP);

            DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("Users").child(vs);
            myRef3.child("results").child(quizIDrev).setValue(quizResult);
            myRef3.child("results").child(quizIDrev).child("timestamp").setValue(ServerValue.TIMESTAMP);

            FirebaseDatabase.getInstance().getReference("Users").child(me)
                    .child("games").child(quizIDrev).removeValue();
        }
    }
}
