package com.example.nikolas.spotfiyusefirsttry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class PlayQuiz extends AppCompatActivity implements GamePlayManager {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quiz);
        main= MainActivity.getInstace();
        playQuiz=this;
        getPlaylistTracks(playlistID,playlistUser);
    }

    MainActivity main;
    private static PlayQuiz playQuiz;

    List<PlaylistTrack> playlistTracks;
    public String playlistID = PlaylistSelect.getPlaylistSelect().getPlaylistID();
    public String playlistUser = PlaylistSelect.getPlaylistSelect().getPlaylistUser();;



    public void getPlaylistTracks(String playlistID, String playlistUser){
        main.getPlaylistTracks(playlistID,playlistUser,this);
    }

    @Override
    public void setPlaylist(List<PlaylistTrack> playlistTracks){
        this.playlistTracks=playlistTracks;
    }

    @Override
    public void proceed(){
        Log.e("getPlaylistTracksTest", "reached proceed");
        createQuiz(playlistTracks);
    }

    public void createQuiz(List<PlaylistTrack> playlistTracks){

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
        Quiz quiz = new Quiz(questionsList);
        Log.e("getPlaylistTracksTest", "createQuiz");
        setSongsOnButtons(randomButton,questionsList);
        Log.e("getPlaylistTracksTest", "setSongsButtons");
//        playQuiz(quiz,randomButton);
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

    public static PlayQuiz getInstance(){
        return playQuiz;
    }

}
